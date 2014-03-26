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

import de.anycook.db.mysql.DBTag;
import de.anycook.user.User;

import java.sql.SQLException;
import java.util.List;


public class Tag {
    private String name;
    private Integer recipeNumber;
    private String recipeName;
    private Boolean active;
    private User suggester;

    public Tag() {}

    public Tag(String name){
        this(name, null);
    }

    public Tag(String name, Integer recipeNumber) {
        this.name = name;
        this.recipeNumber = recipeNumber;
    }

    public Tag(String name, String recipeName, Boolean active, User suggester) {
        this.name = name;
        this.recipeName = recipeName;
        this.active = active;
        this.suggester = suggester;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRecipeNumber() {
        return recipeNumber;
    }

    public void setRecipeNumber(Integer recipeNumber) {
        this.recipeNumber = recipeNumber;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public User getSuggester() {
        return suggester;
    }

    public void setSuggester(User suggester) {
        this.suggester = suggester;
    }

    @Override
    public String toString() {
        return "\"" + name + "\"";
    }

    public static Tag init(String tagName) throws SQLException, DBTag.TagNotFoundException {
        try (DBTag db = new DBTag()) {
            return db.get(tagName);
        }
    }

    public static List<Tag> getAll() throws SQLException {
        try (DBTag db = new DBTag()) {
            return db.getAll();
        }
    }

    public static List<Tag> loadTagsFromRecipe(String recipeName) throws SQLException {
        try (DBTag db = new DBTag()) {
            return db.getTagsForRecipe(recipeName);
        }
    }

    public static int getTotal() throws SQLException {
        try (DBTag db = new DBTag()) {
            return db.getTotal();
        }
    }

    public static List<Tag> getRecipeTags() throws SQLException {
        try(DBTag dbTag = new DBTag()){
            return dbTag.getRecipeTags();
        }
    }

    public static List<Tag> getRecipeTags(boolean active) throws SQLException {
        try(DBTag dbTag = new DBTag()) {
            return dbTag.getRecipeTags(active);
        }
    }

    public static Tag getRecipeTag(String recipeName, String tagName) throws SQLException, DBTag.TagNotFoundException {
        try(DBTag dbTag = new DBTag()) {
            return dbTag.getRecipeTag(recipeName, tagName);
        }
    }

    public static void activateRecipeTag(String recipeName, String tagName) throws SQLException {
        try(DBTag dbTag = new DBTag()) {
            dbTag.activate(recipeName, tagName);
        }
    }

    public static void deleteRecipeTag(String recipeName, String tagName) throws SQLException {
        try(DBTag dbTag = new DBTag()) {
            dbTag.deleteRecipeTag(recipeName, tagName);
        }
    }
}
