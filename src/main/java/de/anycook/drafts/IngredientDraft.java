package de.anycook.drafts;

import de.anycook.recipe.ingredient.Ingredient;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public class IngredientDraft {
    public String name;
    public String amount;

    public IngredientDraft() {

    }

    public IngredientDraft(Ingredient ingredient) {
        this.name = ingredient.getName();
        this.amount = ingredient.getAmount();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
