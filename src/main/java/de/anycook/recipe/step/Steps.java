package de.anycook.recipe.step;

import de.anycook.db.mysql.DBStep;
import de.anycook.drafts.StepDraft;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
public final class Steps {
    private Steps() {

    }

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

    public static List<StepDraft> loadStepDrafts(String recipeName) throws SQLException {
        List<StepDraft> stepDrafts = new LinkedList<>();

        loadRecipeSteps(recipeName).forEach(step -> stepDrafts.add(new StepDraft(step)));
        return stepDrafts;
    }

    public static List<StepDraft> loadStepDrafts(String recipeName, int versionId) throws SQLException {
        List<StepDraft> stepDrafts = new LinkedList<>();

        loadRecipeSteps(recipeName, versionId).forEach(step -> stepDrafts.add(new StepDraft(step)));
        return stepDrafts;
    }
}
