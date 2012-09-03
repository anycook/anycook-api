package de.anycook.graph;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;

import de.anycook.misc.JsonpBuilder;
import de.anycook.recipe.Recipe;
import de.anycook.tag.Tag;


@Path("tag")
public class TagGraph {
	
	@SuppressWarnings("unchecked")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll(@QueryParam("callback") String callback){
		List<Tag> tags = Tag.getAll();
		JSONObject json = new JSONObject();
		json.put("tags", tags);
		json.put("total", tags.size());
		return Response.ok(JsonpBuilder.build(callback, json)).build();
	}
	
	/**
	 * Number of tags
	 * @param callback
	 * @return
	 */
	@GET
	@Path("number")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNum(@QueryParam("callback") String callback){
		return JsonpBuilder.buildResponse(callback, Tag.getTotal());
	}
	
	/**
	 * Tags ordered by popularity.
	 * @param recipe If set tags of this recipe are excluded
	 * @param callback 
	 * @return Map of tags ordered by popularity with number of recipes
	 */
	@GET
	@Path("popular")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPopularTags(@QueryParam("recipe") String recipe,
			@QueryParam("callback") String callback){
			Map<String, Integer> tagmap = null;
		if(recipe==null)
				 tagmap = Recipe.getPopularTags();		
		else
			tagmap = Recipe.getPopularTags(recipe);	
		return JsonpBuilder.buildResponse(callback, tagmap);
		
	}
	
	@GET
	@Path("{tagname}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTag(@QueryParam("callback") String callback,
			@PathParam("tagname") String tagname){
		Tag tag = Tag.init(tagname);
		return Response.ok(JsonpBuilder.build(callback, tag)).build();
	}
}
