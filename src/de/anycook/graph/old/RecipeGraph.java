package de.anycook.graph.old;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import anycook.recipe.Recipe;

public class RecipeGraph extends GraphFactory {

	protected RecipeGraph(String[] path, Map<String, String> additional) {
		super(path, additional);
	}

	@Override
	public String getJSON() {
		switch(path.length){			
		case 1:
			return getAll().toJSONString();
		case 2:
			return getRecipe().toJSONString();
		case 3:
			
		default:
			return null;
		}
	}
	
	public File getImage() {
		//StringBuffer imagepath = new StringBuffer("http://anycook.de/gerichtebilder/");
		StringBuffer imagepath = new StringBuffer("/var/www/sites/anycook.de/htdocs/gerichtebilder/");
		if(additional.containsKey("type")){
			String type = additional.get("type");
			if(type.equals("large"))
				imagepath.append("big/");
			else
				imagepath.append("small/");				
		}else
			imagepath.append("small/");
		
		String imagedata = Recipe.getImageName(path[1]);
		if(imagedata == null)
			imagedata = "nopicture.png";
		imagepath.append(Recipe.getImageName(path[1]));
		return new File(imagepath.toString());
	}

	private JSONObject getRecipe() {
		Recipe recipe;
		if(path[1].equals("random"))
			recipe = Recipe.getRandomRecipe();
		else
			recipe = Recipe.init(path[1]);
		return recipe.getJSON();		
	}

	@SuppressWarnings("unchecked")
	@Override
	protected JSONObject getAll(){
		JSONObject json = new JSONObject();
		List<String> recipes = Recipe.getAll();
		json.put("names", recipes);
		json.put("total", recipes.size());
		return json;
	}

}
