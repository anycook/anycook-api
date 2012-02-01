package de.anycook.graph;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;

import anycook.misc.JsonpBuilder;
import anycook.tag.Tag;

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
	
	@GET
	@Path("{tagname}")
	public Response getTag(@QueryParam("callback") String callback,
			@PathParam("tagname") String tagname){
		Tag tag = Tag.init(tagname);
		return Response.ok(JsonpBuilder.build(callback, tag)).build();
	}
}
