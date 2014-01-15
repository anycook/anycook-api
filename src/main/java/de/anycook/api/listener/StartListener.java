/*
 * This file is part of anycook. The new internet cookbook
 * Copyright (C) 2014 Jan Graßegger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see [http://www.gnu.org/licenses/].
 */

package de.anycook.api.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import de.anycook.db.lucene.FulltextIndex;
import org.apache.log4j.Logger;

import de.anycook.db.mysql.DBHandler;

import java.io.IOException;
import java.sql.SQLException;


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
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        DBHandler.init();
        try {
            FulltextIndex.init().addAllRecipes();
        } catch (SQLException | IOException e) {
            logger.error(e, e);
        }
        logger.info("Server started");
    }

	/**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
    	//DraftApi.destroyThreadPool();
    	DBHandler.closeSource();
    	logger.info("Server stopped");
    }
	
}
