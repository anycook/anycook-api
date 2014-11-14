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

    private String shortName;
    private String fullName;
    private String redirect;
    private String image;

    public MailProvider() {

    }

    public MailProvider(String shortName, String fullName, String redirect, String image) {
        this.shortName = shortName;
        this.fullName = fullName;
        this.redirect = redirect;
        this.image = image;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public static MailProvider getMailProviderForDomain(String domain) throws SQLException,
            DBMailProvider.ProviderNotFoundException {
        try (DBMailProvider dbmailprovider = new DBMailProvider()) {
            return dbmailprovider.getMailProvider(domain);
        }
    }


}
