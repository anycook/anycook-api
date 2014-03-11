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

package de.anycook.social.tumblr;
//package de.anycook.tumblr;
//
//import org.scribe.builder.ServiceBuilder;
//import org.scribe.builder.api.TwitterApi;
//import org.scribe.model.Token;
//import org.scribe.model.Verifier;
//import org.scribe.de.anycook.oauth.OAuthService;
//
//public class TumblrHandler {
//	//static final String consumer_key = "sokRxglnh7fID4b5lxl2BzUvHvIAzg0rymVIqOPPEZhJ1OseFU";
//	//static final String secret_key = "7VuEHpwqvgK7rkUjJ9a43CowPaIBVU7QExjUDPD2ZedvAdctrl";
//	
//	static final String consumer_key = "Wh4L6LREzmgaWoV1sZR8Ow";
//	static final String secret_key = "UFgFZX2sKE08Cp9qmZi8Y3sMTmquKkd3ElZR2drro";
//	
//	public static String exchangeRequestForAccess(String veri_token, String oauth_token){
//		OAuthService service = new ServiceBuilder().provider(TwitterApi.class).
//				apiKey(consumer_key).apiSecret(secret_key).build();
//		Token token = new Token(oauth_token, secret_key);
//		
//		Verifier verifier = new Verifier(veri_token);
//		Token accessToken = service.getAccessToken(token, verifier);
//		return accessToken.getRawResponse();
//	}
//}
