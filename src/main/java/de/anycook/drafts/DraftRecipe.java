package de.anycook.drafts;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import de.anycook.recipe.Time;
import de.anycook.recipe.ingredient.Ingredient;
import de.anycook.recipe.step.Step;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
@XmlRootElement
public class DraftRecipe{
    private String id;
    private long timestamp;
    private String image;
    private String name;
    private String description;
    private List<Step> steps;
    private int persons;
    private List<Ingredient> ingredients;
    private String category;
    private int skill;
    private int calorie;
    private List<String> tags;
    private Time time;
    private double percentage;

    public DraftRecipe(){}

    public DraftRecipe(DBObject dbObject) {
        read(dbObject);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public int getPersons() {
        return persons;
    }

    public void setPersons(int persons) {
        this.persons = persons;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getSkill() {
        return skill;
    }

    public void setSkill(int skill) {
        this.skill = skill;
    }

    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public void read(DBObject dbObject){
        /*
        private String id;
        private long timestamp;
        private String image;
        private String name;
        private String description;
        private List<Step> steps;
        private int persons;
        private List<Ingredient> ingredients;
        private String category;
        private int skill;
        private int calorie;
        private List<Tag> tags;
        private Time time;
        private int percentage;
        */

        this.id = dbObject.get("_id").toString();
        if(dbObject.containsField("value")){
            dbObject = (DBObject)dbObject.get("value");
        }


        if(dbObject.containsField("timestamp")) this.timestamp = (long)dbObject.get("timestamp");
        if(dbObject.containsField("image")) this.image = (String)dbObject.get("image");
        if(dbObject.containsField("name")) this.name = (String)dbObject.get("name");
        if(dbObject.containsField("description")) this.description = (String)dbObject.get("description");

        ObjectMapper mapper = new ObjectMapper();
        if(dbObject.containsField("steps")) {
            this.steps = mapper.convertValue(dbObject.get("steps"),
                    new TypeReference<List<Step>>(){});
        }
        if(dbObject.containsField("persons")) this.persons = (int)dbObject.get("persons");
        if(dbObject.containsField("ingredients")){
            this.ingredients = mapper.convertValue(dbObject.get("ingredients"),
                    new TypeReference<List<Ingredient>>(){});
        }
        if(dbObject.containsField("category")) this.category = (String)dbObject.get("category");
        if(dbObject.containsField("skill")) this.skill = (int)dbObject.get("skill");
        if(dbObject.containsField("calorie")) this.calorie = (int)dbObject.get("calorie");
        if(dbObject.containsField("tags")){
            this.tags = mapper.convertValue(dbObject.get("tags"),
                    new TypeReference<List<String>>(){});
        }
        if(dbObject.containsField("time")) this.time =  mapper.convertValue(dbObject.get("time"), Time.class);
        if(dbObject.containsField("percentage")) this.percentage = (double)dbObject.get("percentage");
    }

    public void write(DBObject updateObj) {
        /*
        private String id;
        private long timestamp;
        private String image;
        private String name;
        private String description;
        private List<Step> steps;
        private int persons;
        private List<Ingredient> ingredients;
        private String category;
        private int skill;
        private int calorie;
        private List<Tag> tags;
        private Time time;
        private int percentage;
        */
        if(image != null) updateObj.put("image", image);
        if(name != null) updateObj.put("name", name);
        if(description != null) updateObj.put("description", description);

        ObjectMapper mapper = new ObjectMapper();
        if(steps != null) {
            List<Map<String, Object>> stepList = mapper.convertValue(steps, new TypeReference<List<Map<String, Object>>>(){});
            updateObj.put("steps", stepList);
        }
        if(persons > 0) updateObj.put("persons", persons);
        if(ingredients != null) {
            List<Map<String, Object>> ingredientList = mapper.convertValue(ingredients, new TypeReference<List<Map<String, Object>>>(){});
            updateObj.put("ingredients", ingredientList);
        }
        if(category != null) updateObj.put("category", category);
        if(skill > 0) updateObj.put("skill", skill);
        if(calorie > 0) updateObj.put("calorie", calorie);
        if(tags != null) updateObj.put("tags", tags);
        if(time != null) {
            Map<String, Integer> timeObject = mapper.convertValue(time, new TypeReference<Map<String, Integer>>(){});
            updateObj.put("time", timeObject);
        }

    }
}
