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

package de.anycook.api;

import com.google.common.collect.Multimap;

import de.anycook.api.util.MediaType;
import de.anycook.api.views.TasteNumView;
import de.anycook.search.Query;
import de.anycook.search.Search;
import de.anycook.search.SearchResult;
import de.anycook.session.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;


@Path("search")
public class SearchApi {

    private final Logger logger = LogManager.getLogger(getClass());

    @POST
    @TasteNumView
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResult search(Query query, @Context Session session) {
        try {
            int loginId = session.checkLoginWithoutException() ? session.getUser().getId() : -1;
            return Search.search(query, loginId);
        } catch (SQLException | IOException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Get possible types for a query. Exact query is matched.
     */
    @GET
    @Path("validate")
    @Produces(MediaType.APPLICATION_JSON)
    public Multimap<String, String> validateSearch(@QueryParam("q") String query) {
        if (query == null) {
            throw new WebApplicationException(400);
        }

        query = query.toLowerCase();
        try {
            return Search.validateSearch(query);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
