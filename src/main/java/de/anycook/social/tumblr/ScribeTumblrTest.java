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
//import java.io.UnsupportedEncodingException;
//import org.scribe.builder.ServiceBuilder;
//import org.scribe.builder.api.TwitterApi;
//import org.scribe.model.Token;
//import org.scribe.de.anycook.oauth.OAuthService;
//
//public class ScribeTumblrTest {
//	public static void main(String[] args) throws UnsupportedEncodingException {
//		//final String api_key = "sokRxglnh7fID4b5lxl2BzUvHvIAzg0rymVIqOPPEZhJ1OseFU";
//		//final String secret_key = "7VuEHpwqvgK7rkUjJ9a43CowPaIBVU7QExjUDPD2ZedvAdctrl";
//		
//		final String api_key = "Wh4L6LREzmgaWoV1sZR8Ow";
//		final String secret_key = "UFgFZX2sKE08Cp9qmZi8Y3sMTmquKkd3ElZR2drro";
//		
//		
//		OAuthService service = new ServiceBuilder().provider(TwitterApi.class).
//				apiKey(api_key).apiSecret(secret_key).build();
//		Token token = service.getRequestToken();
//		System.out.println(token.getToken().toString());
//		System.out.println(service.getAuthorizationUrl(token));
//		
//	}
//}


/*
 *                              .build();
    Scanner in = new Scanner(System.in);

    System.out.println("=== Twitter's OAuth Workflow ===");
    System.out.println();

    // Obtain the Request Token
    System.out.println("Fetching the Request Token...");
    Token requestToken = service.getRequestToken();
    System.out.println("Got the Request Token!");
    System.out.println();

    System.out.println("Now go and authorize Scribe here:");
    System.out.println(service.getAuthorizationUrl(requestToken));
    System.out.println("And paste the verifier here");
    System.out.print(">>");
    Verifier verifier = new Verifier(in.nextLine());
    System.out.println();

    // Trade the Request Token and Verfier for the Access Token
    System.out.println("Trading the Request Token for an Access Token...");
    Token accessToken = service.getAccessToken(requestToken, verifier);
    System.out.println("Got the Access Token!");
    System.out.println("(if your curious it looks like this: " + accessToken + " )");
    System.out.println();

    // Now let's go and ask for a protected resource!
    System.out.println("Now we're going to access a protected resource...");
    OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
    service.signRequest(accessToken, request);
    Response response = request.send();
    System.out.println("Got it! Lets see what we found...");
    System.out.println();
    System.out.println(response.getBody());

    System.out.println();
    System.out.println("Thats it man! Go and build something awesome with Scribe! :)");
  }
 */