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

import de.anycook.recipe.ingredient.Ingredient;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class DBIngredient extends DBHandler {

    public DBIngredient() throws SQLException {
        super();
    }

    /**
     * Nutzt {@link de.anycook.db.mysql.DBIngredient#get(String)} um Existenz der Zutat zu ueberpruefen. Wenn Zutat existiert true, sonst false.
     *
     * @param q String mit Zutatenname
     * @return boolean Wenn vorhanden true, sonst false
     */
    public boolean exists(String q) throws SQLException {
        try {
            get(q);
            return true;
        } catch (IngredientNotFoundException e) {
            return false;
        }
    }

    /**
     * Ueberprueft, ob eine Zutat in der Datenbank vorhanden ist und gibt diese in der richtigen Schreibweise zurueck.
     *
     * @param q String mit dem Zutatenname
     * @return String mit dem Zutatenname aus der Datenbank oder null
     */
    public String get(String q) throws SQLException, IngredientNotFoundException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT name from zutaten WHERE name = ? OR singular = ?");
        pStatement.setString(1, q);
        pStatement.setString(2, q);
        ResultSet data = pStatement.executeQuery();

        if (data.next()) return data.getString("name");

        throw new IngredientNotFoundException(q);

    }

    public Ingredient getIngredient(String ingredientName) throws SQLException, IngredientNotFoundException {
        if (ingredientName == null)
            return null;

        PreparedStatement pStatement = connection.prepareStatement("SELECT name, singular FROM zutaten WHERE name = ?");
        pStatement.setString(1, ingredientName);

        ResultSet data = pStatement.executeQuery();
        if (data.next()) {
            String name = data.getString("name");
            String singular = data.getString("singular");
            List<String> recipes = getRecipesForIngredient(name);

            return new Ingredient(name, singular, recipes);
        }

        throw new IngredientNotFoundException(ingredientName);
    }

    public Ingredient get(String ingredientName, String amount) throws SQLException, IngredientNotFoundException {
        if (ingredientName == null)
            return null;

        PreparedStatement pStatement = connection.prepareStatement("SELECT name, singular, " +
                "COUNT(versions_gerichte_name) AS recipes FROM zutaten " +
                "LEFT JOIN versions_has_zutaten ON name = zutaten_name " +
                "WHERE name = ?");
        pStatement.setString(1, ingredientName);

        ResultSet data = pStatement.executeQuery();
        if (data.next()) {
            String name = data.getString("name");
            String singular = data.getString("singular");
            int recipes = data.getInt("recipes");

            return new Ingredient(name, singular, amount, recipes);
        }

        throw new IngredientNotFoundException(ingredientName);
    }

    public List<String> getRecipesForIngredient(String ingredientName) throws SQLException {
        if (ingredientName == null)
            return null;

        List<String> recipes = new LinkedList<>();
        PreparedStatement pStatement = connection.prepareStatement("SELECT gerichte.name FROM gerichte " +
                "INNER JOIN versions ON gerichte.name = versions.gerichte_name AND gerichte.active_id = versions.id " +
                "INNER JOIN versions_has_zutaten ON versions.gerichte_name = versions_gerichte_name AND id = versions_id " +
                "WHERE zutaten_name = ? GROUP BY gerichte.name");
        pStatement.setString(1, ingredientName);
        ResultSet data = pStatement.executeQuery();
        while (data.next()) {
            String name = data.getString("gerichte.name");
            recipes.add(name);
        }

        return recipes;
    }

    public List<Ingredient> getIngredientsByParent(String parent) throws SQLException {
        List<Ingredient> ingredients = new LinkedList<>();
        PreparedStatement pStatement = connection.prepareStatement("SELECT name FROM zutaten " +
                "WHERE parent_zutaten_name = ? GROUP BY name ORDER BY name");

        if (parent == null) pStatement.setNull(1, Types.VARCHAR);
        else pStatement.setString(1, parent);

        ResultSet data = pStatement.executeQuery();
        while (data.next()) {
            Ingredient ingredient = new Ingredient(data.getString("name"));
            ingredients.add(ingredient);
        }

        return ingredients;
    }

    public List<Ingredient> getAllIngredients() {
        List<Ingredient> ingredients = new ArrayList<>();
        try {
            PreparedStatement pStatement = connection.prepareStatement("SELECT name FROM zutaten");
            ResultSet data = pStatement.executeQuery();
            while (data.next()) {
                ingredients.add(new Ingredient(data.getString("name")));
            }


        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at setParentZutat.", e);
        }
        return ingredients;
    }

    public List<Ingredient> getParent() throws SQLException {
        List<Ingredient> ingredients = new ArrayList<>();
        PreparedStatement pStatement = connection.prepareStatement("SELECT name FROM zutaten " +
                "WHERE parent_zutaten_name IS NULL GROUP BY name ORDER BY name");

        ResultSet data = pStatement.executeQuery();
        while (data.next()) {
            Ingredient ingredient = new Ingredient(data.getString("name"));
            ingredients.add(ingredient);
        }

        return ingredients;
    }

    public void newIngredient(String ingredient) throws IOException, ParseException, SQLException {
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO zutaten(name) VALUES(?)");
        pStatement.setString(1, ingredient);
        pStatement.executeUpdate();
        logger.info("added Ingredient '" + ingredient + "'");
    }

    /**
     * Gibt eine Liste von Zutaten und deren Menge zurueck.
     *
     * @param recipeName {@link String} mit dem Gerichtenamen.
     * @return {@link java.util.List} Liste, die Listen von Strings enthaelt. In den einzelnen Unterlisten stehen jeweils der Name und die Menge einer Zutat.
     */
    public List<Ingredient> getRecipeIngredients(String recipeName) throws SQLException {
        List<Ingredient> ingredients = new LinkedList<>();
        CallableStatement cStatement = connection.prepareCall("{call recipe_ingredients(?)}");
        cStatement.setString(1, recipeName);
        ResultSet data = cStatement.executeQuery();
        while (data.next()) {
            String name = data.getString("zutaten_name");
            String singular = data.getString("singular");
            String menge = data.getString("menge");
            if (menge == null) menge = "";
            Ingredient ingredient = new Ingredient(name, singular, menge);
            ingredients.add(ingredient);
        }

        return ingredients;
    }

    /**
     * Gibt eine Liste von Zutaten und deren Menge zurueck.
     *
     * @param recipeName {@link String} mit dem Gerichtenamen.
     * @return {@link java.util.List} Liste, die Listen von Strings enthaelt. In den einzelnen Unterlisten stehen jeweils der Name und die Menge einer Zutat.
     */
    public List<Ingredient> getRecipeIngredients(String recipeName, int versionId) throws SQLException {
        List<Ingredient> ingredients = new LinkedList<>();
        PreparedStatement pStatement = connection.prepareStatement(
                "SELECT zutaten_name, singular, menge FROM versions_has_zutaten " +
                        "INNER JOIN zutaten ON zutaten_name = name " +
                        "WHERE versions_gerichte_name = ? AND versions_id = ? ");
        pStatement.setString(1, recipeName);
        pStatement.setInt(2, versionId);
        ResultSet data = pStatement.executeQuery();
        while (data.next()) {
            String amount = data.getString("menge");
            if (amount == null) amount = "";
            Ingredient ingredient = new Ingredient(data.getString("zutaten_name"), data.getString("singular"), amount);
            ingredients.add(ingredient);
        }

        return ingredients;
    }

    public long getLastModified() throws SQLException {
        CallableStatement statement = connection.prepareCall("SELECT UPDATE_TIME FROM information_schema.tables " +
               "WHERE  TABLE_SCHEMA = 'anycook_db' AND TABLE_NAME = 'zutaten'");
        ResultSet data = statement.executeQuery();
        if(data.next()) {
            return data.getLong("UPDATE_TIME");
        }
        return 0;

    }

    public static class IngredientNotFoundException extends Exception {
        public IngredientNotFoundException(String queryIngredient) {
            super("ingredient does not exist: " + queryIngredient);
        }
    }
}
