package de.anycook.api;

import de.anycook.api.util.MediaType;
import de.anycook.category.Category;
import de.anycook.db.mysql.DBCategory;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


@Path("/category")
public class CategoryApi {

    private final Logger logger = Logger.getLogger(getClass());

	@SuppressWarnings("unchecked")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
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
	@Produces(MediaType.APPLICATION_JSON)
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
	@Produces(MediaType.APPLICATION_JSON)
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
