package de.anycook.graph;

import java.util.List;

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
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import anycook.misc.JsonpBuilder;
import anycook.misc.enumerations.ImageType;
import anycook.newrecipe.NewRecipe;
import anycook.newrecipe.NewRecipe.NewRecipeException;
import anycook.recipe.Recipe;
import anycook.session.Session;

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
	public Response saveRecipe(@PathParam("recipename") String recipeName,
			@Context HttpServletRequest request,
			@FormParam("recipe") String recipeData){
		logger.info("want to save recipe");
		
		Session session = Session.init(request.getSession());
		if(session.checkLogin()){
			JSONParser parser = new JSONParser();
			JSONObject json;
			try {
				json = (JSONObject) parser.parse(recipeData);
				NewRecipe newRecipe = NewRecipe.initWithJSON(recipeName,json, session.getUser());
				newRecipe.saveNewVersion();
				return Response.ok().build();
			} catch (ParseException | NewRecipeException e) {
				throw new WebApplicationException(Response.status(400).entity(e.getMessage()).build());
			}
			
		}
		
		throw new WebApplicationException(401);
	}
}
