package de.anycook.graph;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;

import anycook.messages.MessageChecker;
import anycook.messages.Messagesession;
import anycook.messages.NewMessageChecker;
import anycook.misc.DaemonThreadFactory;
import anycook.misc.JsonpBuilder;
import anycook.newsstream.News;
import anycook.session.Session;
import anycook.user.User;

@Path("/message")
public class MessageGraph {
	
	private static ExecutorService exec;
    private static final int numThreads = 30;
	
	public static void init() {
		exec = Executors.newCachedThreadPool(DaemonThreadFactory.singleton());
        for(int i = 0; i<numThreads/2; i++){
        	exec.execute(new NewMessageChecker());
        }
        
        for(int i = 0; i<numThreads/2; i++){
        	exec.execute(new MessageChecker());
        }
	}
	
	
	@GET
	public Response getNewsstream(@Context HttpServletRequest request,
			@QueryParam("callback") String callback){
		Session session = Session.init(request.getSession());
		List<News> newsstream = session.getUser().getNewsstream();
		return JsonpBuilder.buildResponse(callback, JSONArray.toJSONString(newsstream));
	}

	@GET
	@Path("{sessionid}")
	public void getMessagesession(@Context HttpServletRequest request,
			@PathParam("sessionid") Integer sessionid,
			@QueryParam("lastid") @DefaultValue("-1") Integer lastid,
			@QueryParam("callback") String callback){
		request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
		Session session = Session.init(request.getSession());
		User user = session.getUser();		
		AsyncContext async = request.startAsync();
		async.setTimeout(20000);
		NewMessageChecker.addContext(lastid, user.id, sessionid, async, callback);
		
		
		
	}
	
	@POST
	@Path("{sessionid}")
	public void writeMessage(@Context HttpServletRequest request,
			@PathParam("sessionid") Integer sessionid,
			@FormParam("message") String message){
		Session session = Session.init(request.getSession());
		User user = session.getUser();
		Messagesession.getSession(sessionid, user.id).newMessage(user.id, message);
	}
	
	@GET
	@Path("number")
	public void getMessageNumber(@Context HttpServletRequest request,
			@QueryParam("lastnum") Integer lastnum,
			@QueryParam("callback") String callback){
		request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
		Session session = Session.init(request.getSession(true));
		session.checkLogin();
		User user = session.getUser();
		AsyncContext async = request.startAsync();
		async.setTimeout(60000);
		MessageChecker.addContext(async, lastnum, user.id, callback);
		
		
	}



	public static void stop() {
		exec.shutdownNow();
		
	}
}
