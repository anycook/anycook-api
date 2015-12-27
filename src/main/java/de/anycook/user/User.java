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

package de.anycook.user;

import de.anycook.api.views.PrivateView;
import de.anycook.api.views.PublicView;
import de.anycook.conf.Configuration;
import de.anycook.db.mysql.DBUser;
import de.anycook.image.Image;
import de.anycook.image.UserImage;
import de.anycook.location.GeoCode;
import de.anycook.location.Location;
import de.anycook.news.life.Lifes;
import de.anycook.notifications.Notification;
import de.anycook.sitemap.SiteMapGenerator;
import de.anycook.social.facebook.FacebookHandler;
import de.anycook.utils.enumerations.ImageType;
import de.anycook.utils.enumerations.NotificationType;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * erzeugt neue User und enthaelt Methoden um Userdaten zu ueberpruefen
 *
 * @author Jan Grassegger
 */
public class User implements Comparable<User> {

    private static final Logger logger;
    private static final String adminMail;
    private final static Pattern passwordRegex =
            Pattern.compile("((?=.*\\d)(?=.*[a-zA-Z@#$%]).{6,})");

    static {
        logger = LogManager.getLogger(User.class);
        adminMail = Configuration.getInstance().getAdminMail();
    }

    public static User init(String nameOrMail)
            throws SQLException, DBUser.UserNotFoundException, IOException {
        try (DBUser dbuser = new DBUser()) {
            int id = dbuser.getUserId(nameOrMail);
            return init(id);
        }
    }

    public static User init(int id) throws SQLException, IOException, DBUser.UserNotFoundException {
        try (DBUser dbuser = new DBUser()) {
            return dbuser.getUser(id);
        }
    }

    public static User initAdmin() {
        return new User(99999, "admin", "admin@anycook.de", -1, null, 2, null, null, null, null, -1,
                        -1,
                        null, null, null);

    }

    public static List<User> getAll() throws SQLException {
        try (DBUser dbuser = new DBUser()) {
            return dbuser.getAllUsers();
        }
    }

    public static boolean checkPassword(String newPw) {
        return passwordRegex.matcher(newPw).matches();
    }

    public static User login(int id, String pwd)
            throws SQLException, IOException, DBUser.UserNotFoundException,
                   LoginException {
        try (DBUser dbuser = new DBUser()) {
            if (dbuser.login(id, pwd)) {
                dbuser.setLastLogin(id);
                logger.info(id + " logged in");
                return User.init(id);
            }

            logger.info("Login for " + id + " failed");
            throw new LoginException(id);
        }
    }

    public static User login(String cookieId)
            throws SQLException, IOException, DBUser.UserNotFoundException,
                   DBUser.CookieNotFoundException {
        try (DBUser dbuser = new DBUser()) {
            int userId = dbuser.getUserIdFromCookieId(cookieId);

            //exists if admin
            if (userId == 99999) {
                logger.info("admin logged in");
                return User.initAdmin();

            }

            dbuser.setLastLogin(userId);
            logger.info(userId + " logged in");
            return User.init(userId);
        }
    }

    public static User facebookLogin(Long uid)
            throws SQLException, IOException, DBUser.UserNotFoundException,
                   LoginException {
        try (DBUser dbuser = new DBUser()) {
            int userId = dbuser.facebookLogin(uid);
            if (userId != -1) {
                dbuser.setLastLogin(userId);
                logger.info(userId + " logged in via Facebook");
                return User.init(userId);
            }

            logger.info("Login for " + uid + " failed");
            throw new LoginException(uid);
        }
    }

	/*public boolean newUser(String mail, String username, String pwd){
        try {

			return dbuser.newUser(mail, pwd, username);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}*/


    /**
     * erzeugt neuen User.
     *
     * @param mail     Mailaddresse des neuen Users
     * @param pwd      Passwort des neuen Users
     * @param username Username des neuen Users
     * @return true, wenn erzeugung erfolgreich
     */
    public static boolean newUser(String mail, String pwd, String username) throws SQLException {
        return newUser(mail, pwd, username, false);
    }

    public static boolean newUser(String mail, String pwd, String username, boolean active)
            throws SQLException {
        logger.info("started to add new user");
        if (!checkPassword(pwd)) {
            logger.info(String.format("bad password: %s %s,  %s", mail, username, pwd));
            return false;
        }

        try (DBUser dbuser = new DBUser()) {
            String activationID = null;
            if (!active) {
                activationID = RandomStringUtils.randomAlphanumeric(20);
            }

            logger.debug("activationId is:" + activationID);
            String image = User.getRandomUserpic();

            Integer newUserId = dbuser.newUser(mail, pwd, username, activationID, image);

            if (newUserId != null) {
                if (!active) {
                    try {
                        User.sendAccountActivationMail(newUserId, username, activationID);
                    } catch (DBUser.UserNotFoundException e) {
                        logger.error(e, e);
                    }
                }
                logger.info("user created. Username:" + username + " Mail: " + mail);
                return true;
            }
            logger.warn("failed to create user. Username:" + username + " Mail: " + mail);
            return false;
        }
    }

    public static boolean newFacebookUser(String mail, String name, long facebook_id)
            throws SQLException {
        try (DBUser dbuser = new DBUser()) {
            Integer newUserId = dbuser.newFacebookUser(mail, name, facebook_id);
            if (newUserId != null) {
                activateByUserId(newUserId);
                logger.info("user created. Username:" + name + " Mail: " + mail);
                return true;
            }

            logger.warn("failed to create user. Username:" + name + " Mail: " + mail);
            return false;
        }
    }

    /**
     * ueberprueft ob username schon in der Datenbank vorhanden ist
     *
     * @param username zu pruefender Username
     * @return true, wenn schon vorhanden
     */
    public static boolean checkUsername(String username) throws SQLException {
        try (DBUser db = new DBUser()) {
            return db.checkUsername(username);
        }
    }

    /**
     * ueberprueft ob mail schon in der Datenbank vorhanden ist
     *
     * @param mail zu pruefende mailadresse
     * @return true, wenn schon vorhanden
     */
    public static boolean checkMail(String mail) throws SQLException {
        try (DBUser db = new DBUser()) {
            return db.checkMail(mail);
        }
    }

    public static void activateById(String activationId)
            throws SQLException, DBUser.ActivationFailedException {
        try (DBUser dbuser = new DBUser()) {
            int userid = dbuser.activateById(activationId);
            Lifes.addLife(Lifes.CaseType.NEW_USER, userid);
            SiteMapGenerator.generateProfileSiteMap();
        }

    }

    public static void activateByUserId(int userId) throws SQLException {
        try (DBUser dbuser = new DBUser()) {
            dbuser.activateUser(userId);
            Lifes.addLife(Lifes.CaseType.NEW_USER, userId);
            SiteMapGenerator.generateProfileSiteMap();
        }
    }

    public static void resetPassword(String id, String newPw)
            throws SQLException, ResetPasswordException {
        try (DBUser dbuser = new DBUser()) {
            if (dbuser.checkResetPasswordID(id)) {
                dbuser.resetPassword(id, newPw);
                dbuser.deletePasswordID(id);
            } else {
                throw new ResetPasswordException(id);
            }
        }
    }

    public static void sendAccountActivationMail(int id, String name, String activationKey)
            throws SQLException, DBUser.UserNotFoundException {
        Map<String, String> data = new HashMap<>();
        data.put("userName", name);
        data.put("baseUrl", Configuration.getInstance().getRedirectDomain());
        data.put("activationKey", activationKey);
        Notification.sendNotification(id, NotificationType.ACCOUNT_ACTIVATION, data);
    }

    public static String getUsername(int userId) throws SQLException, DBUser.UserNotFoundException {
        try (DBUser dbuser = new DBUser()) {
            return dbuser.getUsername(userId);
        }
    }

    public static String getUseremail(String username)
            throws SQLException, DBUser.UserNotFoundException {
        try (DBUser dbuser = new DBUser()) {
            return dbuser.getUserEmail(username);
        }
    }

    public static String getMailCandidate(int userId)
            throws SQLException, DBUser.UserNotFoundException {
        try (DBUser dbuser = new DBUser()) {
            return dbuser.getMailCandidate(userId);
        }
    }

    public static String getUseremail(int userId)
            throws SQLException, DBUser.UserNotFoundException {
        try (DBUser dbuser = new DBUser()) {
            return dbuser.getUserEmail(userId);
        }
    }

    public static boolean checkPasswordID(String resetPwID) throws SQLException {
        try (DBUser dbuser = new DBUser()) {
            return dbuser.checkResetPasswordID(resetPwID);
        }
    }

    public static int getUserId(String nameOrMail)
            throws SQLException, DBUser.UserNotFoundException {
        try (DBUser dbuser = new DBUser()) {
            if (nameOrMail.equals(adminMail)) {
                return -1;
            }
            return dbuser.getUserId(nameOrMail);
        }
    }

    public static String getUserImage(int userId, ImageType type)
            throws SQLException, IOException, DBUser.UserNotFoundException {
        StringBuilder
                imagePath =
                new StringBuilder(Configuration.getInstance().getImageBasePath()).append("user/");
        try (DBUser dbuser = new DBUser()) {
            String userImage = dbuser.getUserImage(userId);

            switch (type) {
                case SMALL:
                    imagePath.append("small/");
                    break;

                case LARGE:
                    imagePath.append("big/");
                    break;
                case ORIGINAL:
                    imagePath.append("original/");
                    break;
            }
            if (userImage != null) {
                return imagePath.append(userImage).toString();
            }

            logger.info("userimage was null");
            User user = User.init(userId);

            if (user != null && user.facebookID != 0) {
                return user.getUserImage(type);
            }
            return imagePath.append(getRandomUserpic()).toString();
        }

    }

    public static List<String> getUsernames(List<User> list) {
        List<String> userNames = new LinkedList<>();
        for (User user : list) {
            userNames.add(user.name);
        }
        return userNames;
    }

    public static int getTotal() throws SQLException {
        try (DBUser db = new DBUser()) {
            return db.getTotal();
        }
    }

    public static String getRandomUserpic() {
        RandomDataGenerator random = new RandomDataGenerator();
        return String.format("userpic%d.png", random.nextInt(1, 3));
    }


    public static void createResetPasswordID(String mail)
            throws SQLException, IOException, DBUser.UserNotFoundException {
        User user = User.init(mail);

        String resetPWID = RandomStringUtils.randomAlphanumeric(16);
        try (DBUser dbuser = new DBUser()) {
            dbuser.setResetPasswordID(user.getId(), resetPWID);
        }
        user.sendResetPasswordMail(resetPWID);
        logger.info(mail + " wants to reset password");
    }

    public static String generateAndSaveActivationId(int userId) throws SQLException {
        String activationId = RandomStringUtils.randomAlphanumeric(20);
        try (DBUser dbUser = new DBUser()) {
            dbUser.setActivationId(userId, activationId);
            return activationId;
        }
    }

    public static void resendActivationId(int userId)
            throws SQLException, DBUser.UserNotFoundException {
        String username = User.getUsername(userId);
        try (DBUser dbUser = new DBUser()) {
            String activationId = dbUser.getActivationId(userId);
            if (activationId == null) {
                activationId = User.generateAndSaveActivationId(userId);
            }
            User.sendAccountActivationMail(userId, username, activationId);
        }
    }


    /* members */

    private int id;
    private String name;
    private Image image;

    @PrivateView
    private String mail;

    @PrivateView
    @PublicView
    private long facebookID;

    @PrivateView
    private String emailCandidate;

    @PublicView
    @PrivateView
    private String text;

    @PublicView
    @PrivateView
    private int level;

    @PublicView
    @PrivateView
    private long createDate;

    @PublicView
    @PrivateView
    private long lastLogin;

    @PublicView
    @PrivateView
    private String place;


    @PublicView
    @PrivateView
    private Location location;

    @PublicView
    @PrivateView
    private List<Integer> following;

    @PublicView
    @PrivateView
    private List<Integer> followers;


    public User() {
    }


    public User(int id, String name, String image) {
        this.name = name;
        this.id = id;
        this.image = new UserImage(image);
        this.facebookID = -1;
        this.level = -1;
    }

    public User(int id, String name,
                String mail,
                long facebookID,
                String image,
                int level,
                String text,
                Date createDate,
                Date lastLogin,
                String place,
                double placeLatitude,
                double placeLongitude,
                List<Integer> following,
                List<Integer> followers,
                String emailCandidate) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.facebookID = facebookID;
        this.image = new UserImage(image);
        this.text = text;
        this.level = level;
        this.createDate = createDate.getTime();
        this.lastLogin = lastLogin != null ? lastLogin.getTime() : -1;
        this.place = place;
        this.location = new Location(placeLatitude, placeLongitude);
        this.following = following;
        this.followers = followers;
        this.emailCandidate = emailCandidate;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMail() {
        return mail;
    }

    public Image getImage() {
        return image;
    }

    public String getText() {
        return text;
    }

    public long getCreateDate() {
        return createDate;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public String getPlace() {
        return place;
    }

    public List<Integer> getFollowing() {
        return following;
    }

    public List<Integer> getFollowers() {
        return followers;
    }

    public long getFacebookID() {
        return facebookID;
    }

    public int getLevel() {
        return level;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setFacebookID(long facebookID) {
        this.facebookID = facebookID;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setFollowing(List<Integer> following) {
        this.following = following;
    }

    public void setFollowers(List<Integer> followers) {
        this.followers = followers;
    }

    public void setEmailCandidate(String emailCandidate) {
        this.emailCandidate = emailCandidate;
    }

    public Location getLocation() {
        return location;
    }

    //@JsonIgnore
    public boolean isAdmin() {
        return level == 2;
    }

    //@JsonIgnore
    public String getUserImage(ImageType type)
            throws SQLException, IOException, DBUser.UserNotFoundException {
        return getUserImage(id, type);
    }

    //@JsonIgnore
    public String getFaceBookAccessToken() throws IOException {
        return facebookID == 0 ? null : FacebookHandler.getUsersOAuthToken(facebookID);
    }

    //@JsonIgnore
    public String getFacebookPermissions() throws IOException {
        return FacebookHandler.getPermissions(getFaceBookAccessToken(), facebookID);
    }

    //@JsonView(Views.PrivateUserView.class)
    public String getEmailCandidate() {
        return emailCandidate;
    }

    //Mails
    public void sendAccountActivationMail(String activationKey) throws SQLException {
        try {
            sendAccountActivationMail(id, name, activationKey);
        } catch (DBUser.UserNotFoundException e) {
            logger.error(e, e);
        }
    }

    public void sendResetPasswordMail(String passwordId) throws SQLException {
        Map<String, String> data = new HashMap<>();
        data.put("resetKey", passwordId);
        try {
            Notification.sendNotification(id, NotificationType.RESET_PASSWORD, data);
        } catch (DBUser.UserNotFoundException e) {
            logger.error(e, e);
        }
    }


    public void follow(int userId) throws SQLException {
        DBUser db = new DBUser();
        db.follow(id, userId);
        logger.info(name + " now following " + userId);
        db.close();
    }

    public void unFollow(int userId) throws SQLException {
        DBUser db = new DBUser();
        db.unFollow(id, userId);
        logger.info(name + " unfollowed " + userId);
        db.close();
    }


    public void setImage(String newImage) throws SQLException {
        try (DBUser db = new DBUser()) {
            db.changeImage(id, newImage);
            logger.info(mail + " changed image to " + image);
            this.image = new UserImage(newImage);
        }
    }

    public boolean setName(String name) throws SQLException {
        if (name == null || this.name != null && this.name.equals(name)) {
            return false;
        }

        try (DBUser db = new DBUser()) {
            db.changeName(id, name);
        }

        this.name = name;
        return true;
    }

    public boolean setPlace(String place) throws SQLException {

        if (place == null || this.place != null && this.place.equals(place)) {
            return false;
        }

        try (DBUser db = new DBUser()) {
            db.changePlace(id, place);

            GeoCode geoCode = new GeoCode();
            setLocation(geoCode.getLocation(place));
        } catch (GeoCode.LocationNotFoundException | IOException e) {
            logger.debug(e, e);
        }

        this.place = place;
        return true;
    }

    public boolean setText(String text) throws SQLException {
        if (text == null || this.text != null && this.text.equals(text)) {
            return false;
        }

        if (text.length() > 140) {
            return false;
        }

        try (DBUser dbuser = new DBUser()) {
            dbuser.changeText(id, text);
        }

        logger.info(mail + " changed text to " + text);

        return true;
    }

    public void setLocation(Location location) throws SQLException {
        if (location == null) {
            return;
        }
        try (DBUser dbUser = new DBUser()) {
            dbUser.changeLocation(id, location);
        }

        this.location = location;

        logger.info(mail + " changed location to " + location);
    }

    public void setMailCandidate(String newMail) throws SQLException {
        String mailActivationCode = RandomStringUtils.randomAlphanumeric(16);

        try (DBUser dbUser = new DBUser()) {
            dbUser.setMailCandidate(id, newMail, mailActivationCode);
        }
        Map<String, String> data = new HashMap<>();
        data.put("activationKey", mailActivationCode);
        try {
            Notification.sendNotification(id, NotificationType.NEW_MAIL, data);
        } catch (DBUser.UserNotFoundException e) {
            //nope
        }
        this.emailCandidate = newMail;
        logger.info(String.format("%d sets a new mail candidate: %s", id, newMail));
    }

    public String confirmMailCandidate(String code) throws DBUser.WrongCodeException, SQLException {
        String oldMail = this.mail;
        try (DBUser dbUser = new DBUser()) {
            dbUser.updateMail(this.id, code);
        }

        this.mail = this.emailCandidate;
        this.emailCandidate = null;

        logger.info(id + " changed mail from " + oldMail + " to " + mail);

        return this.mail;
    }

    public boolean setNewPassword(String oldPassword, String newPassword) throws SQLException {
        try (DBUser dbUser = new DBUser()) {
            return dbUser.changePassword(id, oldPassword, newPassword);
        }
    }

    @Override
    public int compareTo(User anotherUser) {
        return Integer.valueOf(id).compareTo(anotherUser.id);
    }

    public static List<Integer> getAdminIds() throws SQLException {
        try (DBUser dbUser = new DBUser()) {
            return dbUser.getAdminIds();
        }
    }

    // inner classes
    public static class LoginException extends Exception {

        public LoginException(long uid) {
            super("login failed for facebook id: " + uid);
        }

        public LoginException(String s) {
            super("login failed for " + s);
        }

        public LoginException(int s) {
            super("login failed for " + s);
        }
    }

    public static class ResetPasswordException extends Exception {

        public ResetPasswordException(String id) {
            super("failed to reset password for " + id);
        }
    }

}
