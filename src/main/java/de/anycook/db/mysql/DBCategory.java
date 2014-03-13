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

import de.anycook.recipe.category.Category;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class DBCategory extends DBHandler {
    public DBCategory() throws SQLException {
        super();
    }

    //kategorie

    /**
     * Gibt eine gegebene Kategorie q in der Schreibweise der Datenbank zurueck. Ist die Kategorie nicht vorhanden wird null zurueckgegeben.
     *
     * @param q String mit der gesuchten Kategorie.
     * @return String mit dem Kategorienamen aus der Datenbank oder null.
     */
    public Category get(String q) throws SQLException, CategoryNotFoundException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT kategorien.name, sortid, COUNT(gerichte.name) AS recipeNumber FROM kategorien " +
                "LEFT JOIN versions ON kategorien.name = kategorien_name " +
                "LEFT JOIN gerichte ON gerichte.name = gerichte_name AND id = active_id " +
                "WHERE kategorien.name = ?" +
                "GROUP BY kategorien.name ORDER BY kategorien.name");

        pStatement.setString(1, q);
        ResultSet data = pStatement.executeQuery();
        if (data.next()) {
            String name = data.getString("kategorien.name");
            int sortId = data.getInt("sortid");
            int recipeNumber = data.getInt("recipeNumber");
            return new Category(name, sortId, recipeNumber);

        }
        throw new CategoryNotFoundException(q);
    }

    /**
     * Gibt alle in der  Datenbank existierenden Kategorien zurueck.
     *
     * @return {@link java.util.List} mit den Kategorien.
     */
    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new LinkedList<>();

        PreparedStatement pStatement = connection.prepareStatement("SELECT kategorien.name, sortid, COUNT(gerichte.name) AS recipe_count FROM kategorien " +
                "LEFT JOIN versions ON kategorien.name = kategorien_name " +
                "LEFT JOIN gerichte ON gerichte.name = gerichte_name AND id = active_id " +
                "GROUP BY kategorien.name ORDER BY kategorien.name");
        ResultSet data = pStatement.executeQuery();

        while (data.next()) {
            String category = data.getString("kategorien.name");
            int sortId = data.getInt("sortid");
            Integer recipeCount = data.getInt("recipe_count");
            categories.add(new Category(category, sortId, recipeCount));
        }

        return categories;
    }

    /**
     * Gibt eine Liste mit den Kategorienamen zurueck. Dabei werden die Namen der sortid entsprechend geordnet
     *
     * @return sortierte List mit den Kategorienamen
     */
    public List<Category> getAllCategoriesSorted() throws SQLException {
        List<Category> categories = new LinkedList<>();

        PreparedStatement pStatement = connection.prepareStatement("SELECT kategorien.name, sortid, COUNT(gerichte.name) AS recipe_count FROM kategorien " +
                "LEFT JOIN versions ON kategorien.name = kategorien_name " +
                "LEFT JOIN gerichte ON gerichte.name = gerichte_name AND id = active_id " +
                "GROUP BY kategorien.name ORDER BY sortid, kategorien.name");
        ResultSet data = pStatement.executeQuery();

        while (data.next()) {
            String category = data.getString("kategorien.name");
            int sortId = data.getInt("sortid");
            Integer recipeCount = data.getInt("recipe_count");
            categories.add(new Category(category, sortId, recipeCount));
        }
        return categories;
    }

    /**
     * erstellt eine neue Kategorie
     *
     * @param name   Name der neuen Kategorie
     * @param sortId wert fuer die Sortierung
     */
    public void newCategorie(String name, int sortId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO kategorien(name, sortid) VALUES(?,?)");

        pStatement.setString(1, name);
        pStatement.setInt(2, sortId);
        pStatement.executeUpdate();
        logger.info("new Category '" + name + "' added");
    }

    /**
     * veraendert die sortid einer Kategorie
     *
     * @param category Kategoriename bei der die sortid verandert werden soll
     * @param sortId   neue sortid
     */
    public void setCategorieSortid(String category, int sortId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("UPDATE kategorie SET sortid = ? WHERE name = ?");

        pStatement.setInt(1, sortId);
        pStatement.setString(2, category);
        pStatement.executeUpdate();
    }

    public static class CategoryNotFoundException extends Exception {
        public CategoryNotFoundException(String queryCategory) {
            super("category does not exist: " + queryCategory);
        }
    }


}
