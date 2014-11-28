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
import de.anycook.db.drafts.RecipeDraftsStore;
import de.anycook.db.mysql.DBRecipe;
import de.anycook.drafts.RecipeDraft;
import de.anycook.newrecipe.DraftNumberProvider;
import de.anycook.recipe.Recipes;
import de.anycook.session.Session;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
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
	public List<RecipeDraft> get(){

		try(RecipeDraftsStore drafts = RecipeDraftsStore.getRecipeDraftStore()){
            return drafts.getDrafts(session.getUser().getId());
        } catch (Exception e){
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

	@PUT
	public String newDraft(@Context HttpServletRequest request){
		try(RecipeDraftsStore recipeDrafts = RecipeDraftsStore.getRecipeDraftStore()){
            return recipeDrafts.newDraft(session.getUser().getId());
        } catch (Exception e) {
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
        try(RecipeDraftsStore draftsStore = RecipeDraftsStore.getRecipeDraftStore()) {
            int newNum = draftsStore.countDrafts(userId);
            if (lastNum == null || newNum != lastNum) asyncResponse.resume(String.valueOf(newNum));
            else DraftNumberProvider.INSTANCE.suspend(userId, asyncResponse);
        } catch (Exception e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

	@PUT
	@Path("{recipeName}")
	public String initWithRecipe(@PathParam("recipeName") String recipeName,
                                 @FormParam("versionid") Integer versionId){
		if(recipeName == null) throw new WebApplicationException(400);

        try {
            int user_id = session.getUser().getId();

            return Recipes.initDraftWithRecipe(recipeName, versionId, user_id);
        } catch (IOException | SQLException e) {
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
	public RecipeDraft getDraft(@PathParam("id") String id){
        try (RecipeDraftsStore recipeDrafts = RecipeDraftsStore.getRecipeDraftStore()) {
            int userId = session.getUser().getId();
			return recipeDrafts.getDraft(id, userId);
		} catch (Exception e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

	@POST
	@Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
	public Response setData(RecipeDraft draft, @PathParam("id") String id){


		try (RecipeDraftsStore draftsStore = RecipeDraftsStore.getRecipeDraftStore()) {
			int userId = session.getUser().getId();
            draft.setUserId(userId);
			draftsStore.updateDraft(id, draft);
		} catch (Exception e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return Response.ok("true").build();
	}

	@DELETE
	@Path("{id}")
	public void remove(@PathParam("id") String id){
        try(RecipeDraftsStore draftsStore = RecipeDraftsStore.getRecipeDraftStore()){
            int userId = session.getUser().getId();
            draftsStore.deleteDraft(id, userId);
        } catch (Exception e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

    }

}
