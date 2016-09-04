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


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.bind.annotation.XmlElement;

/**
 * Loads app properties. For further information about the property options
 * see https://github.com/anycook/anycook-core/wiki/Configuration-File
 */
public class Configuration {

    private static final Logger logger = LogManager.getLogger(Configuration.class);

    private static final String
        PROPERTIES = "anycook.properties",
        ADMIN_MAIL = "ADMIN_MAIL",
        ADMIN_PASSWORD = "ADMIN_PASSWORD",
        AWS_ACCESS_KEY = "AWS_ACCESS_KEY",
        AWS_ACCESS_SECRET = "AWS_ACCESS_SECRET",
        COOKIE_DOMAIN = "COOKIE_DOMAIN",
        DEVELOPMENT_MODE = "DEVELOPMENT_MODE",
        DYNAMO_DB_DRAFTS = "DYNAMO_DB_DRAFTS",
        DYNAMO_DB_ACCESS_KEY = "DYNAMO_DB_ACCESS_KEY",
        DYNAMO_DB_ACCESS_SECRET = "DYNAMO_DB_ACCESS_SECRET",
        FACEBOOK_APP_ID = "FACEBOOK_APP_ID",
        FACEBOOK_APP_SECRET = "FACEBOOK_APP_SECRET",
        FULL_TEXT_INDEX_PATH = "FULL_TEXT_INDEX_PATH",
        IMAGE_ROOT = "IMAGE_ROOT",
        IMAGE_BASE_PATH = "IMAGE_BASE_PATH",
        IMAGE_S3_UPLOAD = "IMAGE_S3_UPLOAD",
        IMAGE_S3_ACCESS_KEY = "IMAGE_S3_ACCESS_KEY",
        IMAGE_S3_ACCESS_SECRET = "IMAGE_S3_ACCESS_SECRET",
        IMAGE_S3_BUCKET = "IMAGE_S3_BUCKET",
        LOGIN_ATTEMPTS_MAX = "LOGIN_ATTEMPTS_MAX",
        LOGIN_ATTEMPTS_TIME = "LOGIN_ATTEMPTS_TIME",
        MAIL_ADDRESS = "MAIL_ADDRESS",
        MAIL_SENDER = "MAIL_SENDER",
        MYSQL_ADDRESS = "MYSQL_ADDRESS",
        MYSQL_DB = "MYSQL_DB",
        MYSQL_MAX_ACTIVE = "MYSQL_MAX_ACTIVE",
        MYSQL_MAX_IDLE = "MYSQL_MAX_IDLE",
        MYSQL_PORT = "MYSQL_PORT",
        MYSQL_USER = "MYSQL_USER",
        MYSQL_PASSWORD = "MYSQL_PASSWORD",
        REDIRECT_DOMAIN = "REDIRECT_DOMAIN",
        SMTP_HOST = "SMTP_HOST",
        SMTP_PORT = "SMTP_PORT",
        SMTP_USER = "SMTP_USER",
        SMTP_PASSWORD = "SMTP_PASSWORD",
        TUMBLR_APP_ID = "TUMBLR_APP_ID",
        TUMBLR_APP_SECRET = "TUMBLR_APP_SECRET",
        WWW_ROOT = "WWW_ROOT";

    private static Configuration instance;

    public static Configuration getInstance() {
        if(instance == null) instance = new Configuration();
        return instance;
    }


    private Properties properties;

    public Configuration() {
        properties = initProperties();
    }

    /**
     * Loads property file anycook-api.properties and returns parsed properties.
     * At first it searches for global properties at /etc/anycook/
     * Afterwards it looks into the classpath.
     *
     * @throws Error If no conf file has been found or failed to parse the file
     */
    public Properties initProperties() {
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
        return p;
    }

    @XmlElement
    public boolean isDeveloperMode() {
        String developmentMode = properties.getProperty(DEVELOPMENT_MODE, "OFF");
        return developmentMode.equals("ON");
    }

    @XmlElement
    public String getAdminMail() {
        return properties.getProperty(ADMIN_MAIL, "admin@anycook.de");
    }

    public String getAdminPassword() {
        return properties.getProperty(ADMIN_PASSWORD);
    }

    @XmlElement
    public String getAwsAccessKey() {
        return properties.getProperty(AWS_ACCESS_KEY);
    }

    public String getAwsAccessSecret() {
        return properties.getProperty(AWS_ACCESS_SECRET);
    }

    @XmlElement
    public String getCookieDomain() {
        return properties.getProperty(COOKIE_DOMAIN, ".anycook.de");
    }

    @XmlElement
    public boolean isDynamoDbDrafts() {
        return properties.getProperty(DYNAMO_DB_DRAFTS, "OFF").equals("ON");
    }

    @XmlElement
    public String getDynamoDbAccessKey() {
        return properties.getProperty(DYNAMO_DB_ACCESS_KEY, getAwsAccessKey());
    }

    public String getDynamoDbAccessSecret() {
        return properties.getProperty(DYNAMO_DB_ACCESS_SECRET, getAwsAccessSecret());
    }

    @XmlElement
    public String getFacebookAppId() {
        return properties.getProperty(FACEBOOK_APP_ID);
    }

    public String getFacebookAppSecret() {
        return properties.getProperty(FACEBOOK_APP_SECRET);
    }

    @XmlElement
    public String getFullTextIndexPath(){
        return properties.getProperty(FULL_TEXT_INDEX_PATH, "/tmp/full_text_index");
    }

    @XmlElement
    public String getImageRoot() {
        return properties.getProperty(IMAGE_ROOT);
    }

    @XmlElement
    public String getImageBasePath() {
        String path = properties.getProperty(IMAGE_BASE_PATH, "/images/");
        if(!path.endsWith("/")) path += '/';
        return path;
    }

    @XmlElement
    public boolean isImageS3Upload(){
        String uploadToS3 = properties.getProperty(IMAGE_S3_UPLOAD, "OFF");
        return uploadToS3.equals("ON");
    }

    @XmlElement
    public String getImageS3AccessKey() {
        return properties.getProperty(IMAGE_S3_ACCESS_KEY, getAwsAccessKey());
    }

    public String getImageS3AccessSecret() {
        return properties.getProperty(IMAGE_S3_ACCESS_SECRET, getAwsAccessSecret());
    }

    @XmlElement
    public String getImageS3Bucket() {
        return properties.getProperty(IMAGE_S3_BUCKET, "images.anycook.de");
    }

    @XmlElement
    public int getLoginAttemptsMax() {
        return Integer.parseInt(properties.getProperty(LOGIN_ATTEMPTS_MAX, "5"));
    }

    @XmlElement
    public int getLoginAttemptsTime() {
        return Integer.parseInt(properties.getProperty(LOGIN_ATTEMPTS_TIME, "600"));
    }

    @XmlElement
    public String getMailAddress() {
        return properties.getProperty(MAIL_ADDRESS, "no-reply@anycook.de");
    }

    @XmlElement
    public String getMailSender() {
        return properties.getProperty(MAIL_SENDER, "anycook");
    }

    @XmlElement
    public String getMysqlAddress() {
        return properties.getProperty(MYSQL_ADDRESS, "localhost");
    }

    @XmlElement
    public int getMysqlMaxActive() {
        return Integer.parseInt(properties.getProperty(MYSQL_MAX_ACTIVE, "24"));
    }

    @XmlElement
    public int getMysqlMaxIdle() {
        return Integer.parseInt(properties.getProperty(MYSQL_MAX_IDLE, "24"));
    }

    @XmlElement
    public String getMysqlDb() {
        return properties.getProperty(MYSQL_DB, "anycook_db");
    }

    @XmlElement
    public int getPropertyMysqlPort() {
        return Integer.parseInt(properties.getProperty(MYSQL_PORT, "3306"));
    }

    @XmlElement
    public String getMysqlUser() {
        return properties.getProperty(MYSQL_USER, "anycook");
    }

    public String getMysqlPassword() {
        return properties.getProperty(MYSQL_PASSWORD, "");
    }

    @XmlElement
    public String getRedirectDomain() {
        return properties.getProperty(REDIRECT_DOMAIN, "anycook.de");
    }

    @XmlElement
    public String getSMTPHost() {
        return properties.getProperty(SMTP_HOST);
    }

    @XmlElement
    public String getSMTPPort() {
        return properties.getProperty(SMTP_PORT, "465");
    }

    @XmlElement
    public String getSMTPUser() {
        return properties.getProperty(SMTP_USER);
    }

    public String getSMTPPassword() {
        return properties.getProperty(SMTP_PASSWORD);
    }

    @XmlElement
    public String getTumblrAppId() {
        return properties.getProperty(TUMBLR_APP_ID);
    }

    public String getTumblrAppSecret() {
        return properties.getProperty(TUMBLR_APP_SECRET);
    }

    @XmlElement
    public String getWWWRoot() {
        return properties.getProperty(WWW_ROOT, "/var/www/anycook");
    }


}
