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

import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import de.anycook.recipe.Time;
import de.anycook.utils.comparators.InvertedComparator;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class DBSearch extends DBHandler {

    public DBSearch() throws SQLException {
        super();
    }

    /**
     * Gibt ein {@link java.util.HashSet} von Gerichtenamen zurueck, die bestimmte Zutaten enthalten.
     * Die Zutaten sind in einer {@link java.util.List} von String abgespeichert.
     * Die Methode itteriert ueber diese Liste, fragt fuer die jeweilige Zutat aus der Datenbank die Gerichte ab,
     * die diese Zutat enthalten und bildet mit den bisherigen Ergebnissen eine Schnittmenge.
     * Nachdem dies fuer alle Zutaten der Liste durchgefuehrt wurde, wird die entstandene Schnittmenge
     * als Set mit String zurueckgegeben.
     *
     * @param ingredients {@link java.util.Set} mit String die Zutatennamen enthalten.
     * @return {@link java.util.Set} mit String, dass die Gerichte beinhaltet, die alle Zutaten der Liste enthalten.
     */
    public Set<String> getRecipesByIngredients(Set<String> ingredients) throws SQLException {
        Set<String> recipes = null;
        for (String ingredient : ingredients) {
            Set<String> temp = new LinkedHashSet<>(getRecipesByIngredient(ingredient));

            if (recipes == null)
                recipes = temp;
            else
                recipes.addAll(temp);
        }
        return recipes;
    }

    public Set<String> getRecipesByIngredient(String ingredient) throws SQLException {
        Set<String> recipes = new LinkedHashSet<>();
        CallableStatement call = connection.prepareCall("{call search_by_ingredient(?)}");
        call.setString(1, ingredient);

        try (ResultSet data = call.executeQuery()) {
            while (data.next())
                recipes.add(data.getString("name"));
        }
        return recipes;
    }

    public Set<String> getRecipesWithoutIncredients(Set<String> ingredients) throws SQLException {
        Set<String> recipes = null;
        for (String ingredient : ingredients) {
            Set<String> temp = new LinkedHashSet<>(getRecipesWithoutIngredient(ingredient).values());

            if (recipes == null)
                recipes = temp;
            else
                recipes.addAll(temp);
        }
        return recipes;
    }

    public SortedSetMultimap<Integer, String> getRecipesWithoutIngredient(String ingredient) throws SQLException {
        SortedSetMultimap<Integer, String> recipes = TreeMultimap.create(new InvertedComparator<Integer>(),
                new InvertedComparator<String>());
        PreparedStatement pStatement = connection.prepareStatement(
                "SELECT gerichte2.name, COUNT(schmeckt.users_id) AS schmecktcount FROM gerichte AS gerichte2 " +
                        "LEFT JOIN schmeckt ON gerichte2.name = schmeckt.gerichte_name " +
                        "WHERE gerichte2.name NOT IN (" +
                        "SELECT gerichte.name FROM gerichte " +
                        "INNER JOIN versions ON gerichte.name = versions.gerichte_name AND gerichte.active_id = versions.id " +
                        "INNER JOIN versions_has_zutaten ON versions.gerichte_name = versions_gerichte_name AND id = versions_id " +
                        "WHERE zutaten_name = ?) " +
                        "GROUP BY gerichte2.name");
        pStatement.setString(1, ingredient);
        try (ResultSet data = pStatement.executeQuery()) {
            while (data.next())
                recipes.put(data.getInt("schmecktcount"), data.getString("gerichte2.name"));
        }

        return recipes;
    }

    /**
     * Gibt ein Set mit Gerichten zurück die einen Schwierigkeitsgrad <= skill haben
     *
     * @param skill der gesuchte max. Schwierigkeitsgrad
     * @return Set mit den Gerichtenamen
     */
    public Set<String> getRecipesBySkill(int skill) throws SQLException {
        Set<String> recipes = new LinkedHashSet<>();
        PreparedStatement pStatement = connection.prepareStatement("SELECT name FROM gerichte " +
                "INNER JOIN versions ON gerichte.name = gerichte_name AND active_id = id " +
                "LEFT JOIN schmeckt ON gerichte.name = schmeckt.gerichte_name " +
                "WHERE skill = ? GROUP BY gerichte.name ORDER BY COUNT(schmeckt.users_id) DESC");
        pStatement.setInt(1, skill);
        ResultSet data = pStatement.executeQuery();
        while (data.next())
            recipes.add(data.getString("name"));
        return recipes;

    }

    /**
     * Gibt ein Set mit Gerichten zurueck die eine Kalorienwertung <= kalorien haben
     *
     * @param calories gesuchte max. Kalorien
     * @return Set mit den Gerichtenamen
     */
    public Set<String> getRecipesByCalories(int calories) throws SQLException {
        Set<String> recipes = new LinkedHashSet<>();

        PreparedStatement pStatement = connection.prepareStatement("SELECT name FROM gerichte " +
                "INNER JOIN versions ON gerichte.name = gerichte_name AND active_id = id " +
                "LEFT JOIN schmeckt ON schmeckt.gerichte_name = name " +
                "WHERE kalorien = ? GROUP BY gerichte.name ORDER BY COUNT(schmeckt.users_id) DESC");
        pStatement.setInt(1, calories);
        ResultSet data = pStatement.executeQuery();
        while (data.next())
            recipes.add(data.getString("name"));
        return recipes;

    }

    /**
     * Gibt die Gerichte zurueck, die maximal die gegebene Zeit brauchen.
     *
     * @param time
     * @return {@link java.util.Set} mit den gefundenen Gerichten.
     */
    public Set<String> getRecipesByTime(Time time) throws SQLException {
        Set<String> recipes = new LinkedHashSet<>();

        PreparedStatement pStatement = connection.prepareStatement("SELECT name FROM gerichte " +
                "INNER JOIN versions ON gerichte.name = gerichte_name AND active_id = id " +
                "LEFT JOIN schmeckt ON schmeckt.gerichte_name = name " +
                "WHERE (std<?) OR (std=? AND min<=?) GROUP BY gerichte.name ORDER BY COUNT(schmeckt.users_id) DESC");
        pStatement.setInt(1, time.getStd());
        pStatement.setInt(2, time.getStd());
        pStatement.setInt(3, time.getStd());
        ResultSet data = pStatement.executeQuery();
        while (data.next())
            recipes.add(data.getString(1));
        return recipes;

    }

    /**
     * Gibt alle Gerichte einer Kategorie zurueck
     *
     * @param category gegebene Kategorie
     * @return Set mit den Gerichtenamen
     */
    public Set<String> getRecipesByCategory(String category) throws SQLException {
        Set<String> recipes = new LinkedHashSet<>();

        PreparedStatement pStatement = connection.prepareStatement("SELECT name FROM gerichte " +
                "INNER JOIN versions ON gerichte.name = gerichte_name AND active_id = id " +
                "LEFT JOIN schmeckt ON schmeckt.gerichte_name = name " +
                "WHERE kategorien_name = ? GROUP BY gerichte.name ORDER BY COUNT(schmeckt.users_id) DESC");
        pStatement.setString(1, category);
        ResultSet data = pStatement.executeQuery();
        while (data.next()) {
            recipes.add(data.getString("name"));
        }

        return recipes;
    }

    /**
     * Gibt alle Gerichte mit einem bestimmten Tag zurueck.
     *
     * @param tag Name des zu suchenden Tags.
     * @return {@link java.util.List} mit Gerichtenamen als {@link String}
     */
    public Set<String> getRecipesByTag(String tag) throws SQLException {
        Set<String> recipes = new LinkedHashSet<>();

        PreparedStatement pStatement = connection.prepareStatement("SELECT name from gerichte_has_tags " +
                "INNER JOIN gerichte ON gerichte.name = gerichte_has_tags.gerichte_name " +
                "LEFT JOIN schmeckt ON schmeckt.gerichte_name = name " +
                "WHERE tags_name =? AND active_id >=0 AND gerichte_has_tags.active = 1 GROUP BY gerichte.name " +
                "ORDER BY COUNT(schmeckt.users_id) DESC");
        pStatement.setString(1, tag);
        ResultSet data = pStatement.executeQuery();

        while (data.next())
            recipes.add(data.getString(1));
        return recipes;
    }

    /**
     * Gibt alle Gerichte zurueck, die die gegebenen Tags enthalten
     *
     * @param tags Set mit den gesuchten Tags
     * @return Set mit den Gerichtenamen
     */
    public Set<String> getRecipesByTags(Set<String> tags) throws SQLException {
        Set<String> recipes = null;
        for (String tag : tags) {
            Set<String> temp = getRecipesByTag(tag);
            if (recipes == null)
                recipes = temp;
            else
                recipes.retainAll(temp);
        }

        return recipes;
    }

    public Set<String> getAllRecipes() throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT name FROM gerichte " +
                "INNER JOIN versions ON gerichte.name = gerichte_name AND active_id = id " +
                "LEFT JOIN schmeckt ON schmeckt.gerichte_name = name " +
                "GROUP BY gerichte.name ORDER BY COUNT(schmeckt.users_id) DESC");
        ResultSet data = pStatement.executeQuery();

        Set<String> recipes = new LinkedHashSet<>();
        while (data.next()) {
            recipes.add(data.getString("name"));
        }

        return recipes;

    }

    public Set<String> getChildIngredients(String ingredient) throws SQLException {

        Set<String> ingredients = new HashSet<>();

        PreparedStatement pStatement = connection.prepareStatement("SELECT name from zutaten " +
                "WHERE parent_zutaten_name = ?");
        pStatement.setString(1, ingredient);
        ResultSet data = pStatement.executeQuery();

        while (data.next())
            ingredients.add(data.getString("name"));

        return ingredients;
    }
}
