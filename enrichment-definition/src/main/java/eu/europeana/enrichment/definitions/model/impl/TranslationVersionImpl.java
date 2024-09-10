package eu.europeana.enrichment.definitions.model.impl;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.Indexes;

@Entity(value = "TranslationVersionImpl")
@Indexes(@Index(fields = { @Field("storyId"), @Field("itemId"), @Field("type"),
        @Field("tool") }))
public class TranslationVersionImpl extends BaseTranslationImpl {

    public TranslationVersionImpl(BaseTranslationImpl copy) {
        super(copy);
    }

    public TranslationVersionImpl() {
        super();
    }

}
