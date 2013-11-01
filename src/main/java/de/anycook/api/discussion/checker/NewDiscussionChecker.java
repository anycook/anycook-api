package de.anycook.api.discussion.checker;

import de.anycook.discussion.Discussion;
import de.anycook.discussion.db.DBDiscussion;
import de.anycook.messages.Checker;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;


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
	
	private static DiscussionContextObject getContext() throws InterruptedException {
		synchronized (contextQueue) {
			if(contextQueue.isEmpty()){
                contextQueue.wait();
			}
			return contextQueue.poll();
		}
	}
	
	@Override
	public void run() {
        try{
            while(!Thread.currentThread().isInterrupted()){

                DiscussionContextObject data = getContext();
                logger.debug("checking discussion");

                int lastNum = data.lastNum;
                String recipeName = data.recipeName;
                int userId = data.userId;
                AsyncResponse asyncResponse = data.response;
                while (!asyncResponse.isDone() && !asyncResponse.isCancelled()){
                    Discussion newDiscussion;
                    try {
                        newDiscussion = getNewDiscussion(recipeName, lastNum, userId);
                        if(newDiscussion.size() > 0){
                            logger.debug("found new disscussion elements");
                            asyncResponse.resume(Response.ok(newDiscussion, MediaType.APPLICATION_JSON_TYPE).build());
                            break;
                        } else
                            Thread.currentThread().wait(1000);
                    } catch (SQLException e) {
                        logger.error(e);
                        asyncResponse.resume(new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR));
                        break;
                    }
                }
            }
        } catch (InterruptedException e) {
            logger.debug("Thread is closing");
        }

	}
	
	private Discussion getNewDiscussion(String recipeName, int lastNum, int userId) throws SQLException {

		try(DBDiscussion db = new DBDiscussion()){
            return db.getDiscussion(recipeName, lastNum, userId);
        }
	}

	protected static class DiscussionContextObject extends ContextObject{
		public final int lastNum;
		public final String recipeName;
		public final int userId;
		
		public DiscussionContextObject(String recipeName, int lastNum,
				int userId,	AsyncResponse asyncResponse) {
			super(asyncResponse);
			this.lastNum = lastNum;
			this.recipeName = recipeName;
			this.userId = userId;
		}
	}

}
