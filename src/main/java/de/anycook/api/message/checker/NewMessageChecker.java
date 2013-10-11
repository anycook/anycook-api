package de.anycook.api.message.checker;

import de.anycook.messages.MessageSession;
import de.anycook.api.message.checker.MessageChecker.MessageContextObject;
import org.apache.log4j.Logger;

import javax.servlet.AsyncContext;
import javax.ws.rs.container.AsyncResponse;
import java.util.LinkedList;
import java.util.Queue;


public class NewMessageChecker extends Checker {
	private final static Queue<NewMessageContextObject> contextQueue = new LinkedList<>();
	
	public static void addContext(int lastNum, int userId, int sessionId, AsyncResponse asyncResponse){
		NewMessageContextObject contextObject = new NewMessageContextObject(lastNum, userId, asyncResponse, sessionId);
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
			Logger.getLogger(NewMessageChecker.class).debug("get new response from queue");
			return contextQueue.poll();
		}
	}
	
	public NewMessageChecker() {
		super();	
	}
	
	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()){
			NewMessageContextObject data = getContextObject();
			if(data == null)return;
			AsyncResponse asyncResponse = data.response;
			MessageSession session =
					checkMessages(data.sessionId, data.lastNumber, data.userId);
			if(session != null){
				logger.info("found new messages");
                asyncResponse.resume(session);
            }
		}
	}
	
	private MessageSession checkMessages(int sessionid, int lastid, int userid){
		MessageSession session = null;
		timeout = false;
		int countdown = 20;
		do{
			if(session != null){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return null;
				}
			}
			session = MessageSession.getSession(sessionid, userid, lastid);
			countdown--;
		}while(session.isEmpty() && !timeout && countdown > 0);
		return session;
	}
	
	protected static class NewMessageContextObject extends MessageContextObject{
		public final int sessionId;

		public NewMessageContextObject(int lastnum, int userid, AsyncResponse asyncResponse, int sessionId) {
			super(lastnum, userid, asyncResponse);
			this.sessionId = sessionId;
		}
	}

}
