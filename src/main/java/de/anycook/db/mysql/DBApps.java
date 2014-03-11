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

import de.anycook.user.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBApps extends DBHandler {

    public DBApps() throws SQLException {
        super();
    }

    public String getAppSecretByDomain(String domain) throws SQLException, AppNotFoundException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT secret from apps WHERE domain = ?");
        pStatement.setString(1, domain);
        ResultSet data = pStatement.executeQuery();
        if (data.next()) return data.getString("secret");
        throw new AppNotFoundException(domain);
    }

    public Integer getAppIDbyDomain(String domain) throws SQLException, AppNotFoundException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT id from apps WHERE domain = ?");
        pStatement.setString(1, domain);
        ResultSet data = pStatement.executeQuery();
        if (data.next()) return data.getInt("id");
        throw new AppNotFoundException(domain);

    }

    public String getAppSecret(String appId) throws SQLException, AppNotFoundException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT secret from apps WHERE id = ?");
        pStatement.setString(1, appId);
        ResultSet data = pStatement.executeQuery();
        if (data.next()) return data.getString("secret");
        throw new AppNotFoundException(appId);

    }

    public String getAppName(String appId) throws SQLException, AppNotFoundException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT name from apps WHERE id = ?");
        pStatement.setString(1, appId);
        ResultSet data = pStatement.executeQuery();
        if (data.next()) return data.getString("name");
        throw new AppNotFoundException(appId);
    }

    public void authorizeApp(User user, String appId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO users_has_apps (users_id, apps_id) VALUES (?,?);");
        pStatement.setInt(1, user.getId());
        pStatement.setString(2, appId);
        pStatement.executeUpdate();
    }

    public boolean checkUserForApp(User user, String appId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT * from users_has_apps WHERE users_id = ? AND apps_id = ?");
        pStatement.setInt(1, user.getId());
        pStatement.setString(2, appId);
        ResultSet data = pStatement.executeQuery();
        return data.next();
    }

    public void setUserOAuthToken(int userId, String appId, String oauthToken, String oauthSecret) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("UPDATE apps_has_users SET oauth_token = ?, oauth_secret = ?" +
                "WHERE apps_id = ? AND users_id = ?");
        pStatement.setString(1, oauthToken);
        pStatement.setString(2, oauthSecret);
        pStatement.setString(3, appId);
        pStatement.setInt(4, userId);
        pStatement.executeUpdate();
    }

    public static class AppNotFoundException extends Exception {
        public AppNotFoundException(String domain) {
            super(String.format("An App with domain or id %s does not exist", domain));
        }
    }
}
