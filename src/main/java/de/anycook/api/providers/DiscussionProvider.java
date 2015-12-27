package de.anycook.api.providers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import de.anycook.db.mysql.DBDiscussion;
import de.anycook.messages.MessageSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public enum DiscussionProvider {
    INSTANCE;

    private final Logger logger;
    private final Cache<String, BlockingQueue<SuspendedDiscussion>> suspended;

    DiscussionProvider() {
        logger = LogManager.getLogger(getClass());
        suspended = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
    }

    public void wakeUpSuspended(String recipeName) {
        logger.debug("waking up suspended");
        BlockingQueue<SuspendedDiscussion> queue = suspended.getIfPresent(recipeName);
        if (queue == null) {
            return;
        }

        try (DBDiscussion dbDiscussion = new DBDiscussion()) {
            while (!queue.isEmpty()) {
                LogManager.getLogger(MessageSession.class).debug("reading response");
                try {
                    SuspendedDiscussion suspendedDiscussion = queue.take();
                    if (suspendedDiscussion.response.isSuspended()) {
                        suspendedDiscussion.response.resume(dbDiscussion.getDiscussion(recipeName,
                                                                                       suspendedDiscussion.lastId,
                                                                                       suspendedDiscussion.userId));
                    }
                } catch (InterruptedException e) {
                    logger.warn(e, e);
                }
            }
        } catch (SQLException e) {
            logger.error(e, e);
        }
        suspended.put(recipeName, queue);

    }

    public void suspend(String recipeName, int userId, int lastId, AsyncResponse response) {
        response.setTimeoutHandler(asyncResponse -> {
            logger.info("reached timeout");
            asyncResponse.resume(Response.ok().build());
        });

        response.setTimeout(5, TimeUnit.MINUTES);

        logger.debug("supending " + recipeName);
        try {
            BlockingQueue<SuspendedDiscussion> queue =
                    suspended.get(recipeName, () -> new ArrayBlockingQueue<>(300));
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

        public SuspendedDiscussion(int userId, int lastId, AsyncResponse response) {
            this.userId = userId;
            this.lastId = lastId;
            this.response = response;
        }
    }
}

