package de.anycook.api;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.DBObject;
import de.anycook.api.util.MediaType;
import de.anycook.db.mysql.DBRecipe;
import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ManagedAsync;


import de.anycook.db.mongo.recipedrafts.RecipeDrafts;
import de.anycook.api.drafts.DraftChecker;
import de.anycook.recipe.Recipe;
import de.anycook.session.Session;
import de.anycook.utils.DaemonThreadFactory;

@Path("/drafts")
public class DraftApi {
	private static ExecutorService exec;
    private static final int numThreads = 10;
	
	public static void init() {
		exec = Executors.newCachedThreadPool(DaemonThreadFactory.singleton());
        for(int i = 0; i<numThreads; i++){
        	exec.execute(new DraftChecker());
        }
	}
	
	public static void destroyThreadPool() {
		exec.shutdownNow();
	}

    private Logger logger;
    @Context HttpHeaders hh;
    @Context HttpServletRequest request;

    public DraftApi(){
		logger = Logger.getLogger(getClass());
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Map<String, Object>> get(@Context HttpHeaders hh,
			@Context HttpServletRequest request){
		Session session = Session.init(request.getSession());

		try(RecipeDrafts drafts = new RecipeDrafts()){
            session.checkLogin(hh.getCookies());
            return drafts.getAll(session.getUser().getId());
        } catch (IOException e){
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
	}
	
	@PUT
	public String newDraft(@Context HttpHeaders hh,
			@Context HttpServletRequest request){
		try(RecipeDrafts recipeDrafts = new RecipeDrafts()){
            Session session = Session.init(request.getSession());
            session.checkLogin(hh.getCookies());
            return recipeDrafts.newDraft(session.getUser().getId());
        } catch (IOException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

    }

    @GET
    @Path("num")
    @ManagedAsync
    @Produces(MediaType.APPLICATION_JSON)
    public void getDraftNum(@QueryParam("lastNum") Integer lastNum,
                            @Suspended AsyncResponse asyncResponse){
        Session session = Session.init(request.getSession(true));
        int userId = session.getUser().getId();
        RecipeDrafts drafts = new RecipeDrafts();

        try {
            while (!asyncResponse.isCancelled() && !asyncResponse.isDone()){
                int newNum = drafts.count(userId);
                if(lastNum == null || newNum != lastNum){
                    asyncResponse.resume(newNum);
                } else {
                    Thread.sleep(1500);
                }
            }
        } catch (InterruptedException e) {
            logger.error(e, e);
            asyncResponse.resume(new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR));
        }
    }
	
	@PUT
	@Path("{recipeName}")
	public String initWithRecipe(@Context HttpHeaders hh,
			@Context HttpServletRequest request,
			@PathParam("recipeName") String recipeName,
			@FormParam("versionid") Integer versionid){
		if(recipeName == null) throw new WebApplicationException(400);

        try {
            Session session = Session.init(request.getSession());
            session.checkLogin(hh.getCookies());
            int user_id = session.getUser().getId();

            return Recipe.initDraftWithRecipe(recipeName, versionid, user_id);
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
	public Map getDraft(@PathParam("id") String draft_id,
			@Context HttpHeaders hh,
			@Context HttpServletRequest request){
        try (RecipeDrafts recipeDrafts = new RecipeDrafts()) {

            Session session = Session.init(request.getSession());
            session.checkLogin(hh.getCookies());
            int userId = session.getUser().getId();
		

			return recipeDrafts.loadDraft(draft_id, userId).toMap();
		} catch (IOException e){
            logger.error(e,e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
	}
	
	@POST
	@Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
	public Response setData(Map<String, Object> draftData,@PathParam("id") String draft_id,
			@Context HttpHeaders hh, @Context HttpServletRequest request){

		try (RecipeDrafts drafts = new RecipeDrafts()) {
            Session session = Session.init(request.getSession());
            session.checkLogin(hh.getCookies());

			int userId = session.getUser().getId();
			drafts.update(draftData, userId, draft_id);
			
		} catch (IOException e){
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
		
		return Response.ok("true").build();
	}
	
	@DELETE
	@Path("{id}")
	public void remove(@PathParam("id") String draft_id,
			@Context HttpHeaders hh,
			@Context HttpServletRequest request){
		Session session = Session.init(request.getSession());
        try(RecipeDrafts recipeDrafts = new RecipeDrafts()){
            session.checkLogin(hh.getCookies());

            int user_id = session.getUser().getId();
            recipeDrafts.remove(user_id, draft_id);
        } catch (IOException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

    }
	
}
