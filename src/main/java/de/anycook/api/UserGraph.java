package de.anycook.api;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import de.anycook.utils.JsonpBuilder;
import de.anycook.utils.enumerations.ImageType;
import de.anycook.discussion.Discussion;
import de.anycook.recipe.Recipe;
import de.anycook.recommendation.Recommendation;
import de.anycook.session.Session;
import de.anycook.user.User;


@Path("/user")
public class UserGraph {
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
	
	@PUT
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
	public Response checkMail(@QueryParam("mail") String mail,
			@QueryParam("callback") String callback){
        try {
            return JsonpBuilder.buildResponse(callback, User.checkMail(mail));
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	@GET
	@Path("name")
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkUsername(@QueryParam("username") String username,
			@QueryParam("callback") String callback){
        try {
            return JsonpBuilder.buildResponse(callback, User.checkUsername(username));
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	/**
	 * returns the number of users
	 * @param callback
	 * @return
	 */
	@GET
	@Path("number")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNum(@QueryParam("callback") String callback){
        try {
            return JsonpBuilder.buildResponse(callback, User.getTotal());
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

	@GET
	@Path("recommendations")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRecommendations(@Context HttpServletRequest request, 
			@QueryParam("callback") String callback){
		Session session = Session.init(request.getSession());
		session.checkLogin();
		int userid = session.getUser().getId();
        try {
            return JsonpBuilder.buildResponse(callback, Recommendation.recommend(userid));
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	@GET
	@Path("{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam("userId") int userid,
			@QueryParam("callback") String callback){
        User user;
        try {
            user = User.init(userid);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        String jsonString = user.getProfileInfoJSON().toJSONString();
		return JsonpBuilder.buildResponse(callback, jsonString);
	}
	
	@PUT
	@Path("{userId}/follow")
	public Response follow(@PathParam("userId") int userid,
			@Context HttpHeaders hh,
			@Context HttpServletRequest request){
		Session session = Session.init(request.getSession());
		session.checkLogin(hh.getCookies());
		User user = session.getUser();
        try {
            user.follow(userid);
        } catch (SQLException e) {
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
		session.checkLogin(hh.getCookies());
		User user = session.getUser();
        try {
            user.unfollow(userid);
        } catch (SQLException e) {
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
			URI uri = new URI("http://images.anycook.de"+User.getUserImage(userid, type));
			return Response.temporaryRedirect(uri).build();
		} catch (URISyntaxException e) {
			logger.warn(e);
			throw new WebApplicationException(400);
		} catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	@GET
	@Path("{userId}/schmeckt")
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Response schmeckt(@PathParam("userId") int userid,
			@QueryParam("callback") String callback){
        List<String> schmeckt;
        try {
            schmeckt = Recipe.getSchmecktRecipesfromUser(userid);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return JsonpBuilder.buildResponse(callback, schmeckt);
	}
	
	@GET
	@Path("{userId}/discussionnum")
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Response getDiscussionNum(@PathParam("userId") int userid,
			@QueryParam("callback") String callback){
        int discNum;
        try {
            discNum = Discussion.getDiscussionNumforUser(userid);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
        return JsonpBuilder.buildResponse(callback, discNum);
	}
}
