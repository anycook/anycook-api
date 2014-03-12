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


public class Message {
    private int id;
    private User sender;
    private String text;
    private long datetime;
    private boolean unread;

    public Message() {}

    public Message(int id, User sender, String text, long datetime, boolean unread) {
        this.id = id;
        this.sender = sender;
        this.text = text;
        this.datetime = datetime;
        this.unread = unread;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
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
