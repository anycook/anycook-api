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
import de.anycook.recipe.step.Step;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class DBStep extends DBHandler {

    public DBStep() throws SQLException {
        super();
    }

    public List<Step> loadRecipeSteps(String recipeName) throws SQLException {
        List<Step> steps = new LinkedList<>();
        PreparedStatement pStatement = connection.prepareStatement(
                "SELECT idschritte, schritte.beschreibung FROM gerichte " +
                        "INNER JOIN schritte ON active_id = versions_id AND gerichte.name = versions_gerichte_name " +
                        "WHERE gerichte.name = ? " +
                        "ORDER BY idschritte");
        pStatement.setString(1, recipeName);
        try (ResultSet data = pStatement.executeQuery()) {
            while (data.next()) {
                int stepId = data.getInt("idschritte");
                String description = data.getString("schritte.beschreibung");
                List<Ingredient> ingredients = loadStepIngredients(recipeName, stepId);

                steps.add(new Step(stepId, description, ingredients));
            }
        }

        return steps;
    }


    public List<Step> loadRecipeSteps(String recipeName, int versionId) throws SQLException {
        List<Step> steps = new LinkedList<>();
        PreparedStatement pStatement = connection.prepareStatement(
                "SELECT idschritte, beschreibung FROM schritte " +
                        "WHERE versions_gerichte_name = ? AND versions_id = ? " +
                        "ORDER BY idschritte");
        pStatement.setString(1, recipeName);
        pStatement.setInt(2, versionId);

        try (ResultSet data = pStatement.executeQuery()) {
            while (data.next()) {
                int stepId = data.getInt("idschritte");
                String description = data.getString("schritte.beschreibung");
                List<Ingredient> ingredients = loadStepIngredients(recipeName, versionId, stepId);

                steps.add(new Step(stepId, description, ingredients));
            }
        }
        return steps;
    }

    public List<Ingredient> loadStepIngredients(String recipeName, int stepId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT zutaten_name, singular, menge FROM gerichte " +
                "INNER JOIN schritte_has_zutaten ON active_id = schritte_versions_id AND gerichte.name = schritte_versions_gerichte_name " +
                "LEFT JOIN zutaten ON zutaten_name = zutaten.name " +
                "WHERE gerichte.name = ? AND schritte_idschritte = ? " +
                "ORDER BY position");
        preparedStatement.setString(1, recipeName);
        preparedStatement.setInt(2, stepId);

        try (ResultSet data = preparedStatement.executeQuery()) {
            List<Ingredient> ingredients = new LinkedList<>();

            while (data.next()) {
                String ingredientName = data.getString("zutaten_name");
                String singular = data.getString("singular");
                String amount = data.getString("menge");
                ingredients.add(new Ingredient(ingredientName, singular, amount));
            }

            return ingredients;
        }
    }

    public List<Ingredient> loadStepIngredients(String recipeName, int versionId, int stepId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT zutaten_name, singular, menge FROM schritte_has_zutaten " +
                "LEFT JOIN zutaten ON zutaten_name = zutaten.name " +
                "WHERE schritte_versions_gerichte_name = ? AND schritte_versions_id = ? AND schritte_idschritte = ? " +
                "ORDER BY position");
        preparedStatement.setString(1, recipeName);
        preparedStatement.setInt(2, versionId);
        preparedStatement.setInt(3, stepId);

        try (ResultSet data = preparedStatement.executeQuery()) {
            List<Ingredient> ingredients = new LinkedList<>();

            while (data.next()) {
                String ingredientName = data.getString("zutaten_name");
                String singular = data.getString("singular");
                String amount = data.getString("menge");
                ingredients.add(new Ingredient(ingredientName, singular, amount));
            }

            return ingredients;
        }
    }
}
