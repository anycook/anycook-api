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

package de.anycook.recipe.ingredient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.anycook.db.mysql.DBIngredient;
import de.anycook.db.mysql.DBSearch;
import de.anycook.search.Search;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Ingredient{

    public static Ingredient init(String name) throws SQLException, DBIngredient.IngredientNotFoundException {
        try (DBIngredient db = new DBIngredient()) {
            return db.getIngredient(name);
        }
    }

    public static Ingredient init(String name, String amount) throws SQLException,
        DBIngredient.IngredientNotFoundException {
        try (DBIngredient db = new DBIngredient()) {
            return db.get(name, amount);
        }
    }

    public String name = null;
    public String singular = null;
    public String amount = null;
    public Integer recipecounter = null;
    public List<String> recipes = null;

    public Ingredient() {
    }

    public Ingredient(String name) {
        this(name, null);

    }

    public Ingredient(String name, String amount) {
        this(name, amount, -1);
    }

    public Ingredient(String name, String singular, int gerichte) {
        this(name, singular, null, gerichte);
    }

    public Ingredient(String name, String singular, List<String> gerichte) {
        this.name = name;
        this.singular = singular;
        this.recipes = gerichte;
        this.recipecounter = gerichte.size();

    }

    public Ingredient(String name, String singular, String amount) {
        this(name, singular, amount, -1);
    }

    public Ingredient(String name, String singular, String amount, int recipecounter) {
        this.name = name;
        this.singular = singular;
        this.amount = amount;
        this.recipecounter = recipecounter;
    }


    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Ingredient && name.equals(((Ingredient) obj).name);
    }

    public List<Ingredient> getChildren() throws SQLException {
        try (DBIngredient db = new DBIngredient()) {
            return db.getIngredientsByParent(name);
        }
    }

    @JsonIgnore
    public List<String> getRecipes() throws SQLException {
        try (DBSearch db = new DBSearch()) {
            return new LinkedList<>(db.getRecipesByIngredient(name));
        }
    }

    @JsonIgnore
    public List<String> getChildRecipes() throws SQLException {
        Set<String> children = Search.getChildren(name);
        children.add(name);

        try (DBSearch db = new DBSearch()) {
            return new LinkedList<>(db.getRecipesByIngredients(children));
        }
    }


}
