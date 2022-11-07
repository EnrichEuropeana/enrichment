package eu.europeana.enrichment.web.service;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.model.RecordTranslation;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.web.model.EnrichmentTranslationRequest;

public interface EnrichmentTranslationService {

	/*
	 * This method uses a transcribed text and translates it into
	 * English with a specific translation tool. 
	 * The transcribed text is retrieved from the StoryEntity that already has to exist in the database.	 * 
	 * 
	 * @param requestParam				contains information about story, story item,
	 * 									text like language, type (description, summary, ..)
	 * 									and defines which translation tool should be used 
	 * @return 							the English translated text of the 
	 * 									original text. In the case of eTranslation
	 * 									a ID will be returned
	 * @throws							HttpException
	 */
	public TranslationEntity translate(EnrichmentTranslationRequest requestParam, boolean process) throws HttpException, Exception;  
	
	/*
	 * This method uploads the translation text to a TranslationEntity.
	 * 
	 * @param requestParam				contains information about story, story item and
	 * 									text which are used to find and upload translation
	 * @return 							Done string will be returned when 
	 * 									the upload is complete
	 * @throws							HttpException
	 */
	public String uploadTranslation(EnrichmentTranslationRequest requestParam, int i) throws HttpException;


}
