package de.anycook.graph;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;

import de.anycook.misc.JsonpBuilder;
import de.anycook.tag.Tag;


@Path("tag")
public class TagGraph {
	
	@SuppressWarnings("unchecked")
	@GET
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
	public Response getNum(@QueryParam("callback") String callback){
		return JsonpBuilder.buildResponse(callback, Tag.getTotal());
	}
	
	@GET
	@Path("{tagname}")
	public Response getTag(@QueryParam("callback") String callback,
			@PathParam("tagname") String tagname){
		Tag tag = Tag.init(tagname);
		return Response.ok(JsonpBuilder.build(callback, tag)).build();
	}
}
