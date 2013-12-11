package de.anycook.api;

import java.io.IOException;
import java.sql.SQLException;
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
import de.anycook.discover.DiscoverHandler;
import de.anycook.session.Session;
import org.apache.log4j.Logger;


@Path("/discover")
public class DiscoverApi {

    private final Logger logger = Logger.getLogger(getClass());
	
	@GET
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Map<String, List<String>> getDiscover(@Context HttpHeaders hh,
			@Context HttpServletRequest request,
			@DefaultValue("30") @QueryParam("recipenum") int recipenum){
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
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public List<String> getDiscoverRecommended(@Context HttpHeaders hh,
			@Context HttpServletRequest request,
			@DefaultValue("30") @QueryParam("recipenum") int recipenum){
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
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public List<String> getDiscoverTasty(
			@DefaultValue("30") @QueryParam("recipenum") int recipenum,
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
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public List<String> getDiscoverNew(
			@DefaultValue("30") @QueryParam("recipenum") int recipenum,
			@QueryParam("callback") String callback){
        try {
            return DiscoverHandler.getNewestRecipes(recipenum);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
