package de.anycook.db.drafts.mongo.codecs;

import de.anycook.drafts.IngredientDraft;
import de.anycook.drafts.RecipeDraft;
import de.anycook.drafts.RecipeDraftWrapper;
import de.anycook.drafts.StepDraft;
import de.anycook.recipe.Time;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class DraftCodecProvider implements CodecProvider {

    @Override
    @SuppressWarnings("unchecked")
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (clazz == RecipeDraftWrapper.class) {
            return (Codec<T>) new RecipeDraftWrapperCodec(registry);
        }

        if (clazz == RecipeDraft.class) {
            return (Codec<T>) new RecipeDraftCodec(registry);
        }

        if (clazz == IngredientDraft.class) {
            return (Codec<T>) new IngredientDraftCodec();
        }

        if (clazz == StepDraft.class) {
            return (Codec<T>) new StepDraftCodec(registry);
        }

        if (clazz == Time.class) {
            return (Codec<T>) new TimeCodec();
        }

        return null;
    }
}
