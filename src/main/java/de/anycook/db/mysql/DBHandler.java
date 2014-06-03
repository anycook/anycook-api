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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;


/**
 * Alle MySQL-Zugriffe geschehen ueber diese Klasse. Benutzt connectionpool
 *
 * @author Jan Grassegger
 */
public class DBHandler implements AutoCloseable {
    private static Logger sLogger = Logger.getLogger(DBHandler.class);


    protected static BasicDataSource dataSource;

    public static void init() {
        String server = Configuration.getInstance().getMysqlAddress();
        String db = Configuration.getInstance().getMysqlDb();
        int port = Configuration.getInstance().getPropertyMysqlPort();
        String user = Configuration.getInstance().getMysqlUser();
        String password = Configuration.getInstance().getMysqlPassword();
        int maxActive = Configuration.getInstance().getMysqlMaxActive();
        int maxIdle = Configuration.getInstance().getMysqlMaxIdle();

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

        if(Configuration.getInstance().isDeveloperMode())
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

    public static ConnectionStatus getConnectionsStatus() {
        ConnectionStatus status = new ConnectionStatus();
        status.setNumActive(getNumActive());
        status.setMaxActive(getMaxActive());
        status.setNumIdle(getNumIdle());
        status.setMaxIdle(getMaxIdle());
        return status;
    }

    public static class ConnectionStatus {
        private int numActive;
        private int maxActive;
        private int numIdle;
        private int maxIdle;

        public int getNumActive() {
            return numActive;
        }

        public void setNumActive(int numActive) {
            this.numActive = numActive;
        }

        public int getMaxActive() {
            return maxActive;
        }

        public void setMaxActive(int maxActive) {
            this.maxActive = maxActive;
        }

        public int getNumIdle() {
            return numIdle;
        }

        public void setNumIdle(int numIdle) {
            this.numIdle = numIdle;
        }

        public int getMaxIdle() {
            return maxIdle;
        }

        public void setMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
        }
    }


}
