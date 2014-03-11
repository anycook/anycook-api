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

package de.anycook.mailprovider;

import de.anycook.db.mysql.DBMailProvider;

import java.sql.SQLException;

public class MailProvider {

    private final String shortName;
    private final String fullName;
    private final String redirect;
    private final String image;

    public MailProvider(String shortName, String fullName, String redirect, String image) {
        this.shortName = shortName;
        this.fullName = fullName;
        this.redirect = redirect;
        this.image = image;
    }

    public String getShortName() {
        return shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRedirect() {
        return redirect;
    }

    public String getImage() {
        return image;
    }


    public static MailProvider getMailanbieterfromDomain(String domain) throws SQLException,
            DBMailProvider.ProviderNotFoundException {
        try (DBMailProvider dbmailprovider = new DBMailProvider()) {
            return dbmailprovider.getMailProvider(domain);
        }
    }


}
