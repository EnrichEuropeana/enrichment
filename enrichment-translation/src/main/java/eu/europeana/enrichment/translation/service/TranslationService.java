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

	/**
	 * Called when eTranslation returns the translated text back to our application.
	 * Based on the "externalReference" which is unique for each translation request
	 * the corresponding translated text is updated which indicates that we got the
	 * translation from the eTranslation service.
	 * 
	 * @param targetLanguage
	 * @param translatedText
	 * @param requestId
	 * @param externalReference
	 */
	void eTranslationResponse(String targetLanguage, String translatedText, String requestId, String externalReference);
}
