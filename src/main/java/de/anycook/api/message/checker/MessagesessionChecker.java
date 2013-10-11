package de.anycook.api.message.checker;

import de.anycook.messages.MessageSession;
import de.anycook.utils.JsonpBuilder;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;


public class MessagesessionChecker extends Checker {
    private static final Queue<MessagesessionContextObject> contextQueue = new LinkedList<>();
	
	public static void addContext(Long lastChange, int userid, AsyncResponse response){
		MessagesessionContextObject contextObject = 
				new MessagesessionContextObject(userid, lastChange, response);
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
			Logger.getLogger(MessagesessionChecker.class).debug("get new response from queue");
			return contextQueue.poll();
		}
	}
	
	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()){
			MessagesessionContextObject data = getContextObject();
			if(data == null)continue;
			AsyncResponse response = data.response;

            ResponseListener responseListener = new ResponseListener();
            response.setTimeout(1, TimeUnit.MINUTES);
			response.register(responseListener);
            response.setTimeoutHandler(responseListener);




            try {
                List<MessageSession> sessions;

                if(data.lastchange != null){
                    Date changeDate = new Date(data.lastchange);
                    sessions = checkMessages(changeDate, data.userid);
                }else{
                    sessions = MessageSession.getSessionsFromUser(data.userid);
                }

                if(sessions != null){
                    logger.info("found new messages");
                    response.resume(sessions);
                }
            } catch (SQLException e) {
                logger.error(e);
                response.resume(new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR));
            }

		}
	}
	
	private List<MessageSession> checkMessages(Date lastchange, int userid) throws SQLException {
		List<MessageSession> sessions = null;
		timeout = false;
		int countdown = 20;
		do{
			if(sessions != null){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return null;
				}
			}
			sessions = MessageSession.getSessionsFromUser(userid, lastchange);
			countdown--;
		}while(sessions.isEmpty() && !timeout && countdown > 0);
		return sessions;
	}
	
	protected static class MessagesessionContextObject extends ContextObject{
		public final int userid;
		public final Long lastchange;

		public MessagesessionContextObject(int userid, Long lastchange, AsyncResponse response) {
			super(response);
			this.userid = userid;
			this.lastchange = lastchange;
		}
		
	}

}
