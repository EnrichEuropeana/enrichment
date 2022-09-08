package eu.europeana.enrichment.web.service.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.cloud.translate.Translation;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.RecordTranslation;
import eu.europeana.enrichment.model.impl.EuropeanaRecordTranslationImpl;
import eu.europeana.enrichment.mongo.dao.RecordTranslationDao;
import eu.europeana.enrichment.translation.service.impl.TranslationGoogleServiceImpl;
import eu.europeana.enrichment.web.exception.FunctionalRuntimeException;
import eu.europeana.enrichment.web.service.RecordTranslationService;

@Service(EnrichmentConstants.BEAN_RECORD_TRANSLATION_SERVICE)
public class RecordTranslationServiceImpl implements RecordTranslationService {

	/*
	 * Loading all translation services
	 */

	@Autowired(required=false)
	TranslationGoogleServiceImpl googleTranslationService;

	@Autowired
        RecordTranslationDao recordTranslationDao;
        
	Logger logger = LogManager.getLogger(getClass());
	
	/*
	 * Defining the available tools for translation
	 */
//	private static final String googleToolName = "Google";
	private String targetLanguage = "en";
	
        
	public RecordTranslationServiceImpl() {
	    super();
	}
	
	@Override
	public RecordTranslation translate(RecordTranslation record) throws Exception{
		
	    RecordTranslation storedTranslation = recordTranslationDao.findByRecordId(record.getRecordId());
	    if(storedTranslation != null && storedTranslation.isTranslationComplete()) {
	        return storedTranslation;
	    } else {
                return translateAndStore(record, storedTranslation);
            } 
	}


    private RecordTranslation translateAndStore(RecordTranslation record, RecordTranslation dbRecord) {
        if(dbRecord == null) {
            dbRecord = new EuropeanaRecordTranslationImpl();
        } 
        resetTranslationObj(dbRecord, record.getRecordId(), record.getDescription(), record.getIdentifier());
        
        computeTranslations(dbRecord);
        return recordTranslationDao.saveTranslationEntity(dbRecord);
        
    }


    private void computeTranslations(RecordTranslation recordTranslation) {
        if(recordTranslation.getDescription() == null || recordTranslation.getDescription().isEmpty()) {
            throw new FunctionalRuntimeException("Missing descriptions for record:"  + recordTranslation.getRecordId());
        } 
        
        List<Translation> translations = googleTranslationService.translateList(recordTranslation.getDescription(), null, targetLanguage);
        if(hasFailedTranslation(translations, recordTranslation.getDescription().size())) {
            throw new FunctionalRuntimeException("Translation failed for record, see error logs:"  + recordTranslation.getRecordId());
        }
        
        for (Translation translation : translations) {
            if(isFailedTranslation(translation)) {
                throw new FunctionalRuntimeException("Translation failed for record, see error logs:"  + recordTranslation.getRecordId());
            }
            recordTranslation.addTranslation(translation.getSourceLanguage(), translation.getTranslatedText());
        }
        recordTranslation.setTranslationStatus(RecordTranslation.TRANSLATION_STATUS_COMPLETE);
    }


    private boolean isFailedTranslation(Translation translation) {
        return translation == null || StringUtils.isEmpty(translation.getSourceLanguage()) || StringUtils.isEmpty(translation.getTranslatedText());
    }

    private boolean hasFailedTranslation(List<Translation> translations, int expectedTranslations) {
        return translations == null || (translations.size() != expectedTranslations);
    }


    private void resetTranslationObj(RecordTranslation storedTranslation, String recordId, List<String> descriptions, String identifier) {
        //reset descriptions and translations
        storedTranslation.setRecordId(recordId);
        storedTranslation.setDescription(descriptions);
        storedTranslation.setIdentifier(identifier);
        storedTranslation.setTool(identifier);
        if(storedTranslation.getTranslation() != null) {
            storedTranslation.getTranslation().clear();
        }
        if(storedTranslation.getLanguage() != null) {
            storedTranslation.getLanguage().clear();
        }
        storedTranslation.setTranslationStatus(null);
    }
	
		
}
