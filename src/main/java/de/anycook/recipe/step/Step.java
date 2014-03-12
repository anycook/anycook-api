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

package de.anycook.recipe.step;

import de.anycook.db.mysql.DBStep;
import de.anycook.recipe.ingredient.Ingredient;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

public class Step implements Serializable {
    static final long serialVersionUID = 42L;

    private int id = -1;
    private String text = null;
    private List<Ingredient> ingredients = null;

    public Step() {
    }

    public Step(int id, String text, List<Ingredient> ingredients) {
        this.id = id;
        this.text = text;
        this.ingredients = ingredients;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    /*@Override
	public String toJSONString() {		
		return toJSON().toJSONString();
	}*/

	/*public static Step initWithJSON(JSONObject stepJsonObject) {
		int id = Integer.parseInt(stepJsonObject.get("id").toString());
		String text = (String) stepJsonObject.get("text");
		JSONArray ingredientsJSON = (JSONArray) stepJsonObject.get("ingredients");
		List<Ingredient> ingredients = new LinkedList<>();
		for(Object ingredientObject: ingredientsJSON){
			JSONObject ingredientJSON = (JSONObject) ingredientObject;
			ingredients.add(Ingredient.initWithJSON(ingredientJSON));
		}
		return new Step(id, text, ingredients);
	}*/

    public static List<Step> loadRecipeSteps(String recipeName) throws SQLException {
        try (DBStep dbstep = new DBStep()) {
            return dbstep.loadRecipeSteps(recipeName);
        }
    }

    public static List<Step> loadRecipeSteps(String recipeName, int versionId) throws SQLException {
        try (DBStep dbstep = new DBStep()) {
            return dbstep.loadRecipeSteps(recipeName, versionId);
        }
    }

}
