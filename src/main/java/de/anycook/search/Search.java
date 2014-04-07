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

package de.anycook.search;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import de.anycook.db.lucene.FulltextIndex;
import de.anycook.db.mysql.DBGetRecipe;
import de.anycook.db.mysql.DBSearch;
import de.anycook.recipe.Recipes;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;


public class Search {

    public static SearchResult search(Query query, int loginId) throws SQLException, IOException {
        Set<String> recipes = new LinkedHashSet<>();

        try (DBSearch dbsearch = new DBSearch()) {
            if (query.hasIngredients()) {
                for (String ingredient : query.getIngredients()) {
                    Set<String> tempRecipes = dbsearch.getRecipesByIngredient(ingredient);
                    if (recipes.isEmpty())
                        recipes = tempRecipes;
                    else
                        recipes.retainAll(tempRecipes);
                }
            }
            if (query.hasSkill()) {
                if (recipes.isEmpty())
                    recipes = dbsearch.getRecipesBySkill(query.getSkill());
                else
                    recipes.retainAll(dbsearch.getRecipesBySkill(query.getSkill()));
            }
            if (query.hasCalorie()) {
                if (recipes.isEmpty())
                    recipes = dbsearch.getRecipesByCalories(query.getCalorie());
                else
                    recipes.retainAll(dbsearch.getRecipesByCalories(query.getCalorie()));
            }

            if (query.hasTime()) {
                if (recipes.isEmpty())
                    recipes = dbsearch.getRecipesByTime(query.getTime());
                else
                    recipes.retainAll(dbsearch.getRecipesByTime(query.getTime()));
            }
            if (query.hasTags()) {
                if (recipes.isEmpty())
                    recipes = dbsearch.getRecipesByTags(query.getTags());
                else
                    recipes.retainAll(dbsearch.getRecipesByTags(query.getTags()));
            }
            if (query.hasCategory()) {
                if (query.getCategory().equals("alle Kategorien")) {
                    if (recipes.isEmpty())
                        recipes = dbsearch.getAllRecipes();
                    else
                        recipes.retainAll(dbsearch.getAllRecipes());
                } else {
                    if (recipes.isEmpty())
                        recipes = dbsearch.getRecipesByCategory(query.getCategory());
                    else
                        recipes.retainAll(dbsearch.getRecipesByCategory(query.getCategory()));
                }

            }
            if(query.hasTerms()){
                FulltextIndex index = FulltextIndex.init();
                if(recipes.isEmpty())
                    recipes.addAll(index.search(query.getTerms()));
                else{
                    recipes.retainAll(index.search(query.getTerms()));
                }
            }
            if (query.hasUser()) {
                try (DBGetRecipe dbGetRecipe = new DBGetRecipe()) {
                    if (recipes.isEmpty())
                        recipes.addAll(dbGetRecipe.getRecipeNamesForUserId(query.getUser()));
                    else
                        recipes.retainAll(new LinkedHashSet<>(dbGetRecipe.getRecipesFromUser(query.getUser())));
                }
            }

            if (query.hasExcludedIngredients()) {
                if (recipes.isEmpty())
                    recipes.addAll(dbsearch.getAllRecipes());

                for (String ingredient : query.getExcludedIngredients())
                    recipes.removeAll(dbsearch.getRecipesByIngredient(ingredient));
            }
        }

        List<String> results = new LinkedList<>(recipes)
                .subList(query.getStart(), Math.min(recipes.size(), query.getStart() + query.getNum()));
        int resultLength = recipes.size();



        return new SearchResult(resultLength, Recipes.load(results, loginId));


    }

    public static Set<String> getChildren(String ingredientName) throws SQLException {
        try(DBSearch dbSearch = new DBSearch()) {
            Set<String> children = new HashSet<>();
            Set<String> temp = dbSearch.getChildIngredients(ingredientName);
            children.addAll(temp);
            children.addAll(getChildren(temp));
            return children;
        }
    }

    public static Set<String> getChildren(Set<String> ingredientName) throws SQLException {
        try (DBSearch dbSearch = new DBSearch()) {
            Set<String> children = new HashSet<>();
            for (String ingredient : ingredientName) {
                Set<String> temp = dbSearch.getChildIngredients(ingredient);
                children.addAll(temp);
                children.addAll(getChildren(temp));
            }
            return children;
        }
    }

//	public static Map<RecipeFields, String> getRecipeDataforSmallView(String gericht){
//		DBGetRecipe dbgericht = new DBGetRecipe();
//		Map<RecipeFields, String> gerichtdata = new HashMap<RecipeFields, String>();
//		
//		gerichtdata.putAll(dbgericht.getGerichtData(gericht));
//		gerichtdata.put(RecipeFields.SCHMECKT,	dbgericht.getTasteNum(gericht)+"");
//		dbgericht.close();
//		return gerichtdata;
//	}

    public static Multimap<String, String> validateSearch(String query) throws SQLException {
        Multimap<String, String> results = LinkedListMultimap.create(1);

        /*DBCategory dbCategory = new DBCategory();
        DBIngredient dbIngredient = new DBIngredient();
        DBTag dbTag = new DBTag();
        DBUser dbUser = new DBUser();

        DBRecipe dbRecipe = new DBRecipe();
        try {
           return ImmutableMap.of("gerichte", dbRecipe.getName(query));
        } catch (DBRecipe.RecipeNotFoundException e) {

        } finally {
            dbRecipe.close();
        }

        if(Recipe.checkGericht(query))


            DBRecipe dbRecipe = new DBRecipe();
            if(dbRecipe.check(query))
                results.put("gerichte",dbCheck.getGericht(query));
            else if(dbCheck.checkKategorie(query))
                results.put("kategorien", dbCheck.getKategorie(query));

            else if(dbCheck.checkZutat(query))
                results.put("zutaten", dbCheck.getZutat(query));

            else if(dbCheck.exists(query))
                results.put("tags",dbCheck.getTag(query));
            else if(dbCheck.checkUsername(query))
                results.put("user", query);
        }



		dbCheck.close();  */
        return results;
    }


}
