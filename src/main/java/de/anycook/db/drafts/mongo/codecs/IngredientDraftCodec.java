package de.anycook.db.drafts.mongo.codecs;

import de.anycook.drafts.IngredientDraft;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class IngredientDraftCodec implements Codec<IngredientDraft> {

    @Override
    public IngredientDraft decode(BsonReader reader, DecoderContext decoderContext) {
        final IngredientDraft ingredientDraft = new IngredientDraft();

        reader.readStartDocument();

        ingredientDraft.setName(reader.readName());
        ingredientDraft.setAmount(reader.readString());

        reader.readEndDocument();

        return ingredientDraft;
    }

    @Override
    public void encode(BsonWriter writer, IngredientDraft value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString(value.getName(), value.getAmount());
        writer.writeEndDocument();
    }

    @Override
    public Class<IngredientDraft> getEncoderClass() {
        return IngredientDraft.class;
    }
}
