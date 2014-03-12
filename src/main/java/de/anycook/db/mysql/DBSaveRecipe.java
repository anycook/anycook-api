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

import de.anycook.newrecipe.NewRecipe;
import de.anycook.recipe.ingredient.Ingredient;
import de.anycook.recipe.step.Step;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;


public class DBSaveRecipe extends DBRecipe {

    public DBSaveRecipe() throws SQLException {
        super();
    }

    public void newVersion(int id, NewRecipe newRecipe) throws ParseException, SQLException, IOException {
        newVersion(id, newRecipe, -1);
    }

    // new recipe and new version
    public void newVersion(int id, NewRecipe newRecipe, int userId) throws IOException, ParseException, SQLException {
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO versions(id, gerichte_name, " +
                "eingefuegt, users_id, beschreibung, skill, kalorien, imagename, std, min, personen, " +
                "kategorien_name,comment) VALUES(?,?, NOW(), ?, ?, ?, ?, ? , ? ,? ,?, ?,?)");


        pStatement.setInt(1, id);
        pStatement.setString(2, newRecipe.name);
        pStatement.setInt(3, userId);
        pStatement.setString(4, newRecipe.description);
        pStatement.setInt(5, newRecipe.skill);
        pStatement.setInt(6, newRecipe.calorie);
        pStatement.setString(7, newRecipe.image);
        pStatement.setInt(8, newRecipe.time.std);
        pStatement.setInt(9, newRecipe.time.min);
        pStatement.setInt(10, newRecipe.persons);
        pStatement.setString(11, newRecipe.category);
        pStatement.setString(12, newRecipe.comment);
        pStatement.executeUpdate();

        addSteps(newRecipe.name, id, newRecipe.steps);
        addIngredients(newRecipe.name, id, newRecipe.ingredients);
        logger.info("newversion for " + newRecipe.name + " added from " + userId);
    }

    public int getLastId(String recipeName) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT id from versions WHERE gerichte_name = ? " +
                "ORDER BY id DESC LIMIT 1");

        pStatement.setString(1, recipeName);
        try (ResultSet data = pStatement.executeQuery()) {
            if (data.next())
                return data.getInt("id");
            return 0;
        }
    }

    public void newRecipe(String name) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO gerichte(name, eingefuegt) " +
                "VALUES (?, NOW())");
        pStatement.setString(1, name);
        pStatement.executeUpdate();
    }

    private void addSteps(String recipeName, int versionId, List<Step> steps) throws SQLException {
        for (Step step : steps) {
            addStep(recipeName, step.getId(), versionId, step.getText(), step.getIngredients());
        }
    }

    /**
     * Erstellt neuen Schritt zu einem Gericht
     *
     * @param description
     * @param ingredients
     * @return bei erfolg true, sonst false
     */
    private void addStep(String recipeName, int stepId, int versionId, String description,
                                      List<Ingredient> ingredients) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO schritte(idschritte, versions_id, " +
                "versions_gerichte_name, beschreibung) VALUES (?,?,?,?)");

        pStatement.setInt(1, stepId);
        pStatement.setInt(2, versionId);
        pStatement.setString(3, recipeName);
        pStatement.setString(4, description);
        pStatement.executeUpdate();

        addStepIngredients(recipeName, stepId, versionId, ingredients);
    }

    private void addStepIngredients(String recipeName, int stepId,
                                    int versionId, List<Ingredient> ingredients) throws SQLException {

        int position = 1;

        for (Ingredient ing : ingredients) {
            addStepIngredient(recipeName, stepId, versionId, ing, position++);
        }

    }

    private void addStepIngredient(String recipeName, int stepId, int versionId, Ingredient ingredient, int i)
            throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO " +
                "schritte_has_zutaten(schritte_idschritte, schritte_versions_id, schritte_versions_gerichte_name, " +
                "zutaten_name, menge, position) VALUES (?,?,?,?, ?,?)");

        pStatement.setInt(1, stepId);
        pStatement.setInt(2, versionId);
        pStatement.setString(3, recipeName);
        pStatement.setString(4, ingredient.name);
        pStatement.setString(5, ingredient.menge);
        pStatement.setInt(6, i);
        pStatement.executeUpdate();
    }

    private void addIngredients(String recipeName, int versionId, List<Ingredient> ingredients)
            throws SQLException, IOException, ParseException {
        for (Ingredient ingredient : ingredients)
            addIngredient(recipeName, versionId, ingredient);
    }

    /**
     * Fuegt Zutat zu bestehendem Gericht hinzu
     *
     * @throws java.sql.SQLException
     */
    public void addIngredient(String recipeName, int versionId, Ingredient ingredient)
            throws SQLException, IOException, ParseException {


        try (DBIngredient dbIngredient = new DBIngredient()) {
            if (!dbIngredient.exists(ingredient.name)) dbIngredient.newIngredient(ingredient.name);
        }

        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO versions_has_zutaten" +
                "(versions_gerichte_name, versions_id, zutaten_name, menge) VALUES (?,?,?,?)");

        pStatement.setString(1, recipeName);
        pStatement.setInt(2, versionId);
        pStatement.setString(3, ingredient.name);
        pStatement.setString(4, ingredient.menge);
        pStatement.executeUpdate();
        logger.info("added ingredient '" + ingredient.name + "' to " + recipeName);
    }

    public void addTags(String recipeName, Set<String> tags, int userId) throws SQLException {
        for (String tag : tags) addTag(recipeName, tag, userId);
    }

    /**
     * Ordnet tag einem Gericht zu, wenn tag noch nicht vorhanden, wird es angelegt
     *
     * @param recipe Name des Gerichts
     * @param tag    Name des Tags
     */
    public void addTag(String recipe, String tag, int userId) throws SQLException {
        try (DBTag dbTag = new DBTag()) {
            if (!dbTag.exists(tag)) dbTag.create(tag);
        }

        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO gerichte_has_tags(gerichte_name, " +
                "tags_name, active, users_id) VALUES (?,?, 1, ?)");
        pStatement.setString(1, recipe);
        pStatement.setString(2, tag);
        pStatement.setInt(3, userId);
        pStatement.executeUpdate();
        logger.info("added tag '" + tag + "' to " + recipe);
    }

    public boolean isTasty(String recipeName, int userId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT * FROM schmeckt WHERE users_id = ? " +
                "AND gerichte_name = ?");
        pStatement.setInt(1, userId);
        pStatement.setString(2, recipeName);
        ResultSet data = pStatement.executeQuery();
        return data.next();
    }

    public void makeTasty(String recipeName, int userId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO schmeckt(gerichte_name, users_id, eingefuegt) VALUES (?,?, NOW())");
        pStatement.setString(1, recipeName);
        pStatement.setInt(2, userId);
        pStatement.executeUpdate();
    }

    public void unmakeTasty(String recipeName, int userId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("DELETE FROM schmeckt WHERE gerichte_name = ? " +
                "AND users_id = ?");
        pStatement.setString(1, recipeName);
        pStatement.setInt(2, userId);
        pStatement.executeUpdate();
    }

    public void unmakeTasty(int userId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("DELETE FROM schmeckt WHERE users_id = ?");
        pStatement.setInt(1, userId);
        pStatement.executeUpdate();
    }

    public void suggestTag(String recipeName, String tag, int userId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO gerichte_has_tags (gerichte_name, " +
                "tags_name, users_id) VALUES (?,?,?)");

        pStatement.setString(1, recipeName);
        pStatement.setString(2, tag);
        pStatement.setInt(3, userId);
        pStatement.executeUpdate();
        logger.info(userId + " suggested new tag \"" + tag + "\" for " + recipeName);
    }

    public void changeImageName(String recipeName, int versionId, String newImageName) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("UPDATE versions SET imagename = ? " +
                "WHERE gerichte_name = ? AND id = ?");

        pStatement.setString(1, newImageName);
        pStatement.setString(2, recipeName);
        pStatement.setInt(3, versionId);

        pStatement.executeUpdate();
    }

    public void changeStep(String recipeName, int versionId, int stepId, String newText) throws SQLException {

        PreparedStatement pStatement = connection.prepareStatement("UPDATE schritte SET beschreibung = ? " +
                "WHERE versions_gerichte_name = ? AND versions_id = ? AND idschritte = ?");

        pStatement.setString(1, newText);
        pStatement.setString(2, recipeName);
        pStatement.setInt(3, versionId);
        pStatement.setInt(4, stepId);

        pStatement.executeUpdate();

    }

    public void deleteIngredient(String zutat) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("DELETE FROM zutaten WHERE name = ?");

        pStatement.setString(1, zutat);
        pStatement.executeUpdate();
    }


}
