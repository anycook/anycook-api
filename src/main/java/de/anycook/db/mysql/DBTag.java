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

import de.anycook.recipe.tag.Tag;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;


public class DBTag extends DBHandler {

    public DBTag() throws SQLException {
        super();
    }

    /**
     * Prueft, ob ein Tag existiert. Wenn ja, return true, sonst false.
     *
     * @param name Name des Tags
     * @return true wenn vorhanden
     */
    public boolean exists(String name) throws SQLException {
        try {
            get(name);
            return true;
        } catch (TagNotFoundException e) {
            return false;
        }
    }

    public Tag get(String tag) throws SQLException, TagNotFoundException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT name, count(tags_name) AS counter FROM tags " +
                "LEFT JOIN gerichte_has_tags ON name = tags_name WHERE name = ?");

        pStatement.setString(1, tag);
        ResultSet data = pStatement.executeQuery();
        if (data.next()){
            String tagName = data.getString("name");
            int counter = data.getInt("counter");
            return new Tag(tagName, counter);
        }
        throw new TagNotFoundException(tag);
    }

    public List<Tag> getTagsForRecipe(String recipeName) throws SQLException {
        List<Tag> tags = new LinkedList<>();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT name, count(tags_name) AS counter FROM tags " +
                "LEFT JOIN gerichte_has_tags ON name = tags_name WHERE gerichte_name = ? GROUP BY name");
        preparedStatement.setString(1, recipeName);
        ResultSet data = preparedStatement.executeQuery();
        while (data.next()) {
            String tag = data.getString("name");
            int count = data.getInt("counter");
            tags.add(new Tag(tag, count));
        }

        return tags;
    }

    public int getTotal() {
        try {
            PreparedStatement pStatement =
                    connection.prepareStatement("SELECT count(*) AS counter FROM tags");
            ResultSet data = pStatement.executeQuery();
            if (data.next()) {
                return data.getInt("counter");
            }
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at getTotal.", e);
        }
        return -1;
    }

    public List<Tag> getAll() throws SQLException {
        LinkedList<Tag> returnList = new LinkedList<>();
        PreparedStatement pStatement = connection.prepareStatement("SELECT name, count(tags_name) AS counter FROM tags LEFT JOIN gerichte_has_tags ON name=tags_name GROUP BY name");

        ResultSet data = pStatement.executeQuery();
        while (data.next()) {
            Tag tag = new Tag(data.getString("name"), data.getInt("counter"));
            returnList.add(tag);
        }
        return returnList;
    }

    /**
     * Legt neuen tag in Tabelle tag an.
     *
     * @param tag Name des Tags
     */
    public void create(String tag) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO tags VALUES (?)");
        pStatement.setString(1, tag);
        pStatement.executeUpdate();
        logger.info("new Tag '" + tag + "'");
    }

    /**
     * Loescht Tag aus der Tabelle tags
     *
     * @param name Name des Tags
     */
    protected void delete(String name) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("DELETE FROM tags WHERE name = ?");
        pStatement.setString(1, name);
        pStatement.executeUpdate();
    }

    public static class TagNotFoundException extends Exception {
        public TagNotFoundException(String queryTag) {
            super("tag does not exist: " + queryTag);
        }
    }
}
