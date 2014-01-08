package de.anycook.api;

import java.sql.SQLException;
import java.util.HashSet;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import de.anycook.api.util.MediaType;
import de.anycook.search.Query;
import org.apache.log4j.Logger;

import com.google.common.collect.Multimap;

import de.anycook.search.Search;
import de.anycook.search.SearchResult;


@Path("search")
public class SearchApi {

    private final Logger logger = Logger.getLogger(getClass());
	
	@POST
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public SearchResult search(Query query){
        try {
            return Search.search(query);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	
	/**
	 * Get possible types for a query. Exact query is matched. 
	 * @param query 
	 * @return
	 */
	@GET
	@Path("validate")
	@Produces(MediaType.APPLICATION_JSON)
	public Multimap<String, String> validateSearch(@QueryParam("q") String query){
		if(query==null)
			throw new WebApplicationException(400);
			
		query=query.toLowerCase();
        try {
            return Search.validateSearch(query);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
