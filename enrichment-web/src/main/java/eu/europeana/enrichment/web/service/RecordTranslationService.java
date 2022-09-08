package eu.europeana.enrichment.web.service;

import eu.europeana.enrichment.model.RecordTranslation;

public interface RecordTranslationService {


    public RecordTranslation translate(RecordTranslation record) throws Exception;
}
