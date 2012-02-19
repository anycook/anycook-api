package de.anycook.graph;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import anycook.db.couchdb.CouchDB;
import anycook.misc.JsonpBuilder;
import anycook.misc.enumerations.ImageType;
import anycook.newrecipe.NewRecipe;
import anycook.newrecipe.NewRecipe.NewRecipeException;
import anycook.recipe.Recipe;
import anycook.session.Session;
import anycook.user.User;

@Path("/recipe")
public class RecipeGraph {
	Logger logger = Logger.getLogger(getClass());

	@SuppressWarnings("unchecked")
	@GET
	public Response getAll(@QueryParam("callback") String callback){
		JSONObject json = new JSONObject();
		List<String> recipes = Recipe.getAll();
		json.put("names", recipes);
		json.put("total", recipes.size());
		return Response.ok(JsonpBuilder.build(callback, json)).build();
	}
	
	@GET
	@Path("{recipename}")
	public Response getRecipe(@PathParam("recipename") String recipeName, 
			@QueryParam("callback") String callback){
		
		Recipe recipe;
		if(recipeName.equals("random"))
			recipe = Recipe.getRandomRecipe();
		else
			recipe = Recipe.init(recipeName);
		
		return Response.ok(JsonpBuilder.build(callback, recipe)).build();
	}
	
	@GET
	@Path("{recipename}/image")
	@Produces("image/png")
	public Response getImage(@PathParam("recipename") String recipeName,
			@DefaultValue("small") @QueryParam("type") String typeString){
		ImageType type = ImageType.valueOf(typeString.toUpperCase());
		return Response.ok(Recipe.getRecipeImage(recipeName, type)).build();
	}
	
	@POST
	@Path("{recipename}")
	public Response saveRecipe(@Context HttpHeaders hh,
			@PathParam("recipename") String recipeName,
			@Context HttpServletRequest request,
			@FormParam("recipe") String recipeData){
		logger.info("want to save recipe");
		
		if(recipeData == null)
			throw new WebApplicationException(Response.status(400).entity("\"recipe\" is not defined").build());
		logger.debug("recipe:"+recipeData);
		Session session = Session.init(request.getSession());
		Map<String, Cookie> cookies = hh.getCookies();
		JSONParser parser = new JSONParser();
		JSONObject json;
		try {
			json = (JSONObject) parser.parse(recipeData);
		}catch (ParseException e) {
			throw new WebApplicationException(Response.status(400).entity(e.getMessage()).build());
		}
		
		if(json.containsKey("userid")){
			if(session.checkLogin(cookies)){
				 User user = session.getUser();
				 int recipeUserid = Integer.parseInt(json.get("userid").toString());
				 if(user.id != recipeUserid)
					 throw new WebApplicationException(401);
			}
		}
		NewRecipe newRecipe;
		try {
			newRecipe = NewRecipe.initWithJSON(recipeName,json);
		} catch (ParseException | NewRecipeException e) {
			throw new WebApplicationException(Response.status(400).entity(e.getMessage()).build());
		}
		newRecipe.saveNewVersion();
		if(json.containsKey("mongoid"))
			CouchDB.delete(json.get("mongoid").toString(), 
					Integer.parseInt(json.get("userid").toString()));
		return Response.ok().build();			
		
		
	}
}
