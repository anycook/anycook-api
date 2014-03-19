package de.anycook.recipe;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.anycook.conf.Configuration;
import de.anycook.db.mongo.RecipeDrafts;
import de.anycook.db.mysql.DBGetRecipe;
import de.anycook.db.mysql.DBRecipe;
import de.anycook.db.mysql.DBSaveRecipe;
import de.anycook.db.mysql.DBTag;
import de.anycook.recipe.ingredient.Ingredient;
import de.anycook.recipe.step.Step;
import de.anycook.recipe.tag.Tag;
import de.anycook.utils.enumerations.ImageType;
import org.apache.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public class Recipes {
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

            String image = getImageName(recipeName);
            drafts.update("image", image, userId, draft_id);

            return draft_id;
        }
    }

    public static List<Recipe> load(List<String> results) {
        List<Recipe> recipes = new ArrayList<>();

        for(String recipe : results) {
            try {
                recipes.add(Recipe.init(recipe));
            } catch (SQLException | DBRecipe.RecipeNotFoundException e) {
                Logger.getLogger(Recipe.class).error(e, e);
            }
        }
        return recipes;
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
    public static List<Tag> getPopularTags() throws SQLException {
        try (DBRecipe dbRecipe = new DBRecipe()) {
            return dbRecipe.getPopularTags(50);
        }
    }

    public static List<Tag> getPopularTags(String recipe) throws SQLException {
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
            return Recipe.init(recipeOfTheDay);
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

    public static void setActiveId(String recipeName, int activeId) throws SQLException {
        try(DBSaveRecipe dbSaveRecipe = new DBSaveRecipe()){
            dbSaveRecipe.setActiveId(recipeName, activeId);
        }
    }
}
