package de.anycook.graph;

import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import anycook.misc.JsonpBuilder;
import anycook.recipe.Recipe;
import anycook.search.Search;

@Path("search")
public class SearchGraph {
//	if(parameter.contains("tags"))
//		this.addTags(data.get("tags").split(","));
//		
//	if(parameter.contains("ingredients"))
//		this.addZutaten(data.get("ingredients").split(","));			
//		
//	if(parameter.contains("terms"))
//		this.addTerms(data.get("terms").split(","));
//	
//	if(parameter.contains("category"))
//		this.setKategorie(data.get("category"));
//	
//	if(parameter.contains("calorie"))
//		this.setKalorien(data.get("calorie"));
//	
//	if(parameter.contains("skill"))
//		this.setSkill(data.get("skill"));
//	
//	if(parameter.contains("user"))
//		this.setUser(data.get("user"));
//	
//	if(parameter.contains("time"))
//		this.setTime(data.get("time"));
	
	@SuppressWarnings("unchecked")
	@GET
	public Response search(@QueryParam("callback") String callback, 
			@QueryParam("tags") Set<String>	tags,
			@QueryParam("ingredients") Set<String> ingredients,
			@QueryParam("terms") Set<String> terms,
			@QueryParam("category") String category,
			@QueryParam("calorie") int calorie,
			@QueryParam("skill") int skill,
			@QueryParam("time") String time){
		Search search = new Search();
		if(!tags.isEmpty())
			search.addTags(tags);
		if(!ingredients.isEmpty())
			search.addZutaten(ingredients);
		if(!terms.isEmpty())
			search.addTerms(terms);
		search.setKategorie(category);
		search.setCalorie(calorie);
		search.setSkill(skill);
		search.setTime(time);
		
		List<String> results = search.search();
		JSONObject json = new JSONObject();
		JSONArray recipes = new JSONArray();
		for(String result : results){
			recipes.add(Recipe.getJSONforSearch(result));
		}
		
		json.put("size", results.size());
		json.put("recipes", recipes);
		
		return Response.ok(JsonpBuilder.build(callback, json)).build();
	}
}
