package de.anycook.graph.drafts;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;

import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import de.anycook.db.mongo.RecipeDrafts;
import de.anycook.messages.checker.Checker;
import de.anycook.utils.JsonpBuilder;

public class DraftChecker extends Checker {
	private static Queue<DraftContextObject> contextQueue = new LinkedList<>();
	
	public synchronized static void addContext(AsyncContext context, int lastnum, int userid, String callback){
		DraftContextObject contextObject = new DraftContextObject(lastnum, userid, context, callback);
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
			Logger.getLogger(DraftChecker.class).debug("get new context from queue");
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
			
			int lastnum = data.lastnum;
			AsyncContext context = data.context;
			context.addListener(new CheckerListener());
			int userid = data.userid;
			Integer newnum = getNewNum(userid, lastnum);
			String callback = data.callback;
			if(newnum!=null){
				logger.info("found new number:"+newnum);
				ServletResponse response = context.getResponse();
				response.setContentType(MediaType.APPLICATION_JSON);
				PrintWriter writer;
				try {
					writer = response.getWriter();
					writer.print(JsonpBuilder.build(callback, newnum));
		            writer.flush();
		            writer.close();
				} catch (IOException e) {
					logger.error(e);
				}
				
			}
//			context.dispatch();
			context.complete();
		}
	}
	
	private Integer getNewNum(int userid, int lastnum){
		RecipeDrafts drafts = new RecipeDrafts();
		
		timeout = false;
//		logger.debug("lastnum:"+lastnum);
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
		public final int lastnum;
		public final int userid;
		
		public DraftContextObject(int lastnum, int userid, AsyncContext context, String callback) {
			super(context, callback);
			this.lastnum = lastnum;
			this.userid = userid;
		}
	}
}

