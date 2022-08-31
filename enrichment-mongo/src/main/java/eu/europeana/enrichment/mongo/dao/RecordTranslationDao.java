package eu.europeana.enrichment.mongo.dao;

import eu.europeana.enrichment.model.RecordTranslation;

public interface RecordTranslationDao {

    long deleteByRecordId(String recordId);

    void deleteTranslationEntity(RecordTranslation recordTranslation);

    RecordTranslation saveTranslationEntity(RecordTranslation recordTranslation);

    RecordTranslation findByRecordId(String recordId);

}
