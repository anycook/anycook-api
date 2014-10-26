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

import de.anycook.api.providers.LifeProvider;
import de.anycook.api.util.MediaType;
import de.anycook.news.life.Life;
import de.anycook.news.life.Lifes;
import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ManagedAsync;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Graph for lifes stream
 *
 * @author Jan Graßegger <jan@anycook.de>
 */
@Path("/life")
public class LifeApi {
    private final Logger logger = Logger.getLogger(getClass());

    @GET
    @ManagedAsync
    @Produces(MediaType.APPLICATION_JSON)
    public void getLives(@Suspended final AsyncResponse asyncResponse,
                         @DefaultValue("0") @QueryParam("newestid") final int newestId,
                         @QueryParam("oldestid") final Integer oldestId) {

        asyncResponse.setTimeoutHandler(asyncResponse1 -> {
            logger.info("reached timeout");
            asyncResponse1.resume(Response.ok().build());
        });

        asyncResponse.setTimeout(5, TimeUnit.MINUTES);

        try {
            List<Life> lives = oldestId == null ?
                    Lifes.getLastLives(newestId) : Lifes.getOlderLives(oldestId);

            if (lives.size() > 0) {
                final GenericEntity<List<Life>> entity = new GenericEntity<List<Life>>(lives) {
                };
                asyncResponse.resume(entity);
            } else LifeProvider.suspend(asyncResponse);

        } catch (SQLException e) {
            logger.error(e, e);
            asyncResponse.resume(new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR));
        }

    }
}
