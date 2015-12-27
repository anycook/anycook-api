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

package de.anycook.recipe;

import de.anycook.api.views.PublicView;
import de.anycook.api.views.TasteNumView;
import de.anycook.db.mysql.DBGetRecipe;
import de.anycook.db.mysql.DBRecipe;
import de.anycook.drafts.RecipeDraft;
import de.anycook.image.Image;
import de.anycook.image.RecipeImage;
import de.anycook.user.User;

import org.apache.logging.log4j.LogManager;

import java.sql.SQLException;
import java.util.Date;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * Enthaelt die Daten eines aufgerufenen Rezepts
 *
 * @author Jan Grassegger
 */
@XmlRootElement
public class Recipe implements Comparable<Recipe> {

    public static Recipe init(String recipeName)
            throws SQLException, DBRecipe.RecipeNotFoundException {
        return init(recipeName, -1);
    }

    public static Recipe init(String recipeName, int loginId)
            throws SQLException, DBRecipe.RecipeNotFoundException {
        try (DBGetRecipe db = new DBGetRecipe()) {
            return db.get(recipeName, loginId);
        }
    }

    public static Recipe init(String recipeName, int versionId, int loginId)
            throws SQLException, DBRecipe.RecipeNotFoundException {
        try (DBGetRecipe db = new DBGetRecipe()) {
            return db.getVersionData(recipeName, versionId, loginId);
        }
    }

    private String name;
    private int id;
    private Image image;
    private String description;
    private Time time;
    private boolean tasty;
    private int persons;
    private long lastChange;

    @PublicView
    private boolean active;

    @PublicView
    private String category;

    @PublicView
    private int skill;

    @PublicView
    private int calorie;

    @PublicView
    private User author;

    @PublicView
    private long created;

    @PublicView
    private int views;

    public Recipe() {
    }

    public Recipe(int id, String name, String description, String image, int person, Date created,
                  Date lastChange,
                  String category, int skill, int calorie, Time time, int activeId, int views,
                  User author, boolean tasty) {
        this.name = name;
        this.description = description;
        this.image = new RecipeImage(image);
        this.persons = person;
        this.category = category;
        this.skill = skill;
        this.calorie = calorie;
        this.time = time;
        this.id = id;
        this.created = created != null ? created.getTime() : -1;
        this.lastChange = lastChange != null ? lastChange.getTime() : -1;
        this.author = author;
        this.active = id == activeId;
        this.views = views;
        this.tasty = tasty;
    }

    //getter
    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public Image getImage() {
        return image;
    }

    public Time getTime() {
        return time;
    }

    public int getSkill() {
        return skill;
    }

    public int getCalorie() {
        return calorie;
    }

    public int getPersons() {
        return persons;
    }

    @XmlElement
    @TasteNumView
    public int getTasteNum() {
        try (DBGetRecipe dbGetRecipe = new DBGetRecipe()) {
            return dbGetRecipe.getTasteNum(name);
        } catch (SQLException e) {
            LogManager.getLogger(getClass()).error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

    }

    public int getId() {
        return id;
    }

    public User getAuthor() {
        return author;
    }

    public long getCreated() {
        return created;
    }

    public long getLastChange() {
        return lastChange;
    }

    public int getViews() {
        return views;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    //setter
    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public void setSkill(int skill) {
        this.skill = skill;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }

    public void setPersons(int persons) {
        this.persons = persons;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public void setLastChange(long lastChange) {
        this.lastChange = lastChange;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getActiveAuthor() throws SQLException, DBRecipe.RecipeNotFoundException {
        return Recipes.getAuthor(name, id);
    }

    public boolean isTasty() {
        return tasty;
    }

    public void setTasty(boolean tasty) {
        this.tasty = tasty;
    }

    @Override
    public int compareTo(Recipe o) {
        return name.compareTo(o.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public RecipeDraft asDraft() {
        RecipeDraft recipeDraft = new RecipeDraft();
        recipeDraft.setName(name);
        recipeDraft.setDescription(description);
        recipeDraft.setCalorie(calorie);
        recipeDraft.setSkill(skill);
        recipeDraft.setTime(time);
        recipeDraft.setCategory(category);
        recipeDraft.setPersons(persons);

        return recipeDraft;
    }
}
