/**
 * 
 */
package de.anycook.api;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.container.TimeoutHandler;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.anycook.news.life.Life;
import de.anycook.news.life.LifeHandler;
import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ManagedAsync;

/**
 * Graph for lifes stream
 * @author Jan Graßegger <jan@anycook.de>
 *
 */
@Path("/life")
public class LifeGraph {
    private final Logger logger = Logger.getLogger(getClass());

	@GET
    @ManagedAsync
	@Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
	public void getLives(@Suspended AsyncResponse asyncResponse, @QueryParam("newestid") Integer newestId,
                         @QueryParam("oldestid") Integer oldestId){

        asyncResponse.setTimeoutHandler(new TimeoutHandler() {
            @Override
            public void handleTimeout(AsyncResponse asyncResponse) {
                logger.info("reached timeout");
                asyncResponse.resume(Response.ok().build());
            }
        });

        asyncResponse.setTimeout(5, TimeUnit.MINUTES);

        try{
            while(asyncResponse.isSuspended() && !asyncResponse.isCancelled() && !asyncResponse.isDone()){
                List<Life> lives = newestId != null ?
                        LifeHandler.getLastLives(newestId) : LifeHandler.getOlderLives(oldestId);

                if(lives.size() > 0) asyncResponse.resume(lives);
                else Thread.sleep(1000);
            }
        } catch (SQLException | InterruptedException e){
            logger.error(e,e);
            asyncResponse.resume(new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR));
        }

	}
}
