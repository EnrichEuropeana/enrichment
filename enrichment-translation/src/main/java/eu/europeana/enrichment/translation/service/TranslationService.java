package eu.europeana.enrichment.translation.service;

import eu.europeana.enrichment.translation.exception.TranslationException;


public interface TranslationService {
	
	
	/**
	 * This method translate the original transcribed text into English
	 * 
	 * @param text 						original transcribed text
	 * @param sourceLanguage 			is the language of the transcribed text
	 * @return 							the English translation of the
	 * 									original transcribed text
	 * @throws TranslationException	
	 */
	public String translateText(String text, String sourceLanguage, String targetLanguage) throws TranslationException;
}
