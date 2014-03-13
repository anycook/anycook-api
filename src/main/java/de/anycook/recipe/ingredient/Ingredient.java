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

package de.anycook.recipe.ingredient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.anycook.db.mysql.DBGetRecipe;
import de.anycook.db.mysql.DBIngredient;
import de.anycook.db.mysql.DBRecipe;
import de.anycook.db.mysql.DBSearch;
import de.anycook.search.Search;
import org.apache.log4j.Logger;
import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.ext.GermanStemmer;

import java.sql.SQLException;
import java.util.*;

public class Ingredient{
    static final long serialVersionUID = 41L;

    public String name = null;
    public String singular = null;
    public String menge = null;
    public Integer recipecounter = null;
    public List<String> recipes = null;


    public Ingredient() {
    }

    public Ingredient(String name) {
        this(name, null);

    }

    public Ingredient(String name, String menge) {
        this(name, menge, -1);
    }

    public Ingredient(String name, String singular, int gerichte) {
        this(name, singular, null, gerichte);
    }

    public Ingredient(String name, String singular, List<String> gerichte) {
        this.name = name;
        this.singular = singular;
        this.recipes = gerichte;
        this.recipecounter = gerichte.size();

    }

    public Ingredient(String name, String singular, String menge) {
        this(name, singular, menge, -1);
    }

    public Ingredient(String name, String singular, String menge, int recipecounter) {
        this.name = name;
        this.singular = singular;
        this.menge = menge;
        this.recipecounter = recipecounter;
    }


    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Ingredient && name.equals(((Ingredient) obj).name);
    }

    public static Ingredient init(String name) throws SQLException, DBIngredient.IngredientNotFoundException {
        try (DBIngredient db = new DBIngredient()) {
            return db.getIngredient(name);
        }
    }

    public static Ingredient init(String name, String amount) throws SQLException,
            DBIngredient.IngredientNotFoundException {
        try (DBIngredient db = new DBIngredient()) {
            return db.get(name, amount);
        }
    }

    public static List<Ingredient> loadByRecipe(String recipeName) throws SQLException {
        try (DBIngredient db = new DBIngredient()) {
            return db.getRecipeIngredients(recipeName);
        }
    }

    public static List<Ingredient> loadByRecipe(String recipeName, int versionId) throws SQLException {
        try (DBIngredient db = new DBIngredient()) {
            return db.getRecipeIngredients(recipeName, versionId);
        }
    }


    public List<Ingredient> getChildren() throws SQLException {
        try (DBIngredient db = new DBIngredient()) {
            return db.getIngredientsByParent(name);
        }
    }

    @JsonIgnore
    public List<String> getRecipes() throws SQLException {
        try (DBSearch db = new DBSearch()) {
            return new LinkedList<>(db.getRecipesByIngredient(name));
        }
    }


    public static List<Ingredient> loadParents() throws SQLException {
        try (DBIngredient db = new DBIngredient()) {
            return db.getParent();
        }
    }

    public static List<Ingredient> loadParentswithData() throws SQLException {
        List<Ingredient> parents = new LinkedList<>();
        for (Ingredient in : loadParents())
            try {
                parents.add(Ingredient.init(in.name));
            } catch (DBIngredient.IngredientNotFoundException e) {
                //nope
            }
        return parents;
    }

    public static List<Ingredient> getAll() throws SQLException {
        try (DBIngredient db = new DBIngredient()) {
            return db.getAllIngredients();
        }
    }

    @JsonIgnore
    public List<String> getChildRecipes() throws SQLException {
        Set<String> children = Search.getChildren(name);
        children.add(name);

        try (DBSearch db = new DBSearch()) {
            return new LinkedList<>(db.getRecipesByIngredients(children));
        }
    }

    public static int getTotal() throws SQLException {
        DBGetRecipe db = new DBGetRecipe();
        int total = db.getTotalIngredients();
        db.close();
        return total;
    }

	/*public static Ingredient initWithJSON(JSONObject ingredientJSON) {
        String name = (String) ingredientJSON.get("name");
		String menge = (String) ingredientJSON.get("menge");
		return new Ingredient(name, null, menge);
	}*/

    private static Set<Ingredient> searchNGram(List<String> terms, int n, DBRecipe dbRecipe) throws SQLException {
        SnowballProgram stemmer = new GermanStemmer();
        Set<Ingredient> ingredients = new LinkedHashSet<>();

        StringBuffer sb;
        List<Integer> indexToDelete = new LinkedList<>();
        for (int i = 0; i <= terms.size() - n; ++i) {
            sb = new StringBuffer();
            try {
                sb.append(terms.get(i));
            } catch (IndexOutOfBoundsException e) {
                Logger.getLogger(Ingredient.class).error("terms: " + terms + " n:" + n + " i:" + i, e);
            }
            int j;
            for (j = i + 1; j < i + n; ++j)
                sb.append(' ').append(terms.get(j));
            String nGram = sb.toString();
            stemmer.setCurrent(nGram);
            stemmer.stem();

            String stem = stemmer.getCurrent();

            try {
                Ingredient ingredient = dbRecipe.getIngredientForStem(stem);
                ingredients.add(ingredient);
                for (int k = i; k < j; ++k)
                    indexToDelete.add(k);
            } catch (DBIngredient.IngredientNotFoundException e) {
                //nothing to do
            }

        }

        List<String> restTerms = new ArrayList<>();
        for (int i = 0; i < terms.size(); ++i) {
            if (!indexToDelete.contains(i))
                restTerms.add(terms.get(i));
        }

        if (restTerms.size() > 0 && n > 1) {
            ingredients.addAll(searchNGram(restTerms, Math.min(--n, restTerms.size() - 1), dbRecipe));
        }

        return ingredients;
    }

    public static Set<Ingredient> searchNGram(String q, int n) throws SQLException {
        StringTokenizer tokenizer = new StringTokenizer(q.toLowerCase(), " ,.;/-!?+(){}*^[]");
        List<String> terms = new ArrayList<>();
        while (tokenizer.hasMoreElements()) {
            terms.add(tokenizer.nextToken());
        }
        if (terms.size() == 0) return new HashSet<>();
        try(DBRecipe dbRecipe = new DBRecipe()){
            return searchNGram(terms, Math.min(n, terms.size()), dbRecipe);
        }
    }
}
