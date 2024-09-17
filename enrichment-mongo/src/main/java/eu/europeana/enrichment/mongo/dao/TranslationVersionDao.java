package eu.europeana.enrichment.mongo.dao;

import eu.europeana.enrichment.definitions.model.impl.TranslationVersionImpl;

public interface TranslationVersionDao {

    void deleteTranslationVersion(TranslationVersionImpl version);

    TranslationVersionImpl saveTranslationVersion(TranslationVersionImpl version);

}
