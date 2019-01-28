package eu.europeana.enrichment.translation.service;

import eu.europeana.enrichment.translation.exception.TranslationException;


public interface TranslationService {
	
	/**
	 * This method identifies named entities.
	 * 
	 * @param text is the transcribed text
	 * @param sourceLanguage is the language of the transcribed text
	 * @return the translated text
	 * @throws TranslationException	
	 */
	public String translateText(String text, String sourceLanguage) throws TranslationException;
}
