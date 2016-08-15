package de.anycook.db.drafts.mongo.codecs;

import de.anycook.recipe.Time;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class TimeCodec implements Codec<Time> {

    @Override
    public Time decode(BsonReader reader, DecoderContext decoderContext) {
        final Time time = new Time();

        reader.readStartDocument();
        time.setStd(reader.readInt32("std"));
        time.setMin(reader.readInt32("min"));

        reader.readEndDocument();
        return time;
    }

    @Override
    public void encode(BsonWriter writer, Time value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeInt32("std", value.getStd());
        writer.writeInt32("min", value.getMin());
        writer.writeEndDocument();
    }

    @Override
    public Class<Time> getEncoderClass() {
        return Time.class;
    }
}
