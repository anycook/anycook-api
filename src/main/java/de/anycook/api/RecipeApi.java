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
import de.anycook.db.mysql.DBUser;
import de.anycook.newrecipe.NewRecipe;
import de.anycook.news.life.Lifes;
import de.anycook.notifications.Notification;
import de.anycook.recipe.Recipe;
import de.anycook.recipe.Recipes;
import de.anycook.recipe.ingredient.Ingredient;
import de.anycook.recipe.step.Step;
import de.anycook.recipe.tag.Tag;
import de.anycook.session.Session;
import de.anycook.user.User;
import de.anycook.utils.enumerations.ImageType;
import de.anycook.utils.enumerations.NotificationType;
import org.apache.log4j.Logger;
import org.apache.lucene.queryparser.classic.ParseException;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Path("/recipe")
@Produces(MediaType.APPLICATION_JSON)
public class RecipeApi {
	private final Logger logger = Logger.getLogger(getClass());

    @Context
    private Session session;

	@GET
	public Response getAll(@QueryParam("userId") Integer userId, @QueryParam("detailed") final boolean detailed){
        try{
            Annotation[] annotations = detailed ?
                    new Annotation[]{PublicView.Factory.get()} : new Annotation[]{};

            if(userId != null)
                return Response.ok().entity(new GenericEntity<List<Recipe>>(Recipes.getRecipesFromUser(userId)){},
                        annotations).build();
            return Response.ok().entity(new GenericEntity<List<Recipe>>(Recipes.getAll()){}, annotations).build();
        } catch (Exception e){
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
	}
	
	/**
	 * Number of recipes
	 */
	@GET
	@Path("number")
	public Integer getNum(){
        try {
            return Recipes.getTotal();
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	/**
	 * returns the recipe of the day
	 * @return
	 */
	@GET
	@Path("oftheday")
	public Recipe getRecipeOfTheDay(){
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
	@Path("{recipeName}")
    @PublicView
	public Recipe getRecipe(@PathParam("recipeName") String recipeName){
        try {
            return Recipe.init(recipeName);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBRecipe.RecipeNotFoundException e) {
            logger.warn(e, e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @GET
    @Path("{recipeName}/authors")
    public List<User> getAuthors(@PathParam("recipeName") String recipeName){
        try {
            return Recipes.getAuthors(recipeName);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	@GET
	@Path("{recipeName}/ingredients")
	public List<Ingredient> getRecipeIngredients(@PathParam("recipeName") String recipeName){
        try {
            return Ingredient.loadByRecipe(recipeName);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	@GET
	@Path("{recipeName}/tags")
	public List<Tag> getRecipeTags(@PathParam("recipeName") String recipeName){
        try {
            return Tag.loadTagsFromRecipe(recipeName);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path("{recipeName}/tags")
    @Consumes(MediaType.APPLICATION_JSON)
    public void suggestTags(@PathParam("recipeName") String recipeName,
                            List<String> tags) {
        int userId = session.getUser().getId();

        try (DBSaveRecipe dbSaveRecipe = new DBSaveRecipe()){
            for(String tag : tags)
                dbSaveRecipe.suggestTag(recipeName, tag, userId);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	@GET
	@Path("{recipeName}/steps")
	public List<Step> getRecipeSteps(@PathParam("recipeName") String recipeName){
        try {
            return Step.loadRecipeSteps(recipeName);
        } catch (SQLException e) {
            logger.error(e ,e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	

    //version
    @GET
    @PublicView
    @Path("{recipeName}/version")
    public List<Recipe> getAllVersion(@PathParam("recipeName") String recipeName){
        try {
            return Recipes.getAllVersions(recipeName);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

	@GET
	@Path("{recipeName}/version/{versionId}")
    @PublicView
	public Recipe getVersion(@PathParam("recipeName") String recipeName,
			@PathParam("versionId") int versionId){
        try {
            return Recipe.init(recipeName, versionId);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBRecipe.RecipeNotFoundException e) {
            logger.warn(e, e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @PUT
    @Path("{recipeName}/version/{versionId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateVersion(@PathParam("recipeName") String recipeName,
                              @PathParam("versionId") int versionId,
                              Recipe newVersion){

        try {
            session.checkAdminLogin();
            Recipe oldVersion = Recipe.init(recipeName, versionId);
            if(oldVersion.isActive() != newVersion.isActive()){
                Recipes.setActiveId(recipeName, newVersion.isActive() ? newVersion.getId() : -1);

                //version was activated
                if(newVersion.isActive()){
                    Map<String, String> data = new HashMap<>();
                    data.put("recipeName", recipeName);

                    if(oldVersion.getActiveAuthor() >= 0) {
                        Notification.sendNotification(oldVersion.getActiveAuthor(),
                                NotificationType.RECIPE_ACTIVATION, data);
                        Lifes.addLife(Lifes.Case.NEW_VERSION, newVersion.getActiveAuthor(), recipeName);
                    }
                }
            }
        } catch (SQLException | DBUser.UserNotFoundException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBRecipe.RecipeNotFoundException e) {
            logger.warn(e, e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @GET
    @Path("{recipeName}/version/{versionId}/ingredients")
    public List<Ingredient> getVersionIngredients(@PathParam("recipeName") String recipeName,
                               @PathParam("versionId") int versionId){
        try {
            return Ingredient.loadByRecipe(recipeName, versionId);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("{recipeName}/version/{versionId}/steps")
    public List<Step> getVersionSteps(@PathParam("recipeName") String recipeName,
                                    @PathParam("versionId") int versionId){
        try {
            return Step.loadRecipeSteps(recipeName, versionId);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	@GET
	@Path("{recipeName}/image")
	@Produces("image/png")
	public Response getImage(@PathParam("recipeName") String recipeName,
			@DefaultValue("small") @QueryParam("type") String typeString){
		ImageType type = ImageType.valueOf(typeString.toUpperCase());
		try {
			return Response.temporaryRedirect(Recipes.getRecipeImage(recipeName, type)).build();
		} catch (URISyntaxException e) {
			logger.error(e, e);
			throw new WebApplicationException(400);
		} catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	@GET
	@Path("{recipeName}/schmeckt")
	public Boolean checkSchmeckt(@PathParam("recipeName") String recipeName){
		session.checkLogin();
        try {
            return session.checkSchmeckt(recipeName);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	@PUT
	@Path("{recipeName}/schmeckt")
	public void schmeckt(@PathParam("recipeName") String recipeName){
		
        try {
            session.checkLogin();
            boolean schmeckt = session.checkSchmeckt(recipeName);
            if(!schmeckt)
                session.makeSchmeckt(recipeName);
        } catch (SQLException e){
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

		
	}
	
	@DELETE
	@Path("{recipeName}/schmeckt")
	public void schmecktNicht(@PathParam("recipeName") String recipeName){
        try {
            session.checkLogin();
            boolean schmeckt = session.checkSchmeckt(recipeName);
            if(schmeckt)
                session.removeSchmeckt(recipeName);
        } catch (SQLException e){
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }


	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveRecipe(NewRecipe newRecipe){
		logger.info("want to save recipe");
		if(newRecipe == null)
			throw new WebApplicationException(400);


        try {
            int newId;
            if(session.checkLoginWithoutException()) {
                User user = session.getUser();
                newId = newRecipe.save(user.getId());
            }
            else {
                logger.debug("user is not authentificated");
                newId = newRecipe.save();
            }
            Map<String, String> data = new HashMap<>();
            data.put("recipeName", newRecipe.name);
            data.put("versionId", Integer.toString(newId));
            Notification.sendAdminNotification(NotificationType.ADMIN_NEW_VERSION, data);
        } catch (SQLException | IOException | ParseException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (NewRecipe.InvalidRecipeException e) {
            logger.warn(e, e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

    }
}
