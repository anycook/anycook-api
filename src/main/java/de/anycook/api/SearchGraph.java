package de.anycook.api;

import java.sql.SQLException;
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

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.google.common.collect.Multimap;

import de.anycook.utils.JsonpBuilder;
import de.anycook.search.Search;
import de.anycook.search.SearchResult;


@Path("search")
public class SearchGraph {

    private final Logger logger = Logger.getLogger(getClass());
	
	@SuppressWarnings("unchecked")
	@GET
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Response search(@QueryParam("callback") String callback, 
			@QueryParam("tags") StringSet tags,
			@QueryParam("ingredients") StringSet ingredients,
			@QueryParam("excludedingredients") StringSet excludedIngredients,
			@QueryParam("terms") StringSet terms,
			@QueryParam("category") String category,
			@QueryParam("calorie") int calorie,
			@QueryParam("skill") int skill,
			@QueryParam("time") String time,
			@QueryParam("user") String user,
			@DefaultValue("0") @QueryParam("start") int start,
			@DefaultValue("10") @QueryParam("num") int num){
		
		Search search = new Search();
		if(tags!= null && !tags.isEmpty())
			search.addTags(tags);
		if(ingredients != null && !ingredients.isEmpty())
			search.setIngredients(ingredients);
		if(excludedIngredients != null && !excludedIngredients.isEmpty())
			search.setExcludedIngredients(excludedIngredients);
//		if(terms != null && !terms.isEmpty())
//			search.addTerms(terms);
		search.setKategorie(category);
		search.setCalorie(calorie);
		search.setSkill(skill);
		search.setTime(time);
		search.setUser(user);
		
		SearchResult result = search.search(start, num);
		JSONObject json = new JSONObject();
//		JSONArray recipes = new JSONArray();
		List<String> results = result.getResults();
		
		
//		for(String recipe : results)
//			recipes.add(Recipe.getJSONforSearch(recipe));
		
		
		json.put("size", result.getResultLength());
		json.put("recipes", results);
		
		return JsonpBuilder.buildResponse(callback, json.toJSONString());
	}
	
	
	/**
	 * Get possible types for a query. Exact query is matched. 
	 * @param query 
	 * @param callback
	 * @return
	 */
	@GET
	@Path("validate")
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Multimap<String, String> validateSearch(@QueryParam("q") String query,
			@QueryParam("callback") String callback){
		if(query==null)
			throw new WebApplicationException(400);
			
		query=query.toLowerCase();
        try {
            return Search.validateSearch(query);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	public static class StringSet extends HashSet<String>{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public StringSet(String in) {
			super();
			if(in != null){
				for(String split : in.split(","))
					add(split);
			}
		}
	}
}
