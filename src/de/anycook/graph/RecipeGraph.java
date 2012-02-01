package de.anycook.graph;

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
import anycook.recipe.Recipe;

@Path("/recipe")
public class RecipeGraph {

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
}
