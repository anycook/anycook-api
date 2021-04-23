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

package de.anycook.discussion;

import de.anycook.db.mysql.DBDiscussion;
import de.anycook.db.mysql.DBUser;
import de.anycook.notifications.Notification;
import de.anycook.user.User;
import de.anycook.api.utils.enumerations.NotificationType;

import java.sql.SQLException;
import java.util.*;


public class Discussion {
    /**
     *
     */
    private String recipeName;
    private List<DiscussionElement> elements;

    public Discussion(){}

    public Discussion(String recipeName) {
        this(recipeName, new LinkedList<DiscussionElement>());
    }

    public Discussion(String recipeName, List<DiscussionElement> discussionElements) {
        this.recipeName = recipeName;
        this.elements = discussionElements;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public List<DiscussionElement> getElements() {
        return elements;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public void setElements(List<DiscussionElement> elements) {
        this.elements = elements;
    }

    public void addElement(DiscussionElement element) {
        elements.add(element);
    }

    public int size() {
        return elements.size();
    }

    public static Discussion getParents(String recipeName, int userId) throws SQLException {
        try(DBDiscussion dbdiscussion = new DBDiscussion()){
            return dbdiscussion.getDiscussion(recipeName, -1, userId);
        }
    }

    public static Discussion getChildren(String recipeName, int parentId, int userId) throws SQLException {
        try(DBDiscussion dbdiscussion = new DBDiscussion()) {
            return dbdiscussion.getDiscussion(recipeName, parentId, userId);
        }
    }

    public static void discuss(String text, int userId, String recipeName) throws SQLException {
        try(DBDiscussion dbdiscussion = new DBDiscussion()) {
            dbdiscussion.discuss(userId, recipeName, text);
        }

        try {
            Set<Integer> mailTos = getMailTos(recipeName, userId);
            Map<String, String> data = new HashMap<>();
            data.put("userName", User.getUsername(userId));
            data.put("recipeName", recipeName);
            data.put("content", text);
            Notification.sendNotifications(mailTos, NotificationType.DISCUSSION, data);
        } catch (DBUser.UserNotFoundException e) {
            //Nope
        }
    }

    public static void answer(String text, int pid, int userId, String recipeName) throws SQLException {
        try(DBDiscussion dbdiscussion = new DBDiscussion()){
            dbdiscussion.answer(userId, recipeName, pid, text);
        }


        try {
            Set<Integer> mailTos = getAnswerMailTos(recipeName, userId, pid);
            Map<String, String> data = new HashMap<>();
            data.put("userName", User.getUsername(userId));
            data.put("recipeName", recipeName);
            data.put("content", text);
            Notification.sendNotifications(mailTos, NotificationType.DISCUSSION_ANSWER, data);
        } catch (DBUser.UserNotFoundException e) {
            //nope
        }
    }

    public static boolean checkforNew(String recipeName, String maxId) throws SQLException {
        try(DBDiscussion dbdiscussion = new DBDiscussion()) {
            return dbdiscussion.checkForNew(recipeName, Integer.parseInt(maxId));
        }
    }

    public static Discussion getNewDiscussion(String recipeName, int maxId, int userId) throws SQLException {
        try(DBDiscussion dbdiscussion = new DBDiscussion()) {
            return dbdiscussion.getDiscussion(recipeName, maxId, userId);
        }
    }

    public static void like(int userId, String recipeName, int id) throws SQLException {
        try(DBDiscussion dbdiscussion = new DBDiscussion()) {
            if (!dbdiscussion.checkLike(id, recipeName, userId))
                dbdiscussion.newLike(id, recipeName, userId);
        }
    }

    public static void unlike(int userId, String recipeName, int id) throws SQLException {
        try(DBDiscussion dbdiscussion = new DBDiscussion()){
            if (dbdiscussion.checkLike(id, recipeName, userId))
                dbdiscussion.deleteLike(id, recipeName, userId);
        }
    }

    public static Set<Integer> getMailTos(String recipeName, int excludedId) throws SQLException {
        try(DBDiscussion dbdiscussion = new DBDiscussion()) {
            Set<Integer> mailTos = dbdiscussion.getDiscussionsMembers(recipeName);
            mailTos.remove(excludedId);
            return mailTos;
        }
    }

    public static Set<Integer> getAnswerMailTos(String recipeName, int excludedId, int parentId) throws SQLException {
        try(DBDiscussion dbdiscussion = new DBDiscussion()) {
            Set<Integer> mailTos = dbdiscussion.getDiscussionsAnswerMembers(recipeName, parentId);
            mailTos.remove(excludedId);
            return mailTos;
        }
    }

    public static int getDiscussionNumForUser(int userId) throws SQLException {
        try(DBDiscussion dbDiscussion = new DBDiscussion()) {
            return dbDiscussion.getDiscussionCountFromUser(userId);
        }
    }

    public static void addNewRecipeEvent(String recipeName, int userId, String comment, int versionId) throws SQLException {
        try(DBDiscussion db = new DBDiscussion()) {
            db.discussRecipeEvent(userId, recipeName, comment, "newrecipe", versionId);
        }
    }

    public static void addNewVersionEvent(String recipeName, int userid, String comment, int versionId) throws SQLException {
        try (DBDiscussion db = new DBDiscussion()) {
            db.discussRecipeEvent(userid, recipeName, comment, "newversion", versionId);
        }
    }

    public static void addNewRecipeEvent(String name, String comment, int id) throws SQLException {
        addNewRecipeEvent(name, -1, comment, id);
    }

    public static void addNewVersionEvent(String name, String comment, int id) throws SQLException {
        addNewVersionEvent(name, -1, comment, id);
    }
}
