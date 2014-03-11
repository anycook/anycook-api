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

package de.anycook.messages;

import de.anycook.db.mysql.DBMessage;
import de.anycook.user.User;
import de.anycook.utils.enumerations.NotificationType;

import java.sql.SQLException;
import java.util.Date;


public class Message {
    public final int id;
    public final User sender;
    public final String text;
    public final Date datetime;
    public final boolean unread;

    public Message(int id, User sender, String text, Date datetime, boolean unread) {
        this.id = id;
        this.sender = sender;
        this.text = text;
        this.datetime = datetime;
        this.unread = unread;
    }

    public static void read(int sessionId, int messageId, int userId) throws SQLException {
        try(DBMessage db = new DBMessage()){
            db.readMessage(sessionId, messageId, userId);
        }
    }

    public static boolean check(NotificationType type) {
        switch (type) {
            case NEWMESSAGE:
            case RESETPASSWORD:
            case ACCOUNTACTIVATION:
            case NEWMAIL:
                return false;
            default:
                return true;
        }
    }
}
