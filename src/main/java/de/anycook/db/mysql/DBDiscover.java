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

import java.sql.CallableStatement;
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

    public List<Recipe> getTastyRecipes(int num) throws SQLException {
        CallableStatement call = connection.prepareCall("{call tasty_recipes(?)}");
        call.setInt(1, num);

        ResultSet data = call.executeQuery();
        return getRecipes(data);
    }

    /**
     * Gibt Liste mit den zehn neusten Gerichten zurueck
     *
     * @return List mit den neusten Gerichten
     */
    public List<Recipe> getNewestRecipes(int num) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("{call newest_recipes(?)}");
        callableStatement.setInt(1, num);

        ResultSet data = callableStatement.executeQuery();
        return getRecipes(data);
    }

    /**
     * Gibt Liste mit den zehn am meissten angeklickten Rezepten zurueck
     *
     * @return List mit den beliebtesten Gerichten
     */
    public List<Recipe> getPopularRecipes(int num) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall("{call popular_recipes(?)}");
        callableStatement.setInt(1, num);

        ResultSet data = callableStatement.executeQuery();
        return getRecipes(data);
    }
}
