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

import de.anycook.api.util.MediaType;
import de.anycook.discover.Discover;
import de.anycook.location.Location;
import de.anycook.recipe.Recipe;
import de.anycook.session.Session;
import de.anycook.user.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;


@Path("/discover")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoverApi {

    private final Logger logger = LogManager.getLogger(getClass());

    @Context
    private Session session;

    @GET
    public Discover.Recipes get(@DefaultValue("30") @QueryParam("recipeNumber") int recipeNumber){
        try {
            try {
                User user = session.getUser();
                return Discover.getDiscoverRecipes(recipeNumber, user.getId());
            } catch (WebApplicationException e) {
                return Discover.getDiscoverRecipes(recipeNumber);
            }
        } catch (SQLException e){
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

	@GET
	@Path("recommended")
	public List<Recipe> getDiscoverRecommended(
	        @DefaultValue("0") @QueryParam("offset") int offset,
	        @DefaultValue("30") @QueryParam("recipeNumber") int recipeNumber,
                                               @Context Session session){
        try {
            try {
                User user = session.getUser();
                return Discover.getRecommendedRecipes(offset, recipeNumber, user.getId());
            } catch (WebApplicationException e) {
                return Discover.getPopularRecipes(offset, recipeNumber, -1);
            }
        } catch (SQLException e){
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
	}

	@GET
	@Path("tasty")
    public List<Recipe> getDiscoverTasty(
            @DefaultValue("0") @QueryParam("offset") int offset,
            @DefaultValue("30") @QueryParam("recipeNumber") int recipeNumber){
        try {
            int loginId = session.checkLoginWithoutException() ? session.getUser().getId() : -1;
            return Discover.getTastyRecipes(offset, recipeNumber, loginId);
        } catch (SQLException e) {
            logger.error(e ,e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

	@GET
	@Path("new")
    public List<Recipe> getDiscoverNew(
            @DefaultValue("0") @QueryParam("offset") int offset,
            @DefaultValue("30") @QueryParam("recipeNumber") int recipeNumber){
        try {
            int loginId = session.checkLoginWithoutException() ? session.getUser().getId() : -1;
            return Discover.getNewestRecipes(offset, recipeNumber, loginId);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("near")
    public List<Recipe> getNearRecipes(
            @QueryParam("latitude") double latitude,
            @QueryParam("longitude") double longitude,
            @DefaultValue("10") @QueryParam("maxRadius") double maxRadius,
            @DefaultValue("30") @QueryParam("recipeNumber") int recipeNumber) {
        try {
            return Discover.getNearRecipes(new Location(latitude, longitude), maxRadius, recipeNumber);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }


}
