package de.anycook.messages.providers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.anycook.db.mysql.DBMessage;
import de.anycook.messages.MessageSession;
import org.apache.log4j.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.concurrent.*;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public enum MessageProvider {
    INSTANCE;

    private final Logger logger;
    private final Cache<Integer, BlockingQueue<UserResponse>> suspended;

    private MessageProvider(){
        logger = Logger.getLogger(getClass());
        suspended = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
    }

    public void wakeUpSuspended(int sessionId) throws SQLException {
        logger.debug("waking up suspended");
        BlockingQueue<UserResponse> queue = suspended.getIfPresent(sessionId);
        if(queue == null) return;


        while(!queue.isEmpty()){
            Logger.getLogger(MessageSession.class).debug("reading response");
            try {
                UserResponse userResponse = queue.take();
                AsyncResponse asyncResponse = userResponse.response;
                try {
                    MessageSession session = MessageSession.getSession(sessionId, userResponse.userId);
                    if(asyncResponse.isSuspended()) asyncResponse.resume(session);
                } catch (DBMessage.SessionNotFoundException e) {
                    logger.error(e, e);
                    asyncResponse.resume(new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR));
                }

            } catch (InterruptedException e) {
                logger.warn(e, e);
            }
        }
        suspended.put(sessionId, queue);

    }

    public void suspend(int sessionId, int userId, AsyncResponse response){
        logger.debug("supending "+userId);
        try {
            BlockingQueue<UserResponse> queue = suspended.get(sessionId, new Callable<BlockingQueue<UserResponse>>() {
                @Override
                public BlockingQueue<UserResponse> call() throws Exception {
                    return new ArrayBlockingQueue<>(1000);
                }
            });
            queue.add(new UserResponse(userId, response));
            suspended.put(userId, queue);
        } catch (ExecutionException e) {
            logger.error(e, e);
        }
    }

    private static class UserResponse{
        public final int userId;
        public final AsyncResponse response;

        public UserResponse(int userId, AsyncResponse response){
            this.userId = userId;
            this.response = response;
        }
    }
}
