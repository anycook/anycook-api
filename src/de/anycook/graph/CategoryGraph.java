package de.anycook.graph;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;

import de.anycook.category.Category;
import de.anycook.misc.JsonpBuilder;


@Path("/category")
public class CategoryGraph {

	@SuppressWarnings("unchecked")
	@GET
	public Response getAll(@QueryParam("callback") String callback){
		List<String> categories = Category.getAll();
		JSONObject json = new JSONObject();
		json.put("total", categories.size());
		json.put("categories", categories);
		
		return JsonpBuilder.buildResponse(callback, json.toJSONString());
	}
	
	/**
	 * All categories ordered by order attribute in DB
	 * @param callback
	 * @return
	 */
	@Path("sorted")
	@GET
	public Response getAllSorted(@QueryParam("callback") String callback){
		Map<String, Integer> categories = Category.getAllSorted();
		return JsonpBuilder.buildResponse(callback, categories);
	}
	
	@Path("{categoryname}")
	@GET
	public Response getCategory(@QueryParam("callback") String callback,
			@PathParam("categoryname") String categoryname){
		Category category = Category.init(categoryname);
		if(category == null)
			throw new WebApplicationException(400);
		return Response.ok(JsonpBuilder.build(callback, category)).build();
	}
}
