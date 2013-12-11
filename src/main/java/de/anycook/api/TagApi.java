package de.anycook.api;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.anycook.db.mysql.DBTag;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import de.anycook.utils.JsonpBuilder;
import de.anycook.recipe.Recipe;
import de.anycook.tag.Tag;


@Path("tag")
public class TagApi {
    private final Logger logger = Logger.getLogger(getClass());
	
	@SuppressWarnings("unchecked")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Tag> getAll(){
        try {
            return Tag.getAll();
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	/**
	 * Number of tags
	 * @return
	 */
	@GET
	@Path("number")
	@Produces(MediaType.APPLICATION_JSON)
	public int getNum(){
        try {
            return Tag.getTotal();
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	/**
	 * Tags ordered by popularity.
	 * @param recipe If set tags of this recipe are excluded
	 * @return Map of tags ordered by popularity with number of recipes
	 */
	@GET
	@Path("popular")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Integer> getPopularTags(@QueryParam("recipe") String recipe){
		try {
            if(recipe==null)
                return Recipe.getPopularTags();
            return Recipe.getPopularTags(recipe);
        } catch (SQLException e){
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
	}
	
	@GET
	@Path("{tagName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Tag getTag(@PathParam("tagName") String tagName){
        try {
            return Tag.init(tagName);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBTag.TagNotFoundException e) {
            logger.warn(e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
}
