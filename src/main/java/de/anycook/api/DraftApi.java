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
import de.anycook.db.mongo.RecipeDrafts;
import de.anycook.db.mysql.DBRecipe;
import de.anycook.drafts.DraftRecipe;
import de.anycook.newrecipe.DraftNumberProvider;
import de.anycook.recipe.Recipes;
import de.anycook.session.Session;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Path("/drafts")
public class DraftApi {

    private Logger logger;
    @Context
    private Session session;

    public DraftApi(){
		logger = Logger.getLogger(getClass());
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<DraftRecipe> get(){

		try(RecipeDrafts drafts = new RecipeDrafts()){
            return drafts.getAll(session.getUser().getId());
        } catch (IOException e){
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
	}
	
	@PUT
	public String newDraft(@Context HttpServletRequest request){
		try(RecipeDrafts recipeDrafts = new RecipeDrafts()){
            return recipeDrafts.newDraft(session.getUser().getId());
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

    }

    @GET
    @Path("num")
    @Produces(MediaType.APPLICATION_JSON)
    public void getDraftNum(@QueryParam("lastNum") Integer lastNum,
                            @Suspended AsyncResponse asyncResponse){
        int userId = session.getUser().getId();
        RecipeDrafts drafts = new RecipeDrafts();

        int newNum = drafts.count(userId);
        if(lastNum == null || newNum != lastNum) asyncResponse.resume(String.valueOf(newNum));
        else DraftNumberProvider.INSTANCE.suspend(userId, asyncResponse);
    }
	
	@PUT
	@Path("{recipeName}")
	public String initWithRecipe(@PathParam("recipeName") String recipeName,
                                 @FormParam("versionid") Integer versionid){
		if(recipeName == null) throw new WebApplicationException(400);

        try {
            int user_id = session.getUser().getId();

            return Recipes.initDraftWithRecipe(recipeName, versionid, user_id);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBRecipe.RecipeNotFoundException e) {
            logger.warn(e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public DraftRecipe getDraft(@PathParam("id") String draft_id){
        try (RecipeDrafts recipeDrafts = new RecipeDrafts()) {
            int userId = session.getUser().getId();
			return recipeDrafts.loadDraft(draft_id, userId);
		}
    }
	
	@POST
	@Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
	public Response setData(DraftRecipe draftData, @PathParam("id") String draft_id){

		try (RecipeDrafts drafts = new RecipeDrafts()) {
			int userId = session.getUser().getId();
			drafts.update(draftData, userId, draft_id);
		}

        return Response.ok("true").build();
	}
	
	@DELETE
	@Path("{id}")
	public void remove(@PathParam("id") String draft_id){
        try(RecipeDrafts recipeDrafts = new RecipeDrafts()){
            int user_id = session.getUser().getId();
            recipeDrafts.remove(user_id, draft_id);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

    }
	
}
