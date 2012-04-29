package de.anycook.graph;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.Path;

import de.anycook.messages.checker.MessageChecker;
import de.anycook.messages.checker.MessagesessionChecker;
import de.anycook.messages.checker.NewMessageChecker;
import de.anycook.misc.DaemonThreadFactory;

@Path("/message")
public class MessageGraph  {
	
	private static ExecutorService exec;
    private static final int numThreads = 30;
	
	public static void init() {
		exec = Executors.newCachedThreadPool(DaemonThreadFactory.singleton());
        for(int i = 0; i<numThreads/3; i++){
        	exec.execute(new NewMessageChecker());
        	exec.execute(new MessageChecker());
        	exec.execute(new MessagesessionChecker());
        }
	}
	
	
//	@GET
//	public void getMessageStream(@Context HttpServletRequest request,
//			@QueryParam("lastchange") String lastchange,
//			@QueryParam("callback") String callback){
//		
//		Date lastChange = null;
//		if(lastchange != null)
//			lastChange = DateParser.parseDateTime(lastchange);		
//		Session session = Session.init(request.getSession());
//		User user = session.getUser();		
//		AsyncContext async = request.startAsync();
//		async.setTimeout(20000);
//		MessagesessionChecker.addContext(lastChange, user.id, async, callback);
//	}

//	@GET
//	@Path("{sessionid}")
//	public void getMessagesession(@Context HttpServletRequest request,
//			@PathParam("sessionid") Integer sessionid,
//			@QueryParam("lastid") @DefaultValue("-1") Integer lastid,
//			@QueryParam("callback") String callback){
//		Session session = Session.init(request.getSession());
//		User user = session.getUser();		
//		AsyncContext async = request.startAsync();
//		async.setTimeout(20000);
//		NewMessageChecker.addContext(lastid, user.id, sessionid, async, callback);
//		
//		
//		
//	}
//	
//	@POST
//	@Path("{sessionid}")
//	public void writeMessage(@Context HttpServletRequest request,
//			@PathParam("sessionid") Integer sessionid,
//			@FormParam("message") String message){
//		Session session = Session.init(request.getSession());
//		User user = session.getUser();
//		Messagesession.getSession(sessionid, user.id).newMessage(user.id, message);
//	}
//	
//	@GET
//	@Path("number")
//	public void getMessageNumber(@Context HttpServletRequest request,
//			@QueryParam("lastnum") Integer lastnum,
//			@QueryParam("callback") String callback){
//		Session session = Session.init(request.getSession(true));
//		session.checkLogin();
//		User user = session.getUser();
//		AsyncContext async = request.startAsync();
//		async.setTimeout(60000);
//		MessageChecker.addContext(async, lastnum, user.id, callback);
//		
//		
//	}



	public static void stop() {
		exec.shutdownNow();
		
	}
}
