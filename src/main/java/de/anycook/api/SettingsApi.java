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
import de.anycook.user.User;
import de.anycook.user.settings.NotificationSettings;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
@Path("setting")
public class SettingsApi {

    public Logger logger = LogManager.getLogger(getClass());
    @Context
    public Session session;


    @PUT
    @Path("name")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateUsername(String newUsername) {
        try {
            User user = session.getUser();
            user.setName(newUsername);

        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @PUT
    @Path("place")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updatePlace(String newPlace) {
        try {
            User user = session.getUser();
            user.setPlace(newPlace);

        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @PUT
    @Path("text")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateText(String newText) {
        try {
            User user = session.getUser();
            user.setText(newText);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @PUT
    @Path("email")
    @Consumes(MediaType.APPLICATION_JSON)
    public void updateMail(String newMail) {
        if (newMail == null) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }

        try {
            User user = session.getUser();
            user.setMailCandidate(newMail);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path("email")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String confirmMailUpdate(String code) {
        try {
            User user = session.getUser();
            return user.confirmMailCandidate(code);
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (DBUser.WrongCodeException e) {
            logger.warn(e, e);
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
    }

    @PUT
    @Path("password")
    @Consumes(MediaType.APPLICATION_JSON)
    public void changePassword(NewPassword password) {
        try {
            session.checkLogin();
            User user = session.getUser();
            if (!User.checkPassword(password.newPassword) || !user
                    .setNewPassword(password.oldPassword, password.newPassword)) {
                throw new WebApplicationException(Response.Status.BAD_REQUEST);
            }
        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @GET
    @Path("notification")
    @Produces(MediaType.APPLICATION_JSON)
    public NotificationSettings getNotificationSettings() {
        try {
            return NotificationSettings.init(session.getUser().getId());

        } catch (SQLException e) {
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
    public void updateNotificationSettings(NotificationSettings settings) {
        try {
            session.checkLogin();
            NotificationSettings.save(settings);

        } catch (SQLException e) {
            logger.error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

    }

    public static class NewPassword {

        public String oldPassword;
        public String newPassword;
    }

}
