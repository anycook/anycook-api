package de.anycook.graph.old;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import anycook.ingredient.Ingredient;

public class IngredientGraph extends GraphFactory{
	protected IngredientGraph(String[] paths, Map<String, String> additional) {
		super(paths, additional);
	}

	@Override
	public String getJSON() {		
		switch(path.length){
			case 1:
				if(additional.containsKey("parent"))
					return getParents().toJSONString();
				return getAll().toJSONString();
			case 2:
				if(additional.containsKey("children"))
					return getWithChildren(path[1]).toJSONString();
				return get(path[1]).toJSONString();
			default:
				return null;
		}
	}

	private JSONObject getWithChildren(String name) {
		Ingredient result = Ingredient.init(name);
		
		return result.getJSONWithChildRecipes();
	}

	private JSONObject get(String name) {
		Ingredient result = Ingredient.init(name);
		
		return result.getJSON();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected JSONObject getAll() {
		List<Ingredient> ingredients = Ingredient.getAll();
		
		JSONObject json = new JSONObject();
		json.put("ingredients", ingredients);
		json.put("total", ingredients.size());
		
		return json;
	}
	
	@SuppressWarnings("unchecked")
	protected JSONObject getParents(){
		List<Ingredient> ingredients = Ingredient.loadParents();
		JSONObject json = new JSONObject();
		json.put("ingredients", ingredients);
		json.put("total", ingredients.size());
		
		return json;
	}

}
