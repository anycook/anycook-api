package de.anycook.status;

import de.anycook.db.mysql.DBHandler;
import de.anycook.db.mysql.DBRecipe;
import de.anycook.recipe.Recipe;
import de.anycook.recipe.ingredient.Ingredient;
import de.anycook.recipe.tag.Tag;
import de.anycook.user.User;

import java.sql.SQLException;
import java.util.Map;

public class Status {
    private final Recipe dailyDish;
    private final int tags;
    private final int ingredients;
    private final int recipes;
    private final int users;
    private final Map<String, Integer> connectionStatus;

    public Status() throws SQLException, DBRecipe.RecipeNotFoundException {
        dailyDish = Recipe.getRecipeOfTheDay();
        tags = Tag.getTotal();
        ingredients = Ingredient.getTotal();
        recipes = Recipe.getTotal();
        users = User.getTotal();
        connectionStatus = DBHandler.getConnectionsStatus();
    }


    public String getDailyDish() {
        return dailyDish.getName();
    }

    public int getTags() {
        return tags;
    }

    public int getIngredients() {
        return ingredients;
    }

    public int getRecipes() {
        return recipes;
    }

    public int getUsers() {
        return users;
    }

    public Map<String, Integer> getConnectionStatus() {
        return connectionStatus;
    }
}
