package de.anycook.graph.message.checker;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

import de.anycook.messages.Messagesession;
import de.anycook.misc.JsonpBuilder;


public class MessagesessionChecker extends Checker {
private static Queue<MessagesessionContextObject> contextQueue = new LinkedList<>();
	
	public static void addContext(Long lastChange, int userid, AsyncContext context, String callback){
		MessagesessionContextObject contextObject = 
				new MessagesessionContextObject(userid, lastChange, context, callback);
		synchronized (MessagesessionChecker.contextQueue) {
			contextQueue.add(contextObject);
			contextQueue.notify();
		}
		
		
	}
	
	private static MessagesessionContextObject getContextObject(){
		synchronized (MessagesessionChecker.contextQueue) {
			if(contextQueue.isEmpty()){
				try {
					contextQueue.wait();
				} catch (InterruptedException e) {
					return null;
				}
			}
			Logger.getLogger(MessagesessionChecker.class).debug("get new context from queue");
			return contextQueue.poll();
		}
	}
	
	@Override
	public void run() {
		while(running){
			MessagesessionContextObject data = getContextObject();
			if(data == null)return;
			AsyncContext context = data.context;
			context.addListener(new CheckerListener());
			List<Messagesession> sessions = null;
//			if(data.lastchange != null)	
			if(data.lastchange != null){
				Date changeDate = new Date(data.lastchange);
				sessions = checkMessages(changeDate, data.userid);
			}else
				sessions = Messagesession.getSessionsFromUser(data.userid);
			if(sessions != null){
				logger.info("found new messages");
				ServletResponse response = context.getResponse();
				response.setContentType(MediaType.APPLICATION_JSON);
				String callback = data.callback;
				try {
					PrintWriter writer = response.getWriter();
					writer.print(JsonpBuilder.build(callback, 
							JSONArray.toJSONString(sessions)));
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
	
	private List<Messagesession> checkMessages(Date lastchange, int userid){
		List<Messagesession> sessions = null;
		timeout = false;
		int countdown = 20;
		do{
			if(sessions != null){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					stop();
					return null;
				}
			}
			sessions = Messagesession.getSessionsFromUser(userid, lastchange);
			countdown--;
		}while(sessions.isEmpty() && running && !timeout && countdown > 0);
		return sessions;
	}
	
	protected static class MessagesessionContextObject extends ContextObject{
		public final int userid;
		public final Long lastchange;

		public MessagesessionContextObject(int userid, Long lastchange, AsyncContext context, String callback) {
			super(context, callback);
			this.userid = userid;
			this.lastchange = lastchange;
		}
		
	}

}
