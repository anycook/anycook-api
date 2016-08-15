package de.anycook.db.drafts.mongo.codecs;

import de.anycook.drafts.IngredientDraft;
import de.anycook.drafts.RecipeDraft;
import de.anycook.drafts.StepDraft;
import de.anycook.recipe.Time;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class RecipeDraftCodec implements Codec<RecipeDraft> {

    private final Codec<IngredientDraft> ingredientDraftCodec;
    private final Codec<StepDraft> stepDraftCodec;
    private final Codec<Time> timeCodec;

    public RecipeDraftCodec(final CodecRegistry codecRegistry) {
        this.ingredientDraftCodec = codecRegistry.get(IngredientDraft.class);
        this.stepDraftCodec = codecRegistry.get(StepDraft.class);
        this.timeCodec = codecRegistry.get(Time.class);
    }

    @Override
    public RecipeDraft decode(final BsonReader reader, final DecoderContext decoderContext) {
        final RecipeDraft draft = new RecipeDraft();

        reader.readStartDocument();

        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            final String name = reader.readName();
            switch (name) {
                case "userId":
                    draft.setUserId(reader.readInt32());
                    break;
                case "_id":
                    draft.setId(reader.readObjectId().toString());
                    break;
                case "timestamp":
                    draft.setTimestamp(reader.readInt64());
                    break;
                case "description":
                    draft.setDescription(reader.readString());
                    break;
                case "image":
                    draft.setImage(reader.readString());
                    break;
                case "name":
                    draft.setName(reader.readString());
                    break;
                case "persons":
                    draft.setPersons(reader.readInt32());
                    break;
                case "category":
                    draft.setCategory(reader.readString());
                    break;
                case "skill":
                    draft.setSkill(reader.readInt32());
                    break;
                case "calorie":
                    draft.setCalorie(reader.readInt32());
                    break;
                case "percentage":
                    draft.setPercentage(reader.readDouble());
                    break;
                case "time":
                    final Time time = timeCodec.decode(reader, decoderContext);
                    draft.setTime(time);
                    break;
                case "ingredients":
                    final List<IngredientDraft> ingredientDrafts = new ArrayList<>();
                    reader.readStartArray();

                    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                        ingredientDrafts.add(ingredientDraftCodec.decode(reader, decoderContext));
                    }
                    draft.setIngredients(ingredientDrafts);

                    reader.readEndArray();
                    break;
                case "steps":
                    final List<StepDraft> stepDrafts = new ArrayList<>();
                    reader.readStartArray();

                    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                        stepDrafts.add(stepDraftCodec.decode(reader, decoderContext));
                    }
                    draft.setSteps(stepDrafts);
                    reader.readEndArray();
                    break;
                case "tags":
                    final List<String> tags = new ArrayList<>();
                    reader.readStartArray();

                    while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                        tags.add(reader.readString());
                    }
                    draft.setTags(tags);

                    reader.readEndArray();
                    break;
                default:
                    throw new RuntimeException(String.format("Unknown key: %s", name));
            }
        }

        reader.readEndDocument();

        return draft;
    }

    @Override
    public void encode(final BsonWriter writer, final RecipeDraft value,
                       final EncoderContext encoderContext) {
        writer.writeStartDocument();

        writer.writeInt32("userId", value.getUserId());

        final String id = value.getId();
        if (id != null) {
            writer.writeObjectId("_id", new ObjectId(id));
        }

        final Long timestamp = value.getTimestamp();
        if (timestamp != null) {
            writer.writeInt64("timestamp", timestamp);
        }

        final String image = value.getImage();
        if (image != null) {
            writer.writeString("image", image);
        }

        final String name = value.getName();
        if (name != null) {
            writer.writeString("name", name);
        }

        final String description = value.getDescription();
        if (description != null) {
            writer.writeString("description", description);
        }

        final Integer persons = value.getPersons();
        if (persons != null) {
            writer.writeInt32("persons", persons);
        }

        final String category = value.getCategory();
        if (category != null) {
            writer.writeString("category", category);
        }

        final Integer skill = value.getSkill();
        if (skill != null) {
            writer.writeInt32("skill", skill);
        }

        final Integer calorie = value.getCalorie();
        if (calorie != null) {
            writer.writeInt32("calorie", calorie);
        }

        final Time time = value.getTime();
        if (time != null) {
            writer.writeName("time");
            encoderContext.encodeWithChildContext(timeCodec, writer, time);
        }

        final List<IngredientDraft> ingredients = value.getIngredients();
        if (ingredients != null) {
            writer.writeStartArray("ingredients");
            ingredients.forEach(x -> encoderContext.encodeWithChildContext(
                    ingredientDraftCodec, writer, x));
            writer.writeEndArray();
        }


        final List<StepDraft> steps = value.getSteps();
        if (steps != null) {
            writer.writeStartArray("steps");
            steps.forEach(x -> encoderContext.encodeWithChildContext(stepDraftCodec, writer, x));
            writer.writeEndArray();
        }

        final List<String> tags = value.getTags();
        if (tags != null) {
            writer.writeStartArray("tags");
            value.getTags().forEach(writer::writeString);
            writer.writeEndArray();
        }

        writer.writeEndDocument();
    }

    @Override
    public Class<RecipeDraft> getEncoderClass() {
        return RecipeDraft.class;
    }
}
