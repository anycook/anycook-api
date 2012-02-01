package de.anycook.graph.old;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import anycook.recipe.Recipe;
import anycook.search.Search;

public class SearchGraph extends GraphFactory {

	protected SearchGraph(String[] path, Map<String, String> additional) {
		super(path, additional);
	}

	private List<String> search(){
		Search search = new Search(additional);
		return search.search();
	}
	@SuppressWarnings("unchecked")
	@Override
	public String getJSON() {
		JSONArray json = new JSONArray();
		List<String> result = search();
		for(String gerichtename : result){
			JSONObject jsonrecipe = Recipe.getJSONforSearch(gerichtename);
			json.add(jsonrecipe);
		}
		
		JSONObject jsonmap = new JSONObject();
		jsonmap.put("size", json.size());
		jsonmap.put("recipes", json);
		return jsonmap.toJSONString();
	}

	@Override
	protected JSONObject getAll() {
		return null;
	}

}
