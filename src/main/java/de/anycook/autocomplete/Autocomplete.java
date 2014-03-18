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

package de.anycook.autocomplete;

import de.anycook.db.mysql.DBAutocomplete;
import de.anycook.recipe.ingredient.Ingredient;
import de.anycook.user.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;


public class Autocomplete {

    public static List<Ingredient> autocompleteIngredient(String query, int maxResults,
                                                      Set<String> excludedIngredients) throws SQLException {
        try(DBAutocomplete dbAutocomplete = new DBAutocomplete()) {
            return dbAutocomplete.autocompleteIngredient(query, maxResults, excludedIngredients);
        }
    }

    public static List<String> autocompleteTag(String query, int maxResults,
                                               Set<String> excludedTags) throws SQLException {
        try(DBAutocomplete dbAutocomplete = new DBAutocomplete()) {
            return dbAutocomplete.autocompleteTag(query, maxResults, excludedTags);
        }
    }

    public static List<String> autocompleteCategory(String query, int maxResults,
                                                    String excludedCategory) throws SQLException {
        try(DBAutocomplete dbAutocomplete = new DBAutocomplete()) {
            return dbAutocomplete.autocompleteCategory(query, maxResults, excludedCategory);
        }
    }

    public static List<String> autocompleteRecipe(String query, int maxResults) throws SQLException {
        try(DBAutocomplete dbAutocomplete = new DBAutocomplete()) {
            return dbAutocomplete.autocompleteRecipe(query, maxResults);
        }
    }

    public static List<User> autocompleteUsers(String query, int maxResults,
                                               Set<Integer> exclude) throws SQLException {
        try(DBAutocomplete dbAutocomplete = new DBAutocomplete()) {
            return dbAutocomplete.autocompleteUser(query, maxResults, exclude);
        }
    }

    public static Result autoCompleteAll(String query, String excludedCategory,
                                                      Set<String> excludedIngredients, Set<String> excludedTags,
                                                      Set<Integer> excludedUsers, int maxResults) throws SQLException {
        Result result = new Result();
        //getRecipes
        if(maxResults > 0){
            List<String> results = autocompleteRecipe(query, maxResults);
            result.setRecipes(results);
            maxResults -= results.size();
        }

        if (maxResults > 0) {
            // getIngredientResults
            List<Ingredient> results = autocompleteIngredient(query, maxResults, excludedIngredients);
            result.setIngredients(results);
            maxResults -= results.size();
        }

        if (maxResults > 0) {
            // getCategoryResults
            List<String> results = autocompleteCategory(query, maxResults, excludedCategory);
            result.setCategories(results);
            maxResults -= results.size();
        }

        if (maxResults > 0) {
            //getTagsResults
            List<String> results = autocompleteTag(query, maxResults, excludedTags);
            result.setTags(results);
            maxResults -= results.size();
        }
        if (maxResults > 0) {
            List<User> userResults = autocompleteUsers(query, maxResults, excludedUsers);
            result.setUser(userResults);
        }

        return result;
    }

    public static class Result {
        private List<String> recipes;
        private List<Ingredient> ingredients;
        private List<String> categories;
        private List<String> tags;
        private List<User> user;

        public List<String> getRecipes() {
            return recipes;
        }

        public void setRecipes(List<String> recipes) {
            this.recipes = recipes;
        }

        public List<Ingredient> getIngredients() {
            return ingredients;
        }

        public void setIngredients(List<Ingredient> ingredients) {
            this.ingredients = ingredients;
        }

        public List<String> getCategories() {
            return categories;
        }

        public void setCategories(List<String> categories) {
            this.categories = categories;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public List<User> getUser() {
            return user;
        }

        public void setUser(List<User> user) {
            this.user = user;
        }
    }

}
