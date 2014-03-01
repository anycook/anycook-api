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

import com.fasterxml.jackson.annotation.JsonView;
import de.anycook.api.util.MediaType;
import de.anycook.discover.Discover;
import de.anycook.recipe.Recipe;
import de.anycook.recipe.Views;
import de.anycook.session.Session;
import de.anycook.user.User;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


@Path("/discover")
public class DiscoverApi {

    private final Logger logger = Logger.getLogger(getClass());
	
	@GET
	@Path("recommended")
	@Produces(MediaType.APPLICATION_JSON)
    @JsonView(Views.ResultRecipeView.class)
	public List<Recipe> getDiscoverRecommended(@Context HttpHeaders hh,
			@Context HttpServletRequest request,
			@DefaultValue("30") @QueryParam("recipeNumber") int recipeNumber){
		Session session = Session.init(request.getSession());
        try {
            try {
                session.checkLogin(hh.getCookies());
                User user = session.getUser();
                return Discover.getRecommendedRecipes(recipeNumber, user.getId());
            } catch (WebApplicationException e) {
                return Discover.getPopularRecipes(recipeNumber);
            }
        } catch (IOException | SQLException e){
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
	}
	
	@GET
	@Path("tasty")
	@Produces(MediaType.APPLICATION_JSON)
    @JsonView(Views.ResultRecipeView.class)
    public List<Recipe> getDiscoverTasty(
			@DefaultValue("30") @QueryParam("recipeNumber") int recipeNumber){
        try {
            return Discover.getTastyRecipes(recipeNumber);
        } catch (SQLException e) {
            logger.error(e ,e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	@GET
	@Path("new")
	@Produces(MediaType.APPLICATION_JSON)
    @JsonView(Views.ResultRecipeView.class)
    public List<Recipe> getDiscoverNew(
			@DefaultValue("30") @QueryParam("recipeNumber") int recipeNumber){
        try {
            return Discover.getNewestRecipes(recipeNumber);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
