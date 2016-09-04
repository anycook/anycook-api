package de.anycook.recipe;

import de.anycook.conf.Configuration;
import de.anycook.db.drafts.RecipeDraftsStore;
import de.anycook.db.mysql.DBGetRecipe;
import de.anycook.db.mysql.DBRecipe;
import de.anycook.db.mysql.DBSaveRecipe;
import de.anycook.db.mysql.DBTag;
import de.anycook.db.mysql.DBUser;
import de.anycook.drafts.IngredientDraft;
import de.anycook.drafts.RecipeDraft;
import de.anycook.drafts.StepDraft;
import de.anycook.notifications.Notification;
import de.anycook.recipe.ingredient.Ingredients;
import de.anycook.recipe.step.Steps;
import de.anycook.recipe.tag.Tag;
import de.anycook.user.User;
import de.anycook.utils.enumerations.ImageType;
import de.anycook.utils.enumerations.NotificationType;

import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public final class Recipes {

    private Recipes() {

    }

    public static String initDraftWithRecipe(String recipeName, Integer versionId, int userId)
            throws SQLException, DBRecipe.RecipeNotFoundException, IOException {

        Recipe recipe;
        List<StepDraft> steps;
        List<IngredientDraft> ingredients;

        if (versionId == null) {
            recipe = Recipe.init(recipeName);
            steps = Steps.loadStepDrafts(recipeName);
            ingredients = Ingredients.loadIngredientDrafts(recipeName);
        } else {
            recipe = Recipe.init(recipeName, versionId);
            steps = Steps.loadStepDrafts(recipeName, versionId);
            ingredients = Ingredients.loadIngredientDrafts(recipeName, versionId);
        }

        if (recipe == null || steps == null || ingredients == null) {
            return null;
        }

        try (RecipeDraftsStore draftsStore = RecipeDraftsStore.getRecipeDraftStore()) {

            String draftId = draftsStore.newDraft(userId);

            RecipeDraft recipeDraft = recipe.asDraft();
            recipeDraft.setUserId(userId);
            recipeDraft.setSteps(steps);
            recipeDraft.setIngredients(ingredients);
            recipeDraft.setImage(getImageName(recipeName));

            draftsStore.updateDraft(draftId, recipeDraft);

            return draftId;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public static List<Recipe> load(List<String> results, int loginId) {
        List<Recipe> recipes = new ArrayList<>();

        for (String recipe : results) {
            try {
                recipes.add(Recipe.init(recipe, loginId));
            } catch (SQLException | DBRecipe.RecipeNotFoundException e) {
                LogManager.getLogger(Recipe.class).error(e, e);
            }
        }
        return recipes;
    }

    public static int getAuthor(String recipe, int versionId)
            throws SQLException, DBRecipe.RecipeNotFoundException {
        try (DBGetRecipe db = new DBGetRecipe()) {
            return db.getAuthor(recipe, versionId);
        }
    }

    public static List<Recipe> getTastingRecipesForUser(int userId, int loginId)
            throws SQLException {
        try (DBGetRecipe db = new DBGetRecipe()) {
            return db.getTastingRecipes(userId, loginId);
        }
    }

    public static List<String> getRecipeNamesFromUser(int userId) throws SQLException {
        try (DBGetRecipe db = new DBGetRecipe()) {
            return db.getRecipeNamesForUserId(userId);
        }
    }

    public static List<Recipe> getRecipesFromUser(int userId, int loginId) throws SQLException {
        try (DBGetRecipe db = new DBGetRecipe()) {
            return db.getRecipesForUserId(userId, loginId);
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

    public static String getImageName(String recipeName) throws SQLException {
        try (DBGetRecipe dbGetRecipe = new DBGetRecipe()) {
            return dbGetRecipe.getImageName(recipeName);
        }
    }

    public static URI getRecipeImage(String recipeName, ImageType type)
            throws URISyntaxException, SQLException {
        StringBuilder
                imagePath =
                new StringBuilder(Configuration.getInstance().getImageBasePath()).append("recipe/");
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
        try (DBGetRecipe dbGetRecipe = new DBGetRecipe()) {
            return dbGetRecipe.getUsersFromGericht(recipeName);
        }
    }

    public static void suggestTag(String recipeName, String tag, int userId) throws SQLException {
        tag = tag.toLowerCase(Locale.GERMAN);

        try (DBSaveRecipe dbSaveRecipe = new DBSaveRecipe()) {
            if (!dbSaveRecipe.hasTag(recipeName, tag)) {
                try (DBTag dbTag = new DBTag()) {
                    if (!dbTag.exists(tag)) {
                        dbTag.create(tag);
                    }
                }

                dbSaveRecipe.suggestTag(recipeName, tag, userId);
            }
        }
    }

    public static List<Recipe> getAll(int loginId) throws SQLException {
        try (DBGetRecipe dbGetRecipe = new DBGetRecipe()) {
            return dbGetRecipe.getAllRecipes(loginId);
        }
    }

    public static List<Recipe> getAll(int loginId, Date lastModified) throws SQLException {
        try (DBGetRecipe dbGetRecipe = new DBGetRecipe()) {
            return dbGetRecipe.getAllRecipes(loginId, lastModified);
        }
    }

    public static List<Recipe> getAllVersions(String recipeName, int loginId) throws SQLException {
        try (DBGetRecipe dbGetRecipe = new DBGetRecipe()) {
            return dbGetRecipe.getVersions(recipeName, loginId);
        }
    }

    public static void setActiveId(String recipeName, int activeId) throws SQLException {
        try (DBSaveRecipe dbSaveRecipe = new DBSaveRecipe()) {
            dbSaveRecipe.setActiveId(recipeName, activeId);
        }
    }

    public static List<User> getAuthors(String recipeName) throws SQLException {
        try (DBGetRecipe dbGetRecipe = new DBGetRecipe()) {
            return dbGetRecipe.getAuthors(recipeName);
        }
    }

    public static void increaseViewCount(String recipeName) throws SQLException {
        try (DBSaveRecipe dbSaveRecipe = new DBSaveRecipe()) {
            dbSaveRecipe.increaseViewCount(recipeName);
        }
    }

    public static void suggestTags(String name, List<String> tags) throws SQLException {
        suggestTags(name, tags, -1);
    }

    public static void suggestTags(String name, List<String> tags, int userId) throws SQLException {
        if (tags.isEmpty()) {
            return;
        }

        for (String tag : tags) {
            suggestTag(name, tag, userId);
        }

        Map<String, String> data = new HashMap<>(3);
        try {
            data.put("userName", User.getUsername(userId));
        } catch (DBUser.UserNotFoundException e) {
            data.put("userName", "anonymous");
        }
        data.put("recipeName", name);
        data.put("numTags", Integer.toString(tags.size()));
        Notification.sendAdminNotification(NotificationType.ADMIN_SUGGESTED_TAGS, data);
    }

    public static void setLastChange(String recipeName) throws SQLException {
        try (DBSaveRecipe dbSaveRecipe = new DBSaveRecipe()) {
            dbSaveRecipe.setLastChange(recipeName);
        }
    }

    public static Date getLastModified() throws SQLException {
        try (DBGetRecipe dbGetRecipe = new DBGetRecipe()) {
            return dbGetRecipe.getLastModified();
        }
    }


}
