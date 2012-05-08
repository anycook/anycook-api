package de.anycook.graph;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;

import de.anycook.autocomplete.Autocomplete;
import de.anycook.misc.JsonpBuilder;

@Path("autocomplete")
public class AutocompleteGraph {
	
	@GET
	public Response autocomplete(@QueryParam("q") String query,
			@QueryParam("maxlength") @DefaultValue("10") int maxresults,
			@QueryParam("callback")String callback){
		if(query == null)
			throw new WebApplicationException(401);
		JSONObject data = Autocomplete.autocompleteAll(query, maxresults);
		return JsonpBuilder.buildResponse(callback, data.toJSONString());
	}
}
