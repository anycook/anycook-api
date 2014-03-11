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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.anycook.conf.Configuration;
import de.anycook.db.mysql.DBSocial;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Tumblr extends Social {

    static {
        consumer_key = Configuration.getPropertyTumblrAppId();
        secret_key = Configuration.getPropertyTumblrAppSecret();
    }

    private final static String POST_URL = "http://api.tumblr.com/v2/blog/%s/post";
    private Token requestToken;
    private Token accessToken = null;

    private Tumblr() {
        super();
        service = new ServiceBuilder().provider(TumblrApi.class).
                apiKey(consumer_key).apiSecret(secret_key).build();
        requestToken = service.getRequestToken();
    }

    public static Tumblr init(HttpSession session) {
        if (session.getAttribute("socialTumblr") != null)
            return (Tumblr) session.getAttribute("socialTumblr");
        Tumblr social = new Tumblr();
        session.setAttribute("socialTumblr", social);
        return social;
    }

    @Override
    public String getAuthUrl() {
        return service.getAuthorizationUrl(requestToken);
    }

    @Override
    public Token exchangeRequestForAccess(String veri_token, Integer users_id) throws SQLException {
        Verifier verifier = new Verifier(veri_token);
        accessToken = service.getAccessToken(requestToken, verifier);
        DBSocial db = new DBSocial();
        if (users_id != null)
            db.addTumblrUser(users_id, accessToken.getToken(), accessToken.getSecret());
        db.close();
        return accessToken;
    }

    public Token getAccessToken() {
        return accessToken;
    }

    public Token getAccessToken(int users_id) throws SQLException, DBSocial.TumblrNotFoundException {
        try (DBSocial db = new DBSocial()) {
            return db.getAccessToken(users_id);
        }
    }

    public static List<String> getUserBlogs(Token accToken) throws IOException {
        String userInformationUrl = "http://api.tumblr.com/v2/user/info";
        OAuthService service = new ServiceBuilder().apiKey(consumer_key).apiSecret(secret_key).provider(TumblrApi.class).build();
        OAuthRequest request = new OAuthRequest(Verb.GET, userInformationUrl);
        service.signRequest(accToken, request);
        Response response = request.send();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jBlogs = mapper.readTree(response.getBody()).get("response").get("user").get("blogs");

        List<String> blogs = new ArrayList<>();

        for (JsonNode jBlog : jBlogs) {
            blogs.add(jBlog.get("name").toString());
        }

        return blogs;
    }

    public static String post(Token accToken, String host, String text, String link, String photosource, String tags) {
        OAuthService service = new ServiceBuilder().apiKey(consumer_key).apiSecret(secret_key).provider(TumblrApi.class).build();
        String url = String.format(POST_URL, host);
        OAuthRequest request = new OAuthRequest(Verb.POST, url);
        request.addBodyParameter("type", "photo");
        request.addBodyParameter("caption", text);
        request.addBodyParameter("link", link);
        request.addBodyParameter("source", photosource);
        request.addBodyParameter("tags", "de.anycook, recipe");
        service.signRequest(accToken, request);
        Response response = request.send();
        return host + " " + response.getBody();
    }

    public static String postRecipe(Token accToken, String recipeName, String host) throws UnsupportedEncodingException {
        OAuthService service = new ServiceBuilder().apiKey(consumer_key).apiSecret(secret_key).provider(TumblrApi.class).build();
        String url = String.format(POST_URL, host);
        OAuthRequest request = new OAuthRequest(Verb.POST, url);

        StringBuffer sb = new StringBuffer();
        sb.append("de.anycook, recipe");
//		for(String tag : recipe.tags){
//			sb.append(", ").append(tag);
//		}

        String photourl = String.format("api.anycook.de/recipe/%s/image?type=large", URLEncoder.encode(recipeName, "UTF-8"));
        request.addBodyParameter("type", "photo");
        request.addBodyParameter("link", "http://de.anycook.de/#!/recipe/" + URLEncoder.encode(recipeName, "UTF-8"));
        request.addBodyParameter("source", photourl);
        request.addBodyParameter("tags", sb.toString());
        request.addBodyParameter("slug", "via de.anycook.de");
        service.signRequest(accToken, request);
        return request.send().getBody();

    }

}
