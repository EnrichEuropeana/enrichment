package eu.europeana.enrichment.web.service;

import eu.europeana.enrichment.model.RecordTranslation;

public interface RecordTranslationService {


    public <T extends RecordTranslation> RecordTranslation translate(RecordTranslation record, Class<T> objClass) throws Exception;

    RecordTranslation updateDcLanguage(RecordTranslation record, boolean forceUpdate);

    <T extends RecordTranslation> RecordTranslation getByRecordId(String recordId) throws Exception;
}
