package de.anycook.graph;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;

import anycook.ingredient.Ingredient;
import anycook.misc.JsonpBuilder;

@Path("/ingredient")
public class IngredientGraph {
	
	@SuppressWarnings("unchecked")
	@GET
	public Response getAll(@QueryParam("callback") String callback, @QueryParam("parent") String parent){
		List<Ingredient> ingredients;
		if(parent==null)
			ingredients = Ingredient.getAll();
		else
			ingredients = Ingredient.loadParents();
		
		JSONObject json = new JSONObject();
		json.put("ingredients", ingredients);
		json.put("total", ingredients.size());
		return Response.ok(JsonpBuilder.build(callback, json)).build();
	}
	
	@GET
	@Path("{ingredientname}")
	public Response getIngredient(@PathParam("ingredientname") String ingredientName,
			@QueryParam("callback") String callback,
			@QueryParam("children") String children){
		Ingredient ingredient = Ingredient.init(ingredientName);
		
		if(children == null)
			return Response.ok(JsonpBuilder.build(callback, ingredient)).build();
		return Response.ok(JsonpBuilder.build(callback, ingredient.getJSONWithChildRecipes())).build();
	}
}
