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

package de.anycook.newrecipe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.anycook.db.drafts.RecipeDraftsStore;
import de.anycook.db.mysql.DBSaveRecipe;
import de.anycook.discussion.Discussion;
import de.anycook.news.life.Lifes;
import de.anycook.recipe.Recipes;
import de.anycook.recipe.Time;
import de.anycook.recipe.ingredient.Ingredient;
import de.anycook.recipe.step.Step;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;


/**
 * beinhaltet Daten, die während der Rezpterstellung vom User erzeugt und speichert diese zwischen.
 * Beinhaltet ausserdem alle Funktionen die zur Rezeoterstellung benoetigt.
 *
 * @author Jan Grassegger
 */

@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewRecipe {

    private static Logger logger = LogManager.getLogger(NewRecipe.class);

    public String name;
    public String description;
    public String image;
    public String category;
    public List<Step> steps;
    public List<Ingredient> ingredients;
    public List<String> tags;
    public Time time;
    public int skill;
    public int calorie;
    public int persons;
    public String comment;
    public String mongoId;


    @Override
    public String toString() {
        return String.format("description: %s, image: %s, steps: %s," +
                             "ingredients: %s, time: %s, skill: %s, calorie: %s," +
                             "persons: %s, tags: %s, comments: %s", description, image, steps,
                             ingredients, time, skill, calorie, persons, tags, comment);
    }

    private boolean check() {
        return name != null && description != null && category != null &&
               steps != null && steps.size() > 0 && ingredients != null &&
               ingredients.size() > 0 && tags != null && time != null &&
               !(time.getStd() == 0 && time.getMin() == 0) && skill > 0 && skill <= 5 &&
               calorie > 0 && calorie <= 5 && persons > 0;
    }


    public int save(final int userId) throws SQLException, IOException, ParseException,
                                             InvalidRecipeException {
        if (!check()) {
            throw new InvalidRecipeException(name);
        }

        int id = 0;

        try (final DBSaveRecipe db = new DBSaveRecipe()) {
            if (!db.check(name)) {
                db.newRecipe(name);
            } else {
                id = db.getLastId(name) + 1;
            }

            db.newVersion(id, this, userId);
        }

        Recipes.suggestTags(name, tags, userId);

        if (mongoId != null) {

            try (final RecipeDraftsStore draftsStore = RecipeDraftsStore.getRecipeDraftStore()) {
                draftsStore.deleteDraft(mongoId, userId);
            } catch (Exception e) {
                logger.error(e, e);
            }
        }

        if (id == 0) {
            Discussion.addNewRecipeEvent(name, userId, comment, id);
            Lifes.addLife(Lifes.CaseType.NEW_RECIPE, userId, name, id);
        } else {
            Discussion.addNewVersionEvent(name, userId, comment, id);
            Lifes.addLife(Lifes.CaseType.NEW_VERSION, userId, name, id);
        }

        return id;
    }

    /**
     * Saves recipes from anonymous users
     */
    public int save() throws SQLException, IOException, ParseException, InvalidRecipeException {
        if (!check()) {
            throw new InvalidRecipeException(name);
        }

        int id = 0;

        try (DBSaveRecipe db = new DBSaveRecipe()) {
            if (!db.check(name)) {
                db.newRecipe(name);
            } else {
                id = db.getLastId(name) + 1;
            }

            db.newVersion(id, this);
        }

        Recipes.suggestTags(name, tags);

        if (id == 0) {
            Discussion.addNewRecipeEvent(name, comment, id);
        } else {
            Discussion.addNewVersionEvent(name, comment, id);
        }

        return id;
    }

    public static class InvalidRecipeException extends Exception {

        public InvalidRecipeException(String recipeName) {
            super(recipeName + "was not valid");
        }
    }


}
