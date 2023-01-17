package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import eu.europeana.enrichment.model.RecordTranslation;
import eu.europeana.enrichment.model.impl.EuropeanaRecordTranslationImpl;

public interface RecordTranslationDao {

    long deleteByRecordId(String recordId);

    void deleteTranslationEntity(RecordTranslation recordTranslation);

    RecordTranslation saveTranslationEntity(RecordTranslation recordTranslation);

    RecordTranslation findByRecordId(String recordId);
    
    List<EuropeanaRecordTranslationImpl> getAllTranslationRecords();

}
