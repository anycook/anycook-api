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

package de.anycook.social.facebook;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.anycook.conf.Configuration;
import de.anycook.upload.UserUploader;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


public class FacebookHandler {

    private final static Logger LOGGER = LogManager.getLogger(FacebookHandler.class);
    public final static String APP_ID = Configuration.getInstance().getFacebookAppId();
    private final static String APP_SECRET = Configuration.getInstance().getFacebookAppSecret();

    private OAuthService service;

    public FacebookHandler() {
        service = new ServiceBuilder().provider(FacebookApi.class)
                .apiKey(APP_ID).apiSecret(APP_SECRET)
                .callback("http://test.anycook.de/anycook/NewFacebookUser").build();
    }

    public String getAuthURL() {
        Token requestToken = service.getRequestToken();
        return service.getAuthorizationUrl(requestToken);
    }


    @SuppressWarnings("unchecked")
    public static String publishtoWall(long facebookID, String accessToken, String message,
                                       String header)
            throws IOException {
        StringBuilder out = new StringBuilder();
        StringBuilder data = new StringBuilder();
        data.append("access_token=").append(URLEncoder.encode(accessToken, "UTF-8"));
        data.append("&message=").append(URLEncoder.encode(message, "UTF-8"));
        data.append("&name=").append(URLEncoder.encode(header, "UTF-8"));

        URL url = new URL("https://api.facebook.com/" + facebookID + "/feed");
        URLConnection conn = url.openConnection();
        conn.setDoOutput(true);
        try (OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream())) {
            wr.write(data.toString());
            wr.flush();

            try (BufferedReader rd = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = rd.readLine()) != null) {
                    out.append(line);
                }
            }
        }

        return out.toString();
    }

    public static String getPermissions(String accessToken, long facebookID) throws IOException {
        StringBuilder out = new StringBuilder();

        String data = "?access_token=" + accessToken;
        URL url = new URL("https://api.facebook.com/" + facebookID + "/permissions" + data);
        URLConnection conn = url.openConnection();
        //conn.setDoOutput(true);
        //OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        //wr.write(data);
        //wr.flush();

        try (BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = rd.readLine()) != null) {
                out.append(line);
            }
        }

        return out.toString();
    }

    public static FacebookRequest decode(String input) throws IOException {
        String[] split = input.split("\\.");
        String sig = decodeBase64(split[0]);
        String data = decodeBase64(split[1]);
        if (verifySigSHA256(sig, split[1])) {
            LOGGER.debug(data);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(data, FacebookRequest.class);
        }
        throw new IOException("failed to parse fb request");


    }

    public static String getUsersOAuthToken(long facebook_id) throws IOException {
        URL url = new URL("https://graph.facebook.com/anycook.oauth/access_token?" +
                          "client_id=" + APP_ID + "&" +
                          "client_secret=" + APP_SECRET + "&" +
                          "grant_type=client_credentials");
        URLConnection urlconnection = url.openConnection();
        BufferedReader
                rd =
                new BufferedReader(new InputStreamReader(urlconnection.getInputStream()));
        String response = rd.readLine();
        return response.split("=")[1];

    }

    public static boolean verifySigSHA256(String sig, String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret = new SecretKeySpec(APP_SECRET.getBytes(), "HmacSHA256");
            mac.init(secret);
            byte[] digest = mac.doFinal(payload.getBytes());
            String expected_sig = new String(digest);
            if (sig.equals(expected_sig)) {
                return true;
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            LOGGER.error(e, e);
        }
        LOGGER.error("signatures are not the same!");
        return false;
    }

    public static boolean verifySigMD5(String sig, String payload) {
        String tocheck = payload + APP_SECRET;
        String expected_sig = null;
        try {
            expected_sig = DigestUtils.md5Hex(tocheck.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e);
        }
        if (sig.equals(expected_sig)) {
            return true;
        }
        LOGGER.error("signatures are not the same!");
        return false;

    }

    private static String decodeBase64(String input) {
        return new String(new Base64(true).decode(input));
    }

    public static String getFacebookImagePath(Long facebook_id) {
        return "https://graph.facebook.com/" + facebook_id + "/picture";
    }

    public static String saveImage(long facebook_id) throws SQLException, IOException {
        UserUploader up = new UserUploader();
        String imageName = up.saveFBURLImage(getFacebookImagePath(facebook_id));
        LOGGER.info("uploaded new FB image");
        return imageName;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FacebookRequest {

        public String algorithm;

        public String code;

        public long expires;

        public long issued_at;

        public String oauth_token;

        public Map<String, String> registration;

        public Map<String, String> registration_metadata;

        public Map<String, String> user;
        public String user_id;
    }


}
