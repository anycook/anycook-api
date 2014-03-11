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

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import de.anycook.conf.Configuration;
import de.anycook.db.mongo.RecipeDrafts;
import de.anycook.db.mysql.DBGetRecipe;
import de.anycook.db.mysql.DBRecipe;
import de.anycook.db.mysql.DBSaveRecipe;
import de.anycook.db.mysql.DBTag;
import de.anycook.image.Image;
import de.anycook.image.RecipeImage;
import de.anycook.recipe.ingredient.Ingredient;
import de.anycook.recipe.step.Step;
import de.anycook.user.User;
import de.anycook.utils.DateParser;
import de.anycook.utils.enumerations.ImageType;
import org.apache.log4j.Logger;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;


/**
 * Enthaelt die Daten eines aufgerufenen Rezepts
 *
 * @author Jan Grassegger
 */
public class Recipe implements Comparable<Recipe> {

    public static Recipe init(String recipeName) throws SQLException, DBRecipe.RecipeNotFoundException {
        try (DBGetRecipe db = new DBGetRecipe()) {
            return db.get(recipeName);
        }
    }

    public static Recipe init(String recipeName, int versionId) throws SQLException, DBRecipe.RecipeNotFoundException {
        try (DBGetRecipe db = new DBGetRecipe()) {
            return db.getVersionData(recipeName, versionId);
        }
    }

    public static List<Recipe> getAll() throws SQLException {
        try(DBGetRecipe dbGetRecipe = new DBGetRecipe()){
            return dbGetRecipe.getAllRecipes();
        }
    }

    public static List<Recipe> getAllVersions(String recipeName) throws SQLException {
        try(DBGetRecipe dbGetRecipe = new DBGetRecipe()){
            return dbGetRecipe.getVersions(recipeName);
        }
    }


    private String name;
    private int id;
    private int activeId;
    private String category;
    private Image image;
    private String description;
    private Time time;
    private int skill;
    private int calorie;
    private int persons;
    private User author;
    private Date created;
    private int views;

    public Recipe() {
    }

    public Recipe(int id, String name, String description, String image, int person, Date created, String category,
                  int skill, int calorie, Time time, int activeId, int views, User author) {
        this.name = name;
        this.description = description;
        this.image = new RecipeImage(image);
        this.persons = person;
        this.category = category;
        this.skill = skill;
        this.calorie = calorie;
        this.time = time;
        this.id = id;
        this.created = created;
        this.author = author;
        this.activeId = activeId;
        this.views = views;
    }

    @JsonView(Views.ResultRecipeView.class)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonView(Views.ResultRecipeView.class)
    public int getActiveId() {
        return activeId;
    }

    public void setActiveId(int activeId) {
        this.activeId = activeId;
    }

    @JsonView(Views.RecipeView.class)
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @JsonView(Views.ResultRecipeView.class)
    public String getDescription() {
        return description;
    }

    @JsonView(Views.ResultRecipeView.class)
    public Image getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = new RecipeImage(image);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonView(Views.ResultRecipeView.class)
    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    @JsonView(Views.RecipeView.class)
    public int getSkill() {
        return skill;
    }

    public void setSkill(int skill) {
        this.skill = skill;
    }

    @JsonView(Views.RecipeView.class)
    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }

    @JsonView(Views.RecipeView.class)
    public int getPersons() {
        return persons;
    }

    public void setPersons(int persons) {
        this.persons = persons;
    }

    @JsonView(Views.TasteNumView.class)
    public int getTasteNum() {
        try(DBGetRecipe dbGetRecipe = new DBGetRecipe()){
            return dbGetRecipe.getTasteNum(name);
        } catch (SQLException e) {
            Logger.getLogger(getClass()).error(e, e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

    }

    @JsonView(Views.RecipeView.class)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonView(Views.RecipeView.class)
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @JsonView(Views.ResultRecipeView.class)
    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @JsonView(Views.ResultRecipeView.class)
    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public static boolean checkGericht(String recipeName) throws SQLException {
        try (DBRecipe dbRecipe = new DBRecipe()) {
            return dbRecipe.check(recipeName);
        }
    }

    public static Recipe getRecipeOfTheDay() throws SQLException, DBRecipe.RecipeNotFoundException {
        try (DBGetRecipe dbGetRecipe = new DBGetRecipe()) {
            String recipeOfTheDay;

            try {
                recipeOfTheDay = dbGetRecipe.getRecipeOfTheDay();
            } catch (DBRecipe.RecipeNotFoundException e) {
                recipeOfTheDay = dbGetRecipe.createNewRecipeOfTheDay();
            }
            return init(recipeOfTheDay);
        }
    }


    public static Set<Integer> getUsersforGericht(String recipeName) throws SQLException {
        try(DBGetRecipe dbGetRecipe = new DBGetRecipe()){
            return dbGetRecipe.getUsersFromGericht(recipeName);
        }
    }

    public static void suggestTag(String name, String tag) throws SQLException {
        suggestTag(name, tag, -1);
    }

    public static void suggestTag(String recipeName, String tag, int userId) throws SQLException {
        tag = tag.toLowerCase(Locale.GERMAN);

        try (DBSaveRecipe dbSaveRecipe = new DBSaveRecipe()) {
            if (!dbSaveRecipe.hasTag(recipeName, tag)) {
                try (DBTag dbTag = new DBTag()) {
                    if (!dbTag.exists(tag)) dbTag.create(tag);
                }

                dbSaveRecipe.suggestTag(recipeName, tag, userId);
                //			MailHandler mail = new MailHandler();
                //			String username = User.getUsername(user.id);
                //			mail.sendSuggestTagMail(username, recipeName, tag);

            }
        }
    }


    public int getActiveAuthor() throws SQLException, DBRecipe.RecipeNotFoundException {
        return getAuthor(name, id);
    }

    public static int getAuthor(String recipe, int versionId) throws SQLException, DBRecipe.RecipeNotFoundException {
        try (DBGetRecipe db = new DBGetRecipe()) {
            return db.getAuthor(recipe, versionId);
        }
    }

    public static List<Recipe> getTastingRecipesForUser(int userId) throws SQLException {
        try (DBGetRecipe db = new DBGetRecipe()) {
            return db.getTastingRecipes(userId);
        }
    }

    public static List<String> getRecipeNamesFromUser(int userId) throws SQLException {
        try (DBGetRecipe db = new DBGetRecipe()) {
            return db.getRecipeNamesForUserId(userId);
        }
    }

    public static List<Recipe> getRecipesFromUser(int userId) throws SQLException {
        try (DBGetRecipe db = new DBGetRecipe()) {
            return db.getRecipesForUserId(userId);
        }
    }

    /**
     * Gibt eine Map zurueck, die die beliebtesten Tags und eine relative Gewichtung enthaelt
     *
     * @return Map mit den tags und ihrem relativecount
     */
    public static Map<String, Integer> getPopularTags() throws SQLException {
        try (DBRecipe dbRecipe = new DBRecipe()) {
            return dbRecipe.getPopularTags(50);
        }
    }

    public static Map<String, Integer> getPopularTags(String recipe) throws SQLException {
        try (DBRecipe dbRecipe = new DBRecipe()) {
            return dbRecipe.getPopularTagsNotInRecipe(50, recipe);
        }
    }

    public static List<Recipe> getAllActive() throws SQLException {
        try (DBGetRecipe dbGetRecipe = new DBGetRecipe()) {
            return dbGetRecipe.getAllActiveRecipes();
        }
    }

    public static String getImageName(String gerichtname) throws SQLException {
        try (DBGetRecipe dbGetRecipe = new DBGetRecipe()) {
            return dbGetRecipe.getImageName(gerichtname);
        }
    }

    public static URI getRecipeImage(String recipeName, ImageType type) throws URISyntaxException, SQLException {
        StringBuilder imagePath = new StringBuilder(Configuration.getPropertyImageBasePath()).append("recipe/");
        switch (type) {
            case ORIGINAL:
                imagePath.append("original/");
                break;
            case LARGE:
                imagePath.append("big/");
                break;
            default:
                imagePath.append("small/");
        }

        imagePath.append(getImageName(recipeName));
        return new URI(imagePath.toString());
    }

    public static int getTotal() throws SQLException {
        try (DBGetRecipe dbGetRecipe = new DBGetRecipe()) {
            return dbGetRecipe.getTotalRecipes();
        }
    }

    public static String getRandomRecipe() throws SQLException {
        try (DBGetRecipe dbGetRecipe = new DBGetRecipe()) {
            return dbGetRecipe.randomRecipe();
        }
    }

    @Override
    public int compareTo(Recipe o) {
        return name.compareTo(o.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    private Map<String, Object> asMap() {
        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        builder.put("name", name);
        builder.put("description", description);
        builder.put("created", DateParser.dateToString(created));
        builder.put("calorie", calorie);
        builder.put("skill", skill);
        builder.put("time", ImmutableMap.of("std", time.std, "min", time.min));
        builder.put("category", category);
        builder.put("persons", persons);
        builder.put("id", id);
        builder.put("author", author.getId());

        return builder.build();
    }

    public static String initDraftWithRecipe(String recipeName, Integer versionId, int userId)
            throws SQLException, DBRecipe.RecipeNotFoundException {

        Recipe recipe;
        List<Step> steps;
        List<Ingredient> ingredients;


        if (versionId == null) {
            recipe = Recipe.init(recipeName);
            steps = Step.loadRecipeSteps(recipeName);
            ingredients = Ingredient.loadByRecipe(recipeName);
        } else {
            recipe = Recipe.init(recipeName, versionId);
            steps = Step.loadRecipeSteps(recipeName, versionId);
            ingredients = Ingredient.loadByRecipe(recipeName, versionId);
        }

        if (recipe == null || steps == null || ingredients == null) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> stepList = mapper.convertValue(steps, new TypeReference<List<Map<String, Object>>>(){});
        List<Map<String, Object>> ingredientList = mapper.convertValue(ingredients, new TypeReference<List<Map<String, Object>>>(){});

        try (RecipeDrafts drafts = new RecipeDrafts()) {
            String draft_id = drafts.newDraft(userId);

            drafts.update(recipe.asMap(), userId, draft_id);
            drafts.update("steps", stepList, userId, draft_id);
            drafts.update("ingredients", ingredientList, userId, draft_id);

            String image = Recipe.getImageName(recipeName);
            drafts.update("image", image, userId, draft_id);

            return draft_id;
        }
    }

    public static List<Recipe> load(List<String> results) {
        List<Recipe> recipes = new ArrayList<>();

        for(String recipe : results) {
            try {
                recipes.add(init(recipe));
            } catch (SQLException | DBRecipe.RecipeNotFoundException e) {
                Logger.getLogger(Recipe.class).error(e, e);
            }
        }
        return recipes;
    }


}
