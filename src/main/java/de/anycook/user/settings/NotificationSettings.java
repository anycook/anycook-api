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

package de.anycook.user.settings;

import de.anycook.db.mysql.DBNotificationSettings;
import de.anycook.db.mysql.DBUser;
import de.anycook.utils.enumerations.NotificationType;

import java.sql.SQLException;

/**
 * Sets the mail notifications for each user
 */
public class NotificationSettings {

    public static NotificationSettings init(int userId) throws SQLException, DBUser.UserNotFoundException {
        try (DBNotificationSettings db = new DBNotificationSettings()) {
            return db.get(userId);
        }
    }

    public static void save(NotificationSettings settings) throws SQLException {
        try (DBNotificationSettings dbNotificationSettings = new DBNotificationSettings()){
            dbNotificationSettings.update(settings);
        }
    }

    /**
     * Sends a notification if someone responds to a discussion post
     */
    private boolean discussionAnswer;

    /**
     * Sends a mail if user has a new message
     */
    private boolean newMessage;

    /**
     * Notification is send when a submitted recipe was activated
     */
    private boolean recipeActivation;

    /**
     * Sends a notification if someone discusses in a submitted recipe
     */
    private boolean recipeDiscussion;

    /**
     * Sends a notification if a recommended tag is accepted
     */
    private boolean tagAccepted;

    /**
     * Sends a notification if a recommended tag is denied
     */
    private boolean tagDenied;

    /**
     * Newsletter subscription
     */
    private boolean newsletter;

    /**
     * Sends a notification if someone likes one of your recipes
     */
    private boolean tastes;

    public boolean isNewMessage() {
        return newMessage;
    }

    public void setNewMessage(boolean newMessage) {
        this.newMessage = newMessage;
    }

    public boolean isRecipeActivation() {
        return recipeActivation;
    }

    public void setRecipeActivation(boolean recipeActivation) {
        this.recipeActivation = recipeActivation;
    }

    public boolean isRecipeDiscussion() {
        return recipeDiscussion;
    }

    public void setRecipeDiscussion(boolean recipeDiscussion) {
        this.recipeDiscussion = recipeDiscussion;
    }

    public boolean isTagAccepted() {
        return tagAccepted;
    }

    public void setTagAccepted(boolean tagAccepted) {
        this.tagAccepted = tagAccepted;
    }

    public boolean isTagDenied() {
        return tagDenied;
    }

    public void setTagDenied(boolean tagDenied) {
        this.tagDenied = tagDenied;
    }

    public boolean isNewsletter() {
        return newsletter;
    }

    public void setNewsletter(boolean newsletter) {
        this.newsletter = newsletter;
    }

    public boolean isDiscussionAnswer() {
        return discussionAnswer;
    }

    public void setDiscussionAnswer(boolean discussionAnswer) {
        this.discussionAnswer = discussionAnswer;
    }

    public boolean isTastes() {
        return tastes;
    }

    public void setTastes(boolean tastes) {
        this.tastes = tastes;
    }

    public boolean check(NotificationType type) {
        switch (type){
            case DISCUSSIONANSWER:
                return isDiscussionAnswer();
            case RECIPEACTIVATION:
                return isRecipeActivation();
            case DISCUSSION:
                return isRecipeDiscussion();
            case TAGACCEPTED:
                return isTagAccepted();
            case TAGDENIED:
                return isTagDenied();
            case TASTES:
                return isTastes();
            case NEWMESSAGE:
                return isNewMessage();
            case RESETPASSWORD:
            case ACCOUNTACTIVATION:
            case NEWMAIL:
                return true;
            default:
                return false;
        }
    }
}
