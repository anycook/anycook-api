package de.anycook.graph;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import com.google.common.base.Preconditions;

import de.anycook.discussion.Discussion;
import de.anycook.session.Session;

@Path("discussion")
public class DiscussionGraph {
	
	@POST
	@Path("{recipename}")
	public Response discuss(@PathParam("recipename") String recipe,
			@FormParam("comment") String comment, @FormParam("pid") Integer pid,
			@Context HttpHeaders hh,
			@Context HttpServletRequest request){
		
		Preconditions.checkNotNull(comment);
		
		Session shandler = Session.init(request.getSession());
		shandler.checkLogin(hh.getCookies());
		int userid = shandler.getUser().getId();
		if(pid == null) Discussion.discuss(comment, userid, recipe);
		else Discussion.answer(comment, pid, userid, recipe);			

		return Response.ok("true").build();
	}
	
	@PUT
	@Path("like/{recipename}/{id}")
	public Response like(@PathParam("recipename") String recipe,
			@PathParam("id") int id,
			@Context HttpHeaders hh,
			@Context HttpServletRequest request){
		Session shandler = Session.init(request.getSession());
		shandler.checkLogin(hh.getCookies());
		int userid = shandler.getUser().getId();
		
		Discussion.like(userid, recipe, id);
		
		return Response.ok("true").build();		
	}
	
	@DELETE
	@Path("like/{recipename}/{id}")
	public Response unlike(@PathParam("recipename") String recipe,
			@PathParam("id") int id,
			@Context HttpHeaders hh,
			@Context HttpServletRequest request){
		Session shandler = Session.init(request.getSession());
		shandler.checkLogin(hh.getCookies());
		int userid = shandler.getUser().getId();
		
		Discussion.unlike(userid, recipe, userid);
		
		return Response.ok("true").build();		
	}
}
