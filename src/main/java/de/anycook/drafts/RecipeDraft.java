package de.anycook.drafts;

import de.anycook.recipe.Time;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
@XmlRootElement
public class RecipeDraft {

    private int userId;
    private String id;
    private Long timestamp;
    private String image;
    private String name;
    private String description;
    private Integer persons;
    private String category;
    private Integer skill;
    private Integer calorie;

    private Time time;
    private double percentage;

    private List<IngredientDraft> ingredients;
    private List<StepDraft> steps;
    private List<String> tags;

    // getters
    public int getUserId() {
        return userId;
    }

    public String getId() {
        return id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPersons() {
        return persons;
    }

    public List<IngredientDraft> getIngredients() {
        return ingredients;
    }

    public String getCategory() {
        return category;
    }

    public List<StepDraft> getSteps() {
        return steps;
    }

    public Integer getSkill() {
        return skill;
    }

    public Integer getCalorie() {
        return calorie;
    }

    public List<String> getTags() {
        return tags;
    }

    public Time getTime() {
        return time;
    }

    public double getPercentage() {
        return percentage;
    }


    // setters
    public void setId(final String id) {
        this.id = id;
    }

    public void setUserId(final int userId) {
        this.userId = userId;
    }

    public void setTimestamp(final Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setImage(final String image) {
        this.image = image;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setSteps(final List<StepDraft> steps) {
        this.steps = steps;
    }

    public void setPersons(final Integer persons) {
        this.persons = persons;
    }

    public void setIngredients(final List<IngredientDraft> ingredients) {
        this.ingredients = ingredients;
    }

    public void setCategory(final String category) {
        this.category = category;
    }

    public void setSkill(final Integer skill) {
        this.skill = skill;
    }

    public void setCalorie(final Integer calorie) {
        this.calorie = calorie;
    }

    public void setTags(final List<String> tags) {
        this.tags = tags;
    }

    public void setTime(final Time time) {
        this.time = time;
    }

    public void setPercentage(final double percentage) {
        this.percentage = percentage;
    }
}
