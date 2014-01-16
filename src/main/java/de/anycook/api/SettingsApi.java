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

package de.anycook.api;

import de.anycook.api.util.MediaType;
import de.anycook.db.mysql.DBUser;
import de.anycook.session.Session;
import de.anycook.user.settings.NotificationSettings;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
@Path("setting")
public class SettingsApi {

    public Logger logger = Logger.getLogger(getClass());
    @Context
    public HttpServletRequest request;
    @Context
    public HttpHeaders hh;

    @GET
    @Path("notification")
    @Produces(MediaType.APPLICATION_JSON)
    public NotificationSettings getNotificationSettings(){
        Session session = Session.init(request.getSession());
        try {
            session.checkLogin(hh.getCookies());
            return NotificationSettings.init(session.getUser().getId());

        } catch (IOException | SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBUser.UserNotFoundException e) {
            logger.warn(e);
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }
    }

    @PUT
    @Path("notification")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateNotificationSettings(NotificationSettings settings){
        Session session = Session.init(request.getSession());
        try {
            session.checkLogin(hh.getCookies());
            NotificationSettings.save(settings);

        } catch (IOException|SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

    }

}
