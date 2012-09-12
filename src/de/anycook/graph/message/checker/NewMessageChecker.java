package de.anycook.graph.message.checker;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;

import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import de.anycook.graph.message.checker.MessageChecker.MessageContextObject;
import de.anycook.messages.Messagesession;
import de.anycook.misc.JsonpBuilder;


public class NewMessageChecker extends Checker {
	private static Queue<NewMessageContextObject> contextQueue = new LinkedList<>();
	
	public static void addContext(int lastnum, int userid, int sessionid, AsyncContext context, String callback){
		NewMessageContextObject contextObject = new NewMessageContextObject(lastnum, userid, context, sessionid, callback);
		synchronized (NewMessageChecker.contextQueue) {
			contextQueue.add(contextObject);
			contextQueue.notify();
		}
		
		
	}
	
	private static NewMessageContextObject getContextObject(){
		synchronized (NewMessageChecker.contextQueue) {
			if(contextQueue.isEmpty()){
				try {
					contextQueue.wait();
				} catch (InterruptedException e) {
					return null;
				}
			}
			Logger.getLogger(NewMessageChecker.class).debug("get new context from queue");
			return contextQueue.poll();
		}
	}
	
	public NewMessageChecker() {
		super();	
	}
	
	@Override
	public void run() {
		while(running){
			NewMessageContextObject data = getContextObject();
			if(data == null)return;
			AsyncContext context = data.context;
			context.addListener(new CheckerListener());
			Messagesession session = 
					checkMessages(data.sessionid, data.lastnum, data.userid);
			if(session != null){
				logger.info("found new messages");
				ServletResponse response = context.getResponse();
				response.setContentType(MediaType.APPLICATION_JSON);
				String callback = data.callback;
				try {
					PrintWriter writer = response.getWriter();
					
					writer.print(JsonpBuilder.build(callback, session.toCompleteJSON()));
		            writer.flush();
		            writer.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
			context.complete();
		}
		
		stop();
	}
	
	private Messagesession checkMessages(int sessionid, int lastid, int userid){
		Messagesession session = null;
		timeout = false;
		int countdown = 20;
		do{
			if(session != null){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					stop();
					return null;
				}
			}
			session = Messagesession.getSession(sessionid, userid, lastid);
			countdown--;
		}while(session.isEmpty() && running && !timeout && countdown > 0);
		return session;
	}
	
	protected static class NewMessageContextObject extends MessageContextObject{
		public final int sessionid;
		
		public NewMessageContextObject(int lastnum, int userid, AsyncContext context, int sessionid, String callback) {
			super(lastnum, userid, context, callback);
			this.sessionid = sessionid;
		}
	}

}
