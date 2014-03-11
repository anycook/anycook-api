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

package de.anycook.recipe.category;

import de.anycook.db.mysql.DBCategory;
import de.anycook.db.mysql.DBSearch;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Category {

    public final String name;
    public final List<String> recipes;
    public final Integer recipeNumber;

    public Category(String name) {
        this.name = name;
        this.recipes = null;
        this.recipeNumber = null;
    }

    public Category(String name, List<String> recipes) {
        this.name = name;
        this.recipes = recipes;
        this.recipeNumber = recipes.size();
    }

    public static Category init(String categoryName) throws SQLException, DBCategory.CategoryNotFoundException {
        try(DBCategory dbCategory = new DBCategory();
            DBSearch dbsearch = new DBSearch()) {

            String category = dbCategory.get(categoryName);
            Set<String> recipes = dbsearch.getRecipesByCategory(category);
            return new Category(category, new LinkedList<>(recipes));
        }
    }

    public static Map<String, Integer> getAll() throws SQLException {
        try (DBCategory dbCategory = new DBCategory()) {
            return dbCategory.getAllCategories();
        }
    }

    public static Map<String, Integer> getAllSorted() throws SQLException {
        try (DBCategory dbCategory = new DBCategory()) {
            return dbCategory.getAllCategoriesSorted();
        }
    }
}
