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

package de.anycook.db.mysql;
/**
 * enthaelt alle Funktion, die auf die MySQL-Datenbank zugreifen
 */


import com.google.common.base.Preconditions;
import de.anycook.conf.Configuration;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


/**
 * Alle MySQL-Zugriffe geschehen ueber diese Klasse. Benutzt connectionpool
 *
 * @author Jan Grassegger
 */
public class DBHandler implements AutoCloseable {
    private static Logger sLogger = Logger.getLogger(DBHandler.class);


    protected static BasicDataSource dataSource;

    public static void init() {
        String server = Configuration.getPropertyMysqlAddress();
        String db = Configuration.getPropertyMysqlDb();
        int port = Configuration.getPropertyMysqlDPort();
        String user = Configuration.getPropertyMysqlUser();
        String password = Configuration.getPropertyMysqlPassword();
        int maxActive = Configuration.getPropertyMysqlMaxActive();
        int maxIdle = Configuration.getPropertyMysqlMaxIdle();

        dataSource = setupDataSource(server, port, db, user, password, maxActive, maxIdle);
    }

    protected Logger logger;
    protected Connection connection;


    /**
     * Laedt jdbc-Driver und ruft {@link de.anycook.db.mysql.DBHandler#connect()} auf.
     */
    public DBHandler() throws SQLException {
        logger = Logger.getLogger(getClass());
        //logger.debug("created new connection");

        connect();
        checkDataSourceStatus();
    }

    public DBHandler(DBHandler copy) {
        this.connection = copy.connection;
    }

    /**
     * Nur zur Sicherheit, falls jemand vergessen hat die DB-Verbindung zu schließen.
     * Sollte aber trotzdem immer gemacht werden!
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    private static BasicDataSource setupDataSource(String server, int port, String dbName, String username,
                                                   String password, int maxActive, int maxIdle) {
        Preconditions.checkNotNull(server);
        Preconditions.checkNotNull(dbName);
        Preconditions.checkNotNull(username);

        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUsername(username);
        ds.setPassword(password);


        String url = String.format("jdbc:mysql://%s:%d/%s?useConfigs=maxPerformance&useCompression=true",
                server, port, dbName);
        ds.setUrl(url);
        ds.setValidationQuery("SELECT 1;");
        ds.setTestWhileIdle(true);
        ds.setTestOnReturn(true);
        ds.setMaxActive(maxActive);
        ds.setMaxIdle(maxIdle);
        ds.setRemoveAbandoned(true);
        ds.setRemoveAbandonedTimeout(60);

        if(Configuration.isInDeveloperMode())
            ds.setLogAbandoned(true);

        sLogger.info("created new Connectionpool");
        return ds;
    }

    public static void closeSource() {
        try {
            dataSource.close();
            dataSource = null;
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                DriverManager.deregisterDriver(drivers.nextElement());
            }
        } catch (SQLException e) {
            sLogger.error(e);
        }
    }

    private void connect() throws SQLException {
        if (dataSource == null) throw new SQLException("Connection pool has not been initialized");
        connection = dataSource.getConnection();
    }

    @Override
    public void close() {
        try {
            connection.close();
            connection = null;
        } catch (SQLException e) {logger.debug("failed to close connection", e);}
        //logger.debug("closed a connection");
    }

    public String toString() {
        return "Connection: " + connection;
    }

    public static void checkDataSourceStatus() {
        if (dataSource.getMaxActive() - dataSource.getNumActive() <= 12) {
            sLogger.warn("running out of connections!");
            printDataSourceStatus();
        }
    }

    public static void printDataSourceStatus() {
        sLogger.info(String.format("MySQLConn. Idle: %d MaxIdle: %d NumActive: %d MaxActive: %d",
                dataSource.getNumIdle(), dataSource.getMaxIdle(), dataSource.getNumActive(), dataSource.getMaxActive()));

    }

    public static int getNumActive() {
        return dataSource.getNumActive();
    }

    public static int getMaxActive() {
        return dataSource.getMaxActive();
    }

    public static int getNumIdle() {
        return dataSource.getNumIdle();
    }

    public static int getMaxIdle() {
        return dataSource.getMaxIdle();
    }

    public static Map<String, Integer> getConnectionsStatus() {
        Map<String, Integer> connectionStati = new HashMap<>();
        connectionStati.put("numactive", getNumActive());
        connectionStati.put("maxactive", getMaxActive());
        connectionStati.put("numidle", getNumIdle());
        connectionStati.put("maxidle", getMaxIdle());
        return connectionStati;
    }

    /**
     * Loescht alle Daten in der Datenbank
     *
     * @throws java.sql.SQLException
     */
    protected void clearDB() throws SQLException {
        logger.info("clearing DB");
        clearTable("activationids");
        clearTable("apps_has_users");
        clearTable("apps");
        resetAutoIncremement("apps");
        clearTable("cases");
        clearTable("discussions");
        clearTable("discussions_events");
        clearTable("discussions_like");
        clearTable("facebooksettings");
        clearTable("followers");
        clearTable("gerichte");
        clearTable("gerichte_has_tags");
        clearTable("kategorien");
        clearTable("life");
        clearTable("mailanbieter");
        clearTable("maildomains");
        clearTable("users_has_mailnotifications");
        clearTable("mailnotifications");
        clearTable("message_sessions");
        clearTable("message_sessions_has_users");
        clearTable("messages");
        clearTable("messages_unread");
        clearTable("permanent_cookies");
        clearTable("schmeckt");
        clearTable("schritte");
        clearTable("schritte_has_zutaten");
        clearTable("tagesrezepte");
        clearTable("tags");
        clearTable("tumblr");
        clearTable("userlevels");
        clearTable("users");
        resetAutoIncremement("users");
        clearTable("versions");
        clearTable("versions_has_zutaten");
        clearTable("zutaten");
    }

    protected void clearTable(String tablename) throws SQLException {
        logger.info("clearing " + tablename);
        Statement statement = connection.createStatement();
        String query = "TRUNCATE TABLE " + tablename;
        statement.execute(query);
    }

    protected void resetAutoIncremement(String tablename) throws SQLException {
        Statement statement = connection.createStatement();
        String query = "ALTER TABLE " + tablename + " AUTO_INCREMENT = 1";
        statement.execute(query);
    }
}