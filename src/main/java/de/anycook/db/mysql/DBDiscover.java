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

import de.anycook.recipe.Recipe;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


/**
 * Von DBHandler abgeleitet. Enthaelt alle Funktionen, die fuer die Entdeckenseite zustaendig sind.
 *
 * @author Jan Grassegger
 * @see de.anycook.db.mysql.DBHandler
 */
public class DBDiscover extends DBRecipe {

    public DBDiscover() throws SQLException {
        super();
    }

    public List<Recipe> getTastyRecipes(int offset, int num, int loginId) throws SQLException {
        PreparedStatement preparedStatement =
            connection.prepareCall("SELECT versions.id AS id, beschreibung, " +
                "IFNULL(versions.imagename, CONCAT('category/', kategorien.image)) AS image, " +
                "gerichte.eingefuegt AS created, min, std, skill, kalorien, gerichte.name AS name, " +
                "personen, kategorien_name, active_id, users_id, nickname, users.image, " +
                "viewed, last_change, " +
                "(SELECT COUNT(users_id) FROM schmeckt WHERE schmeckt.gerichte_name = gerichte.name) AS counter, " +
                "(SELECT COUNT(users_id) FROM schmeckt WHERE schmeckt.gerichte_name = gerichte.name " +
                "AND schmeckt.users_id = ?) AS tastes " +
                "FROM gerichte " +
                "INNER JOIN versions ON gerichte.name = gerichte_name AND active_id = versions.id " +
                "INNER JOIN users ON users_id = users.id " +
                "INNER JOIN kategorien ON kategorien_name = kategorien.name " +
                "GROUP BY gerichte.name ORDER BY counter DESC LIMIT ?,?;");
        preparedStatement.setInt(1, loginId);
        preparedStatement.setInt(2, offset);
        preparedStatement.setInt(3, num);

        ResultSet data = preparedStatement.executeQuery();
        return getRecipes(data);
    }

    /**
     * Gibt Liste mit den zehn neusten Gerichten zurueck
     *
     * @return List mit den neusten Gerichten
     */
    public List<Recipe> getNewestRecipes(int offset, int num, int loginId) throws SQLException {
        PreparedStatement preparedStatement =
            connection.prepareStatement("SELECT versions.id AS id, beschreibung, " +
                "IFNULL(versions.imagename, CONCAT('category/', kategorien.image)) AS image, " +
                "gerichte.eingefuegt AS created, min, std, skill, kalorien, gerichte.name AS name, personen, " +
                "kategorien_name, active_id, users_id, nickname, users.image, viewed, last_change, " +
                "(SELECT IF(COUNT(users_id) = 1, TRUE, FALSE) FROM schmeckt " +
                "WHERE schmeckt.gerichte_name = gerichte.name AND schmeckt.users_id = ?) AS tastes " +
                "FROM gerichte " +
                "INNER JOIN versions ON gerichte.name = gerichte_name AND active_id = versions.id " +
                "INNER JOIN users ON users_id = users.id " +
                "INNER JOIN kategorien ON kategorien_name = kategorien.name " +
                "GROUP BY gerichte.name ORDER BY gerichte.eingefuegt DESC LIMIT ?,?;");
        preparedStatement.setInt(1, loginId);
        preparedStatement.setInt(2, offset);
        preparedStatement.setInt(3, num);

        ResultSet data = preparedStatement.executeQuery();
        return getRecipes(data);
    }

    /**
     * Gibt Liste mit den zehn am meissten angeklickten Rezepten zurueck
     *
     * @return List mit den beliebtesten Gerichten
     */
    public List<Recipe> getPopularRecipes(int offset, int num, int loginId) throws SQLException {
        PreparedStatement preparedStatement =
            connection.prepareStatement("SELECT versions.id AS id, beschreibung, " +
                "IFNULL(versions.imagename, CONCAT('category/', kategorien.image)) AS image, " +
                "gerichte.eingefuegt AS created, min, std, skill, kalorien, gerichte.name, personen, kategorien_name, "+
                "active_id, users_id, nickname, users.image, viewed, last_change, " +
                "(SELECT COUNT(users_id) FROM schmeckt " +
                "WHERE schmeckt.gerichte_name = gerichte.name AND schmeckt.users_id = ?) AS tastes " +
                "FROM gerichte " +
                "INNER JOIN versions ON gerichte.name = gerichte_name AND active_id = versions.id " +
                "INNER JOIN users ON users_id = users.id " +
                "INNER JOIN kategorien ON kategorien_name = kategorien.name " +
                "GROUP BY gerichte.name ORDER BY viewed DESC LIMIT ?,?;");
        preparedStatement.setInt(1, loginId);
        preparedStatement.setInt(2, offset);
        preparedStatement.setInt(3, num);

        ResultSet data = preparedStatement.executeQuery();
        return getRecipes(data);
    }
}
