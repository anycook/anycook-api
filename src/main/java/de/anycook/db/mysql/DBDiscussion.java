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

import de.anycook.discussion.Discussion;
import de.anycook.discussion.DiscussionElement;
import de.anycook.user.User;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;


public class DBDiscussion extends DBHandler {

    public DBDiscussion() throws SQLException {
        super();
    }

    public void discuss(int userId, String recipeName, String text) throws SQLException {
        int id = getDiscussId(recipeName) + 1;
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO discussions " +
                "(id, gerichte_name, users_id, text, eingefuegt) VALUES (?, ?, ?,?, NOW())");
        pStatement.setInt(1, id);
        pStatement.setString(2, recipeName);
        pStatement.setInt(3, userId);
        pStatement.setString(4, text);
        pStatement.executeUpdate();
        logger.info(userId + " added a comment to " + recipeName);
    }


    public void discussRecipeEvent(int userId, String recipeName, String text, String eventName, int versionId) throws SQLException {
        int id = getDiscussId(recipeName) + 1;
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO discussions (id, gerichte_name, " +
                "users_id, text, eingefuegt, discussions_events_name, versions_id) VALUES (?, ?, ?,?, NOW(), ?, ?)");
        pStatement.setInt(1, id);
        pStatement.setString(2, recipeName);
        pStatement.setInt(3, userId);
        pStatement.setString(4, text);
        pStatement.setString(5, eventName);
        pStatement.setInt(6, versionId);
        pStatement.executeUpdate();
        logger.info(userId + " added a comment to " + recipeName);
    }

    public void answer(int userId, String recipeName, int parentId, String text) throws SQLException {
        int id = getDiscussId(recipeName) + 1;
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO discussions (id, gerichte_name, " +
                "users_id, text, eingefuegt, parent_id) VALUES (?, ?, ?, ?, NOW(), ?)");

        pStatement.setInt(1, id);
        pStatement.setString(2, recipeName);
        pStatement.setInt(3, userId);
        pStatement.setString(4, text);
        pStatement.setInt(5, parentId);
        pStatement.executeUpdate();

        logger.info(userId + " answered comment " + parentId + " for " + recipeName);
    }

    public int getDiscussId(String recipeName) throws SQLException {
        int id = -1;
        PreparedStatement pStatement = connection.prepareStatement("SELECT id from discussions " +
                "WHERE gerichte_name = ? ORDER BY id DESC");
        pStatement.setString(1, recipeName);
        try (ResultSet data = pStatement.executeQuery()) {
            if (data.next()) id = data.getInt("id");
        }
        return id;
    }

    public Discussion getDiscussion(String recipeName, int maxId, int userId) throws SQLException {
        Discussion discussion = new Discussion(recipeName);

        CallableStatement call =
                connection.prepareCall("{call get_discussion(?, ?, ?)}");
        call.setString(1, recipeName);
        call.setInt(2, maxId);
        call.setInt(3, userId);

        try (ResultSet data = call.executeQuery()) {
            while (data.next()) {
                int id = data.getInt("discussions.id");
                DiscussionElement element = new DiscussionElement(id);

                element.setParentId(data.getInt("parent_id"));

                String username = data.getString("nickname");
                int usersId = data.getInt("users.id");
                String userImage = data.getString("users.image");
                element.setUser(new User(usersId, username, userImage));

                element.setText(data.getString("discussions.text"));
                element.setDatetime(data.getString("eingefuegt"));
                element.setSyntax(data.getString("syntax"));

                int versionId = data.getInt("versions_id");
                element.setVersions_id(versionId);
                element.setActive(data.getInt("gerichte.active_id") == versionId);
                element.setLikes(data.getInt("votes"));
                element.setLikedByUser(data.getBoolean("liked"));

                discussion.addElement(element);
            }
        }
        return discussion;
    }

    public int getDiscussionCountFromUser(int userId) throws SQLException {
        int count = 0;
        PreparedStatement pStatement = connection.prepareStatement("SELECT COUNT(*) AS counter FROM " +
                "(SELECT gerichte_name FROM discussions WHERE users_id = ? AND discussions_events_name IS NULL " +
                "GROUP BY gerichte_name) AS temp");

        pStatement.setInt(1, userId);
        ResultSet data = pStatement.executeQuery();
        if (data.next()) count = data.getInt("counter");
        return count;
    }

    public boolean checkForNew(String recipeName, int oldId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT id FROM discussions WHERE id >? " +
                "AND gerichte_name = ?");

        pStatement.setInt(1, oldId);
        pStatement.setString(2, recipeName);
        ResultSet data = pStatement.executeQuery();
        return data.next();
    }

    public void newLike(int id, String recipeName, int userId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO discussions_like" +
                "(users_id, discussions_gerichte_name, discussions_id) VALUES (?, ?, ?)");

        pStatement.setInt(1, userId);
        pStatement.setString(2, recipeName);
        pStatement.setInt(3, id);
        pStatement.executeUpdate();
        logger.info(userId + " " + "likes comment " + id + " from " + recipeName);
    }

    public void deleteLike(int id, String recipeName, int userId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement(
                "DELETE FROM discussions_like WHERE users_id = ? AND discussions_gerichte_name = ? AND discussions_id = ?");

        pStatement.setInt(1, userId);
        pStatement.setString(2, recipeName);
        pStatement.setInt(3, id);
        pStatement.executeUpdate();
        logger.info(userId + " " + "unlikes comment " + id + " from " + recipeName);
    }

    public boolean checkLike(int id, String recipeName, int userId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT * FROM discussions_like WHERE users_id = ? " +
                "AND discussions_gerichte_name = ? AND discussions_id = ?");

        pStatement.setInt(1, userId);
        pStatement.setString(2, recipeName);
        pStatement.setInt(3, id);
        ResultSet data = pStatement.executeQuery();
        return data.next();
    }

    public int getLikeCount(int id, String recipeName) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT count(users_id) AS votes " +
                "FROM discussions_like " +
                "WHERE discussions_gerichte_name = ? AND discussions_id = ? GROUP BY discussions_id");

        pStatement.setString(1, recipeName);
        pStatement.setInt(2, id);
        ResultSet data = pStatement.executeQuery();
        if (data.next())
            return data.getInt("votes");

        return 0;

    }

    public Set<Integer> getDiscussionsMembers(String recipeName) throws SQLException {
        Set<Integer> members = new HashSet<>();
        PreparedStatement pStatement = connection.prepareStatement("SELECT users_id FROM discussions " +
                "WHERE gerichte_name = ? GROUP BY users_id");

        pStatement.setString(1, recipeName);
        ResultSet data = pStatement.executeQuery();
        while (data.next())
            members.add(data.getInt("users_id"));

        return members;
    }

    public Set<Integer> getDiscussionsAnswerMembers(String recipeName, int parent_id) throws SQLException {
        Set<Integer> members = new HashSet<>();
        PreparedStatement pStatement = connection.prepareStatement("SELECT users_id FROM discussions " +
                "WHERE gerichte_name = ? AND (parent_id = ? OR id = ?) GROUP BY users_id");

        pStatement.setString(1, recipeName);
        pStatement.setInt(2, parent_id);
        pStatement.setInt(3, parent_id);
        ResultSet data = pStatement.executeQuery();
        while (data.next())
            members.add(data.getInt("users_id"));
        return members;
    }
}
