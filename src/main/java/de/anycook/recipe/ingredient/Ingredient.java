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

import de.anycook.api.views.PublicView;
import de.anycook.db.mysql.DBIngredient;

import java.sql.SQLException;
import java.util.List;

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

    private String name = null;

    @PublicView
    private String singular = null;

    private String amount = null;

    @PublicView
    private String parent = null;
    private Integer recipeCounter = null;

    @PublicView
    private List<String> recipes = null;

    public Ingredient() {
    }

    public Ingredient(String name) {
        this(name, null);
    }

    public Ingredient(String name, String amount) {
        this(name, amount, null, null);
    }

    public Ingredient(String name, String singular, Integer recipeCounter) {
        this(name, singular, null, recipeCounter);
    }

    public Ingredient(String name, String singular, List<String> recipes) {
        this.name = name;
        this.singular = singular;
        this.recipes = recipes;
        this.recipeCounter = recipes.size();

    }

    public Ingredient(String name, String singular, String amount) {
        this(name, singular, amount, null);
    }

    public Ingredient(String name, String singular, String amount, Integer recipeCounter) {
        this.name = name;
        this.singular = singular;
        this.amount = amount;
        this.recipeCounter = recipeCounter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSingular() {
        return singular;
    }

    public void setSingular(String singular) {
        this.singular = singular;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public Integer getRecipeCounter() {
        return recipeCounter;
    }

    public void setRecipeCounter(Integer recipeCounter) {
        this.recipeCounter = recipeCounter;
    }

    public List<String> getRecipes() {
        return this.recipes;
    }

    public void setRecipes(List<String> recipes) {
        this.recipes = recipes;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Ingredient && name.equals(((Ingredient) obj).name);
    }

    /*@JsonIgnore
    public List<String> getRecipes() throws SQLException {
        try (DBSearch db = new DBSearch()) {
            return new LinkedList<>(db.getRecipesByIngredient(name));
        }
    }*/

    @Override
    public String toString() {
        return "Ingredient{" +
                "name='" + name + '\'' +
                ", singular='" + singular + '\'' +
                ", amount='" + amount + '\'' +
                ", parent='" + parent + '\'' +
                ", recipeCounter=" + recipeCounter +
                ", recipes=" + recipes +
                '}';
    }
}
