package de.anycook.graph;

import java.util.HashSet;
import java.util.List;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;

import de.anycook.autocomplete.Autocomplete;
import de.anycook.graph.SearchGraph.StringSet;
import de.anycook.utils.JsonpBuilder;
import de.anycook.user.User;


/**
 * Handles all autocomplete calls
 * @author Jan Graßegger <jan@anycook.de>
 *
 */
@Path("autocomplete")
public class AutocompleteGraph {
	
	/**
	 * autocompletes for all categories
	 * @param query 
	 * @param maxresults
	 * @param callback
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Response autocomplete(@QueryParam("q") String query,
			@QueryParam("excludedusers") IntSet excludedUsers,
			@QueryParam("excludedtags") StringSet excludedTags,
			@QueryParam("excludedingredients") StringSet excludedIngredients,
			@QueryParam("excludedcategorie") String excludedCategorie,
			@QueryParam("maxresults") @DefaultValue("10") int maxresults,
			@QueryParam("callback")String callback){
		if(query == null)
			throw new WebApplicationException(401);
		JSONObject data = Autocomplete.autocompleteAll(query, maxresults, 
				excludedIngredients, excludedTags, excludedUsers, excludedCategorie);
		return JsonpBuilder.buildResponse(callback, data.toJSONString());
	}
	
	@GET
	@Path("ingredient")
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Response autocompleteIngredient(@QueryParam("q") String query,
			@QueryParam("exclude") StringSet exclude,
			@QueryParam("maxresults") @DefaultValue("10") int maxresults,
			@QueryParam("callback")String callback){
		if(query == null)
			throw new WebApplicationException(401);
		List<String> data = Autocomplete.autocompleteZutat(query, maxresults, exclude);
		return JsonpBuilder.buildResponse(callback, data);
	}
	
	@GET
	@Path("user")
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Response autocompleteUser(@QueryParam("q") String query,
			@QueryParam("exclude") IntSet exclude,
			@QueryParam("maxresults") @DefaultValue("10") int maxresults,
			@QueryParam("callback")String callback){
		if(query == null)
			throw new WebApplicationException(401);
		
		List<User> data = Autocomplete.autocompleteUsernames(query, maxresults, exclude);
		return JsonpBuilder.buildResponse(callback, data);
	}
	
	@GET
	@Path("tag")
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Response autocompleteTags(@QueryParam("q") String query,
			@QueryParam("exclude") StringSet exclude,
			@QueryParam("maxresults") @DefaultValue("10") int maxresults,
			@QueryParam("callback")String callback){
		if(query == null)
			throw new WebApplicationException(401);
		List<String> data = Autocomplete.autocompleteTag(query, maxresults, exclude);
		return JsonpBuilder.buildResponse(callback, data);
	}
	
	public static class IntSet extends HashSet<Integer>{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public IntSet(String in) {
			super();
			if(in != null){
				for(String split : in.split(","))
					add(Integer.parseInt(split));
			}
		}
	}
}
