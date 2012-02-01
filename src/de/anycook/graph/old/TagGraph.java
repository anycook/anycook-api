package de.anycook.graph.old;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import anycook.tag.Tag;

public class TagGraph extends GraphFactory{

	protected TagGraph(String[] path, Map<String, String> additional) {
		super(path, additional);
	}

	@Override
	public String getJSON() {
		switch (path.length) {
		case 1:
			return getAll().toJSONString();
		case 2:
			return get(path[1]).toJSONString();
		default:
			return null;
		}
	}
	
	protected JSONObject get(String name){
		Tag tag = Tag.init(name);
		return tag.getJSON();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected JSONObject getAll() {
		List<Tag> tags = Tag.getAll();
		JSONObject json = new JSONObject();
		json.put("tags", tags);
		json.put("total", tags.size());
		return json;
		
	}

}
