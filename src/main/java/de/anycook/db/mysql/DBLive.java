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

import de.anycook.news.life.Life;
import de.anycook.news.life.Lifes;
import de.anycook.user.User;
import de.anycook.utils.DateParser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * All about saving user actions in DB and retrieving the latest
 *
 * @author Jan Grassegger
 */
public class DBLive extends DBHandler {

    public DBLive() throws SQLException {
        super();
    }

    /**
     * erstellt neuen case fuer die livemitteilungen + syntax
     *
     * @param caseName name des cases
     * @param syntax   syntax des neuen cases %g fuer das Gericht %u fuer den User
     */
    public void newCase(String caseName, String syntax) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO cases (name, syntax) VALUES (?, ?)");
        pStatement.setString(1, caseName);
        pStatement.setString(2, syntax);
        pStatement.executeUpdate();
        logger.info("new case " + caseName + " added");
    }

    public void newLife(int userId, String recipeName, Lifes.Case lifeCase) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO life (users_id, gerichte_name, cases_name, lifetime) VALUES (?,?,?, NOW())");
        pStatement.setInt(1, userId);
        pStatement.setString(2, recipeName);
        pStatement.setString(3, lifeCase.toString());
        pStatement.executeUpdate();
        logger.info("new life entry " + lifeCase + " from " + userId + " for " + recipeName + " added to DB");
    }

    public void newLife(int userId, Lifes.Case lifeCase) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO life (users_id, cases_name, lifetime) VALUES (?,?, NOW())");
        pStatement.setInt(1, userId);
        pStatement.setString(2, lifeCase.toString());
        pStatement.executeUpdate();
        logger.info("new life entry " + lifeCase + " from " + userId + " added to DB");
    }

    public List<Life> getLastLives(int lastId, int limit) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT idlife, nickname, users_id, users.image, syntax, gerichte_name, lifetime FROM life LEFT JOIN cases ON life.cases_name = cases.name LEFT JOIN users ON life.users_id = users.id WHERE idlife > ? ORDER BY idlife DESC LIMIT ?");
        pStatement.setInt(1, lastId);
        pStatement.setInt(2, limit);
        try (ResultSet data = pStatement.executeQuery()) {
            List<Life> lives = new LinkedList<>();
            while (data.next()) {
                int id = data.getInt("idlife");
                String syntax = data.getString("syntax");
                String recipe = data.getString("gerichte_name");
                Date datetime = DateParser.parseDateTime(data.getString("lifetime"));

                String username = data.getString("nickname");
                int userId = data.getInt("users_id");
                String userImage = data.getString("users.image");
                User user = new User(userId, username, userImage);

                Life life = new Life(id, user, syntax, recipe, datetime);
                lives.add(life);
            }
            Collections.reverse(lives);
            return lives;
        }
    }

    public Life getLastLive() throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT idlife, nickname, users_id, users.image, syntax, gerichte_name, lifetime FROM life LEFT JOIN cases ON life.cases_name = cases.name LEFT JOIN users ON life.users_id = users.id ORDER BY idlife DESC LIMIT 1");
        try (ResultSet data = pStatement.executeQuery()) {
            if (data.next()) {
                int id = data.getInt("idlife");
                String syntax = data.getString("syntax");
                String recipe = data.getString("gerichte_name");
                Date datetime = DateParser.parseDateTime(data.getString("lifetime"));

                int userId = data.getInt("users_id");
                String username = data.getString("nickname");
                String userImage = data.getString("users.image");
                User user = new User(userId, username, userImage);


                return new Life(id, user, syntax, recipe, datetime);
            }
        }

        throw new RuntimeException("no new life found");
    }

    public boolean checkLife(int userId, Lifes.Case lifeCase) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT * FROM life WHERE users_id = ? AND cases_name = ?");
        pStatement.setInt(1, userId);
        pStatement.setString(2, lifeCase.toString());
        try (ResultSet data = pStatement.executeQuery()) {
            return data.next();
        }

    }

    public boolean checkLife(int userId, Lifes.Case lifeCase, String recipeName) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT * FROM life WHERE users_id = ? " +
                "AND cases_name = ? AND gerichte_name = ?");
        pStatement.setInt(1, userId);
        pStatement.setString(2, lifeCase.toString());
        pStatement.setString(3, recipeName);
        try (ResultSet data = pStatement.executeQuery()) {
            return data.next();
        }
    }

    public void deleteUserLifes(int userId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("DELETE FROM life  WHERE users_id = ?");
        pStatement.setInt(1, userId);
        pStatement.executeUpdate();
    }

    public List<Life> getOlderLives(int oldestId, int limit) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT idlife, nickname, users_id, users.image, syntax, gerichte_name, lifetime FROM life LEFT JOIN cases ON life.cases_name = cases.name LEFT JOIN users ON life.users_id = users.id WHERE idlife < ? ORDER BY idlife DESC LIMIT ?");
        pStatement.setInt(1, oldestId);
        pStatement.setInt(2, limit);
        try (ResultSet data = pStatement.executeQuery()) {
            List<Life> lives = new LinkedList<>();
            while (data.next()) {
                int id = data.getInt("idlife");
                String syntax = data.getString("syntax");
                String recipe = data.getString("gerichte_name");
                Date datetime = DateParser.parseDateTime(data.getString("lifetime"));
                //Life life = new Life(id, User.init(usersId), syntax, recipe, datetime);

                String username = data.getString("nickname");
                int usersId = data.getInt("users_id");
                String userImage = data.getString("users.image");
                User user = new User(usersId, username, userImage);

                Life life = new Life(id, user, syntax, recipe, datetime);
                lives.add(life);
            }

            return lives;
        }
    }

    public Set<Life> getLastLivesFromFollowers(int lastId, int limit, int userId) throws SQLException {
        Set<Life> livesByDate = new HashSet<>();
        PreparedStatement pStatement = connection.prepareStatement("SELECT idlife, life.users_id, nickname, syntax, gerichte_name, lifetime FROM life " +
                "LEFT JOIN cases ON life.cases_name = cases.name " +
                "LEFT JOIN users ON life.users_id = users.id " +
                "INNER JOIN followers ON following = life.users_id WHERE idlife > ? AND followers.users_id = ? ORDER BY idlife DESC LIMIT ?");
        pStatement.setInt(1, lastId);
        pStatement.setInt(2, userId);
        pStatement.setInt(3, limit);
        try (ResultSet data = pStatement.executeQuery()) {
            while (data.next()) {
                int id = data.getInt("idlife");

                String syntax = data.getString("syntax");
                String recipeName = data.getString("gerichte_name");
                Date datetime = DateParser.parseDateTime(data.getString("lifetime"));

                String username = data.getString("nickname");
                int followerId = data.getInt("users_id");
                String userImage = data.getString("users.image");
                User user = new User(followerId, username, userImage);

                livesByDate.add(new Life(id, user, syntax, recipeName, datetime));

            }
            return livesByDate;
        }
    }
}
