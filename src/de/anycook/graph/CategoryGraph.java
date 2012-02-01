package de.anycook.graph;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;

import anycook.category.Category;
import anycook.misc.JsonpBuilder;

@Path("/category")
public class CategoryGraph {

	@SuppressWarnings("unchecked")
	@GET
	public Response getAll(@QueryParam("callback") String callback){
		List<String> categories = Category.getAll();
		JSONObject json = new JSONObject();
		json.put("total", categories.size());
		json.put("categories", categories);
		
		return Response.ok(JsonpBuilder.build(callback, json)).build();
	}
	
	@Path("{categoryname}")
	@GET
	public Response getCategory(@QueryParam("callback") String callback,
			@PathParam("categoryname") String categoryname){
		Category category = Category.init(categoryname);
		return Response.ok(JsonpBuilder.build(callback, category)).build();
	}
}
