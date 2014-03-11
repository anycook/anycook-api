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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DBRecommend extends DBHandler {

    public DBRecommend() throws SQLException {
        super();
    }

    public Map<String, Integer> getTastyTags(int userId) throws SQLException {
        Map<String, Integer> tastyTags = new HashMap<>();
        PreparedStatement pStatement = connection.prepareStatement(
                "SELECT tags_name, COUNT(gerichte_name) AS counter from schmeckt " +
                        "INNER JOIN gerichte_has_tags USING (gerichte_name) " +
                        "WHERE active = 1 AND schmeckt.users_id = ? GROUP BY tags_name");
        pStatement.setInt(1, userId);

        ResultSet data = pStatement.executeQuery();
        while (data.next()) {
            tastyTags.put(data.getString("tags_name"), data.getInt("counter"));
        }
        return tastyTags;
    }

    public Map<String, Collection<String>> getRecipesByTags(int userId) throws SQLException {
        CallableStatement call =
                connection.prepareCall("{call recipes_from_schmeckttags(?)}");
        call.setInt(1, userId);

        ResultSet data = call.executeQuery();
        Multimap<String, String> recipes = HashMultimap.create();

        while (data.next()) {
            String recipe = data.getString("gerichte_name");
            String tag = data.getString("tags_name");
            recipes.put(recipe, tag);
        }
        return recipes.asMap();
    }
}
