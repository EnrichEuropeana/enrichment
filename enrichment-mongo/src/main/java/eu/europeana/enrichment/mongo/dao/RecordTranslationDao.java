package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import eu.europeana.enrichment.model.RecordTranslation;

public interface RecordTranslationDao {

    <T extends RecordTranslation> long deleteByRecordId(String recordId, Class<T> objClass);

    RecordTranslation saveTranslationEntity(RecordTranslation recordTranslation);

    <T extends RecordTranslation> RecordTranslation findByRecordId(String recordId, Class<T> objClass);
    
    <T extends RecordTranslation> List<T> getAllTranslationRecords(Class<T> objClass);

}
