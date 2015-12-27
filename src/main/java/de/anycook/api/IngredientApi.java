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
import de.anycook.api.views.PublicView;
import de.anycook.db.mysql.DBIngredient;
import de.anycook.recipe.ingredient.Ingredient;
import de.anycook.recipe.ingredient.Ingredients;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;


@Path("/ingredient")
public class IngredientApi {

    private Logger logger = LogManager.getLogger(getClass());

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll(@QueryParam("parent") boolean parent,
                           @QueryParam("detailed") boolean detailed,
                           @Context Request request) {
        Annotation[] annotations = detailed ?
                                   new Annotation[]{PublicView.Factory.get()} : new Annotation[]{};

        try {
            List<Ingredient> ingredients = parent ?
                                           Ingredients.loadParents() : Ingredients.getAll();

            return Response.ok().entity(new GenericEntity<List<Ingredient>>(ingredients) {
            }, annotations)
                    .build();
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Number of ingredients
     */
    @GET
    @Path("number")
    @Produces(MediaType.APPLICATION_JSON)
    public Integer getNum() {
        try {
            return Ingredients.getTotal();
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("extract")
    @Produces(MediaType.APPLICATION_JSON)
    public Set<Ingredient> extractIngredients(@QueryParam("q") String query) {
        try {
            return Ingredients.searchNGram(query, 3);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("{ingredientName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Ingredient getIngredient(@PathParam("ingredientName") String ingredientName) {
        try {
            return Ingredient.init(ingredientName);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBIngredient.IngredientNotFoundException e) {
            logger.info(e, e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
}
