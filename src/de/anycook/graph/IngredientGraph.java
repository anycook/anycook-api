package de.anycook.graph;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;

import de.anycook.ingredient.Ingredient;
import de.anycook.newrecipe.NewRecipe;
import de.anycook.utils.JsonpBuilder;


@Path("/ingredient")
public class IngredientGraph {
	
	@SuppressWarnings("unchecked")
	@GET
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
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
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Response getNum(@QueryParam("callback") String callback){
		return JsonpBuilder.buildResponse(callback, Ingredient.getTotal());
	}
	
	@GET
	@Path("extract")
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Response extractIngredients(@QueryParam("q") String query,
			@QueryParam("callback") String callback){
		Set<String> ingredients = NewRecipe.searchNGram(query, 3);
		
		return JsonpBuilder.buildResponse(callback, new LinkedList<>(ingredients));
		
	}
	
	@GET
	@Path("{ingredientname}")
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Response getIngredient(@PathParam("ingredientname") String ingredientName,
			@QueryParam("callback") String callback,
			@QueryParam("children") String children){
		Ingredient ingredient = Ingredient.init(ingredientName);
		if(ingredient == null)
			throw new WebApplicationException(404);
		
		if(children == null)
			return Response.ok(JsonpBuilder.build(callback, ingredient)).build();
		return Response.ok(JsonpBuilder.build(callback, ingredient.getJSONWithChildRecipes())).build();
	}
}
