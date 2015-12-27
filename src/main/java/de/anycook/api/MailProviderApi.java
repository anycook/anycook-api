package de.anycook.api;

import de.anycook.api.util.MediaType;
import de.anycook.db.mysql.DBMailProvider;
import de.anycook.mailprovider.MailProvider;
import de.anycook.mailprovider.MailProviders;
import de.anycook.session.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
@Path("/mailproviders")
@Produces(MediaType.APPLICATION_JSON)
public class MailProviderApi {

    private final Logger logger = LogManager.getLogger(getClass());

    @Context
    private Session session;

    @GET
    public List<MailProvider> getMailProviders() {
        try {
            return MailProviders.getMailProviders();
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("{shortName}")
    public MailProvider getMailProvider(@PathParam("shortName") String shortName) {
        try {
            return MailProviders.getMailProvider(shortName);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBMailProvider.ProviderNotFoundException e) {
            logger.debug(e);
            throw new WebApplicationException(Response.Status.NO_CONTENT);
        }
    }

    @PUT
    @Path("{shortName}")
    public void updateMailProvider(@PathParam("shortName") String shortName,
                                   MailProvider mailProvider) {
        session.checkAdminLogin();
        try {
            MailProviders.updateMailProvider(shortName, mailProvider);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @DELETE
    @Path("{shortName}")
    public void deleteMailProvider(@PathParam("shortName") String shortName) {
        session.checkAdminLogin();
        try {
            MailProviders.deleteMailProvider(shortName);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("domain/{domain}")
    public MailProvider getMailProviderByDomain(@PathParam("domain") String domain) {
        if (domain == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
        try {
            return MailProviders.getMailProviderForDomain(domain);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBMailProvider.ProviderNotFoundException e) {
            logger.debug(e);
            throw new WebApplicationException(Response.Status.NO_CONTENT);
        }
    }


}
