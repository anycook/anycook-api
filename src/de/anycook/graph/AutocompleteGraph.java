package de.anycook.graph;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;

import de.anycook.autocomplete.Autocomplete;
import de.anycook.misc.JsonpBuilder;
import de.anycook.user.User;

@Path("autocomplete")
public class AutocompleteGraph {
	
	@GET
	public Response autocomplete(@QueryParam("q") String query,
			@QueryParam("maxresults") @DefaultValue("10") int maxresults,
			@QueryParam("callback")String callback){
		if(query == null)
			throw new WebApplicationException(401);
		JSONObject data = Autocomplete.autocompleteAll(query, maxresults);
		return JsonpBuilder.buildResponse(callback, data.toJSONString());
	}
	
	@GET
	@Path("ingredients")
	public Response autocompleteIngredient(@QueryParam("q") String query,
			@QueryParam("maxresults") @DefaultValue("10") int maxresults,
			@QueryParam("callback")String callback){
		if(query == null)
			throw new WebApplicationException(401);
		List<String> data = Autocomplete.autocompleteZutat(query, maxresults);
		return JsonpBuilder.buildResponse(callback, data);
	}
	
	@GET
	@Path("users")
	public Response autocompleteUser(@QueryParam("q") String query,
			@QueryParam("maxresults") @DefaultValue("10") int maxresults,
			@QueryParam("callback")String callback){
		if(query == null)
			throw new WebApplicationException(401);
		List<User> data = Autocomplete.autocompleteUsernames(query, maxresults);
		return JsonpBuilder.buildResponse(callback, data);
	}
	
	@GET
	@Path("tags")
	public Response autocompleteTags(@QueryParam("q") String query,
			@QueryParam("maxresults") @DefaultValue("10") int maxresults,
			@QueryParam("callback")String callback){
		if(query == null)
			throw new WebApplicationException(401);
		List<String> data = Autocomplete.autocompleteTag(query, maxresults);
		return JsonpBuilder.buildResponse(callback, data);
	}
}
