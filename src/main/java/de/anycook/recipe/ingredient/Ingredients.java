package de.anycook.recipe.ingredient;

import de.anycook.db.mysql.DBGetRecipe;
import de.anycook.db.mysql.DBIngredient;
import de.anycook.db.mysql.DBRecipe;
import de.anycook.drafts.IngredientDraft;
import org.apache.logging.log4j.LogManager;
import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.ext.GermanStemmer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public final class Ingredients {

    private Ingredients() {

    }

    public static int getTotal() throws SQLException {
        try (DBGetRecipe db = new DBGetRecipe()) {
            return db.getTotalIngredients();
        }
    }

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
                LogManager.getLogger(Ingredient.class)
                        .error("terms: " + terms + " n:" + n + " i:" + i, e);
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

    public static List<IngredientDraft> convertToDrafts(List<Ingredient> ingredients) {
        List<IngredientDraft> ingredientDrafts = new LinkedList<>();
        ingredients.forEach(ingredient -> ingredientDrafts.add(new IngredientDraft(ingredient)));
        return ingredientDrafts;
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
                parents.add(Ingredient.init(in.getName()));
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

    public static List<IngredientDraft> loadIngredientDrafts(String recipeName) throws SQLException {
        return convertToDrafts(loadByRecipe(recipeName));
    }

    public static List<IngredientDraft> loadIngredientDrafts(String recipeName, int versionId) throws SQLException {
        return convertToDrafts(loadByRecipe(recipeName, versionId));
    }
}
