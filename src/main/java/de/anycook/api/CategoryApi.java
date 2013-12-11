package de.anycook.api;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.anycook.db.mysql.DBCategory;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import de.anycook.category.Category;
import de.anycook.utils.JsonpBuilder;


@Path("/category")
public class CategoryApi {

    private final Logger logger = Logger.getLogger(getClass());

	@SuppressWarnings("unchecked")
	@GET
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public List<String> getAll(){
        try {
            return Category.getAll();
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	/**
	 * All categories ordered by order attribute in DB
	 * @return
	 */
	@Path("sorted")
	@GET
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Map<String, Integer> getAllSorted(){
        try {
            return Category.getAllSorted();
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	@Path("{categoryname}")
	@GET
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Category getCategory(@PathParam("categoryname") String categoryname){
        try {
            return Category.init(categoryname);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBCategory.CategoryNotFoundException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
}
