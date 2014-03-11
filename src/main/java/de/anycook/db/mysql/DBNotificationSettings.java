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

package de.anycook.db.mysql;

import de.anycook.user.settings.NotificationSettings;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class DBNotificationSettings extends DBHandler {

    public DBNotificationSettings() throws SQLException {
        super();
    }

    public NotificationSettings get(int userId) throws SQLException, DBUser.UserNotFoundException {

        PreparedStatement pStatement = connection.prepareStatement(
                "SELECT notification_recipe_activation, notification_recipe_discussion, notification_tag_accepted, " +
                        "notification_tag_denied, notification_newsletter, notification_discussion_answer, " +
                        "notification_tastes, notification_new_message FROM users WHERE id = ?");
        pStatement.setInt(1, userId);


        try(ResultSet data = pStatement.executeQuery()) {
            if (data.next()) {
                NotificationSettings settings = new NotificationSettings();
                settings.setDiscussionAnswer(data.getBoolean("notification_discussion_answer"));
                settings.setNewsletter(data.getBoolean("notification_newsletter"));
                settings.setRecipeActivation(data.getBoolean("notification_recipe_activation"));
                settings.setRecipeDiscussion(data.getBoolean("notification_recipe_discussion"));
                settings.setTagAccepted(data.getBoolean("notification_tag_accepted"));
                settings.setTagDenied(data.getBoolean("notification_tag_denied"));
                settings.setTastes(data.getBoolean("notification_tastes"));
                settings.setNewMessage(data.getBoolean("notification_new_message"));

                return settings;
            }
        }

        throw new DBUser.UserNotFoundException(userId);
    }

    public void update(NotificationSettings settings) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("UPDATE users SET notification_recipe_activation = ?, " +
                "notification_recipe_discussion = ?, notification_tag_accepted = ?," +
                "notification_tag_denied = ?, notification_newsletter = ?, " +
                "notification_discussion_answer = ?, notification_tastes = ?, notification_new_message = ?");

        statement.setBoolean(1, settings.isRecipeActivation());
        statement.setBoolean(2, settings.isRecipeDiscussion());
        statement.setBoolean(3, settings.isTagAccepted());
        statement.setBoolean(4, settings.isTagDenied());
        statement.setBoolean(5, settings.isNewsletter());
        statement.setBoolean(6, settings.isDiscussionAnswer());
        statement.setBoolean(7, settings.isTastes());
        statement.setBoolean(8, settings.isNewMessage());

        statement.executeUpdate();
    }
}
