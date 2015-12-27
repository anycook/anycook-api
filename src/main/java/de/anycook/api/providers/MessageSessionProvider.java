package de.anycook.api.providers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import de.anycook.messages.MessageSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.GenericEntity;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public enum MessageSessionProvider {
    INSTANCE;

    private final Logger logger;
    private final Cache<Integer, BlockingQueue<AsyncResponse>> suspended;

    private MessageSessionProvider() {
        logger = LogManager.getLogger(getClass());
        suspended = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
    }

    public void wakeUpSuspended(int userId) throws SQLException {
        logger.debug("waking up suspended");
        BlockingQueue<AsyncResponse> queue = suspended.getIfPresent(userId);
        if (queue == null) {
            return;
        }

        List<MessageSession> sessions = MessageSession.getSessionsFromUser(userId);
        GenericEntity<List<MessageSession>>
                entity =
                new GenericEntity<List<MessageSession>>(sessions) {
                };

        while (!queue.isEmpty()) {
            LogManager.getLogger(MessageSession.class).debug("reading response");
            try {
                AsyncResponse response = queue.take();
                if (response.isSuspended()) {
                    response.resume(entity);
                }
            } catch (InterruptedException e) {
                logger.warn(e, e);
            }
        }
        suspended.put(userId, queue);

    }

    public void suspend(int userId, AsyncResponse response) {
        logger.debug("supending " + userId);
        try {
            BlockingQueue<AsyncResponse> queue =
                    suspended.get(userId, () -> new ArrayBlockingQueue<>(1000));
            queue.add(response);
            suspended.put(userId, queue);
        } catch (ExecutionException e) {
            logger.error(e, e);
        }
    }
}
