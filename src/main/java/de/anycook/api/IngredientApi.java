package de.anycook.api;

import de.anycook.api.util.MediaType;
import de.anycook.recipe.ingredient.Ingredient;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;


@Path("/ingredient")
public class IngredientApi {

    private Logger logger = Logger.getLogger(getClass());
	
	@SuppressWarnings("unchecked")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
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
	@Produces(MediaType.APPLICATION_JSON)
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
	@Produces(MediaType.APPLICATION_JSON)
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
	@Produces(MediaType.APPLICATION_JSON)
	public Ingredient getIngredient(@PathParam("ingredientname") String ingredientName){
        try {
            return Ingredient.init(ingredientName);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
	}
}
