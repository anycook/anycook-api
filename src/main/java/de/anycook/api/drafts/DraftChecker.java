package de.anycook.api.drafts;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.container.AsyncResponse;

import org.apache.log4j.Logger;

import de.anycook.db.mongo.recipedrafts.RecipeDrafts;
import de.anycook.messages.Checker;

public class DraftChecker extends Checker {
	private final static Queue<DraftContextObject> contextQueue = new LinkedList<>();
	
	public synchronized static void addContext(AsyncResponse asyncResponse, int lastNum, int userId){
		DraftContextObject contextObject = new DraftContextObject(lastNum, userId, asyncResponse);
		synchronized (DraftChecker.contextQueue) {
			contextQueue.add(contextObject);
			contextQueue.notify();
		}
		
	}
	
	private static DraftContextObject getContext(){
		synchronized (DraftChecker.contextQueue) {
			if(contextQueue.isEmpty()){
				try {
					contextQueue.wait();
				} catch (InterruptedException e) {
					return null;					
				}
			}
			Logger.getLogger(DraftChecker.class).debug("get new response from queue");
			return contextQueue.poll();
		}
		
	}
	
	public DraftChecker() {
		super();
	}
	
	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()){
			DraftContextObject data = getContext();
			if(data == null)
				return;
			
			int lastNum = data.lastNum;
			AsyncResponse asyncResponse = data.response;
            ResponseListener listener = new ResponseListener();
            asyncResponse.setTimeout(1, TimeUnit.MINUTES);
            asyncResponse.register(listener);
            asyncResponse.setTimeoutHandler(listener);

            int userId = data.userId;
			Integer newNum = getNewNum(userId, lastNum);

            if(newNum!=null){
				logger.info("found new number:" + newNum);
				asyncResponse.resume(newNum);
			}
		}
	}
	
	private Integer getNewNum(int userid, int lastnum){
		RecipeDrafts drafts = new RecipeDrafts();
		
		timeout = false;
//		logger.debug("lastNumber:"+lastNumber);
		int countdown = 12;
		int newnum = -1;
		do{
//			logger.debug("newnum: "+newnum);
			if(newnum>=0){
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();					
					return null;
				}
			}
			newnum = drafts.count(userid);
			countdown--;
		}while(newnum==lastnum && !timeout && countdown > 0);
		return newnum;
	}
	
	protected static class DraftContextObject extends ContextObject{
		public final int lastNum;
		public final int userId;
		
		public DraftContextObject(int lastNum, int userId, AsyncResponse asyncResponse) {
			super(asyncResponse);
			this.lastNum = lastNum;
			this.userId = userId;
		}
	}
}

