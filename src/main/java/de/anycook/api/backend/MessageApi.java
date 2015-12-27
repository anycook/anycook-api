package de.anycook.api.backend;

import de.anycook.db.mysql.DBMessage;
import de.anycook.messages.MessageSession;
import de.anycook.api.providers.MessageSessionProvider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
@Path("backend/message")
public class MessageApi {
    @Context private HttpServletRequest req;
    private final Logger logger;

    public MessageApi() {
        logger = LogManager.getLogger(getClass());
    }

    @GET
    public void get(@Suspended final AsyncResponse asyncResponse){
        MessageSessionProvider.INSTANCE.suspend(0, asyncResponse);
    }

    @PUT
    @Path("{sessionId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public void answerSession(@PathParam("sessionId") int sessionId,
                              @FormParam("message") String message){
        if(message == null){
            logger.info("message was null");
            throw new WebApplicationException(400);
        }

        try {
            MessageSession.getSession(sessionId, 0).newMessage(0, message);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBMessage.SessionNotFoundException e) {
            logger.warn(e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }


}
