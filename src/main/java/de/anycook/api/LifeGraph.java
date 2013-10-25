/**
 * 
 */
package de.anycook.api;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.anycook.news.life.Life;
import de.anycook.news.life.LifeHandler;
import org.apache.log4j.Logger;

/**
 * Graph for lifes stream
 * @author Jan Graßegger <jan@anycook.de>
 *
 */
@Path("/life")
public class LifeGraph {
    private final Logger logger = Logger.getLogger(getClass());

	@GET
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public List<Life> getLifes(@QueryParam("newestid") Integer newestid,
			@QueryParam("oldestid") Integer oldestid){

        try {
            if(newestid != null)
                return LifeHandler.getLastLives(newestid);
            else
                return LifeHandler.getOlderLives(oldestid);
        } catch (SQLException e){
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

	}
}
