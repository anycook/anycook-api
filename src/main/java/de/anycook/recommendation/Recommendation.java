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

package de.anycook.recommendation;

import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import de.anycook.db.mysql.DBRecipe;
import de.anycook.db.mysql.DBRecommend;
import de.anycook.recipe.Recipe;
import de.anycook.utils.comparators.InvertedComparator;
import de.anycook.utils.comparators.StandardComparator;

import java.sql.SQLException;
import java.util.*;


public class Recommendation {

    public static List<Recipe> recommend(int userId) throws SQLException {
        try(DBRecommend dbrecommend = new DBRecommend()){
            Map<String, Integer> tasteTags = dbrecommend.getTastyTags(userId);
            Map<String, Collection<String>> recipes = dbrecommend.getRecipesByTags(userId);
            //logger.debug("tagRecipes: "+tagRecipes);

            // cos(a,b) = a*b/|a|*|b|
            SortedSetMultimap<Double, String> recommendMap =
                    TreeMultimap.create(new InvertedComparator<Double>(), new StandardComparator<String>());
            double schmecktAmount = 0;
            for (String tag : tasteTags.keySet())
                schmecktAmount += Math.pow(tasteTags.get(tag), 2);
            schmecktAmount = Math.sqrt(schmecktAmount);

            for (String tagRecipe : recipes.keySet()) {
                double enumerator = 0;
                Collection<String> tags = recipes.get(tagRecipe);
                for (String tag : tags) {
                    if (!tasteTags.containsKey(tag)) continue;
                    enumerator += tasteTags.get(tag);
                }

                double denominator = schmecktAmount * Math.sqrt(tags.size());
                double result = enumerator / denominator;
                recommendMap.put(result, tagRecipe);
            }

            List<Recipe> finalRecipes = new ArrayList<>();
            for(String name : recommendMap.values()){
                try {
                    finalRecipes.add(Recipe.init(name));
                } catch (DBRecipe.RecipeNotFoundException e) {
                    //will never happen
                }
            }

            return finalRecipes;
        }
    }

    public static List<Recipe> recommend(int userId, int num) throws SQLException {
        return recommend(userId).subList(0, num);
    }
}
