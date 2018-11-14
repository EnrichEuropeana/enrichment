package eu.europeana.enrichment.web.service.impl;

import javax.annotation.Resource;

import eu.europeana.enrichment.translation.service.TranslationService;
import eu.europeana.enrichment.translation.service.impl.ETranslationEuropaServiceImpl;
import eu.europeana.enrichment.translation.service.impl.TranslationGoogleServiceImpl;
import eu.europeana.enrichment.web.service.EnrichmentTranslationService;

public class EnrichmentTranslationServiceImpl implements EnrichmentTranslationService {

	//@Resource
	TranslationService googleTranslationService;
	TranslationService eTranslationService;
	
	private static final String googleToolName = "Google";
	private static final String eTranslationToolName = "eTranslation";
	
	//TODO: write credential file path to config
	//private static final String googleCredentialFilePath = "C:\\Users\\katicd\\Documents\\Europeana\\Code\\Ait\\additional_data\\EU-Europeana-enrichment-d92edee4115a.json";
	private static final String googleCredentialFilePath = "";
	private static final String eTranslationCredentialFilePath = "C:\\Users\\katicd\\Documents\\Europeana\\Code\\Ait\\additional_data\\eTranslation.txt";
	
	@Override
	public void init() {
		googleTranslationService = new TranslationGoogleServiceImpl();
		googleTranslationService.init(googleCredentialFilePath);
		
		eTranslationService = new ETranslationEuropaServiceImpl();
		eTranslationService.init(eTranslationCredentialFilePath);
	}

	@Override
	public String translate(String text, String sourceLanguage, String tool) {
		String returnValue;
		switch (tool) {
		case googleToolName:
			returnValue = googleTranslationService.translateText(text, sourceLanguage);
			break;
		case eTranslationToolName:
			returnValue = eTranslationService.translateText(text, sourceLanguage);
			break;
		default:
			//TODO: Exception handling
			returnValue = "";
			break;
		}
		return returnValue;
	}

}
