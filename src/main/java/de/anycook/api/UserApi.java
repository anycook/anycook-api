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
import de.anycook.api.views.PrivateView;
import de.anycook.api.views.PublicView;
import de.anycook.db.mysql.DBUser;
import de.anycook.discussion.Discussion;
import de.anycook.recipe.Recipe;
import de.anycook.recipe.Recipes;
import de.anycook.recommendation.Recommendation;
import de.anycook.session.Session;
import de.anycook.user.User;
import de.anycook.api.utils.enumerations.ImageType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

@Path("/user")
public class UserApi {

    private final Logger logger = LogManager.getLogger(getClass());
    @Context
    Session session;

    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getUsers(@QueryParam("detailed") boolean isDetailed) {
        try {
            Annotation[] annotations = isDetailed ?
                                       new Annotation[]{PublicView.Factory.get()} :
                                       new Annotation[]{};
            return Response.ok().entity(new GenericEntity<List<User>>(User.getAll()) {
            }, annotations).build();
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    public void newUser(@FormParam("mail") String mail,
                        @FormParam("username") String username,
                        @FormParam("password") String password) {
        try {
            User.newUser(mail, password, username);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("mail")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean checkMail(@QueryParam("mail") String mail) {
        try {
            return User.checkMail(mail);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("name")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean checkUsername(@QueryParam("username") String username) {
        try {
            return User.checkUsername(username);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * returns the number of users
     */
    @GET
    @Path("number")
    @Produces(MediaType.APPLICATION_JSON)
    public int getNum() {
        try {
            return User.getTotal();
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("recommendations")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Recipe> getRecommendations() {
        int userId = session.getUser().getId();
        try {
            return Recommendation.recommend(userId, 20);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("{userId}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getUser(@PathParam("userId") int userId,
                            @QueryParam("adminDetails") boolean isAdminDetails) {
        try {
            if (isAdminDetails) {
                logger.debug("showing admin details");
                session.checkAdminLogin();
                return Response.ok()
                        .entity(User.init(userId), new Annotation[]{PrivateView.Factory.get()})
                        .build();
            }

            return Response.ok()
                    .entity(User.init(userId), new Annotation[]{PublicView.Factory.get()}).build();
        } catch (IOException | SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBUser.UserNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @PUT
    @Path("{userId}")
    @Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    public void updateUser(@PathParam("userId") int userId, User user) {
        try {
            session.checkAdminLogin();
            try (DBUser dbUser = new DBUser()) {
                User old = dbUser.getUser(userId);
                if (old.getLevel() != user.getLevel()) {
                    dbUser.setUserLevel(userId, user.getLevel());
                    logger.info(String.format("changed user level for user %d to %d", userId,
                                              user.getLevel()));
                }
            }
        } catch (SQLException | IOException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBUser.UserNotFoundException e) {
            logger.warn(e, e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
    }

    @PUT
    @Path("{userId}/follow")
    public Response follow(@PathParam("userId") int userId) {
        try {
            User user = session.getUser();

            user.follow(userId);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return Response.ok().build();
    }


    @DELETE
    @Path("{userId}/follow")
    public Response unfollow(@PathParam("userId") int userId) {
        try {
            User user = session.getUser();
            user.unFollow(userId);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return Response.ok().build();
    }

    @GET
    @Path("{userId}/image")
    @Produces("image/png")
    public Response getImage(@PathParam("userId") int userId,
                             @DefaultValue("small") @QueryParam("type") String typeString) {
        ImageType type = ImageType.valueOf(typeString.toUpperCase());

        try {
            URI uri = new URI(User.getUserImage(userId, type));
            return Response.temporaryRedirect(uri).build();
        } catch (URISyntaxException e) {
            logger.warn(e);
            throw new WebApplicationException(400);
        } catch (IOException | SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBUser.UserNotFoundException e) {
            logger.warn(e, e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @GET
    @Path("{userId}/schmeckt")
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    public List<Recipe> schmeckt(@PathParam("userId") int userId) {
        try {
            int loginId = session.checkLoginWithoutException() ? session.getUser().getId() : -1;
            return Recipes.getTastingRecipesForUser(userId, loginId);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("{userId}/discussionnum")
    @Produces(MediaType.APPLICATION_JSON)
    public int getDiscussionNum(@PathParam("userId") int userId) {
        try {
            return Discussion.getDiscussionNumForUser(userId);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }


    @POST
    @Path("{userId}/resendActivationId")
    @Produces(MediaType.APPLICATION_JSON)
    public void resendActivationRequest(@PathParam("userId") int userId) {
        session.checkAdminLogin();
        try {
            User.resendActivationId(userId);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBUser.UserNotFoundException e) {
            logger.warn(e, e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
}
