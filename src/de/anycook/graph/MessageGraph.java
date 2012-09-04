package de.anycook.graph;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.Path;

import de.anycook.messages.checker.MessageChecker;
import de.anycook.messages.checker.MessagesessionChecker;
import de.anycook.messages.checker.NewMessageChecker;
import de.anycook.misc.DaemonThreadFactory;

@Path("/message")
public class MessageGraph  {
	
	private static ExecutorService exec;
    private static final int numThreads = 30;
	
	public static void init() {
		exec = Executors.newCachedThreadPool(DaemonThreadFactory.singleton());
        for(int i = 0; i<numThreads/3; i++){
        	exec.execute(new NewMessageChecker());
        	exec.execute(new MessageChecker());
        	exec.execute(new MessagesessionChecker());
        }
	}


	public static void stop() {
		exec.shutdownNow();
		
	}
}
