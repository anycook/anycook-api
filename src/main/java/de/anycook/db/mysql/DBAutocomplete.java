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
import de.anycook.recipe.tag.Tag;
import de.anycook.user.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Von DBHandler abgeleitete Klasse. Beinhaltet alle Methoden mit Autocomplete.
 *
 * @author Jan Grassegger
 * @see de.anycook.db.mysql.DBHandler
 */
public class DBAutocomplete extends DBHandler {

    public DBAutocomplete() throws SQLException {
        super();
    }

    /**
     * Gibt eine Liste von Zutaten zurueck, die q in ihrem Namen enthalten.
     *
     * @param q Zu vervollstaendigender String
     * @return {@link java.util.List} mit Strings, die q enthalten.
     */
    public List<Ingredient> autocompleteIngredient(String q, int maxResults, Set<String> excludedIngredients)
            throws SQLException {
        List<Ingredient> list = new LinkedList<>();

        PreparedStatement pStatement = connection.prepareStatement("SELECT name from zutaten WHERE name LIKE ? LIMIT ?");
        pStatement.setString(1, q + "%");
        int size = maxResults +
                (excludedIngredients == null ? 0 : excludedIngredients.size());
        pStatement.setInt(2, size);

        try (ResultSet data = pStatement.executeQuery()) {
            while (data.next() && list.size() < maxResults) {
                String name = data.getString("name");
                if (excludedIngredients != null && excludedIngredients.contains(name))
                    continue;

                list.add(new Ingredient(name));
            }
        }
        return list;
    }

    /**
     * Gibt alle Gerichte zurueck, die die gegebene Zeichenfolge enthalten.
     *
     * @param q {@link String} mit der zu vervollstaendigenden Zeichenfolge.
     * @return {@link java.util.List} mit den gefundenen Gerichten
     */
    public List<String> autocompleteRecipe(String q, int maxResults) throws SQLException {
        List<String> list = new LinkedList<>();

        PreparedStatement pStatement = connection.prepareStatement("SELECT name from gerichte WHERE name LIKE ? AND active_id >=0");
        pStatement.setString(1, q + "%");
        ResultSet data = pStatement.executeQuery();
        int counter = 0;
        while (data.next() && counter < maxResults) {
            list.add(data.getString("name"));
            counter++;
        }
        return list;
    }

    /**
     * Gibt eine Liste von Kategorien zurueck, die q in ihrem Namen enthalten.
     *
     * @param q Zu vervollstaendigender String
     * @return {@link java.util.LinkedList} mit Strings mit Kategorienamen, die q enthalten.
     */
    public List<String> autocompleteCategory(String q, int maxResults, String excludedCategory) throws SQLException {
        List<String> list = new LinkedList<>();

        PreparedStatement pStatement = connection.prepareStatement("SELECT name from kategorien WHERE name LIKE ? LIMIT ?");
        pStatement.setString(1, q + "%");
        int size = maxResults + (excludedCategory == null ? 0 : 1);
        pStatement.setInt(2, size);

        ResultSet data = pStatement.executeQuery();
        while (data.next() && list.size() < size) {
            String name = data.getString("name");

            if (excludedCategory != null && excludedCategory.equals(name))
                continue;
            list.add(name);
        }
        return list;
    }

    /**
     * Gibt eine Liste von Tags zurueck, die q enthalten.
     *
     * @param q            gebebene Zeichenfolge
     * @param excludedTags
     * @return {@link java.util.LinkedList} mit Strings mit Kategorienamen, die q enthalten.
     */
    public List<Tag> autocompleteTag(String q, int maxResults, Set<String> excludedTags) throws SQLException {
        List<Tag> list = new LinkedList<>();
        PreparedStatement pStatement = connection.prepareStatement("SELECT name from tags LEFT JOIN gerichte_has_tags " +
                "ON name = tags_name WHERE active = 1 AND name LIKE ? GROUP BY name LIMIT ?");
        pStatement.setString(1, q + "%");
        int size = maxResults + (excludedTags == null ? 0 : excludedTags.size());
        pStatement.setInt(2, size);
        ResultSet data = pStatement.executeQuery();

        while (data.next() && list.size() < maxResults) {
            String name = data.getString("name");
            if (excludedTags != null && excludedTags.contains(name))
                continue;

            list.add(new Tag(name));

        }
        return list;
    }


    /**
     * Gibt eine Liste von Usernamen zurück, die mit q anfangen
     *
     * @param q
     * @param maxResults
     * @return {@link java.util.LinkedList} mit Strings mit usernamen, die q enthalten.
     */
    public List<User> autocompleteUser(String q, int maxResults, Set<Integer> exclude) throws SQLException {
        List<User> users = new LinkedList<>();
        PreparedStatement pStatement = connection.prepareStatement("SELECT nickname, id, image from users " +
                "WHERE nickname RLIKE ? GROUP BY nickname LIMIT ?");
        pStatement.setString(1, "^(" + q + "|.+ " + q + ")");

        int searchSize = maxResults + (exclude == null ? 0 : exclude.size());
        pStatement.setInt(2, searchSize);
        ResultSet data = pStatement.executeQuery();
        while (data.next() && users.size() < maxResults) {
            String username = data.getString("nickname");
            int id = data.getInt("id");
            String image = data.getString("image");
            if (exclude != null && exclude.contains(id)) continue;
            users.add(new User(id, username, image));
        }
        return users;

    }
}
