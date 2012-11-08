package de.anycook.graph;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.anycook.user.User;
import de.anycook.utils.JsonpBuilder;
import de.anycook.recipe.Recipe;
import de.anycook.search.DiscoverHandler;
import de.anycook.session.Session;


@Path("/discover")
public class DiscoverGraph {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Response getDiscover(@Context HttpHeaders hh,
			@Context HttpServletRequest request,
			@DefaultValue("30") @QueryParam("recipenum") int recipenum,
			@QueryParam("callback") String callback){
		Session session = Session.init(request.getSession());
		Map<String, List<Recipe>> recipes;
		try {
			session.checkLogin(hh.getCookies());
			User user = session.getUser();
			recipes = DiscoverHandler.getDiscoverRecipes(recipenum, user.id);
		} catch (WebApplicationException e) {
			recipes = DiscoverHandler.getDiscoverRecipes(recipenum);
		}
		
		return JsonpBuilder.buildResponse(callback, recipes);
	}
}
