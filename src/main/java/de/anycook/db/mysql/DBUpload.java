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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUpload extends DBHandler {

    public DBUpload() throws SQLException {
        super();
    }

    public boolean checkRecipeFilename(String filename) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT * from versions WHERE imagename = ?");
        pStatement.setString(1, filename);
        ResultSet data = pStatement.executeQuery();
        return !data.next();
    }

    public boolean checkUserFilename(String filename) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT * from users WHERE image = ?");
        pStatement.setString(1, filename);
        ResultSet data = pStatement.executeQuery();
        return !data.next();
    }
}
