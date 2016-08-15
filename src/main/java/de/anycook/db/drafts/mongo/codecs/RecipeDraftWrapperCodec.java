package de.anycook.db.drafts.mongo.codecs;

import de.anycook.drafts.RecipeDraft;
import de.anycook.drafts.RecipeDraftWrapper;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.ObjectId;

public class RecipeDraftWrapperCodec implements Codec<RecipeDraftWrapper> {
    private final Codec<RecipeDraft> recipeDraftCodec;

    public RecipeDraftWrapperCodec(final CodecRegistry codecRegistry) {
        this.recipeDraftCodec = codecRegistry.get(RecipeDraft.class);
    }

    @Override
    public RecipeDraftWrapper decode(BsonReader reader, DecoderContext decoderContext) {
        final RecipeDraftWrapper recipeDraftWrapper = new RecipeDraftWrapper();
        reader.readStartDocument();
        recipeDraftWrapper.setId(reader.readObjectId("_id").toHexString());
        reader.readName("value");
        recipeDraftWrapper.setRecipeDraft(recipeDraftCodec.decode(reader, decoderContext));
        reader.readEndDocument();
        return recipeDraftWrapper;
    }

    @Override
    public void encode(BsonWriter writer, RecipeDraftWrapper value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeObjectId("_id", new ObjectId(value.getId()));
        writer.writeName("value");
        encoderContext.encodeWithChildContext(recipeDraftCodec, writer, value.getRecipeDraft());
        writer.writeEndDocument();
    }

    @Override
    public Class<RecipeDraftWrapper> getEncoderClass() {
        return RecipeDraftWrapper.class;
    }
}
