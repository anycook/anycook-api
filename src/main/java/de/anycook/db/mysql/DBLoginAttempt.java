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

import de.anycook.session.LoginAttempt;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public class DBLoginAttempt extends DBHandler {
    public DBLoginAttempt() throws SQLException {
        super();
    }

    public void save(LoginAttempt attempt) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO login_attemps (`address`, `time`, " +
                "`successfull`, `users_id`) VALUES (?, ?, ?, ?);");
        statement.setString(1, attempt.getAddress());
        statement.setTimestamp(2, new Timestamp(attempt.getTimestamp()));
        statement.setBoolean(3, attempt.isSuccessful());
        statement.setInt(4, attempt.getUserId());

        statement.execute();
    }

    public boolean isLoginAllowed(int userId, int maxAttempts, long inTime) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT COUNT(id) FROM login_attemps " +
                "WHERE users_id = ? AND TIMESTAMPDIFF(SECOND, time, NOW()) <= ? AND successfull = 0");
        statement.setInt(1, userId);
        statement.setLong(2, inTime);

        try (ResultSet data = statement.executeQuery()) {
            if (!data.next()) return true;
            int count = data.getInt(1);
            return count < maxAttempts;
        }
    }

}
