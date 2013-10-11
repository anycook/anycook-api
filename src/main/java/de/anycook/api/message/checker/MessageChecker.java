package de.anycook.api.message.checker;

import de.anycook.db.mysql.DBMessage;
import org.apache.log4j.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;


public class MessageChecker extends Checker {
	private final static Queue<MessageContextObject> contextQueue = new LinkedList<>();
	
	public synchronized static void addContext(AsyncResponse asyncResponse, int lastNumber, int userId){
		MessageContextObject contextObject = new MessageContextObject(lastNumber, userId, asyncResponse);
		synchronized (MessageChecker.contextQueue) {
			contextQueue.add(contextObject);
			contextQueue.notify();
		}
		
	}
	
	private static MessageContextObject getContext(){
		synchronized (MessageChecker.contextQueue) {
			if(contextQueue.isEmpty()){
				try {
					contextQueue.wait();
				} catch (InterruptedException e) {
					return null;					
				}
			}
			Logger.getLogger(MessageChecker.class).debug("get new response from queue");
			return contextQueue.poll();
		}
		
	}
	
	public MessageChecker() {
		super();
	}
	
	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()){
			MessageContextObject data = getContext();
			if(data == null)
				return;
			
			int lastNumber = data.lastNumber;

            AsyncResponse response = data.response;
            ResponseListener responseListener = new ResponseListener();
            response.setTimeout(1, TimeUnit.MINUTES);
            response.register(responseListener);
            response.setTimeoutHandler(responseListener);

			int userId = data.userId;
            try{
                Integer newMessages = getNewNum(userId, lastNumber);
                if(newMessages!=null){
                    logger.info("found new number:"+newMessages);
                    response.resume(newMessages);
                }
            } catch (SQLException e){
                logger.error(e);
                response.resume(new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR));
            }
		}
	}
	
	private Integer getNewNum(int userid, int lastnum) throws SQLException {
        try(DBMessage dbmessage = new DBMessage()){
            timeout = false;
            int countdown = 12;
            int newNumber = -1;
            do{
                if(newNumber>=0){
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return null;
                    }
                }
                newNumber = dbmessage.getNewMessageNum(userid);
                countdown--;
            }while(newNumber==lastnum && !timeout && countdown > 0);

            return newNumber;
        }


	}
	
	protected static class MessageContextObject extends ContextObject{
		public final int lastNumber;
		public final int userId;
		
		public MessageContextObject(int lastNumber, int userId, AsyncResponse asyncResponse) {
			super(asyncResponse);
			this.lastNumber = lastNumber;
			this.userId = userId;
		}
	}
}
