package de.anycook.api.backend;

import de.anycook.conf.Configuration;
import de.anycook.db.mysql.DBRecipe;
import de.anycook.sitemap.SiteMapGenerator;
import de.anycook.status.Status;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;


@Path("backend")
public class BackendApi {

    private final Logger logger;

    public BackendApi() {
        logger = LogManager.getLogger(getClass());
    }

    @GET
    @Path("status")
	@Produces(MediaType.APPLICATION_JSON)
	public Status getStatus(){
        try {
            return new Status();
        } catch (SQLException | DBRecipe.RecipeNotFoundException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("conf")
    @Produces(MediaType.APPLICATION_JSON)
    public Configuration getConfiguration(){
        return Configuration.getInstance();
    }

    @POST
    @Path("site_map")
    public void generateSiteMap(){
        try {
            SiteMapGenerator.generateAllSiteMaps();
        } catch (SQLException e) {
            logger.error(e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

}
