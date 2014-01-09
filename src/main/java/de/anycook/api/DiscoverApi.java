package de.anycook.api;

import de.anycook.api.util.MediaType;
import de.anycook.discover.DiscoverHandler;
import de.anycook.session.Session;
import de.anycook.user.User;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


@Path("/discover")
public class DiscoverApi {

    private final Logger logger = Logger.getLogger(getClass());
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, List<String>> getDiscover(@Context HttpHeaders hh,
			@Context HttpServletRequest request,
			@DefaultValue("30") @QueryParam("recipeNumber") int recipenum){
		Session session = Session.init(request.getSession());
        try {
            try {
                session.checkLogin(hh.getCookies());
                User user = session.getUser();
                return DiscoverHandler.getDiscoverRecipes(recipenum, user.getId());
            } catch (WebApplicationException e) {
                return DiscoverHandler.getDiscoverRecipes(recipenum);
            }
        } catch (IOException | SQLException e){
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
	}
	
	@GET
	@Path("recommended")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getDiscoverRecommended(@Context HttpHeaders hh,
			@Context HttpServletRequest request,
			@DefaultValue("30") @QueryParam("recipeNumber") int recipenum){
		Session session = Session.init(request.getSession());
        try {
            try {
                session.checkLogin(hh.getCookies());
                User user = session.getUser();
                return DiscoverHandler.getRecommendedRecipes(recipenum, user.getId());
            } catch (WebApplicationException e) {
                return DiscoverHandler.getPopularRecipes(recipenum);
            }
        } catch (IOException | SQLException e){
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
	}
	
	@GET
	@Path("tasty")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getDiscoverTasty(
			@DefaultValue("30") @QueryParam("recipeNumber") int recipenum,
			@QueryParam("callback") String callback){
        try {
            return DiscoverHandler.getTastyRecipes(recipenum);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	@GET
	@Path("new")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getDiscoverNew(
			@DefaultValue("30") @QueryParam("recipeNumber") int recipenum,
			@QueryParam("callback") String callback){
        try {
            return DiscoverHandler.getNewestRecipes(recipenum);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
