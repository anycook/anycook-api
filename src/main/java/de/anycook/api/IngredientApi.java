package de.anycook.api;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import de.anycook.ingredient.Ingredient;
import de.anycook.utils.JsonpBuilder;


@Path("/ingredient")
public class IngredientApi {

    private Logger logger = Logger.getLogger(getClass());
	
	@SuppressWarnings("unchecked")
	@GET
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public List<Ingredient> getAll(@QueryParam("parent") String parent){
		 try {
             if(parent==null) return Ingredient.getAll();
            return Ingredient.loadParents();
        } catch (SQLException e) {
            logger.error(e);
             throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
	}
	
	/**
	 * Number of ingredients
	 * @return
	 */
	@GET
	@Path("number")
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Integer getNum(){
        try {
            return Ingredient.getTotal();
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	@GET
	@Path("extract")
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Set<String> extractIngredients(@QueryParam("q") String query){
        try {
            return Ingredient.searchNGram(query, 3);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	@GET
	@Path("{ingredientname}")
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Ingredient getIngredient(@PathParam("ingredientname") String ingredientName){
        try {
            return Ingredient.init(ingredientName);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
	}
}
