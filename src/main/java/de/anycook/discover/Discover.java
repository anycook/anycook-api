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

package de.anycook.discover;

import de.anycook.db.mysql.DBDiscover;
import de.anycook.recipe.Recipe;
import de.anycook.recommendation.Recommendation;

import java.sql.SQLException;
import java.util.List;


public class Discover {
    public static List<Recipe> getRecommendedRecipes(int num, int userId) throws SQLException {
        List<Recipe> recommended = Recommendation.recommend(userId, num);
        if (recommended.size() > 0)
            return recommended;

        return getPopularRecipes(num);
    }

    public static List<Recipe> getTastyRecipes(int num) throws SQLException {
        try(DBDiscover discover = new DBDiscover()) {
            return discover.getTastyRecipes(num);
        }
    }

    public static List<Recipe> getNewestRecipes(int num) throws SQLException {
        try(DBDiscover discover = new DBDiscover()) {
            return discover.getNewestRecipes(num);
        }
    }

    public static List<Recipe> getPopularRecipes(int num) throws SQLException {
        try(DBDiscover discover = new DBDiscover()) {
            return discover.getPopularRecipes(num);
        }
    }
}
