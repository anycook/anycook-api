package de.anycook.drafts;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import de.anycook.recipe.Time;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;

/**
 * @author Jan Graßegger<jan@anycook.de>
 */
@XmlRootElement
@DynamoDBDocument
@DynamoDBTable(tableName = "anycook_db_drafts")
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


    public RecipeDraft(){}

    public RecipeDraft(DBObject dbObject) {
        read(dbObject);
    }

    @DynamoDBHashKey(attributeName = "userId")
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @DynamoDBRangeKey(attributeName = "id")
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Long timestamp) {
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

    public List<StepDraft> getSteps() {
        return steps;
    }
    public void setSteps(List<StepDraft> steps) {
        this.steps = steps;
    }

    public Integer getPersons() {
        return persons;
    }
    public void setPersons(Integer persons) {
        this.persons = persons;
    }

    public List<IngredientDraft> getIngredients() {
        return ingredients;
    }
    public void setIngredients(List<IngredientDraft> ingredients) {
        this.ingredients = ingredients;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getSkill() {
        return skill;
    }
    public void setSkill(Integer skill) {
        this.skill = skill;
    }

    public Integer getCalorie() {
        return calorie;
    }
    public void setCalorie(Integer calorie) {
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
                    new TypeReference<List<StepDraft>>(){});
        }
        if(dbObject.containsField("persons")) this.persons = (int)dbObject.get("persons");
        if(dbObject.containsField("ingredients")){
            this.ingredients = mapper.convertValue(dbObject.get("ingredients"),
                    new TypeReference<List<IngredientDraft>>(){});
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
        if(image != null) updateObj.put("image", image);
        if(name != null) updateObj.put("name", name);
        if(description != null) updateObj.put("description", description);

        ObjectMapper mapper = new ObjectMapper();
        if(steps != null) {
            List<Map<String, Object>> stepList = mapper.convertValue(steps, new TypeReference<List<Map<String, Object>>>(){});
            updateObj.put("steps", stepList);
        }
        if(persons != null) updateObj.put("persons", persons);
        if(ingredients != null) {
            List<Map<String, Object>> ingredientList = mapper.convertValue(ingredients, new TypeReference<List<Map<String, Object>>>(){});
            updateObj.put("ingredients", ingredientList);
        }
        if(category != null) updateObj.put("category", category);
        if(skill != null) updateObj.put("skill", skill);
        if(calorie != null) updateObj.put("calorie", calorie);
        if(tags != null) updateObj.put("tags", tags);
        if(time != null) {
            Map<String, Integer> timeObject = mapper.convertValue(time, new TypeReference<Map<String, Integer>>(){});
            updateObj.put("time", timeObject);
        }

    }
}
