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

import de.anycook.recipe.Recipe;
import de.anycook.user.User;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.sql.*;
import java.util.*;
import java.util.Date;


public class DBGetRecipe extends DBRecipe {



    public DBGetRecipe() throws SQLException {
        super();
    }


    public Recipe get(String name) throws SQLException, RecipeNotFoundException {
        return get(name, -1);
    }
    /**
     * Gibt alle Daten eines Gerichts zurueck. Sucht das Gericht per Gerichtenamen und schreibt den Namen, die Wertung,
     * die Beschreibung, die Dauer, den Schwierigkeitsgrad, die Kalorien, den Bildlink und
     * ob es vegetarisch ist in eine {@link java.util.Map} und gibt diese zurueck.
     *
     * @param name {@link String} mit dem Gerichtenamen
     * @return {@link java.util.Map} mit den Daten des Gerichts.
     */
    public Recipe get(String name, int loginId) throws RecipeNotFoundException, SQLException {
        PreparedStatement preparedStatement =
            connection.prepareStatement("SELECT versions.id AS id, gerichte.name AS name, beschreibung, " +
                "IFNULL(versions.imagename, CONCAT('category/', kategorien.image)) AS image, " +
                "gerichte.eingefuegt AS created," +
                "min, std, skill, kalorien, personen, kategorien_name, active_id, users_id, nickname, users.image, " +
                "viewed, last_change, " +
                "(SELECT COUNT(users_id) FROM schmeckt WHERE schmeckt.gerichte_name = ? " +
                "AND schmeckt.users_id = ?) AS tastes FROM gerichte " +
                "INNER JOIN versions ON IF(active_id > 0, gerichte.name = gerichte_name " +
                "AND active_id = versions.id, gerichte.name = gerichte_name) " +
                "INNER JOIN users ON users_id = users.id " +
                "INNER JOIN kategorien ON kategorien_name = kategorien.name " +
                "WHERE gerichte.name = ?;");
        preparedStatement.setString(1, name);
        preparedStatement.setInt(2, loginId);
        preparedStatement.setString(3, name);
        ResultSet data = preparedStatement.executeQuery();

        if (!data.next()) throw new RecipeNotFoundException(name);
        return getRecipe(data);
    }

    public List<Recipe> getAllRecipes(int loginId) throws SQLException {
        PreparedStatement preparedStatement =
            connection.prepareStatement("SELECT versions.id AS id, gerichte.name AS name, beschreibung, " +
                "IFNULL(versions.imagename, " +
                "CONCAT('category/', kategorien.image)) AS image, gerichte.eingefuegt AS created, " +
                "min, std, skill, kalorien, personen, kategorien_name, " +
                "active_id, users_id, users.image, nickname, viewed, last_change, " +
            "(SELECT COUNT(users_id) FROM schmeckt " +
                "WHERE schmeckt.gerichte_name = gerichte.name AND schmeckt.users_id = ?) AS tastes " +
            "FROM gerichte " +
            "INNER JOIN versions ON IF(active_id > 0, gerichte.name = gerichte_name " +
                "AND active_id = versions.id, gerichte.name = gerichte_name) " +
            "INNER JOIN users ON users_id = users.id " +
            "INNER JOIN kategorien ON kategorien_name = kategorien.name " +
            "GROUP BY name;");
        preparedStatement.setInt(1, loginId);
        ResultSet data = preparedStatement.executeQuery();

        return getRecipes(data);
    }

    public List<Recipe> getAllRecipes(int loginId, Date lastModified) throws SQLException {
        PreparedStatement preparedStatement =
            connection.prepareStatement("SELECT versions.id AS id, gerichte.name AS name, beschreibung, " +
                "IFNULL(versions.imagename, " +
                "CONCAT('category/', kategorien.image)) AS image, gerichte.eingefuegt AS created, " +
                "min, std, skill, kalorien, personen, kategorien_name, " +
                "active_id, users_id, users.image, nickname, viewed, last_change, " +
                "(SELECT COUNT(users_id) FROM schmeckt " +
                "WHERE schmeckt.gerichte_name = gerichte.name AND schmeckt.users_id = ?) AS tastes " +
                "FROM gerichte " +
                "INNER JOIN versions ON IF(active_id > 0, gerichte.name = gerichte_name " +
                "AND active_id = versions.id, gerichte.name = gerichte_name) " +
                "INNER JOIN users ON users_id = users.id " +
                "INNER JOIN kategorien ON kategorien_name = kategorien.name " +
                "WHERE last_change > ? " +
                "GROUP BY name;");
        preparedStatement.setInt(1, loginId);
        preparedStatement.setTimestamp(2, new Timestamp(lastModified.getTime()));
        ResultSet data = preparedStatement.executeQuery();

        return getRecipes(data);
    }


    public List<Recipe> getVersions(String recipeName, int loginId) throws SQLException {
        PreparedStatement preparedStatement =
            connection.prepareStatement("SELECT versions.id AS id, beschreibung, " +
                "IFNULL(versions.imagename, CONCAT('category/', kategorien.image)) AS image, " +
                "gerichte.eingefuegt AS created, min, std, skill, kalorien, gerichte.name, personen, kategorien_name, " +
                "active_id, users_id, nickname, users.image, viewed, versions.eingefuegt AS last_change, " +
                "(SELECT COUNT(users_id) FROM schmeckt WHERE schmeckt.gerichte_name = ? " +
                "AND schmeckt.users_id = ?) AS tastes " +
                "FROM gerichte " +
                "INNER JOIN versions ON gerichte.name = gerichte_name " +
                "INNER JOIN users ON users_id = users.id " +
                "INNER JOIN kategorien ON kategorien_name = kategorien.name " +
                "WHERE gerichte.name = ?;");
        preparedStatement.setString(1, recipeName);
        preparedStatement.setInt(2, loginId);
        preparedStatement.setString(3, recipeName);

        try(ResultSet data = preparedStatement.executeQuery()){
            return getRecipes(data);
        }
    }

    public Recipe getVersionData(String recipeName, int versionId, int loginId) throws RecipeNotFoundException, SQLException {
        PreparedStatement preparedStatement =
            connection.prepareStatement("SELECT versions.id AS id, beschreibung, " +
                "IFNULL(versions.imagename, CONCAT('category/', kategorien.image)) AS image, " +
                "gerichte.eingefuegt AS created, versions.eingefuegt AS last_change, min, std, skill, kalorien, " +
                "gerichte.name, personen, kategorien_name, active_id, users_id, nickname, users.image, viewed, " +
                "(SELECT IF(COUNT(users_id) = 1, TRUE, FALSE) FROM schmeckt " +
                "WHERE schmeckt.gerichte_name = gerichte.name AND schmeckt.users_id = ?) AS tastes " +
                "FROM gerichte " +
                "INNER JOIN versions ON gerichte.name = gerichte_name " +
                "INNER JOIN users ON users_id = users.id " +
                "INNER JOIN kategorien ON kategorien_name = kategorien.name " +
                "WHERE gerichte.name = ? AND versions.id = ?;");
        preparedStatement.setInt(1, loginId);
        preparedStatement.setString(2, recipeName);
        preparedStatement.setInt(3, versionId);

        ResultSet data = preparedStatement.executeQuery();

        if (!data.next()) throw new RecipeNotFoundException(recipeName);
        return getRecipe(data);

    }

    public List<String> getAllRecipeNames() throws SQLException {
        List<String> result = new LinkedList<>();
        PreparedStatement pStatement = connection.prepareStatement("SELECT name FROM gerichte ORDER BY name");
        ResultSet data = pStatement.executeQuery();
        while (data.next()) {
            result.add(data.getString("name"));
        }
        return result;
    }

    public List<String> getAllActiveRecipeNamesWithImage() throws SQLException {
        List<String> result = new ArrayList<>();
        PreparedStatement pStatement = connection.prepareStatement("SELECT name FROM gerichte " +
                "INNER JOIN versions ON  name = gerichte_name AND active_id = id " +
                "WHERE imagename IS NOT NULL AND NOT (imagename = \"\") " +
                "AND name NOT IN (SELECT tagesrezepte.gerichte_name FROM tagesrezepte WHERE DATE > DATE_SUB(curdate(),INTERVAL 1 MONTH)) " +
                "GROUP BY name");

        ResultSet data = pStatement.executeQuery();
        while (data.next()) {
            result.add(data.getString("name"));
        }

        return result;
    }

    /**
     * Gibt einen zufaelligen Gerichtenamen zurueck. Waehlt aus dem Gesamtbestand an Gerichten mithilfe von {@link java.util.Random} eines aus.
     *
     * @return {@link String} mit dem zufaelligen Gerichtenamen
     */
    public String randomRecipe() throws SQLException {
        RandomDataGenerator generator = new RandomDataGenerator();
        List<String> recipes = getAllActiveRecipeNamesWithImage();
        if (recipes.size() == 0) return null;
        return recipes.get(generator.nextInt(1, recipes.size()));
    }

    /**
     * Gibt alle Tags eines Gericht als {@link java.util.List} zurueck.
     *
     * @param recipeName Name des Gerichts
     * @return {@link java.util.List} mit Tags als String
     */
    public List<String> getTags(String recipeName) throws SQLException {
        List<String> tags = new LinkedList<>();
        PreparedStatement pStatement = connection.prepareStatement("SELECT tags_name FROM gerichte_has_tags WHERE gerichte_name = ? AND active = 1");
        pStatement.setString(1, recipeName);
        ResultSet data = pStatement.executeQuery();
        while (data.next())
            tags.add(data.getString(1));

        return tags;
    }

    public List<String> getAllActiveRecipeNames() throws SQLException {
        List<String> result = new LinkedList<>();
        PreparedStatement pStatement = connection.prepareStatement("SELECT name FROM gerichte WHERE active_id > -1 ORDER BY name");

        ResultSet data = pStatement.executeQuery();
        while (data.next()) {
            result.add(data.getString("name"));
        }

        return result;
    }

    public int getActiveIdfromRecipe(String recipe) throws SQLException, RecipeNotFoundException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT active_id FROM gerichte WHERE name = ?");
        pStatement.setString(1, recipe);
        ResultSet data = pStatement.executeQuery();
        if (data.next())
            return data.getInt("active_id");
        throw new RecipeNotFoundException(recipe);
    }

    public int getRecipeNumber() throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT COUNT(name) AS recipenumber FROM gerichte ORDER BY name");
        ResultSet data = pStatement.executeQuery();
        if (data.next())
            return data.getInt("recipenumber");

        return 0;

    }

    public String getRecipeOfTheDay() throws SQLException, RecipeNotFoundException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT gerichte_name FROM tagesrezepte WHERE date = CURDATE()");
        try (ResultSet data = pStatement.executeQuery()) {
            if (data.next())
                return data.getString("gerichte_name");
            throw new RecipeNotFoundException("no recipes found");
        }
    }


    public String createNewRecipeOfTheDay() throws RecipeNotFoundException, SQLException {
        String recipeOfTheDay = randomRecipe();
        if (recipeOfTheDay == null) throw new RecipeNotFoundException("no random recipe found");

        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO tagesrezepte (date, gerichte_name) VALUES (CURDATE(),?)");
        pStatement.setString(1, recipeOfTheDay);
        pStatement.executeUpdate();

        return recipeOfTheDay;
    }

    public Set<Integer> getUsersFromGericht(String recipeName) throws SQLException {
        Set<Integer> users = new HashSet<>();
        PreparedStatement pStatement = connection.prepareStatement("SELECT users_id FROM versions WHERE gerichte_name = ? GROUP BY users_id");
        pStatement.setString(1, recipeName);
        ResultSet data = pStatement.executeQuery();
        while (data.next())
            users.add(data.getInt("users_id"));
        return users;
    }

    /**
     * Ueberprueft, ob eine Zutat noch Rezepte besitzt
     *
     * @param ingredient
     * @return true, wenn zutat noch rezepte besitzt, sonst false
     */
    public boolean checkZutatforGerichte(String ingredient) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT count(versions_gerichte_name) AS counter FROM versions_has_zutaten WHERE zutaten_name = ? GROUP BY versions_gerichte_name");
        pStatement.setString(1, ingredient);
        ResultSet data = pStatement.executeQuery();

        if (data.next())
            return data.getInt("counter") != 0;
        return false;
    }

    /**
     * Ueberprueft, ob eine Zutat noch Children besitzt
     *
     * @param ingredientName
     * @return true, wenn zutat noch children besitzt, sonst false
     */
    public boolean checkZutatforChildrens(String ingredientName) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT * FROM zutaten WHERE parent_zutaten_name = ? GROUP BY name");
        pStatement.setString(1, ingredientName);
        ResultSet data = pStatement.executeQuery();


        if (data.next())
            return true;
        return false;
    }

    public Set<String> getRecipesFromUser(int userId) throws SQLException {
        Set<String> recipes = new HashSet<>();
        PreparedStatement pStatement = connection.prepareStatement("SELECT gerichte_name FROM versions WHERE users_id = ? GROUP BY gerichte_name");
        pStatement.setInt(1, userId);

        ResultSet data = pStatement.executeQuery();
        while (data.next()) {
            recipes.add(data.getString("gerichte_name"));
        }
        return recipes;
    }

    public int getAuthor(String recipeName, int versionId) throws RecipeNotFoundException, SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT users_id FROM versions WHERE gerichte_name = ? AND id = ?");
        pStatement.setString(1, recipeName);
        pStatement.setInt(2, versionId);
        ResultSet data = pStatement.executeQuery();
        if (data.next())
            return data.getInt("users_id");

        throw new RecipeNotFoundException(recipeName, versionId);
    }

    public List<User> getAuthors(String recipeName) throws SQLException {
        List<User> authors = new LinkedList<>();
        PreparedStatement pStatement = connection.prepareStatement("SELECT nickname, users_id, users.image FROM versions INNER JOIN users ON users_id = users.id WHERE gerichte_name = ? GROUP BY nickname ORDER BY versions.id DESC ");
        pStatement.setString(1, recipeName);
        ResultSet data = pStatement.executeQuery();
        while (data.next()) {
            String username = data.getString("nickname");
            int userId = data.getInt("users_id");
            String userImage = data.getString("users.image");
            authors.add(new User(userId, username, userImage));
        }
        return authors;
    }

    public boolean checkUserForGerichte(int userId) throws SQLException {
        return getRecipesFromUser(userId).size() > 0;
    }

    public List<String> getAllTags() throws SQLException {
        List<String> tags = new LinkedList<>();
        PreparedStatement pStatement = connection.prepareStatement("SELECT name FROM tags");
        ResultSet data = pStatement.executeQuery();
        while (data.next())
            tags.add(data.getString("name"));
        return tags;
    }

    public String getImageName(String recipe) throws SQLException {
        CallableStatement call = connection.prepareCall("{call recipe_image(?)}");
        call.setString(1, recipe);
        ResultSet data = call.executeQuery();
        if (data.next()) {
            String imageName = data.getString("imagename");
            String categoryImage = "category/" + data.getString("image");

            return imageName == null || imageName.equals("")
                    ? categoryImage : imageName;

        }

        return "nopicture.png";

    }

    public int getTotalIngredients() throws SQLException {
        PreparedStatement pStatement =
                connection.prepareStatement("SELECT count(*) AS counter FROM zutaten");
        ResultSet data = pStatement.executeQuery();
        if (data.next()) {
            return data.getInt("counter");
        }
        return -1;
    }

    public int getTotalRecipes() throws SQLException {
        PreparedStatement pStatement =
                connection.prepareStatement("SELECT count(*) AS counter FROM gerichte");
        ResultSet data = pStatement.executeQuery();
        if (data.next()) {
            return data.getInt("counter");
        }
        return 0;
    }

    public List<Recipe> getTastingRecipes(int userId, int loginId) throws SQLException {
        PreparedStatement preparedStatement =
            connection.prepareStatement("SELECT versions.id AS id, beschreibung, " +
                "IFNULL(versions.imagename, CONCAT('category/', kategorien.image)) AS image, " +
                "gerichte.eingefuegt AS created, min, std, skill, kalorien, gerichte.name AS name, personen, " +
                "kategorien_name, active_id, users.id AS users_id, nickname, users.image, viewed, last_change, " +
                "(SELECT IF(COUNT(users_id) = 1, TRUE, FALSE) FROM schmeckt " +
                "WHERE schmeckt.gerichte_name = gerichte.name " +
                "AND schmeckt.users_id = ?) AS tastes " +
                "FROM gerichte " +
                "INNER JOIN versions ON gerichte.name = gerichte_name AND active_id = versions.id " +
                "INNER JOIN users ON versions.users_id = users.id " +
                "INNER JOIN kategorien ON kategorien_name = kategorien.name " +
                "INNER JOIN schmeckt ON gerichte.name = schmeckt.gerichte_name " +
                "WHERE schmeckt.users_id = ? GROUP BY name ORDER BY schmeckt.eingefuegt DESC;");
        preparedStatement.setInt(1, loginId);
        preparedStatement.setInt(2, userId);
        ResultSet data = preparedStatement.executeQuery();
        return getRecipes(data);
    }

    public List<String> getRecipeNamesForUserId(int userId) throws SQLException {
        List<String> result = new LinkedList<>();
        PreparedStatement pStatement = connection.prepareStatement("SELECT gerichte_name FROM versions " +
            "LEFT JOIN gerichte ON gerichte_name = name " +
            "WHERE versions.users_id = ? AND active_id > -1 ORDER BY versions.eingefuegt DESC");
        pStatement.setInt(1, userId);
        ResultSet data = pStatement.executeQuery();
        while (data.next()) {
            String recipeName = data.getString("gerichte_name");
            result.add(recipeName);
        }
        return result;
    }

    public List<Recipe> getRecipesForUserId(int userId, int loginId) throws SQLException {
        PreparedStatement statement =
            connection.prepareStatement("SELECT versions.id AS id, beschreibung, " +
                "IFNULL(versions.imagename, CONCAT('category/', kategorien.image)) AS image, " +
                "gerichte.eingefuegt AS created, min, std, skill, kalorien, gerichte.name AS name, personen, " +
                "kategorien_name, active_id, users_id, nickname, users.image, COUNT(users_id) AS counter, viewed, " +
                "last_change, " +
                "(SELECT COUNT(users_id) FROM schmeckt WHERE schmeckt.gerichte_name = gerichte.name " +
                "AND schmeckt.users_id = ?) AS tastes " +
                "FROM gerichte " +
                "INNER JOIN versions ON gerichte.name = gerichte_name " +
                "INNER JOIN users ON users_id = users.id " +
                "INNER JOIN kategorien ON kategorien_name = kategorien.name " +
                "WHERE versions.users_id = ? AND active_id > -1 " +
                "GROUP BY gerichte.name ORDER BY versions.eingefuegt DESC;");
        statement.setInt(1, loginId);
        statement.setInt(2, userId);
        ResultSet data = statement.executeQuery();
        return getRecipes(data);
    }

    public Date getLastModified() throws SQLException {
        PreparedStatement preparedStatement =
            connection.prepareStatement("SELECT last_change FROM gerichte ORDER BY last_change DESC LIMIT 1");
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getTimestamp("last_change");
        }
        return new Date();
    }
}
