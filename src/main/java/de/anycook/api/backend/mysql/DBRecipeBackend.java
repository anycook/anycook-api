package de.anycook.api.backend.mysql;

import de.anycook.api.backend.recipe.Recipe;
import de.anycook.api.backend.recipe.Version;
import de.anycook.db.mysql.DBHandler;
import de.anycook.db.mysql.DBRecipe;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class DBRecipeBackend extends DBHandler {

    public DBRecipeBackend() throws SQLException {
        super();
    }

    public List<Recipe> getAllRecipes() throws SQLException {

        CallableStatement call = connection.prepareCall("{call backend_get_all_recipes()}");

        try(ResultSet data = call.executeQuery()){
            List<Recipe> recipes = new LinkedList<>();
            while(data.next()){
                String name = data.getString("name");
                Date added = data.getDate("gerichte.eingefuegt");
                int active_id = data.getInt("active_id");
                int viewed = data.getInt("viewed");
                int schmeckt = data.getInt("schmeckt");
                boolean admin_viewed = data.getBoolean("adminviewed");
                int numVersions = data.getInt("num_versions");

                Recipe recipe = new Recipe(name, active_id, added, viewed, schmeckt, admin_viewed, numVersions);
                recipes.add(recipe);
            }
            return recipes;
        }
	}

    public Recipe getRecipe(String recipeName) throws SQLException, DBRecipe.RecipeNotFoundException {
        CallableStatement call = connection.prepareCall("{call backend_get_recipe(?)}");
        call.setString(1, recipeName);

        ResultSet data = call.executeQuery();
        if(data.next()){
            String name = data.getString("name");
            Date added = data.getDate("gerichte.eingefuegt");
            int active_id = data.getInt("active_id");
            int viewed = data.getInt("viewed");
            int schmeckt = data.getInt("schmeckt");
            boolean admin_viewed = data.getBoolean("adminviewed");
            int numVersions = data.getInt("num_versions");

            return new Recipe(name, active_id, added, viewed, schmeckt, admin_viewed, numVersions);
        }

        throw new DBRecipe.RecipeNotFoundException(recipeName);
    }

	public List<Version> getVersions(String recipeName) throws SQLException {
        CallableStatement call = connection.prepareCall("{call backend_get_version(?)}");
        call.setString(1, recipeName);

        List<Version> versions = new LinkedList<>();
        try(ResultSet data = call.executeQuery()){
            while(data.next()){
                int id = data.getInt("id");
                Date added = data.getDate("eingefuegt");
                int steps = data.getInt("steps.count");
                int ingredients = data.getInt("ingredients.count");
                int userId = data.getInt("users_id");
                boolean admin_viewed = data.getBoolean("viewed_by_admin");
                String image = data.getString("imagename");
                String description = data.getString("beschreibung");

                Version version = new Version(id, description, steps, ingredients, added, userId, admin_viewed, image);
                versions.add(version);
            }
        }
		return versions;
	}
	
	
	//activate/deactivate
	public void activateVersion(String recipeName, int versionId) throws SQLException {
        PreparedStatement pStatement = connection.prepareStatement("UPDATE gerichte SET active_id = ? WHERE name = ?");

        pStatement.setInt(1, versionId);
        pStatement.setString(2, recipeName);
        if(pStatement.executeUpdate() == 0)
            throw new SQLException(recipeName+" doesn't affect any rows");
	}
	
	public int getUserIdFromVersion(String recipeName, int versionId) throws SQLException, DBRecipe.RecipeNotFoundException {
        PreparedStatement pStatement =
                connection.prepareStatement("SELECT users_id FROM versions WHERE gerichte_name = ? AND id =?");

        pStatement.setString(1, recipeName);
        pStatement.setInt(2, versionId);
        ResultSet data = pStatement.executeQuery();
        if(data.next()){
            return data.getInt(1);
        }

        throw new DBRecipe.RecipeNotFoundException(recipeName, versionId);
	}

    /**
     * TODO Currently updates only active_id
     * @param recipeName
     * @param recipe
     */
    public void updateRecipe(String recipeName, Recipe recipe) throws SQLException, DBRecipe.RecipeNotFoundException {
        CallableStatement call = connection.prepareCall("{call backend_update_recipe(?, ?)}");
        call.setString(1, recipeName);
        call.setInt(2, recipe.active_id);
        call.executeUpdate();
    }

    public void deleteRecipe(String recipeName) throws SQLException {
        CallableStatement call = connection.prepareCall("{call backend_delete_recipe(?)}");
        call.setString(1, recipeName);
        call.executeUpdate();
    }
}
