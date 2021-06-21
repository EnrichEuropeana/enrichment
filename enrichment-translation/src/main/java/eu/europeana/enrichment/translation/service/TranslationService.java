package eu.europeana.enrichment.translation.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.stereotype.Service;

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
	 * @throws InterruptedException 
	 * @throws UnsupportedEncodingException 
	 */
	public String translateText(List<String> textArray, String sourceLanguage, String targetLanguage) throws TranslationException, InterruptedException, UnsupportedEncodingException;

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
	 * @throws UnsupportedEncodingException 
	 */
	void eTranslationResponse(String targetLanguage, String translatedText, String requestId, String externalReference, String body) throws UnsupportedEncodingException;
}
