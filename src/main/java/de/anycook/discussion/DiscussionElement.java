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

import de.anycook.user.User;


public class DiscussionElement {
    private final int id;
    private int parentId;
    private User user;
    private String text;
    private String syntax;
    private int versions_id;
    private int likes;
    private String datetime;
    private boolean active;
    private boolean likedByUser;

    public DiscussionElement(int id) {
        this.id = id;
    }

    public DiscussionElement(int id, int parent_id, User user,
                             String text, String syntax, int versions_id, int likes,
                             String datetime, boolean active) {
        this.id = id;
        this.parentId = parent_id;
        this.user = user;
        this.text = text;
        this.syntax = syntax;
        this.versions_id = versions_id;
        this.likes = likes;
        this.datetime = datetime;
        this.active = active;
        this.likedByUser = false;

    }

    public DiscussionElement(int id, User user,
                             String text, String datetime) {
        this(id, -1, user, text, null, -1, -1, datetime, false);
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parent_id) {
        this.parentId = parent_id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSyntax() {
        return syntax;
    }

    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }

    public int getVersions_id() {
        return versions_id;
    }

    public void setVersions_id(int versions_id) {
        this.versions_id = versions_id;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isLikedByUser() {
        return likedByUser;
    }

    public void setLikedByUser(boolean liked) {
        this.likedByUser = liked;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "DiscussionElement{" +
                "id=" + id +
                ", parentId=" + parentId +
                ", user=" + user +
                ", text='" + text + '\'' +
                ", syntax='" + syntax + '\'' +
                ", versions_id=" + versions_id +
                ", likes=" + likes +
                ", datetime='" + datetime + '\'' +
                ", active=" + active +
                ", likedByUser=" + likedByUser +
                '}';
    }
}
