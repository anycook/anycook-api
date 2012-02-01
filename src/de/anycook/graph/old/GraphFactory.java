package de.anycook.graph.old;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

public abstract class GraphFactory implements Graphable{
	public enum Path{
		INGREDIENT, RECIPE, TAG, USER, CATEGORY, SEARCH;
		
	}
	
	protected final String[] path;
	protected final Map<String, String> additional;
	protected static final Logger logger = Logger.getLogger(GraphFactory.class);
	
	protected GraphFactory(String[] path, Map<String, String> additional){
		this.path = path;
		this.additional = additional;
	}
	
	public static GraphFactory create(Map<String, String> data){
		if(!data.containsKey("path"))
			return null;
		
		String path = data.get("path");
		if(path.startsWith("/"))
			path = path.substring(1);
		String[] paths = path.split("/");
		
		Path type;
		try{
			 type = Path.valueOf(paths[0].toUpperCase());
		}catch(IllegalArgumentException e){
			return null;
		}
		
		
		switch(type){
		case INGREDIENT:
			return new IngredientGraph(paths, data);
			
		case RECIPE:
			return new RecipeGraph(paths, data);
			
		case TAG:
			return new TagGraph(paths, data);
			
		case USER:
			return new UserGraph(paths, data);
		case CATEGORY:
			return new CategoryGraph(paths, data);
		case SEARCH:
			return new SearchGraph(paths, data);
		}
		return null;
	}
	
	protected abstract JSONObject getAll();
	
	@Override
	public File getImage() {
		return null;
	}
	
	
}
