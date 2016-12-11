/*
 * This file is part of anycook. The new internet cookbook
 * Copyright (C) 2014 Jan Graßegger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see [http://www.gnu.org/licenses/].
 */

package de.anycook.api;

import de.anycook.api.util.MediaType;
import de.anycook.api.views.PublicView;
import de.anycook.db.mysql.DBRecipe;
import de.anycook.db.mysql.DBSaveRecipe;
import de.anycook.db.mysql.DBTag;
import de.anycook.db.mysql.DBUser;
import de.anycook.newrecipe.NewRecipe;
import de.anycook.news.life.Lifes;
import de.anycook.notifications.Notification;
import de.anycook.recipe.Recipe;
import de.anycook.recipe.Recipes;
import de.anycook.recipe.ingredient.Ingredient;
import de.anycook.recipe.ingredient.Ingredients;
import de.anycook.recipe.step.Step;
import de.anycook.recipe.step.Steps;
import de.anycook.recipe.tag.Tag;
import de.anycook.session.Session;
import de.anycook.sitemap.SiteMapGenerator;
import de.anycook.user.User;
import de.anycook.utils.enumerations.ImageType;
import de.anycook.utils.enumerations.NotificationType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;


@Path("/recipe")
@Produces(MediaType.APPLICATION_JSON)
public class RecipeApi {

    private final Logger logger = LogManager.getLogger(getClass());

    @Context
    private Session session;

    @GET
    public Response getAll(@HeaderParam(HttpHeaders.IF_MODIFIED_SINCE) final Date date,
                           @QueryParam("userId") final Integer userId,
                           @QueryParam("startsWith") final String prefix,
                           @QueryParam("detailed") final boolean detailed) {
        try {
            final int loginId =
                    session.checkLoginWithoutException() ? session.getUser().getId() : -1;
            final Annotation[] annotations = detailed ? new Annotation[]{PublicView.Factory.get()}
                                                      : new Annotation[]{};

            List<Recipe> recipes;
            if (date != null) {
                recipes = Recipes.getAll(loginId, date);
                if (recipes.size() == 0) {
                    throw new WebApplicationException(Response.Status.NOT_MODIFIED);
                }
            } else {
                recipes = userId != null ?
                          Recipes.getRecipesFromUser(userId, loginId) : Recipes.getAll(loginId);
            }

            if (prefix != null) {
                recipes = recipes.parallelStream()
                        .filter(r -> r.getName().toLowerCase().startsWith(prefix.toLowerCase()))
                        .collect(Collectors.toList());
            }

            return Response.ok().entity(new GenericEntity<List<Recipe>>(recipes) {}, annotations)
                    .lastModified(Recipes.getLastModified())
                    .build();
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Number of recipes
     */
    @GET
    @Path("number")
    public Integer getNum() {
        try {
            return Recipes.getTotal();
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * returns the recipe of the day
     */
    @GET
    @Path("oftheday")
    public Recipe getRecipeOfTheDay() {
        try {
            return Recipes.getRecipeOfTheDay();
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBRecipe.RecipeNotFoundException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @GET
    @Path("tag")
    public List<Tag> getRecipeTags(@QueryParam("active") final Boolean active) {
        try {
            if (active != null) {
                return Tag.getRecipeTags(active);
            }
            return Tag.getRecipeTags();
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }


    @GET
    @Path("{recipeName}")
    @PublicView
    public Recipe getRecipe(@PathParam("recipeName") final String recipeName) {
        try {
            final int loginId = session.checkLoginWithoutException() ? session.getUser().getId()
                                                                     : -1;
            Recipes.increaseViewCount(recipeName);
            return Recipe.init(recipeName, loginId);
        } catch (final SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (final DBRecipe.RecipeNotFoundException e) {
            logger.warn(e, e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @GET
    @Path("{recipeName}/authors")
    public List<User> getAuthors(@PathParam("recipeName") final String recipeName) {
        try {
            return Recipes.getAuthors(recipeName);
        } catch (final SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("{recipeName}/ingredients")
    public Response getRecipeIngredients(@Context final Request request,
                                         @PathParam("recipeName") final String recipeName) {

        try {
            final long lastChange = Recipe.init(recipeName).getLastChange();
            final Date lastChangeDate = new Date(lastChange);
            final Response.ResponseBuilder responseBuilder =
                    request.evaluatePreconditions(lastChangeDate);

            if (responseBuilder != null) {
                throw new WebApplicationException(responseBuilder.build());
            }

            return Response
                    .ok(new GenericEntity<List<Ingredient>>(
                            Ingredients.loadByRecipe(recipeName)) {})
                    .lastModified(lastChangeDate).build();
        } catch (final SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (final DBRecipe.RecipeNotFoundException e) {
            logger.warn(e, e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @GET
    @Path("{recipeName}/tags")
    public List<Tag> getRecipeTags(@PathParam("recipeName") final String recipeName) {
        try {
            return Tag.loadTagsFromRecipe(recipeName);
        } catch (final SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path("{recipeName}/tags")
    @Consumes(MediaType.APPLICATION_JSON)
    public void suggestTags(@PathParam("recipeName") final String recipeName,
                            final List<Tag> tags) {
        final int userId = session.getUser().getId();

        try (final DBSaveRecipe dbSaveRecipe = new DBSaveRecipe()) {
            for (final Tag tag : tags) {
                dbSaveRecipe.suggestTag(recipeName, tag.getName(), userId);
            }

            //send notification to admin
            final Map<String, String> data = new HashMap<>(6);
            data.put("userName", session.getUser().getName());
            data.put("recipeName", recipeName);
            data.put("numTags", Integer.toString(tags.size()));
            Notification.sendAdminNotification(NotificationType.ADMIN_SUGGESTED_TAGS, data);
        } catch (final SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @PUT
    @Path("{recipeName}/tags/{tagName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateRecipeTag(@PathParam("recipeName") final String recipeName,
                                @PathParam("tagName") final String tagName,
                                final Tag tag) {
        session.checkAdminLogin();
        try {
            final Tag oldTag = Tag.getRecipeTag(recipeName, tagName);
            if (oldTag.getActive() != tag.getActive() && tag.getActive()) {
                Tag.activateRecipeTag(recipeName, tagName);

                final Map<String, String> data = new HashMap<>(3);
                data.put("tagName", tag.getName());
                data.put("recipeName", recipeName);
                Notification.sendNotification(oldTag.getSuggester().getId(),
                                              NotificationType.TAG_ACCEPTED, data);
                SiteMapGenerator.generateTagSitemap();
            }
        } catch (final DBUser.UserNotFoundException | SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (final DBTag.TagNotFoundException e) {
            logger.warn(e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @DELETE
    @Path("{recipeName}/tags/{tagName}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteRecipeTag(@PathParam("recipeName") final String recipeName,
                                @PathParam("tagName") final String tagName) {
        session.checkAdminLogin();
        try {
            final Tag oldTag = Tag.getRecipeTag(recipeName, tagName);
            Tag.deleteRecipeTag(recipeName, tagName);

            final Map<String, String> data = new HashMap<>(4);
            data.put("tagName", tagName);
            data.put("recipeName", recipeName);
            Notification.sendNotification(oldTag.getSuggester().getId(),
                                          NotificationType.TAG_DENIED,
                                          data);
        } catch (final DBUser.UserNotFoundException | SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (final DBTag.TagNotFoundException e) {
            logger.warn(e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @GET
    @Path("{recipeName}/steps")
    public List<Step> getRecipeSteps(@PathParam("recipeName") final String recipeName) {
        try {
            return Steps.loadRecipeSteps(recipeName);
        } catch (final SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }


    //version
    @GET
    @PublicView
    @Path("{recipeName}/version")
    public List<Recipe> getAllVersion(@PathParam("recipeName") final String recipeName) {
        try {
            final int loginId = session.checkLoginWithoutException() ? session.getUser().getId()
                                                                     : -1;
            return Recipes.getAllVersions(recipeName, loginId);
        } catch (final SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("{recipeName}/version/{versionId}")
    @PublicView
    public Recipe getVersion(@PathParam("recipeName") final String recipeName,
                             @PathParam("versionId") final int versionId) {
        try {
            final int loginId = session.checkLoginWithoutException() ? session.getUser().getId()
                                                                     : -1;
            return Recipe.init(recipeName, versionId, loginId);
        } catch (final SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (final DBRecipe.RecipeNotFoundException e) {
            logger.warn(e, e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @PUT
    @Path("{recipeName}/version/{versionId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateVersion(@PathParam("recipeName") final String recipeName,
                              @PathParam("versionId") final int versionId,
                              final Recipe newVersion) {

        try {
            session.checkAdminLogin();
            final int userId = session.getUser().getId();
            final Recipe oldVersion = Recipe.init(recipeName, versionId, userId);
            if (oldVersion.isActive() != newVersion.isActive()) {
                Recipes.setActiveId(recipeName, newVersion.isActive() ? newVersion.getId() : -1);

                //version was activated
                if (newVersion.isActive()) {
                    final Map<String, String> data = new HashMap<>();
                    data.put("recipeName", recipeName);

                    if (oldVersion.getActiveAuthor() >= 0) {
                        Notification.sendNotification(oldVersion.getActiveAuthor(),
                                                      NotificationType.RECIPE_ACTIVATION, data);
                        Lifes.addLife(Lifes.CaseType.ACTIVATED, newVersion.getActiveAuthor(),
                                      recipeName);
                    }
                    logger.debug("activated version #{} of {}", versionId, recipeName);
                    Recipes.setLastChange(recipeName);
                    SiteMapGenerator.generateRecipeSiteMap();
                }


            }
        } catch (final SQLException | DBUser.UserNotFoundException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (final DBRecipe.RecipeNotFoundException e) {
            logger.warn(e, e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @GET
    @Path("{recipeName}/version/{versionId}/ingredients")
    public List<Ingredient> getVersionIngredients(@PathParam("recipeName") final String recipeName,
                                                  @PathParam("versionId") final int versionId) {
        try {
            return Ingredients.loadByRecipe(recipeName, versionId);
        } catch (final SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("{recipeName}/version/{versionId}/steps")
    public List<Step> getVersionSteps(@PathParam("recipeName") final String recipeName,
                                      @PathParam("versionId") final int versionId) {
        try {
            return Steps.loadRecipeSteps(recipeName, versionId);
        } catch (final SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("{recipeName}/image")
    @Produces("image/png")
    public Response getImage(@PathParam("recipeName") final String recipeName,
                             @DefaultValue("small") @QueryParam("type") final String typeString) {
        final ImageType type = ImageType.valueOf(typeString.toUpperCase());
        try {
            return Response.temporaryRedirect(Recipes.getRecipeImage(recipeName, type)).build();
        } catch (final URISyntaxException e) {
            logger.error(e, e);
            throw new WebApplicationException(400);
        } catch (final SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("{recipeName}/schmeckt")
    public Boolean checkSchmeckt(@PathParam("recipeName") final String recipeName) {
        session.checkLogin();
        try {
            return session.checkSchmeckt(recipeName);
        } catch (final SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @PUT
    @Path("{recipeName}/schmeckt")
    public void schmeckt(@PathParam("recipeName") final String recipeName) {
        try {
            session.checkLogin();
            final boolean schmeckt = session.checkSchmeckt(recipeName);
            if (!schmeckt) {
                session.makeSchmeckt(recipeName);
            }
        } catch (final SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }


    }

    @DELETE
    @Path("{recipeName}/schmeckt")
    public void schmecktNicht(@PathParam("recipeName") final String recipeName) {
        try {
            session.checkLogin();
            final boolean schmeckt = session.checkSchmeckt(recipeName);
            if (schmeckt) {
                session.removeSchmeckt(recipeName);
            }
        } catch (final SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }


    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void saveRecipe(final NewRecipe newRecipe) {
        logger.info("want to save recipe");
        if (newRecipe == null) {
            throw new WebApplicationException(400);
        }

        try {
            final int newId;
            if (session.checkLoginWithoutException()) {
                User user = session.getUser();
                newId = newRecipe.save(user.getId());

            } else {
                logger.debug("user is not authentificated");
                newId = newRecipe.save();
            }
            final Map<String, String> data = new HashMap<>();
            data.put("recipeName", newRecipe.name);
            data.put("versionId", Integer.toString(newId));
            Notification.sendAdminNotification(NotificationType.ADMIN_NEW_VERSION, data);
        } catch (final SQLException | IOException | ParseException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (final NewRecipe.InvalidRecipeException e) {
            logger.warn(e, e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

    }
}
