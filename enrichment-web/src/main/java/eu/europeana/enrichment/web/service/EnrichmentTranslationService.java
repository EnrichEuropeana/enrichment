package eu.europeana.enrichment.web.service;

public interface EnrichmentTranslationService {

	public void init();
	
	/*
	 * This method translates a transcribed text
	 * 
	 * @param text 
	 * @param sourceLanguage
	 * @return the translated text
	 * @throws
	 */
	public String translate(String text, String sourceLanguage, String tool);  
	
}
