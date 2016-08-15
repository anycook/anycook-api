package de.anycook.db.drafts.mongo.codecs;

import de.anycook.drafts.IngredientDraft;
import de.anycook.drafts.StepDraft;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

import java.util.ArrayList;
import java.util.List;

public class StepDraftCodec implements Codec<StepDraft> {

    private final Codec<IngredientDraft> ingredientDraftCodec;

    public StepDraftCodec(final CodecRegistry codecRegistry) {
        this.ingredientDraftCodec = codecRegistry.get(IngredientDraft.class);
    }

    @Override
    public StepDraft decode(final BsonReader reader, final DecoderContext decoderContext) {
        final StepDraft stepDraft = new StepDraft();

        reader.readStartDocument();
        stepDraft.setId(reader.readInt32("id"));
        stepDraft.setText(reader.readString("text"));

        reader.readStartArray();
        final List<IngredientDraft> stepIngredients = new ArrayList<>();

        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            stepIngredients.add(ingredientDraftCodec.decode(reader, decoderContext));
        }

        stepDraft.setIngredients(stepIngredients);
        reader.readEndArray();

        reader.readEndDocument();

        return stepDraft;
    }

    @Override
    public void encode(final BsonWriter writer, final StepDraft value,
                       final EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeInt32("id", value.getId());
        writer.writeString("text", value.getText());

        writer.writeStartArray("ingredients");
        value.getIngredients().forEach(x -> encoderContext.encodeWithChildContext(
                ingredientDraftCodec, writer, x));
        writer.writeEndArray();

        writer.writeEndDocument();
    }

    @Override
    public Class<StepDraft> getEncoderClass() {
        return StepDraft.class;
    }
}
