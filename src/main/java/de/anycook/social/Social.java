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

package de.anycook.social;

import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import java.sql.SQLException;

public abstract class Social {
    protected static OAuthService service;
    protected static String consumer_key = null;
    protected static String secret_key = null;
    protected static String callback = null;

    protected Social() {
    }


    public void addService(OAuthService s) {
        service = s;
    }

    public OAuthService getService() {
        return service;
    }

    public abstract Token exchangeRequestForAccess(String veri_token, Integer users_id) throws SQLException;


    public abstract String getAuthUrl();
}
