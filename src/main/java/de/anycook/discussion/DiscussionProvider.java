package de.anycook.discussion;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.anycook.db.mysql.DBDiscussion;
import de.anycook.messages.MessageSession;
import org.apache.log4j.Logger;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.TimeoutHandler;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.concurrent.*;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public enum DiscussionProvider {
    INSTANCE;

    private final Logger logger;
    private final Cache<String, BlockingQueue<SuspendedDiscussion>> suspended;

    private DiscussionProvider(){
        logger = Logger.getLogger(getClass());
        suspended = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
    }

    public void wakeUpSuspended(String recipeName) {
        logger.debug("waking up suspended");
        BlockingQueue<SuspendedDiscussion> queue = suspended.getIfPresent(recipeName);
        if(queue == null) return;

        try(DBDiscussion dbDiscussion = new DBDiscussion()){
            while(!queue.isEmpty()){
                Logger.getLogger(MessageSession.class).debug("reading response");
                try {
                    SuspendedDiscussion suspendedDiscussion = queue.take();
                    if(suspendedDiscussion.response.isSuspended()) {
                        suspendedDiscussion.response.resume(dbDiscussion.getDiscussion(recipeName,
                                suspendedDiscussion.lastId, suspendedDiscussion.userId));
                    }
                } catch (InterruptedException e) {
                    logger.warn(e, e);
                }
            }
        } catch (SQLException e) {
            logger.error(e ,e);
        }
        suspended.put(recipeName, queue);

    }

    public void suspend(String recipeName, int userId, int lastId, AsyncResponse response){
        response.setTimeoutHandler(new TimeoutHandler() {
            @Override
            public void handleTimeout(AsyncResponse asyncResponse) {
                logger.info("reached timeout");
                asyncResponse.resume(Response.ok().build());
            }
        });

        response.setTimeout(5, TimeUnit.MINUTES);

        logger.debug("supending "+recipeName);
        try {
            BlockingQueue<SuspendedDiscussion> queue =  suspended.get(recipeName, new Callable<BlockingQueue<SuspendedDiscussion>>() {
                @Override
                public BlockingQueue<SuspendedDiscussion> call() throws Exception {
                    return new ArrayBlockingQueue<>(300);
                }
            });
            queue.add(new SuspendedDiscussion(userId, lastId, response));
            suspended.put(recipeName, queue);
        } catch (ExecutionException e) {
            logger.error(e, e);
        }
    }

    private static class SuspendedDiscussion {
        public final int userId;
        public final int lastId;
        public final AsyncResponse response;

        public SuspendedDiscussion(int userId, int lastId, AsyncResponse response){
            this.userId = userId;
            this.lastId = lastId;
            this.response = response;
        }
    }
}

