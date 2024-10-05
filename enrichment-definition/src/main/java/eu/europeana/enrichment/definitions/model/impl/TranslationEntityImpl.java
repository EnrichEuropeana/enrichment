package eu.europeana.enrichment.definitions.model.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexes;

@Entity(value = "TranslationEntityImpl")
@Indexes({ @Index(fields = { @Field("storyId"), @Field("itemId"), @Field("type"), @Field("tool") }, options = @IndexOptions(unique = true)),
@Index(fields = { @Field("storyId"), @Field("itemId"), @Field("type"), @Field("tool"), @Field("userId")}, options = @IndexOptions(unique = true)) })
public class TranslationEntityImpl extends BaseTranslationImpl {

    public TranslationEntityImpl(BaseTranslationImpl copy) {
        super(copy);
    }

    public TranslationEntityImpl() {
        super();
    }

    @JsonIgnore
    public boolean isManualTranslation() {
        return getUserId() != null;
    }
}
