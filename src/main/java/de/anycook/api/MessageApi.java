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

import de.anycook.api.providers.MessageNumberProvider;
import de.anycook.api.providers.MessageProvider;
import de.anycook.api.providers.MessageSessionProvider;
import de.anycook.api.util.MediaType;
import de.anycook.db.mysql.DBMessage;
import de.anycook.messages.Message;
import de.anycook.messages.MessageSession;
import de.anycook.session.Session;
import de.anycook.user.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.ws.rs.Consumes;
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
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Path("/message")
public class MessageApi {

	private final Logger logger;

    @Context
    private Session session;

	/**
	 *
	 */
	public MessageApi() {
		logger = Logger.getLogger(getClass());
	}

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public void get(@Suspended final AsyncResponse asyncResponse, @QueryParam("lastChange") Long lastChange){
        asyncResponse.setTimeoutHandler(asyncResponse1 -> asyncResponse1.resume(Response.ok().build()));

        asyncResponse.setTimeout(5, TimeUnit.MINUTES);


        User user = session.getUser();

        if(lastChange == null){
            try {
                List<MessageSession> sessions = MessageSession.getSessionsFromUser(user.getId());
                GenericEntity<List<MessageSession>> entity = new GenericEntity<List<MessageSession>>(sessions){};
                asyncResponse.resume(entity);
            } catch (SQLException e) {
                logger.error(e, e);
                asyncResponse.resume(new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR));
            }
            return;
        }

        Date changeDate = new Date(lastChange);
        try{
            List<MessageSession> sessions = MessageSession.getSessionsFromUser(user.getId(), changeDate);
            GenericEntity<List<MessageSession>> entity = new GenericEntity<List<MessageSession>>(sessions){};
            if(!sessions.isEmpty()) asyncResponse.resume(entity);
            else MessageSessionProvider.INSTANCE.suspend(user.getId(), asyncResponse);
        } catch (SQLException | DBMessage.SessionNotFoundException e){
            logger.error(e,e);
            asyncResponse.resume(new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR));
        }

    }

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    public void newMessage(NewMessage message){
        if(message == null)
            throw new WebApplicationException(Response.Status.BAD_REQUEST);

		try {
			int userId = session.getUser().getId();
            message.recipients.add(userId);
			MessageSession.getSession(message.recipients).newMessage(userId, message.text);
		} catch ( SQLException | DBMessage.SessionNotFoundException e ) {
			logger.error(e, e);
			throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

    @GET
    @Path("number")
    @Produces(MediaType.APPLICATION_JSON)
    public void getMessageNumber(@Suspended AsyncResponse asyncResponse, @QueryParam("lastNum") int lastNumber){
        try{
            int userId = session.getUser().getId();

            int newMessageNum = MessageSession.getNewMessageNum(userId);
            if(newMessageNum == lastNumber){
                MessageNumberProvider.INSTANCE.suspend(userId, asyncResponse);
            } else{
                logger.info("return message num");
                asyncResponse.resume(newMessageNum);
            }
        } catch (SQLException e) {
            logger.error(e, e);
            asyncResponse.resume(new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR));
        }

    }

    @GET
    @Path("{sessionId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void getMessagesFromSession(@Suspended AsyncResponse asyncResponse, @PathParam("sessionId") int sessionId,
                                       @QueryParam("lastId") Integer lastId){
        int userId = session.getUser().getId();
        try {
            if(lastId == null) {
                asyncResponse.resume(MessageSession.getSession(sessionId, userId));
                return;
            }

            MessageSession messageSession = MessageSession.getSession(sessionId, userId, lastId);
            if(!messageSession.isEmpty()) asyncResponse.resume(messageSession);
            else MessageProvider.INSTANCE.suspend(sessionId, userId, asyncResponse);
        } catch (SQLException e) {
            logger.error(e, e);
            asyncResponse.resume(new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR));
        } catch (DBMessage.SessionNotFoundException e) {
            logger.warn(e, e);
            asyncResponse.resume(new WebApplicationException(Response.Status.BAD_REQUEST));
        }


    }

	@POST
	@Path("{sessionId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void answerSession(@PathParam("sessionId") int sessionId, String message){
		if(message == null){
			logger.info("text was null");
			throw new WebApplicationException(400);
		}

        try {
            int userId = session.getUser().getId();
            MessageSession.getSession(sessionId, userId).newMessage(userId, message);
        } catch ( SQLException | DBMessage.SessionNotFoundException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

	@PUT
	@Path("{sessionId}/{messageId}")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void readMessage(@PathParam("sessionId") int sessionId, @PathParam("messageId") int messageId){
        try {
            int userId = session.getUser().getId();
            Message.read(sessionId, messageId, userId);
            MessageNumberProvider.INSTANCE.wakeUpSuspended(userId);
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    public static class NewMessage {
        public List<Integer> recipients;
        public String text;

        @Override
        public String toString() {
            return String.format("{recipients : %s, text : %s}", StringUtils.join(recipients, ","), text);
        }
    }

}
