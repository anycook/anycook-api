package de.anycook.graph.discussion.checker;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;

import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;
import javax.ws.rs.core.MediaType;

import de.anycook.discussion.Discussion;
import de.anycook.discussion.db.DBDiscussion;
import de.anycook.messages.checker.Checker;
import de.anycook.utils.JsonpBuilder;


public class NewDiscussionChecker extends Checker {
	private static Queue<DiscussionContextObject> contextQueue = new LinkedList<>();
	
	
	public static void addContext(AsyncContext context, String recipename, int lastnum, String callback){
		DiscussionContextObject contextObject = new DiscussionContextObject(recipename, lastnum, context, callback);
		synchronized (contextQueue) {
			contextQueue.add(contextObject);
			contextQueue.notify();
		}
	}
	
	private static DiscussionContextObject getContext(){
		synchronized (contextQueue) {
			if(contextQueue.isEmpty()){
				try {
					contextQueue.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return null;
				}
			}
			return contextQueue.poll();
		}
	}
	
	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()){
			timeout = false;
			DiscussionContextObject data = getContext();
			if(data == null)
				return;
			
			int lastnum = data.lastnum;
			String recipename = data.recipename;
			AsyncContext context = data.context;
			context.addListener(new CheckerListener());
			Discussion newDiscussion = getNewDiscussion(recipename, lastnum);
			if(newDiscussion!=null){
				String callback = data.callback;
				logger.debug("found new disscussion elements");
				ServletResponse response = context.getResponse();
				response.setContentType(MediaType.APPLICATION_JSON);
				PrintWriter writer;
				try {
					writer = response.getWriter();
					writer.print(JsonpBuilder.build(callback, newDiscussion));
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
	
	private Discussion getNewDiscussion(String recipename, int lastnum) {
		DBDiscussion db = new DBDiscussion();
		Discussion newDiscussion = null;
		int countdown = 20;
		do{
			if(newDiscussion != null){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					db.close();
					Thread.currentThread().interrupt();				
					return null;
				}
			}
			newDiscussion = db.getDiscussion(recipename, lastnum);
			countdown--;
		}while(newDiscussion.isEmpty() && !timeout && countdown>0);
		
		db.close();
		return newDiscussion;
	}

	protected static class DiscussionContextObject extends ContextObject{
		public final int lastnum;
		public final String recipename;
		
		public DiscussionContextObject(String recipename, int lastnum, AsyncContext context, String callback) {
			super(context, callback);
			this.lastnum = lastnum;
			this.recipename = recipename;
		}
	}

}
