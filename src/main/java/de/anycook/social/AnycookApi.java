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

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

public class AnycookApi extends DefaultApi10a {
    private static final String AUTHORIZE_URL = "http://api.anycook.de/de.anycook.oauth/authorize?oauth_token=%s";
    private static final String REQUEST_TOKEN_RESOURCE = "api.anycook.de/de.anycook.oauth/request_token";
    private static final String ACCESS_TOKEN_RESOURCE = "api.anycook.de/de.anycook.oauth/access_token?";

    @Override
    public String getAccessTokenEndpoint() {
        return "http://" + ACCESS_TOKEN_RESOURCE;
    }

    @Override
    public String getAuthorizationUrl(Token requestToken) {
        System.out.println(requestToken.getToken());
        return String.format(AUTHORIZE_URL, requestToken.getToken());
    }

    @Override
    public String getRequestTokenEndpoint() {
        return "http://" + REQUEST_TOKEN_RESOURCE;
    }


}
