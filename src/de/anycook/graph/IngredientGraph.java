package de.anycook.graph;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;

import de.anycook.ingredient.Ingredient;
import de.anycook.misc.JsonpBuilder;


@Path("/ingredient")
public class IngredientGraph {
	
	@SuppressWarnings("unchecked")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
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
	
	/**
	 * Number of ingredients
	 * @param callback
	 * @return
	 */
	@GET
	@Path("number")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNum(@QueryParam("callback") String callback){
		return JsonpBuilder.buildResponse(callback, Ingredient.getTotal());
	}
	
	@GET
	@Path("{ingredientname}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getIngredient(@PathParam("ingredientname") String ingredientName,
			@QueryParam("callback") String callback,
			@QueryParam("children") String children){
		Ingredient ingredient = Ingredient.init(ingredientName);
		
		if(children == null)
			return Response.ok(JsonpBuilder.build(callback, ingredient)).build();
		return Response.ok(JsonpBuilder.build(callback, ingredient.getJSONWithChildRecipes())).build();
	}
}
