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

package de.anycook.session;

import de.anycook.conf.Configuration;
import de.anycook.db.mysql.DBLoginAttempt;

import java.sql.SQLException;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public class LoginAttempt {
    private int userId;
    private boolean successful;
    private String address;
    private long timestamp;

    public LoginAttempt(int userId, String address, long timestamp) {
        this.userId = userId;
        this.successful = false;
        this.address = address;
        this.timestamp = timestamp;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void save() throws SQLException {
        try (DBLoginAttempt dbLoginAttempt = new DBLoginAttempt()) {
            dbLoginAttempt.save(this);
        }
    }

    public static boolean isLoginAllowed(int userId) throws SQLException {
        int maxAttempts = Configuration.getInstance().getLoginAttemptsMax();
        int inTime = Configuration.getInstance().getLoginAttemptsTime();

        try (DBLoginAttempt dbLoginAttempt = new DBLoginAttempt()) {
            return dbLoginAttempt.isLoginAllowed(userId, maxAttempts, inTime);
        }
    }
}
