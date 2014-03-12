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

package de.anycook.news.life;

import de.anycook.api.providers.LifeProvider;
import de.anycook.db.mysql.DBLive;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;


public class Lifes {
    public static List<Life> getLastLives(int lastId) throws SQLException {
        try (DBLive dbLive = new DBLive()) {
            return dbLive.getLastLives(lastId, 10);
        }
    }

    public static Life getLastLife() throws SQLException {
        try(DBLive dbLive = new DBLive()){
            return dbLive.getLastLive();
        }
    }

    public static void addLife(String caseName, int userId) throws SQLException {
        try (DBLive dbLive = new DBLive()) {
            if (!dbLive.checkLife(userId, caseName)) {
                dbLive.newLife(userId, caseName);
            }
        }
    }

    public static void addLife(String caseName, int userId, String recipeName) throws SQLException {
        try (DBLive dbLive = new DBLive()) {
            if (!dbLive.checkLife(userId, caseName, recipeName)) {
                dbLive.newLife(userId, recipeName, caseName);
                LifeProvider.wakeUpSuspended();
            }
        }
    }

    public static List<Life> getOlderLives(int oldestId) throws SQLException {
        try (DBLive dbLive = new DBLive()) {
            return dbLive.getOlderLives(oldestId, 10);
        }
    }

    public static Set<Life> getLastLifesFromFollowing(int lastId, int limit, int userId) throws SQLException {
        try (DBLive dbLive = new DBLive()) {
            return dbLive.getLastLivesFromFollowers(lastId, limit, userId);
        }
    }
}
