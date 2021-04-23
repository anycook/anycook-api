package de.anycook.drafts;

import java.util.List;

import de.anycook.recipe.ingredient.Ingredients;
import de.anycook.recipe.step.Step;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public class StepDraft implements Comparable<StepDraft>{
    private Integer id;
    private String text;
    private List<IngredientDraft> ingredients;

    public StepDraft() {}

    public StepDraft(Step step) {
        this.id = step.getId();
        this.text = step.getText();
        this.ingredients = Ingredients.convertToDrafts(step.getIngredients());
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public List<IngredientDraft> getIngredients() {
        return ingredients;
    }
    public void setIngredients(List<IngredientDraft> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public int compareTo(StepDraft anotherStepDraft) {
        return id.compareTo(anotherStepDraft.id);
    }
}
