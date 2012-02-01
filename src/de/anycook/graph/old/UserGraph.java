package de.anycook.graph.old;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import anycook.misc.enumerations.ImageType;
import anycook.user.User;

public class UserGraph extends GraphFactory {
	

	protected UserGraph(String[] path, Map<String, String> additional) {
		super(path, additional);
	}

	@Override
	public String getJSON() {
		switch (path.length) {
		case 1:
			return getAll().toJSONString();
		case 2:
			return get(Integer.parseInt(path[1])).toJSONString();

		default:
			return null;
		}
	}

	private JSONObject get(int id) {
		User user = User.init(id);
		return user.getProfileInfoJSON();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected JSONObject getAll() {
		JSONObject json = new JSONObject();
		List<User> users = User.getAll();
		json.put("users", users);
		json.put("total", users.size());
		return json;
	}
	
	public File getImage() {
		String typeString = additional.get("type");
		ImageType type = typeString != null ? ImageType.valueOf(typeString.toUpperCase()) : ImageType.SMALL;
		String imagePath = "/var/www/sites/anycook.de/htdocs"+User.getUserImage(Integer.parseInt(path[1]), type);
		return new File(imagePath);
	}

}
