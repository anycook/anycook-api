package de.anycook.graph;

import java.io.File;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;

import anycook.misc.JsonpBuilder;
import anycook.misc.enumerations.ImageType;
import anycook.user.User;

@Path("/user")
public class UserGraph {
	
	@SuppressWarnings("unchecked")
	@GET
	public Response getUsers(@QueryParam("appid") int appid,
			@QueryParam("callback") String callback){
		JSONObject json = new JSONObject();
		List<User> users = User.getAll();
		json.put("users", users);
		json.put("total", users.size());
		
		return Response.ok(JsonpBuilder.build(callback, json)).build();
	}
	
	@GET
	@Path("{userid}")
	public Response getUser(@PathParam("userid") int userid,
			@QueryParam("callback") String callback){
		User user = User.init(userid);
		return Response.ok(JsonpBuilder.build(callback, user.getProfileInfoJSONWithRecipes())).build();
	}
	
	@GET
	@Path("{userid}/image")
	@Produces("image/png")
	public Response getImage(@PathParam("userid") int userid,
			@DefaultValue("small") @QueryParam("type") String typeString){
		ImageType type = ImageType.valueOf(typeString.toUpperCase());
		String imagePath = "/var/www/sites/anycook.de/htdocs"+User.getUserImage(userid, type);
		return Response.ok(new File(imagePath)).build();
	}
}
