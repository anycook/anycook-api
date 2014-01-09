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
import de.anycook.recipe.category.Category;
import de.anycook.db.mysql.DBCategory;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


@Path("/category")
public class CategoryApi {

    private final Logger logger = Logger.getLogger(getClass());

	@SuppressWarnings("unchecked")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getAll(){
        try {
            return Category.getAll();
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	/**
	 * All categories ordered by order attribute in DB
	 * @return
	 */
	@Path("sorted")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Integer> getAllSorted(){
        try {
            return Category.getAllSorted();
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
	
	@Path("{categoryname}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Category getCategory(@PathParam("categoryname") String categoryname){
        try {
            return Category.init(categoryname);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBCategory.CategoryNotFoundException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
}
