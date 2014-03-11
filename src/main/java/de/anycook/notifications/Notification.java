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

package de.anycook.notifications;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;
import de.anycook.conf.Configuration;
import de.anycook.db.mysql.DBMessage;
import de.anycook.db.mysql.DBUser;
import de.anycook.mail.MailHandler;
import de.anycook.messages.Message;
import de.anycook.messages.MessageSession;
import de.anycook.user.User;
import de.anycook.user.settings.NotificationSettings;
import de.anycook.utils.enumerations.NotificationType;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Notification {

    private static SoyTofu tofu;

    static {
        try {
            String messagePath = "/notification/message.soy";
            String subjectPath = "/notification/subject.soy";
            SoyFileSet soyFileSet = (new SoyFileSet.Builder())
                    .add(Notification.class.getResource(messagePath).toURI().toURL(), messagePath)
                    .add(Notification.class.getResource(subjectPath).toURI().toURL(), subjectPath).build();
            tofu = soyFileSet.compileToTofu().forNamespace("de.anycook.notification");
        } catch (URISyntaxException | MalformedURLException e) {
            Logger.getLogger(Notification.class).error(e, e);
        }

    }

    public static void sendNotifications(List<Integer> recipientIds, NotificationType type, Map<String, String> data)
            throws SQLException, DBUser.UserNotFoundException {
        for(int recipientId : recipientIds)
            sendNotification(recipientId, type, data);
    }

    public static void sendNotification(int recipientId, NotificationType type, Map<String, String> data)
            throws SQLException, DBUser.UserNotFoundException {
        Notification notification = new Notification(recipientId, type, data);
        notification.send();
    }

    private static String getMessage(NotificationType type, Map<String, String> data){
        return tofu.newRenderer(".message."+type).setData(data).render();
    }

    private static String getSubject(NotificationType type, Map<String, String> data){
        return tofu.newRenderer(".subject."+type).setData(data).render();
    }

    private Logger logger;
    private int recipientId;
    private NotificationType type;
    private Map<String, String> data;

    public Notification(int recipientId, NotificationType type, Map<String, String> data) throws SQLException, DBUser.UserNotFoundException {
        this.logger = Logger.getLogger(getClass());

        this.recipientId = recipientId;
        this.type = type;

        data.put("baseUrl", Configuration.getPropertyRedirectDomain());
        data.put("recipientName", User.getUsername(recipientId));
        this.data = data;
    }

    public void send() throws SQLException, DBUser.UserNotFoundException {

        boolean sendMail;

        if (Configuration.isInDeveloperMode()) {
            try {
                sendMail = type == NotificationType.ACCOUNTACTIVATION || type == NotificationType.RESETPASSWORD ||
                        type == NotificationType.NEWMAIL ||
                        User.init(recipientId).isAdmin() && NotificationSettings.init(recipientId).check(type);
                logger.debug("sendMail is "+sendMail+" for "+type+" for user "+recipientId);
            } catch (DBUser.UserNotFoundException | IOException e) {
                //won't happen
                sendMail = false;
            }
        } else {
            sendMail = NotificationSettings.init(recipientId).check(type);
        }


        String message = getMessage(type, data);

        if (sendMail) {
            String userMail = type == NotificationType.NEWMAIL ?
                    User.getMailCandidate(recipientId) : User.getUseremail(recipientId);
            String subject = getSubject(type, data);
            logger.debug(String.format("sending mail to %d", recipientId));
            MailHandler mailhandler = MailHandler.getSingleton();
            mailhandler.sendMail(userMail, subject, message);
        }
        if (Message.check(type)) {
            logger.debug("sending notification message to " + recipientId);
            try {
                MessageSession session = MessageSession.getAnycookSession(recipientId);
                session.newMessage(1, message, false);
            } catch (DBMessage.SessionNotFoundException e) {
                logger.error(e, e);
            }

        }
    }

}
