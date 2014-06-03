SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `anycook_db` DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci ;
USE `anycook_db` ;

-- -----------------------------------------------------
-- Table `anycook_db`.`gerichte`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`gerichte` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`gerichte` (
  `name` VARCHAR(45) NOT NULL,
  `eingefuegt` DATETIME NOT NULL,
  `parent_gericht` VARCHAR(45) NULL DEFAULT NULL,
  `viewed` INT NULL DEFAULT 0,
  `active_id` INT NULL DEFAULT -1,
  PRIMARY KEY (`name`),
  INDEX `fk_gerichte_gerichte` (`parent_gericht` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `anycook_db`.`tags`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`tags` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`tags` (
  `name` VARCHAR(20) NOT NULL,
  `feature` TINYINT(1) NULL DEFAULT 0,
  PRIMARY KEY (`name`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `anycook_db`.`userlevels`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`userlevels` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`userlevels` (
  `id` INT NOT NULL,
  `shortname` VARCHAR(45) NULL,
  `fullname` VARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `anycook_db`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`users` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nickname` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NULL DEFAULT NULL,
  `lastlogin` DATETIME NULL DEFAULT NULL,
  `createdate` DATE NOT NULL,
  `image` VARCHAR(45) NULL DEFAULT NULL,
  `userlevels_id` INT NOT NULL DEFAULT -1,
  `facebook_id` VARCHAR(15) NULL DEFAULT 0,
  `text` TEXT NULL DEFAULT NULL,
  `place` VARCHAR(45) NULL DEFAULT NULL,
  `place_lat` DOUBLE NULL DEFAULT NULL,
  `place_lng` DOUBLE NULL DEFAULT NULL,
  `notification_recipe_activation` TINYINT(1) NOT NULL DEFAULT 1,
  `notification_recipe_discussion` TINYINT(1) NOT NULL DEFAULT 1,
  `notification_tag_accepted` TINYINT(1) NOT NULL DEFAULT 1,
  `notification_tag_denied` TINYINT(1) NOT NULL DEFAULT 1,
  `notification_newsletter` TINYINT(1) NOT NULL DEFAULT 1,
  `notification_discussion_answer` TINYINT(1) NOT NULL DEFAULT 1,
  `notification_tastes` TINYINT(1) NOT NULL DEFAULT 1,
  `notification_new_message` TINYINT(1) NOT NULL DEFAULT 1,
  `email_candidate` VARCHAR(45) NULL DEFAULT NULL,
  `email_candidate_code` CHAR(16) NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_users_userlevels1` (`userlevels_id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `anycook_db`.`gerichte_has_tags`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`gerichte_has_tags` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`gerichte_has_tags` (
  `gerichte_name` VARCHAR(45) NOT NULL,
  `tags_name` VARCHAR(20) NOT NULL,
  `active` TINYINT(1) NULL DEFAULT 0,
  `users_id` INT NOT NULL,
  PRIMARY KEY (`gerichte_name`, `tags_name`),
  INDEX `fk_gerichte_has_tags_tags1` (`tags_name` ASC),
  INDEX `fk_gerichte_has_tags_users1` (`users_id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `anycook_db`.`kategorien`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`kategorien` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`kategorien` (
  `name` VARCHAR(45) NOT NULL,
  `sortid` INT NOT NULL DEFAULT 0,
  `image` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`name`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `anycook_db`.`versions`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`versions` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`versions` (
  `gerichte_name` VARCHAR(45) NOT NULL,
  `id` INT NOT NULL,
  `eingefuegt` DATETIME NOT NULL,
  `beschreibung` TEXT NULL,
  `personen` INT NULL,
  `skill` INT NULL,
  `kalorien` INT NULL,
  `kategorien_name` VARCHAR(45) NULL,
  `imagename` VARCHAR(25) NULL DEFAULT NULL,
  `min` INT(2) NULL,
  `std` INT(2) NULL,
  `viewed_by_admin` TINYINT(1) NULL DEFAULT 0,
  `users_id` INT NULL DEFAULT -1,
  `comment` TEXT NULL DEFAULT NULL,
  PRIMARY KEY (`id`, `gerichte_name`),
  INDEX `fk_versions_gerichte1` (`gerichte_name` ASC),
  INDEX `fk_versions_kategorien1` (`kategorien_name` ASC),
  INDEX `fk_versions_users1` (`users_id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `anycook_db`.`schritte`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`schritte` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`schritte` (
  `idschritte` INT NOT NULL,
  `beschreibung` TEXT NOT NULL,
  `versions_gerichte_name` VARCHAR(45) NOT NULL,
  `versions_id` INT NOT NULL,
  PRIMARY KEY (`idschritte`, `versions_gerichte_name`, `versions_id`),
  INDEX `fk_schritte_versions1` (`versions_id` ASC, `versions_gerichte_name` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `anycook_db`.`schmeckt`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`schmeckt` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`schmeckt` (
  `gerichte_name` VARCHAR(45) NOT NULL,
  `users_id` INT NOT NULL,
  `eingefuegt` DATETIME NOT NULL,
  PRIMARY KEY (`gerichte_name`, `users_id`),
  INDEX `fk_users_has_gerichte_gerichte1` (`gerichte_name` ASC),
  INDEX `fk_schmeckt_users1` (`users_id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `anycook_db`.`zutaten`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`zutaten` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`zutaten` (
  `name` VARCHAR(45) NOT NULL,
  `parent_zutaten_name` VARCHAR(45) NULL DEFAULT NULL,
  `singular` VARCHAR(45) NULL DEFAULT NULL,
  `stem` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`name`),
  INDEX `fk_zutaten_zutaten1` (`parent_zutaten_name` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `anycook_db`.`cases`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`cases` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`cases` (
  `name` VARCHAR(30) NOT NULL,
  `syntax` TEXT NOT NULL,
  `id` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`, `name`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `anycook_db`.`life`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`life` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`life` (
  `idlife` INT NOT NULL AUTO_INCREMENT,
  `lifetime` DATETIME NOT NULL,
  `gerichte_name` VARCHAR(45) NULL,
  `cases_name` VARCHAR(30) NOT NULL,
  `users_id` INT NOT NULL,
  PRIMARY KEY (`idlife`),
  INDEX `fk_life_gerichte1` (`gerichte_name` ASC),
  INDEX `fk_life_cases1` (`cases_name` ASC),
  INDEX `fk_life_users1` (`users_id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `anycook_db`.`activationids`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`activationids` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`activationids` (
  `activationid` VARCHAR(20) NOT NULL,
  `users_id` INT NOT NULL,
  PRIMARY KEY (`users_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `anycook_db`.`mailanbieter`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`mailanbieter` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`mailanbieter` (
  `shortname` VARCHAR(20) NOT NULL,
  `fullname` VARCHAR(45) NOT NULL,
  `redirect` VARCHAR(60) NOT NULL,
  `image` VARCHAR(60) NULL,
  PRIMARY KEY (`shortname`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `anycook_db`.`maildomains`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`maildomains` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`maildomains` (
  `domain` VARCHAR(45) NOT NULL,
  `mailanbieter_shortname` VARCHAR(20) NOT NULL,
  PRIMARY KEY (`domain`, `mailanbieter_shortname`),
  INDEX `fk_maildomains_mailanbieter1` (`mailanbieter_shortname` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `anycook_db`.`versions_has_zutaten`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`versions_has_zutaten` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`versions_has_zutaten` (
  `versions_gerichte_name` VARCHAR(45) NOT NULL,
  `versions_id` INT NOT NULL,
  `zutaten_name` VARCHAR(45) NOT NULL,
  `menge` VARCHAR(45) NULL,
  `position` INT NULL DEFAULT 0,
  PRIMARY KEY (`versions_gerichte_name`, `versions_id`, `zutaten_name`),
  INDEX `fk_versions_has_zutaten_zutaten1` (`zutaten_name` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `anycook_db`.`discussions_events`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`discussions_events` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`discussions_events` (
  `name` VARCHAR(45) NOT NULL,
  `syntax` TEXT NOT NULL,
  PRIMARY KEY (`name`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `anycook_db`.`discussions`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`discussions` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`discussions` (
  `gerichte_name` VARCHAR(45) NOT NULL,
  `id` INT NOT NULL,
  `parent_id` INT NULL DEFAULT -1,
  `text` TEXT NULL,
  `eingefuegt` DATETIME NULL,
  `discussions_events_name` VARCHAR(45) NULL DEFAULT NULL,
  `users_id` INT NOT NULL,
  `schritte_idschritte` INT NULL DEFAULT NULL,
  `versions_id` INT NULL DEFAULT NULL,
  PRIMARY KEY (`gerichte_name`, `id`),
  INDEX `fk_discussions_discussions_events1` (`discussions_events_name` ASC),
  INDEX `fk_discussions_users1` (`users_id` ASC),
  INDEX `fk_discussions_schritte1` (`schritte_idschritte` ASC),
  INDEX `fk_discussions_versions1` (`versions_id` ASC));


-- -----------------------------------------------------
-- Table `anycook_db`.`discussions_like`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`discussions_like` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`discussions_like` (
  `discussions_gerichte_name` VARCHAR(45) NOT NULL,
  `discussions_id` INT NOT NULL,
  `users_id` INT NOT NULL,
  PRIMARY KEY (`discussions_gerichte_name`, `discussions_id`, `users_id`),
  INDEX `fk_like_not_discussions1` (`discussions_gerichte_name` ASC, `discussions_id` ASC),
  INDEX `fk_like_not_users1` (`users_id` ASC));


-- -----------------------------------------------------
-- Table `anycook_db`.`tagesrezepte`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`tagesrezepte` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`tagesrezepte` (
  `date` DATE NOT NULL,
  `gerichte_name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`date`),
  INDEX `fk_tagesrezepte_gerichte1` (`gerichte_name` ASC));


-- -----------------------------------------------------
-- Table `anycook_db`.`permanent_cookies`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`permanent_cookies` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`permanent_cookies` (
  `id` VARCHAR(25) NOT NULL,
  `users_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_permanent_cookies_users1` (`users_id` ASC));


-- -----------------------------------------------------
-- Table `anycook_db`.`facebooksettings`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`facebooksettings` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`facebooksettings` (
  `postschmeckt` TINYINT(1) NOT NULL DEFAULT 0,
  `users_id` INT NOT NULL,
  INDEX `fk_facebooksettings_users1` (`users_id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `anycook_db`.`tumblr`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`tumblr` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`tumblr` (
  `users_id` INT NOT NULL,
  `oauth_key` VARCHAR(64) NOT NULL,
  `oauth_secret` VARCHAR(64) NOT NULL,
  `default_blog` VARCHAR(64) NULL DEFAULT NULL,
  INDEX `fk_tumblr_users1` (`users_id` ASC),
  PRIMARY KEY (`users_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `anycook_db`.`message_sessions`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`message_sessions` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`message_sessions` (
  `id` INT NOT NULL,
  PRIMARY KEY (`id`));


-- -----------------------------------------------------
-- Table `anycook_db`.`message_sessions_has_users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`message_sessions_has_users` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`message_sessions_has_users` (
  `message_sessions_id` INT NOT NULL,
  `users_id` INT NOT NULL,
  PRIMARY KEY (`message_sessions_id`, `users_id`),
  INDEX `fk_message_sessions_has_users_users1` (`users_id` ASC),
  INDEX `fk_message_sessions_has_users_message_sessions1` (`message_sessions_id` ASC));


-- -----------------------------------------------------
-- Table `anycook_db`.`messages`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`messages` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`messages` (
  `id` INT NOT NULL,
  `text` TEXT NOT NULL,
  `datetime` DATETIME NOT NULL,
  `sender` INT NOT NULL,
  `message_sessions_id` INT NOT NULL,
  PRIMARY KEY (`id`, `message_sessions_id`),
  INDEX `fk_messages_users1` (`sender` ASC),
  INDEX `fk_messages_message_sessions1` (`message_sessions_id` ASC));


-- -----------------------------------------------------
-- Table `anycook_db`.`followers`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`followers` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`followers` (
  `users_id` INT NOT NULL,
  `following` INT NOT NULL,
  PRIMARY KEY (`users_id`, `following`),
  INDEX `fk_users_has_users_users2` (`following` ASC),
  INDEX `fk_users_has_users_users1` (`users_id` ASC));


-- -----------------------------------------------------
-- Table `anycook_db`.`messages_unread`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`messages_unread` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`messages_unread` (
  `messages_id` INT NOT NULL,
  `messages_message_sessions_id` INT NOT NULL,
  `users_id` INT NOT NULL,
  PRIMARY KEY (`messages_id`, `messages_message_sessions_id`, `users_id`),
  INDEX `fk_messages_has_users_users1` (`users_id` ASC),
  INDEX `fk_messages_has_users_messages1` (`messages_id` ASC, `messages_message_sessions_id` ASC));


-- -----------------------------------------------------
-- Table `anycook_db`.`schritte_has_zutaten`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`schritte_has_zutaten` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`schritte_has_zutaten` (
  `schritte_idschritte` INT NOT NULL,
  `schritte_versions_gerichte_name` VARCHAR(45) NOT NULL,
  `schritte_versions_id` INT NOT NULL,
  `zutaten_name` VARCHAR(45) NOT NULL,
  `menge` VARCHAR(45) NULL,
  `position` INT NOT NULL,
  PRIMARY KEY (`schritte_idschritte`, `schritte_versions_gerichte_name`, `schritte_versions_id`, `zutaten_name`),
  INDEX `fk_schritte_has_zutaten_zutaten1` (`zutaten_name` ASC),
  INDEX `fk_schritte_has_zutaten_schritte1` (`schritte_idschritte` ASC, `schritte_versions_gerichte_name` ASC, `schritte_versions_id` ASC));


-- -----------------------------------------------------
-- Table `anycook_db`.`apps`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`apps` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`apps` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `secret` VARCHAR(45) NOT NULL,
  `domain` VARCHAR(150) NOT NULL,
  `name` VARCHAR(45) NOT NULL,
  `mail` VARCHAR(200) NULL DEFAULT NULL,
  `website` VARCHAR(200) NULL DEFAULT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `anycook_db`.`apps_has_users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`apps_has_users` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`apps_has_users` (
  `apps_id` INT NOT NULL,
  `users_id` INT NOT NULL,
  `oauth_key` VARCHAR(45) NULL DEFAULT NULL,
  `oauth_secret` VARCHAR(45) NULL DEFAULT NULL,
  PRIMARY KEY (`apps_id`, `users_id`),
  INDEX `fk_apps_has_users_users1` (`users_id` ASC),
  INDEX `fk_apps_has_users_apps1` (`apps_id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `anycook_db`.`users_has_apps`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`users_has_apps` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`users_has_apps` (
  `users_id` INT NOT NULL,
  `apps_id` INT NOT NULL,
  PRIMARY KEY (`users_id`, `apps_id`),
  INDEX `fk_users_has_apps_apps1` (`apps_id` ASC),
  INDEX `fk_users_has_apps_users1` (`users_id` ASC));


-- -----------------------------------------------------
-- Table `anycook_db`.`login_attemps`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`login_attemps` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`login_attemps` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `address` VARCHAR(45) NULL,
  `time` TIMESTAMP NULL,
  `successfull` TINYINT(1) NULL,
  `users_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_login_attemps_users1_idx` (`users_id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `anycook_db`.`resetpasswordids`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `anycook_db`.`resetpasswordids` ;

CREATE TABLE IF NOT EXISTS `anycook_db`.`resetpasswordids` (
  `id` CHAR(16) NOT NULL,
  `users_id` INT NOT NULL,
  PRIMARY KEY (`id`, `users_id`),
  INDEX `fk_resetpasswordids_users1_idx` (`users_id` ASC))
ENGINE = InnoDB;

USE `anycook_db` ;

-- -----------------------------------------------------
-- procedure search_by_ingredient
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`search_by_ingredient`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `anycook_db`.`search_by_ingredient` (IN ingredient_in VARCHAR(45))
BEGIN

DECLARE rowcount INT;
DECLARE current_ingredient VARCHAR(45);

CREATE TEMPORARY TABLE tmp_ingredients (ingredient VARCHAR(45) PRIMARY KEY NOT NULL) engine=memory;
CREATE TEMPORARY TABLE ingredient_stack (ingredient VARCHAR(45) PRIMARY KEY NOT NULL) engine=memory SELECT ingredient_in AS ingredient;

REPEAT
	SELECT ingredient INTO current_ingredient FROM ingredient_stack LIMIT 1;
	INSERT INTO tmp_ingredients VALUES (current_ingredient);
	DELETE FROM ingredient_stack WHERE ingredient = current_ingredient;
	INSERT INTO ingredient_stack (ingredient) SELECT name FROM zutaten WHERE parent_zutaten_name = current_ingredient;
	SET rowcount = (SELECT COUNT(*) FROM ingredient_stack);
UNTIL (rowcount = 0) END REPEAT;

DROP TEMPORARY TABLE ingredient_stack;

SELECT found_recipes.name, COUNT(found_recipes.users) AS schmecktcount FROM (
	SELECT gerichte.name AS name, schmeckt.users_id AS users from gerichte
		INNER JOIN versions_has_zutaten ON gerichte.name = versions_gerichte_name AND gerichte.active_id = versions_id
		LEFT JOIN schmeckt ON gerichte.name = schmeckt.gerichte_name
		WHERE zutaten_name IN	(SELECT * FROM tmp_ingredients) 
		GROUP BY gerichte.name, schmeckt.users_id) AS found_recipes 
	GROUP BY found_recipes.name
	ORDER BY schmecktcount DESC;

DROP TEMPORARY TABLE tmp_ingredients;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure new_user
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`new_user`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `anycook_db`.`new_user` (IN uname VARCHAR(45), IN upass VARCHAR(45), IN umail VARCHAR(45), IN uniqueid VARCHAR(20), 
	IN uimage VARCHAR(45), OUT uid INT)
BEGIN
	DECLARE usercount BOOL;	
	DECLARE no_more_types BOOL DEFAULT 0;
	DECLARE mailnot_type VARCHAR(40);
	DECLARE mailnot_cur CURSOR FOR SELECT type FROM mailnotifications;
	DECLARE CONTINUE HANDLER FOR SQLSTATE '02000' SET no_more_types = 1;
	

	SET uid = 0;
	SELECT COUNT(*) INTO usercount FROM users WHERE nickname = uname OR email = umail LIMIT 1;
	IF usercount = 0 THEN
		INSERT INTO users(nickname, email, password, createdate, image) VALUES (uname, umail, PASSWORD(upass), CURDATE(), uimage);
		SELECT id INTO uid FROM users WHERE email = umail;

		OPEN mailnot_cur;
		FETCH mailnot_cur INTO mailnot_type;
		REPEAT
			INSERT INTO users_has_mailnotifications(users_id, mailnotifications_type) VALUES (uid, mailnot_type);
			FETCH mailnot_cur INTO mailnot_type;
		UNTIL no_more_types = 1
		END REPEAT;
		CLOSE mailnot_cur;

		IF uniqueid IS NOT NULL THEN
			INSERT INTO activationids (users_id, activationid) VALUES (uid, uniqueid);
		ELSE
			UPDATE users SET userlevels_id = 0;
		END IF;

	END IF;		
	
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure recipe_ingredients
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`recipe_ingredients`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `anycook_db`.`recipe_ingredients` (IN recipe_name VARCHAR(45))
BEGIN
	SELECT zutaten_name, singular, menge FROM gerichte
		INNER JOIN versions_has_zutaten ON gerichte.active_id = versions_id AND gerichte.name = versions_gerichte_name 
		INNER JOIN zutaten ON zutaten_name = zutaten.name 
		WHERE versions_gerichte_name = recipe_name;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure recipes_from_schmeckttags
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`recipes_from_schmeckttags`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `anycook_db`.`recipes_from_schmeckttags` (IN userid INT)
BEGIN
	SELECT gerichte_name, tags_name FROM gerichte_has_tags
	WHERE tags_name IN 
		(SELECT tags_name from schmeckt 
			INNER JOIN gerichte_has_tags USING (gerichte_name)
			WHERE active = 1 AND schmeckt.users_id = userid GROUP BY tags_name)
	AND gerichte_name NOT IN 
		(SELECT gerichte_name FROM versions WHERE users_id = userid GROUP BY gerichte_name)
	AND gerichte_name NOT IN
		(SELECT gerichte_name FROM schmeckt WHERE users_id = userid);	
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure get_recipe
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`get_recipe`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `anycook_db`.`get_recipe` (IN recipe_name VARCHAR(45), IN login_id INT)
BEGIN

SELECT versions.id AS id, gerichte.name AS name, beschreibung, IFNULL(versions.imagename, CONCAT('category/', kategorien.image)) AS image, gerichte.eingefuegt AS created, 
	min, std, skill, kalorien, personen, kategorien_name, active_id, users_id, nickname, users.image, viewed,
	(SELECT MAX(eingefuegt) FROM versions WHERE gerichte_name = gerichte.name) AS lastChange,
	(SELECT COUNT(users_id) FROM schmeckt WHERE schmeckt.gerichte_name = recipe_name AND schmeckt.users_id = login_id) AS tastes
FROM gerichte
INNER JOIN versions ON IF(active_id > 0, gerichte.name = gerichte_name AND active_id = versions.id, gerichte.name = gerichte_name) 
INNER JOIN users ON users_id = users.id
INNER JOIN kategorien ON kategorien_name = kategorien.name 
WHERE gerichte.name = recipe_name;

END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure recipe_image
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`recipe_image`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `anycook_db`.`recipe_image` (IN recipe_name VARCHAR(45))
BEGIN
	SELECT imagename, kategorien.image FROM gerichte 
		INNER JOIN versions ON gerichte_name = name AND id = active_id
		INNER JOIN kategorien ON versions.kategorien_name = kategorien.name
		WHERE gerichte.name = recipe_name;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure get_discussion
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`get_discussion`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `anycook_db`.`get_discussion` (IN recipe_name VARCHAR(45), IN maxid INT, IN userid INT)
BEGIN
	SELECT parent_id, discussions.id, nickname, users.id, users.image, versions_id, discussions.text, discussions.eingefuegt, 
		syntax, COUNT(discussions_like.users_id) AS votes, IF(discussions_like.users_id = userid, 1, 0) AS liked,
		gerichte.active_id FROM discussions
		LEFT JOIN users ON discussions.users_id = users.id 
		LEFT JOIN gerichte ON gerichte_name = gerichte.name 
		LEFT JOIN discussions_like ON gerichte_name = discussions_gerichte_name AND discussions.id = discussions_id 
		LEFT JOIN discussions_events ON discussions_events_name = discussions_events.name 
		WHERE gerichte_name = recipe_name AND discussions.id > maxid GROUP BY discussions.id ORDER BY discussions.id;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure backend_get_version
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`backend_get_version`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `anycook_db`.`backend_get_version` (IN recipeName VARCHAR(45))
BEGIN
	SELECT id, eingefuegt, steps.count, ingredients.count, viewed_by_admin, users_id, imagename, beschreibung
		FROM versions
		LEFT JOIN (SELECT versions_id, COUNT(idschritte) AS count FROM schritte 
			WHERE versions_gerichte_name = recipeName GROUP BY versions_id) AS steps ON id = steps.versions_id
		LEFT JOIN (SELECT versions_id, COUNT(zutaten_name) AS count FROM versions_has_zutaten 
			WHERE versions_gerichte_name = recipeName GROUP BY versions_id) AS ingredients ON id = ingredients.versions_id
		WHERE gerichte_name = recipeName
		GROUP BY id ORDER BY id;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure backend_get_all_recipes
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`backend_get_all_recipes`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `anycook_db`.`backend_get_all_recipes` ()
BEGIN
	SELECT name, gerichte.eingefuegt, active_id, viewed, COUNT(schmeckt.users_id) AS schmeckt, 
		MIN(viewed_by_admin) AS adminviewed, 
		(SELECT COUNT(id) FROM versions WHERE gerichte_name = name) AS num_versions 
		FROM gerichte
		LEFT JOIN versions ON versions.gerichte_name = name
		LEFT JOIN schmeckt ON schmeckt.gerichte_name = name
		GROUP BY name, versions.id ORDER BY eingefuegt DESC;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure backend_get_recipe
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`backend_get_recipe`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `anycook_db`.`backend_get_recipe` (IN recipeName VARCHAR(45))
BEGIN
	SELECT name, gerichte.eingefuegt, active_id, viewed, COUNT(schmeckt.users_id) AS schmeckt, 
		MIN(viewed_by_admin) AS adminviewed, 
		(SELECT COUNT(id) FROM versions WHERE gerichte_name = recipeName) AS num_versions 
		FROM gerichte
		LEFT JOIN versions ON versions.gerichte_name = name
		LEFT JOIN schmeckt ON schmeckt.gerichte_name = name
		WHERE name = recipeName
		GROUP BY name, versions.id ORDER BY eingefuegt DESC;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure backend_update_recipe
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`backend_update_recipe`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `anycook_db`.`backend_update_recipe` (IN recipeName VARCHAR(45), IN new_activeId INT)
BEGIN
	UPDATE gerichte SET active_id = new_activeId WHERE name = recipeName;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure backend_delete_recipe
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`backend_delete_recipe`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `anycook_db`.`backend_delete_recipe` (IN recipeName VARCHAR(45))
BEGIN
	DECLARE check_exist BOOL;

	SELECT COUNT(*) INTO check_exist FROM gerichte WHERE recipeName = name;

	IF check_exist THEN
		DELETE FROM discussions_like WHERE discussions_gerichte_name = recipeName;
		DELETE FROM tagesrezepte WHERE gerichte_name = recipeName;
		DELETE FROM discussions WHERE gerichte_name = recipeName;
		DELETE FROM gerichte_has_tags WHERE gerichte_name = recipeName;
		DELETE FROM life WHERE gerichte_name = recipeName;
		DELETE FROM schmeckt WHERE gerichte_name = recipeName;
		DELETE FROM schritte_has_zutaten WHERE schritte_versions_gerichte_name = recipeName;
 		DELETE FROM schritte WHERE versions_gerichte_name = recipeName;
		DELETE FROM versions_has_zutaten WHERE versions_gerichte_name = recipeName;
		DELETE FROM versions WHERE gerichte_name = recipeName;
		DELETE FROM gerichte WHERE name = recipeName;

	END IF;

END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure backend_get_users
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`backend_get_users`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `anycook_db`.`backend_get_users` ()
BEGIN
	SELECT id, nickname, email, lastlogin, createdate, userlevels_id, facebook_id, place, 
		follower.followerCount AS numFollowers, following.followingCount AS numFollowing, recipes.recipeCount AS recipeCount FROM users
		LEFT JOIN (SELECT COUNT(users_id) AS followerCount, following FROM followers GROUP BY following) AS follower ON id = follower.following
		LEFT JOIN (SELECT COUNT(following) AS followingCount, users_id FROM followers GROUP BY users_id) AS following ON id = following.users_id
		LEFT JOIN (SELECT COUNT(name) AS recipeCount, users_id FROM versions INNER JOIN gerichte ON name = gerichte_name GROUP BY users_id) AS recipes 
			ON recipes.users_id = id;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure backend_get_user
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`backend_get_user`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `anycook_db`.`backend_get_user` (IN userId INT)
BEGIN
	SELECT id, nickname, email, lastlogin, createdate, userlevels_id, facebook_id, place, 
		follower.followerCount AS numFollowers, following.followingCount AS numFollowing, recipes.recipeCount AS recipeCount FROM users
		LEFT JOIN (SELECT COUNT(users_id) AS followerCount, following FROM followers GROUP BY following) AS follower ON id = follower.following
		LEFT JOIN (SELECT COUNT(following) AS followingCount, users_id FROM followers GROUP BY users_id) AS following ON id = following.users_id
		LEFT JOIN (SELECT COUNT(name) AS recipeCount, users_id FROM versions INNER JOIN gerichte ON name = gerichte_name GROUP BY users_id) AS recipes 
			ON recipes.users_id = id
		WHERE id = userId;
END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure tasty_recipes
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`tasty_recipes`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `tasty_recipes` (IN length INT, IN login_id INT)
BEGIN

SELECT versions.id AS id, beschreibung, IFNULL(versions.imagename, CONCAT('category/', kategorien.image)) AS image, gerichte.eingefuegt AS created, 
	min, std, skill, kalorien, gerichte.name AS name, personen, kategorien_name, active_id, users_id, nickname, users.image, COUNT(users_id) AS counter, viewed,
	(SELECT MAX(eingefuegt) FROM versions WHERE gerichte_name = gerichte.name) AS lastChange,
	(SELECT COUNT(users_id) FROM schmeckt WHERE schmeckt.gerichte_name = gerichte.name AND schmeckt.users_id = login_id) AS tastes 
	FROM gerichte
	INNER JOIN versions ON gerichte.name = gerichte_name AND active_id = versions.id 
	INNER JOIN users ON users_id = users.id
	INNER JOIN kategorien ON kategorien_name = kategorien.name 
	GROUP BY gerichte.name ORDER BY counter DESC LIMIT length;

END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure popular_recipes
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`popular_recipes`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `popular_recipes` (IN length INT, IN login_id INT)
BEGIN

SELECT beschreibung, IFNULL(versions.imagename, CONCAT('category/', kategorien.image)) AS image, gerichte.eingefuegt AS created, 
	min, std, skill, kalorien, gerichte.name, personen, kategorien_name, active_id, users_id, nickname, users.image, viewed,
	(SELECT MAX(eingefuegt) FROM versions WHERE gerichte_name = gerichte.name) AS lastChange,
	(SELECT COUNT(users_id) FROM schmeckt WHERE schmeckt.gerichte_name = gerichte.name AND schmeckt.users_id = login_id) AS tastes
	FROM gerichte
	INNER JOIN versions ON gerichte.name = gerichte_name AND active_id = versions.id 
	INNER JOIN users ON users_id = users.id
	INNER JOIN kategorien ON kategorien_name = kategorien.name 
	GROUP BY gerichte.name ORDER BY viewed DESC LIMIT length;

END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure newest_recipes
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`newest_recipes`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `newest_recipes` (IN length INT, IN login_id INT)
BEGIN

SELECT versions.id AS id, beschreibung, IFNULL(versions.imagename, CONCAT('category/', kategorien.image)) AS image, gerichte.eingefuegt AS created, 
	min, std, skill, kalorien, gerichte.name AS name, personen, kategorien_name, active_id, users_id, nickname, users.image, viewed,
	(SELECT MAX(eingefuegt) FROM versions WHERE gerichte_name = gerichte.name) AS lastChange,
	(SELECT IF(COUNT(users_id) = 1, true, false) FROM schmeckt WHERE schmeckt.gerichte_name = gerichte.name AND schmeckt.users_id = login_id) AS tastes
	FROM gerichte
	INNER JOIN versions ON gerichte.name = gerichte_name AND active_id = versions.id 
	INNER JOIN users ON users_id = users.id
	INNER JOIN kategorien ON kategorien_name = kategorien.name 
	GROUP BY gerichte.name ORDER BY gerichte.eingefuegt DESC LIMIT length;

END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure user_recipes
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`user_recipes`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `user_recipes` (IN userId INT, IN login_id INT)
BEGIN

SELECT versions.id AS id, beschreibung, IFNULL(versions.imagename, CONCAT('category/', kategorien.image)) AS image, gerichte.eingefuegt AS created, 
	min, std, skill, kalorien, gerichte.name AS name, personen, kategorien_name, active_id, users_id, nickname, users.image, COUNT(users_id) AS counter, viewed,
	(SELECT MAX(eingefuegt) FROM versions WHERE gerichte_name = gerichte.name) AS lastChange,
	(SELECT COUNT(users_id) FROM schmeckt WHERE schmeckt.gerichte_name = gerichte.name AND schmeckt.users_id = login_id) AS tastes
	FROM gerichte
	INNER JOIN versions ON gerichte.name = gerichte_name 
	INNER JOIN users ON users_id = users.id
	INNER JOIN kategorien ON kategorien_name = kategorien.name 
	WHERE versions.users_id = userId AND active_id > -1 
	GROUP BY gerichte.name ORDER BY versions.eingefuegt DESC;

END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure active_recipes
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`active_recipes`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `active_recipes` ()
BEGIN

SELECT versions.id AS id, gerichte.name AS name, beschreibung, IFNULL(versions.imagename, CONCAT('category/', kategorien.image)) AS image, gerichte.eingefuegt AS created, 
	min, std, skill, kalorien, personen, kategorien_name, active_id, users_id, users.image, nickname,
	(SELECT MAX(eingefuegt) FROM versions WHERE gerichte_name = gerichte.name) AS lastChange
	FROM gerichte
	INNER JOIN versions ON gerichte.name = gerichte_name AND active_id = versions.id 
	INNER JOIN users ON users_id = users.id
	INNER JOIN kategorien ON kategorien_name = kategorien.name
	WHERE active_id > -1;

END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure tasting_recipes
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`tasting_recipes`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `tasting_recipes` (IN user_id INT, IN login_id INT)
BEGIN

SELECT versions.id AS id, beschreibung, IFNULL(versions.imagename, CONCAT('category/', kategorien.image)) AS image, gerichte.eingefuegt AS created, 
	min, std, skill, kalorien, gerichte.name AS name, personen, kategorien_name, active_id, users.id AS users_id, nickname, users.image, viewed,
	(SELECT MAX(eingefuegt) FROM versions WHERE gerichte_name = gerichte.name) AS lastChange,
	(SELECT IF(COUNT(users_id) = 1, true, false) FROM schmeckt WHERE schmeckt.gerichte_name = gerichte.name AND schmeckt.users_id = login_id) AS tastes
	FROM gerichte
	INNER JOIN versions ON gerichte.name = gerichte_name AND active_id = versions.id 
	INNER JOIN users ON versions.users_id = users.id
	INNER JOIN kategorien ON kategorien_name = kategorien.name
	INNER JOIN schmeckt ON gerichte.name = schmeckt.gerichte_name
	WHERE schmeckt.users_id = user_id GROUP BY name ORDER BY schmeckt.eingefuegt DESC;

END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure get_version
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`get_version`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `anycook_db`.`get_version` (IN recipe_name VARCHAR(45), IN version_id INT, IN login_id INT)
BEGIN

SELECT versions.id AS id, beschreibung, IFNULL(versions.imagename, CONCAT('category/', kategorien.image)) AS image, gerichte.eingefuegt AS created, versions.eingefuegt AS lastChange, 
min, std, skill, kalorien, gerichte.name, personen, kategorien_name, active_id, users_id, nickname, users.image, viewed,
(SELECT IF(COUNT(users_id) = 1, true, false) FROM schmeckt WHERE schmeckt.gerichte_name = gerichte.name AND schmeckt.users_id = login_id) AS tastes
FROM gerichte
INNER JOIN versions ON gerichte.name = gerichte_name
INNER JOIN users ON users_id = users.id
INNER JOIN kategorien ON kategorien_name = kategorien.name 
WHERE gerichte.name = recipe_name AND versions.id = version_id;

END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure get_all_versions
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`get_all_versions`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `anycook_db`.`get_all_versions` (IN recipe_name VARCHAR(45), IN login_id INT)
BEGIN

SELECT versions.id AS id, beschreibung, IFNULL(versions.imagename, CONCAT('category/', kategorien.image)) AS image, gerichte.eingefuegt AS created, 
	min, std, skill, kalorien, gerichte.name, personen, kategorien_name, active_id, users_id, nickname, users.image, viewed, versions.eingefuegt AS lastChange,
	(SELECT COUNT(users_id) FROM schmeckt WHERE schmeckt.gerichte_name = recipe_name AND schmeckt.users_id = login_id) AS tastes
FROM gerichte
INNER JOIN versions ON gerichte.name = gerichte_name
INNER JOIN users ON users_id = users.id
INNER JOIN kategorien ON kategorien_name = kategorien.name 
WHERE gerichte.name = recipe_name;

END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure get_all_recipes
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`get_all_recipes`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `anycook_db`.`get_all_recipes` (IN login_id INT)
BEGIN

SELECT versions.id AS id, gerichte.name AS name, beschreibung, IFNULL(versions.imagename, CONCAT('category/', kategorien.image)) AS image, gerichte.eingefuegt AS created, 
	min, std, skill, kalorien, personen, kategorien_name, active_id, users_id, users.image, nickname, viewed,
	(SELECT MAX(eingefuegt) FROM versions WHERE gerichte_name = gerichte.name) AS lastChange,
	(SELECT COUNT(users_id) FROM schmeckt WHERE schmeckt.gerichte_name = gerichte.name AND schmeckt.users_id = login_id) AS tastes  
FROM gerichte
INNER JOIN versions ON IF(active_id > 0, gerichte.name = gerichte_name AND active_id = versions.id, gerichte.name = gerichte_name) 
INNER JOIN users ON users_id = users.id
INNER JOIN kategorien ON kategorien_name = kategorien.name
GROUP BY name;

END$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure get_all_users
-- -----------------------------------------------------

USE `anycook_db`;
DROP procedure IF EXISTS `anycook_db`.`get_all_users`;

DELIMITER $$
USE `anycook_db`$$
CREATE PROCEDURE `get_all_users` ()
BEGIN

SELECT id, nickname, facebook_id, email, lastlogin, createdate, image, userlevels_id, text, place, email_candidate FROM users;

END$$

DELIMITER ;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
