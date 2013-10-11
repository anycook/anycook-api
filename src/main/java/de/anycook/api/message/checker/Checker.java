package de.anycook.api.message.checker;

import org.apache.log4j.Logger;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.ConnectionCallback;
import javax.ws.rs.container.TimeoutHandler;
import java.io.IOException;

public abstract class Checker implements Runnable {
	protected final Logger logger;
	protected boolean timeout;
	
	public Checker() {
		logger = Logger.getLogger(getClass());
		timeout = false;
	}
	
	protected void timeout(){
		timeout = true;
	}
	
	public class ResponseListener implements ConnectionCallback, TimeoutHandler{


        @Override
        public void onDisconnect(AsyncResponse disconnected) {
            Thread.currentThread().interrupt();
            logger.debug("disconnected");
        }

        @Override
        public void handleTimeout(AsyncResponse asyncResponse) {
            Thread.currentThread().interrupt();
            logger.debug("timeout");
        }
    }
	
	protected static class ContextObject{
		public final AsyncResponse response;

		public ContextObject(AsyncResponse response) {
			this.response = response;
		}
	}

}
