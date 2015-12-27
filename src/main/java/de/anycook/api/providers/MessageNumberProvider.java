package de.anycook.api.providers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import de.anycook.messages.MessageSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.container.AsyncResponse;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public enum MessageNumberProvider {
    INSTANCE;

    private final Logger logger;
    private final Cache<Integer, BlockingQueue<AsyncResponse>> suspended;

    MessageNumberProvider() {
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

        int newNumber = MessageSession.getNewMessageNum(userId);

        while (!queue.isEmpty()) {
            LogManager.getLogger(MessageSession.class).debug("reading response");
            try {
                AsyncResponse response = queue.take();
                if (response.isSuspended()) {
                    response.resume(newNumber);
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