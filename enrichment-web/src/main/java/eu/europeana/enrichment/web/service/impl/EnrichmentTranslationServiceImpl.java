package eu.europeana.enrichment.web.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;

import eu.europeana.enrichment.common.definitions.TranslationEntity;
import eu.europeana.enrichment.common.model.TranslationEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.translation.internal.TranslationLanguageTool;
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
	
	@Resource(name = "persistentTranslationEntityService")
	PersistentTranslationEntityService persistentTranslationEntityService;
	
	TranslationLanguageTool translationLanguageTool = new TranslationLanguageTool();
	
	@Override
	public void init() {
		googleTranslationService = new TranslationGoogleServiceImpl();
		googleTranslationService.init(googleCredentialFilePath);
		
		eTranslationService = new ETranslationEuropaServiceImpl();
		eTranslationService.init(eTranslationCredentialFilePath);
	}

	//@Cacheable("translationResults")
	@Override
	public String translate(String text, String sourceLanguage, String tool) {
		
		TranslationEntity entity = new TranslationEntityImpl();
		entity.setOriginalText(text);
		entity.setSHA256Hash(text);
		entity.setTool(tool);
		TranslationEntity persistentEntity = persistentTranslationEntityService.findTranslationEntity(entity.getSHA256Hash());
		if(persistentEntity != null) {
			return persistentEntity.getTranslatedText();
		}
		
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
		
		if(returnValue.equals(""))
			return returnValue;
		
		String translatedText = persistentEntity.getTranslatedText();
		List<String> sentences = translationLanguageTool.sentenceSplitter(translatedText);
		for (String translatedSentence : sentences) {
			double ratio = translationLanguageTool.checkLangauge(translatedSentence);
			System.out.println("Sentence ratio: " + ratio + " ("+translatedSentence+")");
			//TODO: save ratio
		}
		
		
		TranslationEntity newEntity = new TranslationEntityImpl();
		newEntity.setOriginalText(text);
		newEntity.setSHA256Hash(text);
		newEntity.setTool(tool);
		newEntity.setTranslatedText(returnValue);
		persistentTranslationEntityService.saveTranslationEntity(newEntity);
		
		return returnValue;
	}

}
