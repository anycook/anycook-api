package de.anycook.graph;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import anycook.misc.JsonpBuilder;
import anycook.recipe.Recipe;
import anycook.search.DiscoverHandler;
import anycook.session.Session;

@Path("/discover")
public class DiscoverGraph {
	
	@GET
	public Response getDiscover(@Context HttpHeaders hh,
			@Context HttpServletRequest request,
			@DefaultValue("30") @QueryParam("recipeNum") int recipenum,
			@QueryParam("callback") String callback){
		Session session = Session.init(request.getSession());
		Map<String, List<Recipe>> recipes;
		try {
			session.checkLogin(hh.getCookies());
			recipes = DiscoverHandler.getDiscoverRecipes(recipenum, session.getUser());
		} catch (WebApplicationException e) {
			recipes = DiscoverHandler.getDiscoverRecipes(recipenum);
		}
		
		return JsonpBuilder.buildResponse(callback, recipes);
	}
}
