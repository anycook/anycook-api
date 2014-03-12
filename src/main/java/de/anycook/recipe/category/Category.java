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

import java.sql.SQLException;
import java.util.List;


public class Category {

    private String name;
    private int recipeNumber;
    private int sortId;

    public Category(){}

    public Category(String name) {
        this.name = name;
    }

    public Category(String name, int sortId, int recipeNumber) {
        this.name = name;
        this.sortId = sortId;
        this.recipeNumber = recipeNumber;
    }

    public static Category init(String categoryName) throws SQLException, DBCategory.CategoryNotFoundException {
        try(DBCategory dbCategory = new DBCategory()) {
            return dbCategory.get(categoryName);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRecipeNumber() {
        return recipeNumber;
    }

    public void setRecipeNumber(int recipeNumber) {
        this.recipeNumber = recipeNumber;
    }

    public int getSortId() {
        return sortId;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }

    public static List<Category> getAll() throws SQLException {
        try (DBCategory dbCategory = new DBCategory()) {
            return dbCategory.getAllCategories();
        }
    }

    public static List<Category> getAllSorted() throws SQLException {
        try (DBCategory dbCategory = new DBCategory()) {
            return dbCategory.getAllCategoriesSorted();
        }
    }
}
