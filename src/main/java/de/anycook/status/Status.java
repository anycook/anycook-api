package de.anycook.status;

import de.anycook.db.mysql.DBHandler;
import de.anycook.db.mysql.DBRecipe;
import de.anycook.recipe.Recipes;
import de.anycook.recipe.ingredient.Ingredients;
import de.anycook.recipe.tag.Tag;
import de.anycook.user.User;

import java.sql.SQLException;

public class Status {
    private String dailyDish;
    private int tags;
    private int ingredients;
    private int recipes;
    private int users;
    private DBHandler.ConnectionStatus connectionStatus;

    public Status() throws SQLException, DBRecipe.RecipeNotFoundException {
        dailyDish = Recipes.getRecipeOfTheDay().getName();
        tags = Tag.getTotal();
        ingredients = Ingredients.getTotal();
        recipes = Recipes.getTotal();
        users = User.getTotal();
        connectionStatus = DBHandler.getConnectionsStatus();
    }


    public String getDailyDish() {
        return dailyDish;
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

    public void setDailyDish(String dailyDish) {
        this.dailyDish = dailyDish;
    }

    public void setTags(int tags) {
        this.tags = tags;
    }

    public void setIngredients(int ingredients) {
        this.ingredients = ingredients;
    }

    public void setRecipes(int recipes) {
        this.recipes = recipes;
    }

    public void setUsers(int users) {
        this.users = users;
    }

    public void setConnectionStatus(DBHandler.ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public DBHandler.ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }
}
