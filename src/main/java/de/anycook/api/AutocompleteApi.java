package de.anycook.api;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import de.anycook.api.util.MediaType;
import org.apache.log4j.Logger;

import de.anycook.autocomplete.Autocomplete;
import de.anycook.user.User;


/**
 * Handles all autocomplete calls
 * @author Jan Graßegger <jan@anycook.de>
 *
 */
@Path("autocomplete")
public class AutocompleteApi {

    private final Logger logger = Logger.getLogger(getClass());
	
	/**
	 * autocompletes for all categories
	 * @param query 
	 * @param maxResults
	 * @return
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> autocomplete(@QueryParam("q") String query,
			@QueryParam("excludedusers") IntSet excludedUsers,
			@QueryParam("excludedtags") StringSet excludedTags,
			@QueryParam("excludedingredients") StringSet excludedIngredients,
			@QueryParam("excludedcategorie") String excludedCategory,
			@QueryParam("maxresults") @DefaultValue("10") int maxResults){
		if(query == null)
			throw new WebApplicationException(401);
        try {
            return Autocomplete.autoCompleteAll(query, maxResults,
                    excludedIngredients, excludedTags, excludedUsers, excludedCategory);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
	}
	
	@GET
	@Path("ingredient")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> autocompleteIngredient(@QueryParam("q") String query,
			@QueryParam("exclude") StringSet exclude,
			@QueryParam("maxresults") @DefaultValue("10") int maxResults){
		if(query == null)
			throw new WebApplicationException(401);
        try {
            return Autocomplete.autocompleteZutat(query, maxResults, exclude);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
	}
	
	@GET
	@Path("user")
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> autoCompleteUser(@QueryParam("q") String query,
                                     @QueryParam("exclude") IntSet exclude,
                                     @QueryParam("maxresults") @DefaultValue("10") int maxResults){
		if(query == null)
			throw new WebApplicationException(401);

        try {
            return Autocomplete.autocompleteUsernames(query, maxResults, exclude);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
	}
	
	@GET
	@Path("tag")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> autocompleteTags(@QueryParam("q") String query,
			@QueryParam("exclude") StringSet exclude,
			@QueryParam("maxresults") @DefaultValue("10") int maxresults,
			@QueryParam("callback")String callback){
		if(query == null)
			throw new WebApplicationException(401);
        try {
            return Autocomplete.autocompleteTag(query, maxresults, exclude);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
	}
	
	public static class IntSet extends HashSet<Integer>{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public IntSet(String in) {
			super();
			if(in != null){
				for(String split : in.split(","))
					add(Integer.parseInt(split));
			}
		}
	}

    public static class StringSet extends HashSet<String>{
        private static final long serialVersionUID = 1L;

        public StringSet(String in) {
            super();
            if(in != null){
                for(String split : in.split(","))
                    add(split);
            }
        }
    }
}
