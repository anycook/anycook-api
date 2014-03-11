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

package de.anycook.conf;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.Properties;

/**
 * Loads app properties. For further information about the property options
 * see https://github.com/anycook/anycook-core/wiki/Configuration-File
 */
public class Configuration {

    private static final Logger logger = Logger.getLogger(Configuration.class);

    private static final String
        PROPERTIES = "anycook.properties",
        PROPERTY_ADMIN_MAIL = "ADMIN_MAIL",
        PROPERTY_ADMIN_PASSWORD = "ADMIN_PASSWORD",
        PROPERTY_COOKIE_DOMAIN = "COOKIE_DOMAIN",
        PROPERTY_DEVELOPMENT_MODE = "DEVELOPMENT_MODE",
        PROPERTY_FACEBOOK_APP_ID = "FACEBOOK_APP_ID",
        PROPERTY_FACEBOOK_APP_SECRET = "FACEBOOK_APP_SECRET",
        PROPERTY_FULL_TEXT_INDEX_PATH = "FULL_TEXT_INDEX_PATH",
        PROPERTY_IMAGE_ROOT = "IMAGE_ROOT",
        PROPERTY_IMAGE_BASE_PATH = "IMAGE_BASE_PATH",
        PROPERTY_IMAGE_S3_UPLOAD = "IMAGE_S3_UPLOAD",
        PROPERTY_IMAGE_S3_ACCESS_KEY = "IMAGE_S3_ACCESS_KEY",
        PROPERTY_IMAGE_S3_ACCESS_SECRET = "IMAGE_S3_ACCESS_SECRET",
        PROPERTY_IMAGE_S3_BUCKET = "IMAGE_S3_BUCKET",
        PROPERTY_LOGIN_ATTEMPTS_MAX = "LOGIN_ATTEMPTS_MAX",
        PROPERTY_LOGIN_ATTEMPTS_TIME = "LOGIN_ATTEMPTS_TIME",
        PROPERTY_MAIL_ADDRESS = "MAIL_ADDRESS",
        PROPERTY_MAIL_SENDER = "MAIL_SENDER",
        PROPERTY_MYSQL_ADDRESS = "MYSQL_ADDRESS",
        PROPERTY_MYSQL_DB = "MYSQL_DB",
        PROPERTY_MYSQL_MAX_ACTIVE = "MYSQL_MAX_ACTIVE",
        PROPERTY_MYSQL_MAX_IDLE = "MYSQL_MAX_IDLE",
        PROPERTY_MYSQL_PORT = "MYSQL_PORT",
        PROPERTY_MYSQL_USER = "MYSQL_USER",
        PROPERTY_MYSQL_PASSWORD = "MYSQL_PASSWORD",
        PROPERTY_REDIRECT_DOMAIN = "REDIRECT_DOMAIN",
        PROPERTY_SMTP_HOST = "SMTP_HOST",
        PROPERTY_SMTP_PORT = "SMTP_PORT",
        PROPERTY_SMTP_USER = "SMTP_USER",
        PROPERTY_SMTP_PASSWORD = "SMTP_PASSWORD",
        PROPERTY_TUMBLR_APP_ID = "TUMBLR_APP_ID",
        PROPERTY_TUMBLR_APP_SECRET = "TUMBLR_APP_SECRET";


    private static Properties properties;


    /**
     * Loads property file anycook-api.properties and returns parsed properties.
     * At first it searches for global properties at /etc/anycook/
     * Afterwards it looks into the classpath.
     *
     * @throws Error If no conf file has been found or failed to parse the file
     */
    public static Properties init() {
        if(properties == null) {

            Properties p = new Properties();

            File globalConf = new File("/etc/anycook", PROPERTIES);
            InputStream in = null;

            if (globalConf.exists()) {
                logger.info("loading global properties");
                try {
                    in = new FileInputStream(globalConf);
                } catch (FileNotFoundException e) {
                    //nope. checked it before!
                }
            } else {
                logger.info("loading properties from classpath");
                ClassLoader cl = Configuration.class.getClassLoader();
                in = cl.getResourceAsStream(PROPERTIES);
            }

            if (in == null) logger.error("failed to load property file");
            else {

                try {
                    p.load(in);
                    in.close();
                } catch (IOException e) {
                    logger.error(e,e);
                }
            }

            properties = p;
        }
        return properties;
    }

    public static boolean isInDeveloperMode() {
        String developmentMode = init().getProperty(PROPERTY_DEVELOPMENT_MODE, "OFF");
        return developmentMode.equals("ON");
    }

    public static String getPropertyAdminMail() {
        return init().getProperty(PROPERTY_ADMIN_MAIL, "admin@anycook.de");
    }

    public static String getPropertyAdminPassword() {
        return init().getProperty(PROPERTY_ADMIN_PASSWORD);
    }

    public static String getPropertyCookieDomain() {
        return init().getProperty(PROPERTY_COOKIE_DOMAIN, ".anycook.de");
    }

    public static String getPropertyFacebookAppId() {
        return init().getProperty(PROPERTY_FACEBOOK_APP_ID);
    }

    public static String getPropertyFacebookAppSecret() {
        return init().getProperty(PROPERTY_FACEBOOK_APP_SECRET);
    }

    public static String getPropertyFullTextIndexPath(){
        return init().getProperty(PROPERTY_FULL_TEXT_INDEX_PATH, "/tmp/full_text_index");
    }

    public static String getPropertyImageRoot() {
        return init().getProperty(PROPERTY_IMAGE_ROOT);
    }

    public static String getPropertyImageBasePath() {
        String path = init().getProperty(PROPERTY_IMAGE_BASE_PATH, "/images/");
        if(!path.endsWith("/")) path += '/';
        return path;
    }

    public static boolean imageS3Upload(){
        String uploadToS3 = init().getProperty(PROPERTY_IMAGE_S3_UPLOAD, "OFF");
        return uploadToS3.equals("ON");
    }

    public static String getPropertyImageS3AccessKey() {
        return init().getProperty(PROPERTY_IMAGE_S3_ACCESS_KEY);
    }

    public static String getPropertyImageS3AccessSecret() {
        return init().getProperty(PROPERTY_IMAGE_S3_ACCESS_SECRET);
    }

    public static String getPropertyImageS3Bucket() {
        return init().getProperty(PROPERTY_IMAGE_S3_BUCKET, "images.anycook.de");
    }

    public static int getPropertyLoginAttemptsMax() {
        return Integer.parseInt(init().getProperty(PROPERTY_LOGIN_ATTEMPTS_MAX, "5"));
    }

    public static int getPropertyLoginAttemptsTime() {
        return Integer.parseInt(init().getProperty(PROPERTY_LOGIN_ATTEMPTS_TIME, "600"));
    }

    public static String getPropertyMailAddress() {
        return init().getProperty(PROPERTY_MAIL_ADDRESS, "no-reply@anycook.de");
    }

    public static String getPropertyMailSender() {
        return init().getProperty(PROPERTY_MAIL_SENDER, "anycook");
    }

    public static String getPropertyMysqlAddress() {
        return init().getProperty(PROPERTY_MYSQL_ADDRESS);
    }

    public static int getPropertyMysqlMaxActive() {
        return Integer.parseInt(init().getProperty(PROPERTY_MYSQL_MAX_ACTIVE, "24"));
    }

    public static int getPropertyMysqlMaxIdle() {
        return Integer.parseInt(init().getProperty(PROPERTY_MYSQL_MAX_IDLE, "24"));
    }

    public static String getPropertyMysqlDb() {
        return init().getProperty(PROPERTY_MYSQL_DB, "anycook_db");
    }

    public static int getPropertyMysqlDPort() {
        return Integer.parseInt(init().getProperty(PROPERTY_MYSQL_PORT, "3306"));
    }

    public static String getPropertyMysqlUser() {
        return init().getProperty(PROPERTY_MYSQL_USER, "anycook");
    }

    public static String getPropertyMysqlPassword() {
        return init().getProperty(PROPERTY_MYSQL_PASSWORD, "");
    }

    public static String getPropertyRedirectDomain() {
        return init().getProperty(PROPERTY_REDIRECT_DOMAIN, "anycook.de");
    }

    public static String getPropertySmtpHost() {
        return init().getProperty(PROPERTY_SMTP_HOST);
    }

    public static String getPropertySmtpPort() {
        return init().getProperty(PROPERTY_SMTP_PORT, "465");
    }

    public static String getPropertySmtpUser() {
        return init().getProperty(PROPERTY_SMTP_USER);
    }

    public static String getPropertySmtpPassword() {
        return init().getProperty(PROPERTY_SMTP_PASSWORD);
    }

    public static String getPropertyTumblrAppId() {
        return init().getProperty(PROPERTY_TUMBLR_APP_ID);
    }

    public static String getPropertyTumblrAppSecret() {
        return init().getProperty(PROPERTY_TUMBLR_APP_SECRET);
    }


}
