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

import de.anycook.location.Location;
import de.anycook.social.facebook.FacebookHandler;
import de.anycook.user.User;

import java.io.IOException;
import java.sql.*;
import java.util.Date;
import java.util.*;

/**
 * Von DBHandler abgeleitet. Enthaelt alle Funktionen, die fuer das Userhandling zustaendig sind.
 *
 * @author Jan Grassegger
 * @see de.anycook.db.mysql.DBHandler
 */
public class DBUser extends DBHandler {
    public DBUser() throws SQLException {
        super();
    }

    //login

    /**
     * Ueberprueft E-Mailaddresse und Passwort des Nutzers, wenn diese mit der Datenbank uebereinstimmen, wird das Lastloginfeld auf die aktuelle Zeit gesetzt und die email des Users zurueckgegeben. Existiert kein solcher User wird null zurueckgegeben.
     *
     * @param userId Id of the user
     * @param pwd    Passwort, das in das Loginformular eingegeben wurde.
     * @return String mit der Mail oder null
     */
    public boolean login(int userId, String pwd) throws SQLException {

        if (pwd == null)
            return false;
        PreparedStatement pStatement = connection.prepareStatement("SELECT * from users WHERE id = ? AND password = PASSWORD(?) AND userlevels_id >= 0");
        pStatement.setInt(1, userId);
        pStatement.setString(2, pwd);
        ResultSet data = pStatement.executeQuery();
        return data.next();
    }

    public int facebookLogin(Long uid) throws SQLException {
        int userId = -1;
        PreparedStatement pStatement = connection.prepareStatement("SELECT id from users WHERE facebook_id = ? AND userlevels_id >= 0");
        pStatement.setLong(1, uid);
        ResultSet data = pStatement.executeQuery();
        if (data.next())
            userId = data.getInt("id");
        return userId;
    }

    /**
     * Ueberprueft, ob ein Username in der Datenbank existiert, wenn ja gibt die Methode true zurueck, ansonsten false
     *
     * @param username Zu ueberpruefender Username
     * @return boolean Wenn Username vorhanden true, sonst false
     */
    public boolean checkUsername(String username) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT * from users WHERE nickname = ?");
        pStatement.setString(1, username);
        ResultSet data = pStatement.executeQuery();
        return data.next();
    }

    /**
     * Ueberprueft, ob eine EMailadresse in der Datenbank existiert, wenn ja gibt die Methode true zurueck, ansonsten false
     *
     * @param mail Zu ueberpruefende Mailadresse
     * @return boolean Wenn Mailadresse vorhanden true, sonst false
     */
    public boolean checkMail(String mail) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT * from users WHERE email = ?");
        pStatement.setString(1, mail);
        ResultSet data = pStatement.executeQuery();
        return data.next();
    }


    /**
     * Bekommt eine Mailaddresse und gibt den dazugehoerigen Username zurueck
     *
     * @param userId unique id of the user
     * @return String mit dem Usernamen
     */
    public String getUsername(int userId) throws UserNotFoundException, SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT nickname from users WHERE id=?");
        pStatement.setInt(1, userId);
        ResultSet data = pStatement.executeQuery();
        if (data.next())
            return data.getString(1);

        throw new UserNotFoundException(userId);
    }

    public String getUserImage(int id) throws SQLException, UserNotFoundException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT image FROM users WHERE id = ?");
        pStatement.setInt(1, id);
        ResultSet data = pStatement.executeQuery();
        if (data.next())
            return data.getString("image");

        throw new UserNotFoundException(id);
    }

    //newuser
    public Integer newUser(String mail, String pwd, String username,
                           String activationID, String image) throws SQLException {
        CallableStatement call = connection.prepareCall("{call new_user(?,?,?,?,?,?)}");
        call.setString(1, username);
        call.setString(2, pwd);
        call.setString(3, mail);
        call.setString(4, activationID);
        call.setString(5, image);
        call.registerOutParameter(6, Types.INTEGER);
        call.execute();

        return call.getInt(6);
    }

    public Integer newFacebookUser(String mail, String username, Long facebook_id) throws SQLException {
        if (checkUsername(username) || checkMail(mail))
            return null;
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO users(nickname, email, facebook_id, createdate) VALUES (?, ?, ?, CURDATE())");
        pStatement.setString(1, username);
        pStatement.setString(2, mail);
        pStatement.setLong(3, facebook_id);
        pStatement.executeUpdate();
        logger.info(String.format("new FacebookUser '%s' created. mail: %s", username, mail));

        try {
            return getUserId(mail);
        } catch (UserNotFoundException e) {
            // nope
            throw new RuntimeException(e);
        }

    }

    /**
     * Setzt die activationid bei einem User
     *
     * @param userId       mailaddresse des users
     * @param activationId zu setztend id
     */
    public void setActivationId(int userId, String activationId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO activationids(users_id, activationid) VALUES (?,?)");
        pStatement.setInt(1, userId);
        pStatement.setString(2, activationId);
        pStatement.executeUpdate();
    }

    public void setResetPasswordID(int userId, String resetId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO resetpasswordids(users_id, id) VALUES (?,?)");
        pStatement.setInt(1, userId);
        pStatement.setString(2, resetId);
        pStatement.executeUpdate();
    }

    /**
     * sucht die gebene activationid und aktiviert den dazugehoerigen User
     *
     * @param activationId Id the user has sent
     * @return true wenn erfolgreich, sonst false
     */
    public Integer activateById(String activationId) throws SQLException, ActivationFailedException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT users_id FROM activationids WHERE activationid = ?");
        pStatement.setString(1, activationId);
        ResultSet data = pStatement.executeQuery();

        if (data.next()) {
            int userId = data.getInt("users_id");
            setUserLevel(userId, 0);
            deleteActivationId(userId);
            return userId;
        }
        logger.warn(String.format("activation failed. activationId: %s", activationId));
        throw new ActivationFailedException(activationId);
    }

    public void setUserLevel(int id, int level) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("UPDATE users SET userlevels_id = ? WHERE id = ?");
        pStatement.setInt(1, level);
        pStatement.setInt(2, id);
        pStatement.executeUpdate();
    }

    public void deleteActivationId(int userId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("DELETE FROM activationids WHERE users_id = ?");
        pStatement.setInt(1, userId);
        pStatement.executeUpdate();
    }

    public void activateUser(int userId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("UPDATE users SET userlevels_id = 0 WHERE id = ? AND userlevels_id < 0");
        pStatement.setInt(1, userId);
        pStatement.executeUpdate();
        deleteActivationId(userId);
    }

    /**
     * setzt den letzten anmeldezeitpunkt eines Users
     *
     * @param userId Mailaddresse des Users
     */
    public void setLastLogin(int userId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("UPDATE users SET lastlogin = NOW() WHERE id = ?");
        pStatement.setInt(1, userId);
        pStatement.executeUpdate();
    }


    public int getUserlevel(String mail) throws SQLException, UserNotFoundException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT userlevels_id FROM users WHERE email = ?");
        pStatement.setString(1, mail);
        ResultSet data = pStatement.executeQuery();
        if (data.next())
            return data.getInt(1);
        throw new UserNotFoundException(mail);
    }

    public void deleteUser(int userId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("DELETE FROM users WHERE id = ?");
        pStatement.setInt(1, userId);
        pStatement.executeUpdate();
    }

    public void deleteMailSettings(int userId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("DELETE FROM mailsettings WHERE users_id = ?");
        pStatement.setInt(1, userId);
        pStatement.executeUpdate();
    }

    /**
     * Überprüft, ob eine CookieID bereits existiert
     *
     * @param id
     * @return true, wenn vorhanden, sonst false
     */
    public boolean checkCookieId(String id) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT * FROM permanent_cookies WHERE id = ?");
        pStatement.setString(1, id);
        ResultSet data = pStatement.executeQuery();
        return data.next();
    }

    public int getUserIdFromCookieId(String id) throws SQLException, CookieNotFoundException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT users_id FROM permanent_cookies WHERE id = ?");
        pStatement.setString(1, id);
        ResultSet data = pStatement.executeQuery();
        if (data.next())
            return data.getInt("users_id");

        throw new CookieNotFoundException(id);
    }

    public void setCookieId(String id, int userId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO permanent_cookies(id, users_id) VALUES (?, ?)");
        pStatement.setString(1, id);
        pStatement.setInt(2, userId);
        pStatement.executeUpdate();
    }


    public void deleteCookieId(String id) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("DELETE FROM permanent_cookies WHERE id = ?");
        pStatement.setString(1, id);
        pStatement.executeUpdate();
    }

    public void deleteCookieIdbyUserId(int userId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("DELETE FROM permanent_cookies WHERE users_id = ?");
        pStatement.setInt(1, userId);
        pStatement.executeUpdate();
    }

    @Deprecated
    public Long getFacebookID(int userId) throws SQLException, UserNotFoundException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT facebook_id FROM users WHERE id = ?");
        pStatement.setInt(1, userId);
        ResultSet data = pStatement.executeQuery();
        if (data.next())
            return data.getLong("facebook_id");
        throw new UserNotFoundException(userId);
    }


    public String getUserEmail(String username) throws SQLException, UserNotFoundException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT email from users WHERE nickname=?");
        pStatement.setString(1, username);
        ResultSet data = pStatement.executeQuery();
        if (data.next())
            return data.getString(1);
        throw new UserNotFoundException(username);
    }

    public String getUserEmail(int userId) throws SQLException, UserNotFoundException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT email from users WHERE id=?");
        pStatement.setInt(1, userId);
        ResultSet data = pStatement.executeQuery();
        if (data.next())
            return data.getString(1);

        throw new UserNotFoundException(userId);
    }


    public boolean checkResetPasswordID(String resetPasswordId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT users_id FROM resetpasswordids WHERE id = ?");
        pStatement.setString(1, resetPasswordId);
        ResultSet data = pStatement.executeQuery();
        return data.next();
    }

    public void resetPassword(String resetPasswordId, String newPw) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("UPDATE users SET password = PASSWORD(?) " +
                "WHERE id IN " +
                "(SELECT users_id FROM resetpasswordids WHERE id = ?)");
        pStatement.setString(1, newPw);
        pStatement.setString(2, resetPasswordId);
        pStatement.execute();
    }

    public void deletePasswordID(String resetPasswordId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("DELETE FROM resetpasswordids WHERE id = ?");
        pStatement.setString(1, resetPasswordId);
        pStatement.execute();
    }

    public void deletePasswordIDbyId(int id) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("DELETE FROM resetpasswordids WHERE users_id = ?");
        pStatement.setInt(1, id);
        pStatement.execute();
    }

    // favorites

    @Deprecated
    public List<String> getActiveUsers() throws SQLException {
        List<String> users = new LinkedList<>();
        PreparedStatement pStatement = connection.prepareStatement("SELECT nickname from users WHERE userlevels_id >= 0");
        ResultSet data = pStatement.executeQuery();
        while (data.next())
            users.add(data.getString("nickname"));
        return users;
    }


    public void changeText(int userId, String text) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("UPDATE users SET text = ? WHERE id = ?");
        pStatement.setString(1, text);
        pStatement.setInt(2, userId);
        pStatement.executeUpdate();
    }


    public void changeName(int id, String name) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("UPDATE users SET nickname = ? WHERE id = ?");
        pStatement.setString(1, name);
        pStatement.setInt(2, id);
        pStatement.executeUpdate();
    }


    public void changeImage(int id, String image) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("UPDATE users SET image = ? WHERE id = ?");
        pStatement.setString(1, image);
        pStatement.setInt(2, id);
        pStatement.executeUpdate();
    }

    public void changeMail(int id, String mail) throws SQLException {
        if (checkMail(mail)) return;
        PreparedStatement pStatement = connection.prepareStatement("UPDATE users SET email = ? WHERE id = ?");
        pStatement.setString(1, mail);
        pStatement.setInt(2, id);
        pStatement.executeUpdate();
    }

    public void changePlace(int id, String data) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("UPDATE users SET place = ? WHERE id = ?");
        pStatement.setString(1, data);
        pStatement.setInt(2, id);
        pStatement.executeUpdate();
    }

    public void changePassword(int id, String data) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("UPDATE users SET password = PASSWORD(?) WHERE id = ?");
        pStatement.setString(1, data);
        pStatement.setInt(2, id);
        pStatement.executeUpdate();
    }

    public boolean changePassword(int id, String oldPassword, String newPassword) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("UPDATE users SET password = PASSWORD(?) WHERE id = ? " +
                "AND password = PASSWORD(?)");
        pStatement.setString(1, newPassword);
        pStatement.setInt(2, id);
        pStatement.setString(3, oldPassword);
        return pStatement.executeUpdate() == 1;
    }


    public int getUserId(String nameOrMail) throws UserNotFoundException, SQLException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT id FROM users WHERE email = ? OR nickname = ?");
        pStatement.setString(1, nameOrMail);
        pStatement.setString(2, nameOrMail);
        ResultSet data = pStatement.executeQuery();
        if (data.next())
            return data.getInt("id");

        throw new UserNotFoundException(nameOrMail);
    }

    public List<Integer> getFollowing(int id) throws SQLException {
        List<Integer> following = new LinkedList<>();

        PreparedStatement pStatement = connection.prepareStatement("SELECT following FROM followers " +
                "WHERE users_id = ?");
        pStatement.setInt(1, id);
        ResultSet data = pStatement.executeQuery();

        while (data.next()) following.add(data.getInt("following"));

        return following;
    }

    public List<Integer> getFollowers(int id) throws SQLException {
        List<Integer> followers = new LinkedList<>();

        PreparedStatement pStatement = connection.prepareStatement("SELECT users_id FROM followers " +
                "WHERE following = ?");
        pStatement.setInt(1, id);
        ResultSet data = pStatement.executeQuery();
        while (data.next()) followers.add(data.getInt("users_id"));

        return followers;
    }

    public void follow(int user_id, int followId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("INSERT INTO followers (users_id, following) VALUES (?,?)");
        pStatement.setInt(1, user_id);
        pStatement.setInt(2, followId);
        pStatement.execute();
    }

    public void unFollow(int user_id, int followId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("DELETE FROM followers " +
                "WHERE users_id = ? AND following = ?");
        pStatement.setInt(1, user_id);
        pStatement.setInt(2, followId);
        pStatement.execute();
    }

    public int getTotal() throws SQLException {
        PreparedStatement pStatement =
                connection.prepareStatement("SELECT count(*) AS counter FROM users");
        ResultSet data = pStatement.executeQuery();
        if (data.next())
            return data.getInt("counter");
        return 0;
    }

    private List<User> loadUsers(ResultSet data) throws SQLException {
        List<User> users = new ArrayList<>();
        while (data.next()){
            users.add(loadUser(data));
        }
        return users;
    }

    private User loadUser(ResultSet data) throws SQLException {
        int id = data.getInt("id");
        String name = data.getString("nickname");
        String mail = data.getString("email");
        Date lastLogin = data.getDate("lastlogin");
        int level = data.getInt("userlevels_id");
        Date createDate = data.getDate("createdate");
        String text = data.getString("text");
        String place = data.getString("place");
        double placeLat = data.getDouble("place_lat");
        double placeLng = data.getDouble("place_lng");
        String image = data.getString("image");
        long facebookId = data.getLong("facebook_id");
        String emailCandidate = data.getString("email_candidate");

        List<Integer> following = getFollowing(id);
        List<Integer> followers = getFollowers(id);

        User user = new User(id, name, mail, facebookId, image, level, text,
                createDate, lastLogin, place, placeLat, placeLng, following, followers, emailCandidate);

        if (image == null && facebookId != 0) {
            try {
                image = FacebookHandler.saveImage(facebookId);
                user.setImage(image);
            } catch (IOException e) {
                logger.error("failed to load facebook image", e);
            }
        }

        return user;
    }

    public List<User> getAllUsers() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, nickname, facebook_id, email, " +
                "lastlogin, createdate, image, userlevels_id, text, place, place_lat, place_lng, email_candidate FROM users");
        ResultSet data = preparedStatement.executeQuery();
        return loadUsers(data);
    }

    public User getUser(int id) throws SQLException, UserNotFoundException, IOException {
        PreparedStatement pStatement = connection.prepareStatement("SELECT id, nickname, facebook_id, email, lastlogin, createdate, image, userlevels_id, text, place, place_lat, place_lng, email_candidate FROM users WHERE id = ?");
        pStatement.setInt(1, id);
        ResultSet data = pStatement.executeQuery();
        if (!data.next()) throw new UserNotFoundException(id);

        return loadUser(data);
    }

    public String getMailCandidate(int userId) throws SQLException, UserNotFoundException {
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT email_candidate FROM users WHERE id = ?")){
            preparedStatement.setInt(1, userId);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next())
                    return resultSet.getString("email_candidate");
                throw new UserNotFoundException(userId);
            }
        }
    }

    public void setMailCandidate(int id, String newMail, String mailActivationCode) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users SET email_candidate = ?, " +
                "email_candidate_code = ? WHERE id = ?")){
            preparedStatement.setString(1, newMail);
            preparedStatement.setString(2, mailActivationCode);
            preparedStatement.setInt(3, id);
            preparedStatement.executeUpdate();
        }
    }

    public void updateMail(int id, String code) throws SQLException, WrongCodeException {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE users SET email = email_candidate, " +
                "email_candidate = NULL, email_candidate_code = NULL " +
                "WHERE id = ? AND email_candidate_code = ?");
        preparedStatement.setInt(1, id);
        preparedStatement.setString(2, code);
        if(preparedStatement.executeUpdate() == 0){
            throw new WrongCodeException(id, code);
        }
    }

    public List<Integer> getAdminIds() throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT id FROM users WHERE userlevels_id = 2;");
        List<Integer> adminIds = new LinkedList<>();
        try(ResultSet data = statement.executeQuery()){
            while (data.next()){
                adminIds.add(data.getInt("id"));
            }
        }
        return adminIds;
    }

    public void changeLocation(int id, Location location) throws SQLException {
        PreparedStatement statement =
                connection.prepareStatement("UPDATE users SET place_lat = ?, place_lng = ? WHERE id = ?");
        statement.setDouble(1, location.getLatitude());
        statement.setDouble(2, location.getLongitude());
        statement.setInt(3, id);

        statement.executeUpdate();
    }

    public Map<Integer, Location> getUserLocations() throws SQLException {
        Map<Integer, Location> locationMap = new HashMap<>();

        PreparedStatement statement = connection.prepareStatement("SELECT id, place_lat, place_lng FROM users " +
                "WHERE place_lat != -1 OR place_lng != -1");
        ResultSet data = statement.executeQuery();
        while (data.next()) {
            Location location = new Location(data.getDouble("place_lat"), data.getDouble("place_lng"));
            locationMap.put(data.getInt("id"), location);
        }

        return locationMap;
    }

    public static class UserNotFoundException extends Exception {
        public UserNotFoundException(String username) {
            super("user does not exist: " + username);
        }

        public UserNotFoundException(int userId) {
            super("user id does not exist: " + userId);
        }
    }


    public class ActivationFailedException extends Exception {
        public ActivationFailedException(String activationId) {
            super("activation failed for " + activationId);
        }
    }

    public class CookieNotFoundException extends Exception {
        public CookieNotFoundException(String id) {
            super("cookie id not found " + id);
        }
    }

    public class WrongCodeException extends Exception {
        public WrongCodeException(int userId, String code){
            super(userId+ " failed update mail address. Wrong code! "+code);
        }
    }
}
