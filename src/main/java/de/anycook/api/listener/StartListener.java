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

import com.google.code.geocoder.Geocoder;
import de.anycook.db.mysql.DBHandler;
import de.anycook.db.mysql.DBUser;
import de.anycook.location.GeoCode;
import de.anycook.location.Location;
import de.anycook.user.User;
import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;


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

        try {
            DBHandler.init();

            // get user locations
            try(DBUser dbUser = new DBUser()) {
                GeoCode geoCode = new GeoCode();
                for(User user : dbUser.getAllUsers()) {
                    if(user.getPlace() == null) continue;
                    try {
                        Location location = geoCode.getLocation(user.getPlace());
                        user.setLocation(location);
                    } catch (IOException|GeoCode.LocationNotFoundException e){
                        logger.debug(e, e);
                    }
                }
            }

        } catch (Exception e) {
            logger.error(e, e);
            e.printStackTrace();
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
