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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Deprecated
public class DBBackend extends DBHandler {

    public DBBackend() throws SQLException {
        super();
    }

    @Deprecated
    public List<Map<String, String>> getAllVersionsHeaders() {

        List<Map<String, String>> headerList = new LinkedList<Map<String, String>>();
        try {
            PreparedStatement pStatement = connection.prepareStatement("SELECT gerichte_name, eingefuegt, nickname, active_id FROM versions INNER JOIN users ON users_id = users.id ORDER BY eingefuegt DESC");
            ResultSet data = pStatement.executeQuery();
            while (data.next()) {
                Map<String, String> header = new HashMap<String, String>();
                header.put("name", data.getString("gerichte_name"));
                header.put("date", data.getString("eingefuegt"));
                header.put("username", data.getString("nickname"));
                header.put("active_id", data.getString("active_id"));
                headerList.add(header);
            }
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at getAllVersionsHeaders", e);
        }
        return headerList;
    }


    public List<Map<String, String>> getNewVersionsHeaders() {

        List<Map<String, String>> headerList = new LinkedList<Map<String, String>>();
        try {
            PreparedStatement pStatement = connection.prepareStatement("SELECT gerichte_name, eingefuegt, nickname, id FROM versions INNER JOIN users ON users_id = users.id WHERE viewed_by_admin = 0 ORDER BY eingefuegt DESC");
            ResultSet data = pStatement.executeQuery();
            while (data.next()) {
                Map<String, String> header = new HashMap<String, String>();
                header.put("name", data.getString("gerichte_name"));
                header.put("date", data.getString("eingefuegt"));
                header.put("username", data.getString("nickname"));
                header.put("id", data.getString("id"));
                headerList.add(header);
            }
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at getNewVersionsHeaders", e);
        }
        return headerList;
    }

    public List<Map<String, String>> getAllGerichteInfo() {

        List<Map<String, String>> gerichte = new LinkedList<Map<String, String>>();
        try {
            PreparedStatement pStatement = connection.prepareStatement("SELECT name, gerichte.eingefuegt, active_id, parent_gericht, viewed, COUNT(schmeckt.users_id) AS schmeckt, MIN(viewed_by_admin) AS adminviewed FROM gerichte " +
                    "LEFT JOIN schmeckt ON name = schmeckt.gerichte_name " +
                    "LEFT JOIN versions ON name = versions.gerichte_name " +
                    "GROUP BY name ORDER BY eingefuegt DESC");

            ResultSet data = pStatement.executeQuery();
            while (data.next()) {
                Map<String, String> gericht = new HashMap<String, String>();
                gericht.put("name", data.getString("name"));
                gericht.put("eingefuegt", data.getString("gerichte.eingefuegt"));
                gericht.put("active_id", data.getString("active_id"));
                gericht.put("viewed", data.getString("viewed"));
                gericht.put("schmeckt", data.getString("schmeckt"));
                gericht.put("parent_gericht", data.getString("parent_gericht"));
                gericht.put("admin_viewed", data.getString("adminviewed"));
                gerichte.add(gericht);
            }
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at getAllGerichteInfo", e);
        }
        return gerichte;
    }

    public Map<String, String> getVersionInfo(String gericht, int id) {
        Map<String, String> version = new HashMap<String, String>();
        try {
            PreparedStatement pStatement = connection.prepareStatement("SELECT id, users_id, eingefuegt, viewed_by_admin FROM versions " +
                    "WHERE gerichte_name = ? AND id = ?");

            pStatement.setString(1, gericht);
            pStatement.setInt(2, id);
            ResultSet data = pStatement.executeQuery();
            if (data.next()) {
                version.put("id", data.getString("id"));
                version.put("userid", data.getString("users_id"));
                version.put("eingefuegt", data.getString("eingefuegt"));
                version.put("viewed_by_admin", data.getString("viewed_by_admin"));
            }
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at getVersionInfo", e);
        }
        return version;
    }

    public List<Map<String, String>> getVersionsInfo(String gericht) {
        List<Map<String, String>> versions = new LinkedList<Map<String, String>>();
        try {
            PreparedStatement pStatement = connection.prepareStatement("SELECT id, users_id, versions.eingefuegt, COUNT(schritte.idschritte) AS schritteAnz, viewed_by_admin, " +
                    "IF(active_id = id, 1, 0) AS active FROM versions " +
                    "LEFT JOIN schritte ON gerichte_name = schritte.versions_gerichte_name AND id = schritte.versions_id " +
                    "LEFT JOIN gerichte ON gerichte_name = name " +
                    "WHERE gerichte_name = ?");
            pStatement.setString(1, gericht);
            ResultSet data = pStatement.executeQuery();
            while (data.next()) {
                Map<String, String> version = new HashMap<String, String>();
                version.put("id", data.getString("id"));
                version.put("eingefuegt", data.getString("versions.eingefuegt"));
                version.put("userid", data.getString("users_id"));
                version.put("schritte", data.getString("schritteAnz"));
                version.put("viewed_by_admin", data.getString("viewed_by_admin"));
                version.put("active", data.getString("active"));
                versions.add(version);
            }
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at getVersionsInfo", e);
        }
        return versions;
    }

    public int countZutaten(String gericht, int version_id) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("SELECT COUNT(zutaten_name) AS zutatAnz FROM versions_has_zutaten " +
                    "WHERE versions_gerichte_name = ? AND versions_id = ? GROUP BY versions_gerichte_name, versions_id");
            pStatement.setString(1, gericht);
            pStatement.setInt(2, version_id);
            ResultSet data = pStatement.executeQuery();
            if (data.next())
                return data.getInt(1);
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at countZutaten", e);
        }
        return 0;
    }


    public int getNumZutatenChilds(String parent) {
        int count = 0;
        try {
            PreparedStatement pStatement = connection.prepareStatement("SELECT count(name) AS count FROM zutaten WHERE parent_zutaten_name = ? GROUP BY name");

            pStatement.setString(1, parent);
            ResultSet data = pStatement.executeQuery();
            if (data.next())
                count = data.getInt("count");
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at getNumZutatenChilds", e);
        }
        return count;
    }

    public List<Map<String, String>> getAllUsers() {
        return getAllUsers("nickname", false);
    }

    public List<Map<String, String>> getAllUsers(String orderBy, boolean desc) {
        List<Map<String, String>> userslist = new LinkedList<Map<String, String>>();
        PreparedStatement pStatement = null;
        try {
            if (desc) {
                pStatement = connection.prepareStatement("SELECT nickname, email, image, lastlogin, facebook_id, createdate, fullname, COUNT(versions.id) AS versions FROM users " +
                        "LEFT JOIN userlevels ON userlevels_id = userlevels.id LEFT JOIN versions ON users.id = versions.users_id GROUP BY users.id ORDER BY " + orderBy + " DESC");
            } else {
                pStatement = connection.prepareStatement("SELECT nickname, email, image, lastlogin, facebook_id, createdate, fullname, COUNT(versions.id) AS versions FROM users " +
                        "LEFT JOIN userlevels ON userlevels_id = userlevels.id LEFT JOIN versions ON users.id = versions.users_id GROUP BY users.id ORDER BY " + orderBy);
            }


            ResultSet data = pStatement.executeQuery();
            while (data.next()) {
                Map<String, String> usermap = new HashMap<String, String>();
                usermap.put("name", data.getString("nickname"));
                usermap.put("mail", data.getString("email"));
                usermap.put("image", data.getString("image"));
                usermap.put("lastlogin", data.getString("lastlogin"));
                usermap.put("facebook_id", data.getString("facebook_id"));
                usermap.put("createdate", data.getString("createdate"));
                usermap.put("level", data.getString("fullname"));
                usermap.put("versions", data.getString("versions"));
                userslist.add(usermap);
            }
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at getAllUsers", e);
        }
        return userslist;
    }

    public void deleteRecipe(String recipe) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("DELETE FROM gerichte WHERE name = ?");

            pStatement.setString(1, recipe);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at deleteRecipe.", e);
        }
    }

    public void deleteAllSteps(String recipe) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("DELETE FROM schritte WHERE versions_gerichte_name = ?");
            pStatement.setString(1, recipe);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at deleteAllSteps.", e);
        }
    }

    public void deleteAllVersions(String recipe) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("DELETE FROM versions WHERE gerichte_name = ?");

            pStatement.setString(1, recipe);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at deleteAllVersions.", e);
        }
    }

    public void deleteAllZutaten(String recipe) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("DELETE FROM versions_has_zutaten WHERE versions_gerichte_name = ?");

            pStatement.setString(1, recipe);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at deleteAllZutaten.", e);
        }
    }

    public void deleteZutat(String zutat) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("DELETE FROM zutaten WHERE name = ?");
            pStatement.setString(1, zutat);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at deleteIngredient.", e);
        }
    }

    public void deleteAllTags(String recipe) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("DELETE FROM gerichte_has_tags WHERE gerichte_name = ?");

            pStatement.setString(1, recipe);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at deleteAllTags.", e);
        }
    }

    public void deleteAllSchmeckt(String recipe) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("DELETE FROM schmeckt WHERE gerichte_name = ?");

            pStatement.setString(1, recipe);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at deleteAllSchmeckt.", e);
        }
    }

    public void deleteAllTagesrezepte(String recipe) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("DELETE FROM tagesrezepte WHERE gerichte_name = ?");
            pStatement.setString(1, recipe);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at deleteAllTagesrezepte.", e);
        }
    }

    public void deleteAllDiscussions(String recipe) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("DELETE FROM discussions WHERE gerichte_name = ?");
            pStatement.setString(1, recipe);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at deleteAllDiscussions.", e);
        }
    }

    public void setParentZutat(String zutat, String parent) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE zutaten SET parent_zutaten_name = ? WHERE name = ?");
            pStatement.setString(1, parent);
            pStatement.setString(2, zutat);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at setParentZutat.", e);
        }

    }

    public void viewedByAdmin(String gerichtename, int versionid) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE versions SET viewed_by_admin = 1 WHERE gerichte_name = ? AND id = ? AND viewed_by_admin = 0");

            pStatement.setString(1, gerichtename);
            pStatement.setInt(2, versionid);
            pStatement.executeUpdate();

        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at viewedByAdmin.", e);
        }

    }

    public void renameZutat(String oldName, String newName) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE zutaten SET name = ? WHERE name = ?");

            pStatement.setString(1, newName);
            pStatement.setString(2, oldName);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at renameZutat.", e);
        }

    }

    public void renameZutatinVersions(String oldName, String newName) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE versions_has_zutaten SET zutaten_name = ? WHERE zutaten_name = ?");

            pStatement.setString(1, newName);
            pStatement.setString(2, oldName);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at renameZutat.", e);
        }
    }

    public void renameParent(String oldName, String newName) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE zutaten SET parent_zutaten_name = ? WHERE parent_zutaten_name = ?");

            pStatement.setString(1, newName);
            pStatement.setString(2, oldName);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at renameParent.", e);
        }

    }

    public void changeRecipeZutat(String gericht, int version, String oldZutat, String newZutat) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE versions_has_zutaten SET zutaten_name = ? WHERE zutaten_name = ? AND versions_gerichte_name = ? AND versions_id = ?");
            pStatement.setString(1, newZutat);
            pStatement.setString(2, oldZutat);
            pStatement.setString(3, gericht);
            pStatement.setInt(4, version);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at changeRecipeZutat.", e);
        }


    }

    public void changeRecipeZutatMenge(String recipe, int version,
                                       String zutat, String menge) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE versions_has_zutaten SET menge = ? WHERE zutaten_name = ? AND versions_gerichte_name = ? AND versions_id = ?");

            pStatement.setString(1, menge);
            pStatement.setString(2, zutat);
            pStatement.setString(3, recipe);
            pStatement.setInt(4, version);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at changeRecipeZutatMenge.", e);
        }

    }

    public void changeBeschreibung(String recipe, int version,
                                   String newBeschreibung) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE versions SET beschreibung = ? WHERE gerichte_name = ? AND id = ?");

            pStatement.setString(1, newBeschreibung);
            pStatement.setString(2, recipe);
            pStatement.setInt(3, version);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at changeBeschreibung.", e);
        }

    }

    public void changeRecipeName(String recipe, String newName) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE gerichte SET name = ? WHERE name = ?");
            pStatement.setString(1, newName);
            pStatement.setString(2, recipe);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at changeRecipeName.", e);
        }

    }

    public void changeRecipeNameinVersions(String recipe, String newName) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE versions SET gerichte_name = ? WHERE gerichte_name = ?");
            pStatement.setString(1, newName);
            pStatement.setString(2, recipe);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at changeRecipeNameinVersions.", e);
        }

    }

    public void changeRecipeNameinTagesRezepte(String recipe, String newName) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE tagesrezepte SET gerichte_name = ? WHERE gerichte_name = ?");

            pStatement.setString(1, newName);
            pStatement.setString(2, recipe);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at changeRecipeNameinTagesRezepte.", e);
        }
    }

    public void changeRecipeNameinSchmeckt(String recipe, String newName) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE schmeckt SET gerichte_name = ? WHERE gerichte_name = ?");

            pStatement.setString(1, newName);
            pStatement.setString(2, recipe);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at changeRecipeNameinSchmeckt.", e);
        }
    }

    public void changeRecipeNameinGerichtetags(String recipe, String newName) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE gerichte_has_tags SET gerichte_name = ? WHERE gerichte_name = ?");

            pStatement.setString(1, newName);
            pStatement.setString(2, recipe);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at changeRecipeNameinGerichtetags.", e);
        }
    }

    public void changeRecipeNameinDiscussions(String recipe, String newName) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE discussions SET gerichte_name = ? WHERE gerichte_name = ?");

            pStatement.setString(1, newName);
            pStatement.setString(2, recipe);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at changeRecipeNameinDiscussions.", e);
        }
    }

    public void changeRecipeNameinLife(String recipe, String newName) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE life SET gerichte_name = ? WHERE gerichte_name = ?");

            pStatement.setString(1, newName);
            pStatement.setString(2, recipe);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at changeRecipeNameinLife.", e);
        }
    }

    public void changeRecipeNameinLikeNot(String recipe, String newName) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE like_not SET discussions_gerichte_name = ? WHERE discussions_gerichte_name = ?");

            pStatement.setString(1, newName);
            pStatement.setString(2, recipe);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at changeRecipeNameinLikeNot.", e);
        }
    }

    public void changeRecipeNameinSchritte(String recipe, String newName) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE schritte SET versions_gerichte_name = ? WHERE versions_gerichte_name = ?");

            pStatement.setString(1, newName);
            pStatement.setString(2, recipe);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at changeRecipeNameinSchritte.", e);
        }
    }

    public void changeRecipeNameinVersionsZutaten(String recipe, String newName) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE versions_has_zutaten SET versions_gerichte_name = ? WHERE versions_gerichte_name = ?");

            pStatement.setString(1, newName);
            pStatement.setString(2, recipe);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at changeRecipeNameinVersionsZutaten.", e);
        }
    }

    public void deleteRecipeZutat(String recipe, int version, String zutat) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("DELETE FROM versions_has_zutaten WHERE versions_gerichte_name = ? AND versions_id = ? AND " +
                    "zutaten_name IN (SELECT name FROM zutaten WHERE name = ? OR singular = ?)");
            pStatement.setString(1, recipe);
            pStatement.setInt(2, version);
            pStatement.setString(3, zutat);
            pStatement.setString(4, zutat);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at deleteRecipeZutat.", e);
        }
    }

    public void deleteTag(String tagName) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("DELETE FROM tags WHERE name = ?");

            pStatement.setString(1, tagName);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at deleteTag.", e);
        }
    }

    public void deleteTagfromRelation(String tagName) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("DELETE FROM gerichte_has_tags WHERE tags_name = ?");

            pStatement.setString(1, tagName);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at deleteTagfromRelation.", e);
        }
    }

    public void setZutatSingular(String zutat, String singular) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE zutaten SET singular = ? WHERE name = ?");
            pStatement.setString(1, singular);
            pStatement.setString(2, zutat);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at setZutatSingular.", e);
        }
    }


    public int getUserIdforTag(String tagName, String gerName) {
        Integer userid = null;
        try {
            PreparedStatement pStatement = connection.prepareStatement("SELECT users_id FROM gerichte_has_tags WHERE tags_name=? AND gerichte_name=?");

            pStatement.setString(1, tagName);
            pStatement.setString(2, gerName);
            ResultSet data = pStatement.executeQuery();
            if (data.next()) {
                userid = data.getInt("users_id");
            }
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at getUserIdforTag.", e);
        }
        return userid;
    }

    public void makeNewKategorie(String kategorie, int position) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("INSERT INTO kategorien (name, sortid) VALUES (?,?)");

            pStatement.setString(1, kategorie);
            pStatement.setInt(2, position);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at makeNewKategorie.", e);
        }
    }

    public void renameKategorie(String oldName, String newName) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE kategorien SET name = ? WHERE name = ?");

            pStatement.setString(1, newName);
            pStatement.setString(2, oldName);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at renameKategorie.", e);
        }
    }

    public void renameKategorieinVersions(String oldName, String newName) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE versions SET kategorien_name = ? WHERE kategorien_name = ?");
            pStatement.setString(1, newName);
            pStatement.setString(2, oldName);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at renameKategorieinVersions.", e);
        }
    }

    public void setKategoriePosition(String kategorie, int newPosition) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE kategorien SET sortid = ? WHERE name = ?");

            pStatement.setInt(1, newPosition);
            pStatement.setString(2, kategorie);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at setKategoriePosition.", e);
        }
    }

    public Map<String, Integer> getKategories() {
        Map<String, Integer> kategories = new LinkedHashMap<String, Integer>();
        try {
            PreparedStatement pStatement = connection.prepareStatement("SELECT * FROM kategorien ORDER BY sortid, name");

            ResultSet data = pStatement.executeQuery();
            while (data.next()) {
                kategories.put(data.getString("name"), data.getInt("sortid"));
            }
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at setKategoriePosition.", e);
        }
        return kategories;
    }

    public void changeRecipeKategorie(String recipe, int version,
                                      String kategorie) {
        try {
            PreparedStatement pStatement = connection.prepareStatement("UPDATE versions SET kategorien_name = ? WHERE gerichte_name = ? AND id = ?");
            pStatement.setString(1, kategorie);
            pStatement.setString(2, recipe);
            pStatement.setInt(3, version);
            pStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error("execute MySQL-query failed at renameKategorieinVersions.", e);
        }

    }

    //versiondata

}
