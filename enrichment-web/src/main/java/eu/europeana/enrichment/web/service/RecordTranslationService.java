package eu.europeana.enrichment.web.service;

import eu.europeana.enrichment.model.RecordTranslation;

public interface RecordTranslationService {


    public <T extends RecordTranslation> RecordTranslation translate(RecordTranslation record, Class<T> objClass) throws Exception;
}
