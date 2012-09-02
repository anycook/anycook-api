package de.anycook.graph;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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

import de.anycook.misc.JsonpBuilder;
import de.anycook.misc.enumerations.ImageType;
import de.anycook.session.Session;
import de.anycook.user.User;


@Path("/user")
public class UserGraph {
	private final Logger logger = Logger.getLogger(getClass());
	
	
	@SuppressWarnings("unchecked")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsers(@QueryParam("appid") int appid,
			@QueryParam("callback") String callback){
		JSONObject json = new JSONObject();
		List<User> users = User.getAll();
		json.put("users", users);
		json.put("total", users.size());
		
		return Response.ok(JsonpBuilder.build(callback, json)).build();
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
		return JsonpBuilder.buildResponse(callback, User.getTotal());
	}

	@GET
	@Path("recommendations")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRecommendations(@Context HttpServletRequest request, 
			@QueryParam("callback") String callback){
		Session session = Session.init(request.getSession());
		session.checkLogin();
		return JsonpBuilder.buildResponse(callback, session.getUser().getRecommendations());
	}
	
	@GET
	@Path("{userid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUser(@PathParam("userid") int userid,
			@QueryParam("callback") String callback){
		User user = User.init(userid);
		return Response.ok(JsonpBuilder.build(callback, user.getProfileInfoJSONWithRecipes())).build();
	}
	
	@POST
	@Path("{userid}/follow")
	@Produces(MediaType.APPLICATION_JSON)
	public void follow(@PathParam("userid") int userid,
			@Context HttpHeaders hh,
			@Context HttpServletRequest request){
		Session session = Session.init(request.getSession());
		session.checkLogin(hh.getCookies());
		User user = session.getUser();
		user.follow(userid);
	}
	
	@POST
	@Path("{userid}/unfollow")
	@Produces(MediaType.APPLICATION_JSON)
	public void unfollow(@PathParam("userid") int userid,
			@Context HttpHeaders hh,
			@Context HttpServletRequest request){
		Session session = Session.init(request.getSession());
		session.checkLogin(hh.getCookies());
		User user = session.getUser();
		user.unfollow(userid);
	}
	
	@GET
	@Path("{userid}/image")
	@Produces("image/png")
	public Response getImage(@PathParam("userid") int userid,
			@DefaultValue("small") @QueryParam("type") String typeString){
		ImageType type = ImageType.valueOf(typeString.toUpperCase());
		
		try {
			URI uri = new URI("http://images.anycook.de"+User.getUserImage(userid, type));
			return Response.temporaryRedirect(uri).build();
		} catch (URISyntaxException e) {
			logger.warn(e);
			throw new WebApplicationException(400);
		}
	}
}
