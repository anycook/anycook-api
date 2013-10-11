package de.anycook.api;

import com.google.common.base.Preconditions;
import de.anycook.api.discussion.checker.NewDiscussionChecker;
import de.anycook.discussion.Discussion;
import de.anycook.session.Session;
import de.anycook.utils.DaemonThreadFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Path("discussion")
public class DiscussionGraph {

    private static ExecutorService exec;
    private static final int numThreads = 20;

    public static void init() {
        exec = Executors.newCachedThreadPool(DaemonThreadFactory.singleton());
        for(int i = 0; i<numThreads; i++){
            exec.execute(new NewDiscussionChecker());
        }
    }

    public static void destroyThreadPool() {
        exec.shutdownNow();
    }

    @Context HttpHeaders hh;
    @Context HttpServletRequest request;


    @GET
    @Path("{recipeName}")
    public void get(@PathParam("recipeName") String recipeName, @QueryParam("lastid") Integer lastId,
                    @Suspended AsyncResponse asyncResponse){
        Session session = Session.init(request.getSession());
        int userId;
        try{
            session.checkLogin(request.getCookies());
            userId = session.getUser().getId();
        }catch(WebApplicationException e){
            userId = -1;
        }

        recipeName = recipeName.toLowerCase();
        NewDiscussionChecker.addContext(asyncResponse, recipeName, lastId, userId);
    }
	
	@POST
	@Path("{recipeName}")
	public Response discuss(@PathParam("recipeName") String recipe,
			@FormParam("comment") String comment, @FormParam("pid") Integer pid){
		
		Preconditions.checkNotNull(comment);
		
		Session shandler = Session.init(request.getSession());
		shandler.checkLogin(hh.getCookies());
		int userid = shandler.getUser().getId();
		if(pid == null) Discussion.discuss(comment, userid, recipe);
		else Discussion.answer(comment, pid, userid, recipe);			

		return Response.ok("true").build();
	}
	
	@PUT
	@Path("like/{recipeName}/{id}")
	public Response like(@PathParam("recipeName") String recipe,
			@PathParam("id") int id){
		Session shandler = Session.init(request.getSession());
		shandler.checkLogin(hh.getCookies());
		int userid = shandler.getUser().getId();
		
		Discussion.like(userid, recipe, id);
		
		return Response.ok("true").build();		
	}
	
	@DELETE
	@Path("like/{recipeName}/{id}")
	public Response unlike(@PathParam("recipeName") String recipe,
			@PathParam("id") int id,
			@Context HttpHeaders hh,
			@Context HttpServletRequest request){
		Session shandler = Session.init(request.getSession());
		shandler.checkLogin(hh.getCookies());
		int userid = shandler.getUser().getId();
		
		Discussion.unlike(userid, recipe, id);
		
		return Response.ok("true").build();		
	}
}
