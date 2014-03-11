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

import org.scribe.model.Token;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBSocial extends DBHandler {

    public DBSocial() throws SQLException {
        super();
    }

    public void addTumblrUser(int userId, String oauthKey, String oauthSecret) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO tumblr(users_id, oauth_key, " +
                "oauth_secret) VALUES (?, ?, ?)");
        preparedStatement.setInt(1, userId);
        preparedStatement.setString(2, oauthKey);
        preparedStatement.setString(3, oauthSecret);
        preparedStatement.executeUpdate();
        logger.info("User " + userId + " connected with Tumblr");
    }

    public void setDefaultBlog(String users_id, String blogName) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE tumblr SET default_blog = ? " +
                "WHERE users_id = ?");
        preparedStatement.setString(1, users_id);
        preparedStatement.setString(1, blogName);
        preparedStatement.executeUpdate();
        logger.info("User " + users_id + " set " + blogName + " as default blog");
    }

    public boolean checkId(String users_id) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT * from tumblr WHERE users_id = ?");
        pStatement.setString(1, users_id);
        ResultSet data = pStatement.executeQuery();
        return data.next();
    }

    public Token getAccessToken(int userId) throws SQLException, TumblrNotFoundException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT oauth_key, oauth_secret " +
                "FROM tumblr WHERE users_id = ?");
        pStatement.setInt(1, userId);
        ResultSet data = pStatement.executeQuery();
        if (data.next()) {
            String oauth_key = data.getString("oauth_key");
            String oauth_secret = data.getString("oauth_secret");
            return new Token(oauth_key, oauth_secret);
        }

        throw new TumblrNotFoundException(userId);
    }

    public String getDefaultBlog(int userId) throws SQLException, TumblrNotFoundException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT default_blog from tumblr WHERE users_id = ?");
        pStatement.setInt(1, userId);
        ResultSet data = pStatement.executeQuery();
        if (data.next()) {
            return data.getString("default_blog");
        }
        throw new TumblrNotFoundException(userId);
    }

    public static class TumblrNotFoundException extends Exception {
        public TumblrNotFoundException(int userId) {
            super(String.format("no tumblr for user %d found", userId));
        }
    }


}
