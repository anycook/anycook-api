package de.anycook.graph.message.checker;

import java.io.IOException;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;

import org.apache.log4j.Logger;

public abstract class Checker implements Runnable {
	protected final Logger logger;
	protected boolean running;
	protected boolean timeout;
	
	public Checker() {
		logger = Logger.getLogger(getClass());
		running = true;
		timeout = false;
	}
	
	protected void stop(){
		running = false;
		Thread.currentThread().interrupt();
	}
	
	protected void timeout(){
		timeout = true;
	}
	
	@Override
	protected void finalize() throws Throwable {
		stop();
	}
	
	public class CheckerListener implements AsyncListener{
		
		
		@Override
		public void onComplete(AsyncEvent event) throws IOException {}

		@Override
		public void onError(AsyncEvent event) throws IOException {
			Throwable error = event.getThrowable();
			if(error != null)
				logger.debug("error event: "+error.getMessage());
			else
				logger.debug("error event");
			timeout();
			
		}

		@Override
		public void onStartAsync(AsyncEvent event) throws IOException {}

		@Override
		public void onTimeout(AsyncEvent event) throws IOException {
			stop();			
		}
		
	}
	
	protected static class ContextObject{
		public final AsyncContext context;
		public final String callback;
		
		public ContextObject(AsyncContext context, String callback) {
			this.context = context;
			this.callback = callback;
		}
	}

}
