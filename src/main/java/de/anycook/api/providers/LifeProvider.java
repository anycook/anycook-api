package de.anycook.api.providers;

import com.google.common.collect.ImmutableList;
import de.anycook.news.life.Life;
import de.anycook.news.life.Lifes;
import org.apache.log4j.Logger;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.GenericEntity;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public class LifeProvider {
    private static BlockingQueue<AsyncResponse> suspended = new ArrayBlockingQueue<>(500);

    public static void suspend(AsyncResponse response){
        suspended.add(response);
    }

    public static void wakeUpSuspended() throws SQLException {
        Life newLife = Lifes.getLastLife();
        List<Life> list = ImmutableList.of(newLife);
        while(!suspended.isEmpty()){
            try {
                AsyncResponse response = suspended.take();
                if(response.isSuspended()){
                    //response.resume(list);
                    final GenericEntity<List<Life>> entity = new GenericEntity<List<Life>>(list) { };
                    response.resume(entity);
                }
            } catch (InterruptedException e) {
                Logger.getLogger(LifeProvider.class).warn(e, e);
            }
        }
    }
}
