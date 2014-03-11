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

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;

import javax.servlet.http.HttpSession;


public class Twitter extends Social {
    static {
        consumer_key = "Wh4L6LREzmgaWoV1sZR8Ow";
        secret_key = "UFgFZX2sKE08Cp9qmZi8Y3sMTmquKkd3ElZR2drro";
    }

    final String callbackstring = "http://test.anycook.de/de.anycook/ServiceCallback?serviceId=2";
    private Token requestToken;
    private Token accessToken;

    private Twitter() {
        super();
        service = new ServiceBuilder().provider(TwitterApi.class).
                apiKey(consumer_key).apiSecret(secret_key).
                callback(callbackstring).build();
        requestToken = service.getRequestToken();
    }

    public static Twitter init(HttpSession session) {
        if (session.getAttribute("socialTwitter") != null)
            return (Twitter) session.getAttribute("socialTwitter");
        Twitter social = new Twitter();
        session.setAttribute("socialTwitter", social);
        return social;
    }

    @Override
    public Token exchangeRequestForAccess(String veri_token, Integer users_id) {
        Verifier verifier = new Verifier(veri_token);
        accessToken = service.getAccessToken(requestToken, verifier);
        return accessToken;
    }

    @Override
    public String getAuthUrl() {
        return service.getAuthorizationUrl(requestToken);
    }

}
