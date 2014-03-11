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

package de.anycook.recipe.tag;

import de.anycook.db.mysql.DBGetRecipe;
import de.anycook.db.mysql.DBTag;

import java.sql.SQLException;
import java.util.List;


public class Tag {
    public final String name;
    public final int count;
    public final List<String> recipes;

    public Tag(String name) {
        this(name, 0);
    }

    public Tag(String name, int count) {
        this.name = name;
        this.count = count;
        this.recipes = null;
    }

    public Tag(String name, List<String> recipes) {
        this.name = name;
        this.count = recipes.size();
        this.recipes = recipes;
    }

    @Override
    public String toString() {
        return "\"" + name + "\"";
    }

    public static Tag init(String tagName) throws SQLException, DBTag.TagNotFoundException {
        try (DBTag db = new DBTag()) {
            return db.getTagRecipes(tagName);
        }
    }

    public static List<Tag> getAll() throws SQLException {
        try (DBTag db = new DBTag()) {
            return db.getAll();
        }
    }

    public static List<String> loadTagsFromRecipe(String recipeName) throws SQLException {
        try (DBGetRecipe db = new DBGetRecipe()) {
            return db.loadRecipeTags(recipeName);
        }
    }

    public static int getTotal() throws SQLException {
        try (DBTag db = new DBTag()) {
            return db.getTotal();
        }
    }
}
