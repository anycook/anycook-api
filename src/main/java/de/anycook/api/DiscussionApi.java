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
import com.google.common.base.Preconditions;
import de.anycook.api.util.MediaType;
import de.anycook.db.mysql.DBDiscussion;
import de.anycook.discussion.Discussion;
import de.anycook.api.providers.DiscussionProvider;
import de.anycook.session.Session;
import de.anycook.user.views.Views;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.container.TimeoutHandler;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;


@Path("discussion")
public class DiscussionApi {

    private final Logger logger = Logger.getLogger(getClass());
    @Context HttpHeaders hh;
    @Context HttpServletRequest request;

    @GET
    @Path("{recipeName}")
    @JsonView(Views.ResultView.class)
    @Produces(MediaType.APPLICATION_JSON)
    public void get(@Suspended final AsyncResponse asyncResponse, @PathParam("recipeName") final String recipeName,
                    @DefaultValue("-1") @QueryParam("lastid") final int lastId, @Context HttpServletRequest request){

        Session session = Session.init(request.getSession());
        int userId;
        try{
            session.checkLogin(request.getCookies());
            userId = session.getUser().getId();
        }catch(WebApplicationException e){
            userId = -1;
        } catch (IOException | SQLException e){
            logger.error(e);
            asyncResponse.resume(new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR));
            return;
        }

        asyncResponse.setTimeoutHandler(new TimeoutHandler() {
            @Override
            public void handleTimeout(AsyncResponse asyncResponse) {
                logger.info("reached timeout");
                asyncResponse.resume(Response.ok().build());
            }
        });
        asyncResponse.setTimeout(5, TimeUnit.MINUTES);

        Discussion newDiscussion;
        try(DBDiscussion dbDiscussion = new DBDiscussion()) {
            newDiscussion = dbDiscussion.getDiscussion(recipeName, lastId, userId);
        } catch (SQLException e) {
            logger.error(e, e);
            if(asyncResponse.isSuspended())
                asyncResponse.resume(new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR));
            return;
        }

        if(newDiscussion.size() > 0){
                logger.debug("found new disscussion elements for " + recipeName);
                asyncResponse.resume(Response.ok(newDiscussion, MediaType.APPLICATION_JSON_TYPE).build());
        }
        else DiscussionProvider.INSTANCE.suspend(recipeName, userId, lastId, asyncResponse);
    }

	@POST
	@Path("{recipeName}")
	public Response discuss(@PathParam("recipeName") String recipe,
			@FormParam("comment") String comment, @FormParam("pid") Integer pid){
		
		Preconditions.checkNotNull(comment);

        try {
            Session shandler = Session.init(request.getSession());
            shandler.checkLogin(hh.getCookies());
            int userid = shandler.getUser().getId();


            if(pid == null) Discussion.discuss(comment, userid, recipe);
            else Discussion.answer(comment, pid, userid, recipe);
        } catch (IOException | SQLException e){
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        DiscussionProvider.INSTANCE.wakeUpSuspended(recipe);
		return Response.ok("true").build();
	}
	
	@PUT
	@Path("like/{recipeName}/{id}")
	public Response like(@PathParam("recipeName") String recipe,
			@PathParam("id") int id){
        try{
            Session shandler = Session.init(request.getSession());
            shandler.checkLogin(hh.getCookies());
            int userid = shandler.getUser().getId();

            Discussion.like(userid, recipe, id);
        } catch (IOException | SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return Response.ok("true").build();
	}
	
	@DELETE
	@Path("like/{recipeName}/{id}")
	public Response unlike(@PathParam("recipeName") String recipe,
			@PathParam("id") int id,
			@Context HttpHeaders hh,
			@Context HttpServletRequest request){
        try {
            Session shandler = Session.init(request.getSession());
            shandler.checkLogin(hh.getCookies());
            int userid = shandler.getUser().getId();


            Discussion.unlike(userid, recipe, id);
        } catch (IOException | SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        return Response.ok("true").build();
	}
}
