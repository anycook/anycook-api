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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class WallPoster {
    private StringBuffer content;
    private URLConnection connection;


    private final Logger logger;
    private final ObjectMapper mapper;

    public WallPoster(long facebookID, String accessToken) throws IOException {
        logger = Logger.getLogger(WallPoster.class);
        mapper = new ObjectMapper();
        content = new StringBuffer("access_token=" + URLEncoder.encode(accessToken, "UTF-8"));
        URL url = new URL("https://api.facebook.com/" + facebookID + "/feed");
        connection = url.openConnection();
        connection.setDoOutput(true);
    }

    public void addMessage(String message) {
        try {
            content.append("&message=").append(URLEncoder.encode(message, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }
    }

    public void addName(String name) {
        try {
            content.append("&name=").append(URLEncoder.encode(name, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }
    }

    public void addLink(String link) {
        try {
            content.append("&link=").append(URLEncoder.encode(link, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }
    }

    public void addPicture(String path) {
        try {
            content.append("&picture=").append(URLEncoder.encode(path, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }
    }

    public void addCaption(String caption) {
        try {
            content.append("&caption=").append(URLEncoder.encode(caption, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }
    }

    public void addDescription(String description) {
        try {
            content.append("&description=").append(URLEncoder.encode(description, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }
    }

    @SuppressWarnings("unchecked")
    public void addAction(String action, String link) throws JsonProcessingException {
        Map<String, String> json = new HashMap<>();
        try {
            json.put("name", URLEncoder.encode(action, "UTF-8"));
            json.put("link", URLEncoder.encode(link, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }
        content.append("&actions=").append(mapper.writeValueAsString(json));
    }

    @SuppressWarnings("unchecked")
    public void addPrivacy(Map<String, String> privacy) throws JsonProcessingException {
        Map<String, String> json = new HashMap<>();
        for (String key : privacy.keySet()) {
            json.put(key, privacy.get(key));
        }
        content.append("&privacy=").append(mapper.writeValueAsString(json));
    }

    public void post() {
        StringBuffer out = new StringBuffer();
        OutputStreamWriter wr;
        try {
            wr = new OutputStreamWriter(connection.getOutputStream());
            wr.write(content.toString());
            wr.flush();
            wr.close();
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                out.append(line);
            }
            logger.info(out.toString());
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public void postSchmeckt(String gericht) throws JsonProcessingException {
        addMessage("schmeckt:");
        addName(gericht);
        try {
            addLink("http://de.anycook.de/#!/recipe/" + URLEncoder.encode(gericht, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }
        addAction("de.anycook.de", "http://de.anycook.de");
        logger.info(content);
        post();
    }


}
