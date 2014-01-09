package de.anycook.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.annotation.JsonView;
import de.anycook.api.util.MediaType;
import de.anycook.db.mysql.DBUser;
import de.anycook.views.Views;
import org.apache.log4j.Logger;

import de.anycook.utils.enumerations.ImageType;
import de.anycook.discussion.Discussion;
import de.anycook.recipe.Recipe;
import de.anycook.recommendation.Recommendation;
import de.anycook.session.Session;
import de.anycook.user.User;


@Path("/user")
public class UserApi {
	private final Logger logger = Logger.getLogger(getClass());
	
	
	@SuppressWarnings("unchecked")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Integer> getUsers(){
        try {
            return User.getAll();
        } catch (SQLException e) {
            logger.error(e);
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
            logger.error(e);
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
            logger.error(e);
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
            logger.error(e);
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
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

	@GET
	@Path("recommendations")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getRecommendations(@Context HttpServletRequest request){
		Session session = Session.init(request.getSession());
		session.checkLogin();
		int userId = session.getUser().getId();
        try {
            return Recommendation.recommend(userId);
        } catch (SQLException e) {
            logger.error(e);
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
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBUser.UserNotFoundException e) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
	
	@PUT
	@Path("{userId}/follow")
	public Response follow(@PathParam("userId") int userid,
			@Context HttpHeaders hh,
			@Context HttpServletRequest request){
		Session session = Session.init(request.getSession());

        try {
            session.checkLogin(hh.getCookies());
            User user = session.getUser();

            user.follow(userid);
        } catch (IOException | SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return Response.ok().build();
	}
	
	@DELETE
	@Path("{userId}/follow")
	public Response unfollow(@PathParam("userId") int userid,
			@Context HttpHeaders hh,
			@Context HttpServletRequest request){
		Session session = Session.init(request.getSession());

        try {
            session.checkLogin(hh.getCookies());
            User user = session.getUser();

            user.unFollow(userid);
        } catch (IOException | SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return Response.ok().build();
	}
	
	@GET
	@Path("{userId}/image")
	@Produces("image/png")
	public Response getImage(@PathParam("userId") int userid,
			@DefaultValue("small") @QueryParam("type") String typeString){
		ImageType type = ImageType.valueOf(typeString.toUpperCase());
		
		try {
			URI uri = new URI(User.getUserImage(userid, type));
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
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public List<String> schmeckt(@PathParam("userId") int userId){
        try {
            return Recipe.getSchmecktRecipesFromUser(userId);
        } catch (SQLException e) {
            logger.error(e);
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
}