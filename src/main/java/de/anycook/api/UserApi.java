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

import com.fasterxml.jackson.annotation.JsonView;
import de.anycook.api.util.MediaType;
import de.anycook.conf.Configuration;
import de.anycook.db.mysql.DBUser;
import de.anycook.discussion.Discussion;
import de.anycook.recipe.Recipe;
import de.anycook.recommendation.Recommendation;
import de.anycook.session.Session;
import de.anycook.social.facebook.FacebookHandler;
import de.anycook.user.User;
import de.anycook.user.views.Views;
import de.anycook.utils.enumerations.ImageType;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;


@Path("/user")
public class UserApi {
	private final Logger logger = Logger.getLogger(getClass());
	
	
	@SuppressWarnings("unchecked")
	@GET
    @JsonView(Views.PublicUserView.class)
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> getUsers(){
        try {
            return User.getAll();
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	@POST
	public void newUser(@FormParam("mail") String mail,
			@FormParam("username") String username,
			@FormParam("password") String password){
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
	public boolean checkMail(@QueryParam("mail") String mail){
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
	public boolean checkUsername(@QueryParam("username") String username){
        try {
            return User.checkUsername(username);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	/**
	 * returns the number of users
	 * @return
	 */
	@GET
	@Path("number")
	@Produces(MediaType.APPLICATION_JSON)
	public int getNum(){
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
    @JsonView(de.anycook.recipe.Views.ResultRecipeView.class)
	public List<Recipe> getRecommendations(@Context HttpServletRequest request){
		Session session = Session.init(request.getSession());
		session.checkLogin();
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
    @JsonView(Views.PublicUserView.class)
	@Produces(MediaType.APPLICATION_JSON)
	public User getUser(@PathParam("userId") int userId){
        try {
            return User.init(userId);
        } catch (IOException | SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBUser.UserNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
	
	@PUT
	@Path("{userId}/follow")
	public Response follow(@PathParam("userId") int userId,
			@Context HttpHeaders hh,
			@Context HttpServletRequest request){
		Session session = Session.init(request.getSession());

        try {
            session.checkLogin(hh.getCookies());
            User user = session.getUser();

            user.follow(userId);
        } catch (IOException | SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return Response.ok().build();
	}
	
	@DELETE
	@Path("{userId}/follow")
	public Response unfollow(@PathParam("userId") int userId,
			@Context HttpHeaders hh,
			@Context HttpServletRequest request){
		Session session = Session.init(request.getSession());

        try {
            session.checkLogin(hh.getCookies());
            User user = session.getUser();

            user.unFollow(userId);
        } catch (IOException | SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return Response.ok().build();
	}
	
	@GET
	@Path("{userId}/image")
	@Produces("image/png")
	public Response getImage(@PathParam("userId") int userId,
			@DefaultValue("small") @QueryParam("type") String typeString){
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
            logger.warn(e,e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
	
	@GET
	@Path("{userId}/schmeckt")
    @JsonView(de.anycook.recipe.Views.ResultRecipeView.class)
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public List<Recipe> schmeckt(@PathParam("userId") int userId){
        try {
            return Recipe.getTastingRecipesForUser(userId);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
	}
	
	@GET
	@Path("{userId}/discussionnum")
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public int getDiscussionNum(@PathParam("userId") int userId){
        try {
            return Discussion.getDiscussionNumforUser(userId);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
	}

    @POST
    @Path("facebook")
    @Produces(MediaType.TEXT_HTML)
    public Response registerFacebookUser(@FormParam("signed_request") String requestString){
        if(requestString == null) throw new WebApplicationException(Response.Status.BAD_REQUEST);
        FacebookHandler.FacebookRequest request;

        try {
            request = FacebookHandler.decode(requestString);
        } catch (IOException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        String mail = request.registration.get("email");
        String name = request.registration.get("name");

        String responseText;

        try {
            if(User.checkMail(mail)||User.checkUsername(name)){
                logger.info("user already exists");
                responseText="exists";
            }
            else{
                if(User.newFacebookUser(mail, name, Long.parseLong(request.user_id))){
                    responseText="success";
                }
                else {
                    responseText="error";
                    logger.info("error creating new fb user");
                }

            }
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        String uri = String.format("http://%s/#fbregistration?response=%s", Configuration.getPropertyRedirectDomain(),
                responseText);

        String content = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n<HTML><HEAD><META HTTP-EQUIV=\"REFRESH\" content=\"0; url="+uri+"\"></HEAD>\n<BODY></BODY></HTML>";
        return Response.ok(content).build();
    }
}
