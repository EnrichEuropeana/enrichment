package eu.europeana.enrichment.web.service;

public interface EnrichmentTranslationService {

	/*
	 * This method uses a transcribed text and translates it into
	 * English with a specific translation tool.
	 * 
	 * @param text 						is the original transcribed text
	 * 									which should be translated
	 * @param sourceLanguage			defines the language of the transcribed text
	 * @return 							the English translated text of the 
	 * 									original text. In the case of eTranslation
	 * 									a ID will be returned
	 * @throws
	 */
	public String translate(String text, String sourceLanguage, String tool);  
	
}
