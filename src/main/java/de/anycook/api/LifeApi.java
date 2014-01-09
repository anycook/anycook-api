/*
 * This file is part of anycook. The new internet cookbook
 * Copyright (C) 2014 Jan Graßegger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see [http://www.gnu.org/licenses/].
 */

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
import javax.ws.rs.core.Response;

import de.anycook.api.util.MediaType;
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
public class LifeApi {
    private final Logger logger = Logger.getLogger(getClass());

	@GET
    @ManagedAsync
	@Produces(MediaType.APPLICATION_JSON)
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
