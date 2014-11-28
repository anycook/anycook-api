package de.anycook.newrecipe;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.anycook.db.drafts.RecipeDraftsStore;
import org.apache.log4j.Logger;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public enum DraftNumberProvider {
    INSTANCE;

    private final Logger logger;
    private final Cache<Integer, BlockingQueue<AsyncResponse>> suspended;

    private DraftNumberProvider(){
    logger = Logger.getLogger(getClass());
    suspended = CacheBuilder.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();
    }

    public void wakeUpSuspended(int userId) throws SQLException {
        logger.debug("waking up suspended");
        BlockingQueue<AsyncResponse> queue = suspended.getIfPresent(userId);
        if(queue == null) return;

        int newNumber = 0;

        try(RecipeDraftsStore draftsStore = RecipeDraftsStore.getRecipeDraftStore()) {
            newNumber = draftsStore.countDrafts(userId);
        } catch (Exception e) {
            logger.error("failed to retrieve new draft number", e);
        }

        while(!queue.isEmpty()){
            logger.debug("reading response");
            try {
                AsyncResponse response = queue.take();
                if(response.isSuspended()) response.resume(String.valueOf(newNumber));
            } catch (InterruptedException e) {
                logger.warn(e, e);
            }
        }
        suspended.put(userId, queue);

    }

    public void suspend(int userId, AsyncResponse response){
        response.setTimeoutHandler(asyncResponse -> {
            logger.info("reached timeout");
            asyncResponse.resume(Response.ok().build());
        });

        response.setTimeout(5, TimeUnit.MINUTES);

        logger.debug("supending "+userId);
        try {
            BlockingQueue<AsyncResponse> queue =  suspended.get(userId, () -> new ArrayBlockingQueue<>(1000));
            queue.add(response);
            suspended.put(userId, queue);
        } catch (ExecutionException e) {
            logger.error(e, e);
        }
    }
}
