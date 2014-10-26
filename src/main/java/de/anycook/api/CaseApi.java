package de.anycook.api;

import de.anycook.api.util.MediaType;
import de.anycook.db.mysql.DBLive;
import de.anycook.news.Case;
import de.anycook.news.Cases;
import de.anycook.session.Session;
import org.apache.log4j.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
@Path("/case")
public class CaseApi {
    private final Logger logger = Logger.getLogger(getClass());

    @Context
    private Session session;

    @GET
    public List<Case> getCases() {
        try {
            return Cases.getCases();
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addCase(Case cAse) {
        session.checkAdminLogin();
        try {
            String id = Cases.newCase(cAse);
            return Response.created(new URI("/case/"+id)).build();
        } catch (SQLException | URISyntaxException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("{name}")
    public Case getCase(@PathParam("name") String name) {
        try {
            return Cases.getCase(name);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBLive.CaseNotFoundException e) {
            logger.warn(e, e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @PUT
    @Path("{name}")
    public void updateCase(@PathParam("name") String name, Case caze) {
        session.checkAdminLogin();
        try {
            Cases.updateCase(name, caze.getSyntax());
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @DELETE
    @Path("{name}")
    public void deleteCase(@PathParam("name") String name) {
        session.checkAdminLogin();
        try {
            Cases.deleteCase(name);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}


