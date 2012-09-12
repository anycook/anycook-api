package de.anycook.graph.message.checker;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Queue;

import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import de.anycook.db.mysql.DBMessage;
import de.anycook.misc.JsonpBuilder;


public class MessageChecker extends Checker {
	private static Queue<MessageContextObject> contextQueue = new LinkedList<>();
	
	public synchronized static void addContext(AsyncContext context, int lastnum, int userid, String callback){
		MessageContextObject contextObject = new MessageContextObject(lastnum, userid, context, callback);
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
			Logger.getLogger(MessageChecker.class).debug("get new context from queue");
			return contextQueue.poll();
		}
		
	}
	
	public MessageChecker() {
		super();
	}
	
	@Override
	public void run() {
		while(running){
			MessageContextObject data = getContext();
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
		
		
		stop();
	}
	
	private Integer getNewNum(int userid, int lastnum){
		DBMessage dbmessage = new DBMessage();
		timeout = false;
		int countdown = 12;
		int newnum = -1;
		do{
			if(newnum>=0){
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					stop();
					dbmessage.close();
					return null;
				}
			}
			newnum = dbmessage.getNewMessageNum(userid);
			countdown--;
		}while(newnum==lastnum && running && !timeout && countdown > 0);
		dbmessage.close();
		return newnum;
	}
	
	protected static class MessageContextObject extends ContextObject{
		public final int lastnum;
		public final int userid;
		
		public MessageContextObject(int lastnum, int userid, AsyncContext context, String callback) {
			super(context, callback);
			this.lastnum = lastnum;
			this.userid = userid;
		}
	}
}
