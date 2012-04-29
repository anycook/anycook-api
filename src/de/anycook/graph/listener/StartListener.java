package de.anycook.graph.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.log4j.Logger;

import de.anycook.db.couchdb.CouchDB;
import de.anycook.graph.MessageGraph;


/**
 * Application Lifecycle Listener implementation class StartListener
 *
 */
@WebListener
public class StartListener implements ServletContextListener {
	private Logger logger;
    /**
     * Default constructor. 
     */
    public StartListener() {
    	logger = Logger.getLogger(getClass());
    }

	/**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent arg0) {
    	 logger.info("Server started");
    	 MessageGraph.init();
         CouchDB.start();
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent arg0) {
    	MessageGraph.stop();
    	logger.info("Server stopped");
    }
	
}
