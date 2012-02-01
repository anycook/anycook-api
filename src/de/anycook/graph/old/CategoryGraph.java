package de.anycook.graph.old;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import anycook.category.Category;

public class CategoryGraph extends GraphFactory {

	protected CategoryGraph(String[] path, Map<String, String> additional) {
		super(path, additional);
	}

	@Override
	public String getJSON() {
		switch(path.length){
		case 1:
			return getAll().toJSONString();
		case 2:
			return get(path[1]).toJSONString();
		default: 
			return null;
		}
	}
	
	protected JSONObject get(String name){
		Category category = Category.init(name);
		return category.getJSON();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected JSONObject getAll() {
		List<String> categories = Category.getAll();
		JSONObject json = new JSONObject();
		json.put("total", categories.size());
		json.put("categories", categories);
		return json;
	}

}
