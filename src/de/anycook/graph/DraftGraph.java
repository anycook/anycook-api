package de.anycook.graph;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import de.anycook.db.mongo.RecipeDrafts;
import de.anycook.session.Session;
import de.anycook.utils.JsonpBuilder;

@Path("/drafts")
public class DraftGraph {
	
	@GET
	@Path("{id}")
	public Response getDraft(@PathParam("id") String draft_id,
			@QueryParam("callback") String callback,
			@Context HttpHeaders hh,
			@Context HttpServletRequest request){
		Session session = Session.init(request.getSession());
		session.checkLogin(hh.getCookies());
		RecipeDrafts recipeDrafts = new RecipeDrafts();
		JSONObject json = recipeDrafts.loadDraft(draft_id, session.getUser().id);
		return JsonpBuilder.buildResponse(callback, json.toJSONString());
	}
	
	@PUT
	public String newDraft(@Context HttpHeaders hh,
			@Context HttpServletRequest request){
		RecipeDrafts recipeDrafts = new RecipeDrafts();
		Session session = Session.init(request.getSession());
		session.checkLogin(hh.getCookies());
		return recipeDrafts.newDraft(session.getUser().id);
	}
	
	@POST
	public void setData(@FormParam("data") String data, 
			@FormParam("id") String draft_id,
			@Context HttpHeaders hh,
			@Context HttpServletRequest request){		
		Session session = Session.init(request.getSession());
		session.checkLogin(hh.getCookies());
		JSONParser parser = new JSONParser();
		try {
			JSONObject json = (JSONObject)parser.parse(data);
			RecipeDrafts drafts = new RecipeDrafts();
			drafts.update(json, session.getUser().id, draft_id);
			
		} catch (ParseException e) {
			throw new WebApplicationException(400);
		}
	}
}
