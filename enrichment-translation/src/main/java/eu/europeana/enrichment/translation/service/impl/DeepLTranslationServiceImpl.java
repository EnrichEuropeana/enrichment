package eu.europeana.enrichment.translation.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.europeana.enrichment.translation.exception.TranslationException;
import eu.europeana.enrichment.translation.service.TranslationService;

public class DeepLTranslationServiceImpl implements TranslationService {

	private final String baseUrl = "https://api.deepl.com/v2/translate";
	
	private final List<String> sourceLangauges; 
	{
		String[] languages = {"EN", "DE", "FR", "ES", "PT", "IT", "NL", "PL", "RU"};
		sourceLangauges = Arrays.asList(languages);
	}
	private final String sourceTag = "source_lang";
	private final String targetTag = "target_lang";
	private final String textTag = "text";
	private final String authTag = "auth_key";
	
	@Override
	public void init(String credentialFilePath) {
		// TODO Read credential file and set authentication key
	}
	@Override
	public String translateText(String text, String sourceLanguage) throws TranslationException {
		// TODO Auto-generated method stub
		
		// https://www.deepl.com/api.html
		return null;
	}
	
	
}
