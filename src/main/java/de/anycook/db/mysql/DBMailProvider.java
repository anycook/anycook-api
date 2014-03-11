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

import de.anycook.mailprovider.MailProvider;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBMailProvider extends DBHandler {

    public DBMailProvider() throws SQLException {
        super();
    }

    public void newMailProvider(String shortName, String fullName, String redirect, String imageName) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO mailanbieter VALUES (?,?,?,?)");
        pStatement.setString(1, shortName);
        pStatement.setString(2, fullName);
        pStatement.setString(3, redirect);
        pStatement.setString(4, imageName);
        pStatement.executeUpdate();
        logger.info(String.format("created new mailprovider '%s'", shortName));
    }

    public void addDomaintoMailProvider(String shortName, String domain) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO maildomains VALUES (?,?)");
        pStatement.setString(1, shortName);
        pStatement.setString(2, domain);
        pStatement.executeUpdate();

        logger.info("new domain '" + domain + "' added to mailanbieter '" + shortName + "'");
    }

    public MailProvider getMailProvider(String domain) throws ProviderNotFoundException, SQLException {
        MailProvider mailProvider = null;
        PreparedStatement pStatement = connection.prepareStatement("SELECT shortname, fullname, redirect, image " +
                "FROM maildomains LEFT JOIN mailanbieter ON shortname = mailanbieter_shortname WHERE domain = ?");

        pStatement.setString(1, domain);
        try (ResultSet data = pStatement.executeQuery()) {
            if (data.next()) {
                String shortName = data.getString("shortname");
                String fullName = data.getString("fullname");
                String redirect = data.getString("redirect");
                String image = data.getString("image");
                mailProvider = new MailProvider(shortName, fullName, redirect, image);
            } else throw new ProviderNotFoundException(domain);
        }
        return mailProvider;
    }

    public static class ProviderNotFoundException extends Exception {
        public ProviderNotFoundException(String domain) {
            super(String.format("provider domain '%s' does not exist", domain));
        }
    }
}
