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
import de.anycook.db.mysql.DBUser;
import de.anycook.location.Location;
import de.anycook.recipe.Recipe;
import de.anycook.recommendation.Recommendation;

import java.sql.SQLException;
import java.util.*;


public class Discover {
    public static Discover.Recipes getDiscoverRecipes(int num) throws SQLException {
        Recipes recipes = new Recipes();
        try (DBDiscover dbDiscover = new DBDiscover()){
            recipes.setNewest(dbDiscover.getNewestRecipes(num, -1));
            recipes.setTasty(dbDiscover.getTastyRecipes(num, -1));
            recipes.setRecommended(dbDiscover.getPopularRecipes(num, -1));
        }
        return recipes;
    }

    public static Discover.Recipes getDiscoverRecipes(int num, int userId) throws SQLException {
        Recipes recipes = new Recipes();
        try (DBDiscover dbDiscover = new DBDiscover()){
            recipes.setNewest(dbDiscover.getNewestRecipes(num, userId));
            recipes.setTasty(dbDiscover.getTastyRecipes(num, userId));
            List<Recipe> recommended = Recommendation.recommend(userId, num);
            if(recommended.size() < 0)
                recommended = dbDiscover.getPopularRecipes(num, userId);
            recipes.setRecommended(recommended);
        }
        return recipes;
    }


    public static List<Recipe> getRecommendedRecipes(int num, int userId) throws SQLException {
        List<Recipe> recommended = Recommendation.recommend(userId, num);
        if (recommended.size() > 0)
            return recommended;

        return getPopularRecipes(num, userId);
    }

    public static List<Recipe> getTastyRecipes(int num, int loginId) throws SQLException {
        try(DBDiscover discover = new DBDiscover()) {
            return discover.getTastyRecipes(num, loginId);
        }
    }

    public static List<Recipe> getNewestRecipes(int num, int loginId) throws SQLException {
        try(DBDiscover discover = new DBDiscover()) {
            return discover.getNewestRecipes(num, loginId);
        }
    }

    public static List<Recipe> getPopularRecipes(int num, int loginId) throws SQLException {
        try(DBDiscover discover = new DBDiscover()) {
            return discover.getPopularRecipes(num, loginId);
        }
    }

    public static List<Recipe> getNearRecipes(Location location, double maxRadius, int recipeNumber) throws SQLException {
        Set<Recipe> recipeSet = new TreeSet<>((o1, o2) -> -Long.compare(o1.getLastChange(), o2.getLastChange()));

        try(DBUser dbUser = new DBUser()) {
            Map<Integer, Location> locations = dbUser.getUserLocations();
            for(Map.Entry<Integer, Location> entry : locations.entrySet()) {
                if(location.isInRadius(maxRadius, entry.getValue())) {
                    List<Recipe> recipes = de.anycook.recipe.Recipes.getRecipesFromUser(entry.getKey(), -1);
                    recipeSet.addAll(recipes);
                }
            }
        }

        List<Recipe> recipes = new LinkedList<>(recipeSet);
        return recipes.subList(0, Math.min(recipeNumber, recipes.size()));
    }

    public static class Recipes{
        private List<Recipe> recommended;
        private List<Recipe> tasty;
        private List<Recipe> newest;

        public List<Recipe> getRecommended() {
            return recommended;
        }

        public void setRecommended(List<Recipe> recommended) {
            this.recommended = recommended;
        }

        public List<Recipe> getTasty() {
            return tasty;
        }

        public void setTasty(List<Recipe> tasty) {
            this.tasty = tasty;
        }

        public List<Recipe> getNewest() {
            return newest;
        }

        public void setNewest(List<Recipe> newest) {
            this.newest = newest;
        }
    }
}
