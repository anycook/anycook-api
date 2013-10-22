package de.anycook.api.discussion.checker;

import de.anycook.messages.Checker;
import de.anycook.discussion.Discussion;
import de.anycook.discussion.db.DBDiscussion;

import javax.ws.rs.container.AsyncResponse;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;


public class NewDiscussionChecker extends Checker {

    private static final Queue<DiscussionContextObject> contextQueue = new LinkedList<>();

	public static void addContext(AsyncResponse asyncResponse, String recipeName,
			int lastNum, int userId){
		DiscussionContextObject contextObject = 
				new DiscussionContextObject(recipeName, lastNum, userId,
						asyncResponse);
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
			
			int lastNum = data.lastNum;
			String recipeName = data.recipeName;
			int userId = data.userId;
			AsyncResponse response = data.response;
            ResponseListener listener = new ResponseListener();
            response.setTimeout(1, TimeUnit.MINUTES);
            response.setTimeoutHandler(listener);
            response.register(listener);


            Discussion newDiscussion;
            try {
                newDiscussion = getNewDiscussion(recipeName, lastNum, userId);
                if(newDiscussion!=null){
                    logger.debug("found new disscussion elements");
                    response.resume(newDiscussion);
                }

            } catch (SQLException e) {
                logger.error(e);

            }

		}

	}
	
	private Discussion getNewDiscussion(String recipename, int lastnum, 
			int userid) throws SQLException {
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
			newDiscussion = db.getDiscussion(recipename, lastnum, userid);
			countdown--;
		}while(newDiscussion.isEmpty() && !timeout && countdown>0);
		
		db.close();
		return newDiscussion;
	}

	protected static class DiscussionContextObject extends ContextObject{
		public final int lastNum;
		public final String recipeName;
		public final int userId;
		
		public DiscussionContextObject(String recipeName, int lastNum,
				int userId,	AsyncResponse response) {
			super(response);
			this.lastNum = lastNum;
			this.recipeName = recipeName;
			this.userId = userId;
		}
	}

}
