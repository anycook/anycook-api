/**
 * 
 */
package de.anycook.graph;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.anycook.news.life.Life;
import de.anycook.news.life.LifeHandler;
import de.anycook.utils.JsonpBuilder;

/**
 * Graph for lifes stream
 * @author Jan Graßegger <jan@anycook.de>
 *
 */
@Path("/life")
public class LifeGraph {	
	@GET
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public Response getLifes(@QueryParam("newestid") Integer newestid,
			@QueryParam("oldestid") Integer oldestid,
			@QueryParam("callback") String callback){
		List<Life> lifelist = null;
		if(newestid != null)
			lifelist = LifeHandler.getLastLifes(newestid);
		else
			lifelist = LifeHandler.getOlderLifes(oldestid);
		
		return JsonpBuilder.buildResponse(callback, lifelist);
	}
}
