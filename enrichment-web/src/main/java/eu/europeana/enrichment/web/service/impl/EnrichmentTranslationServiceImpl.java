package eu.europeana.enrichment.web.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;

import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.mongo.model.TranslationEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.translation.internal.TranslationLanguageTool;
import eu.europeana.enrichment.translation.service.TranslationService;
import eu.europeana.enrichment.web.service.EnrichmentTranslationService;

public class EnrichmentTranslationServiceImpl implements EnrichmentTranslationService {

	/*
	 * Loading all translation services
	 */
	@Resource(name = "googleTranslationService")
	TranslationService googleTranslationService;
	@Resource(name = "eTranslationService")
	TranslationService eTranslationService;
	
	/*
	 * Defining the available tools for translation
	 */
	private static final String googleToolName = "Google";
	private static final String eTranslationToolName = "eTranslation";
	
	@Resource(name = "translationLanguageTool")
	TranslationLanguageTool translationLanguageTool;
	
	@Resource(name = "persistentTranslationEntityService")
	PersistentTranslationEntityService persistentTranslationEntityService;

	//@Cacheable("translationResults")
	@Override
	public String translate(String text, String sourceLanguage, String tool) {
		try {
			TranslationEntity entity = new TranslationEntityImpl();
			entity.setOriginalText(text);
			entity.setKey(text);
			entity.setTool(tool);
			TranslationEntity persistentEntity = persistentTranslationEntityService.findTranslationEntity(entity.getKey());
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
				double ratio = translationLanguageTool.getLanguageRatio(translatedSentence);
				System.out.println("Sentence ratio: " + ratio + " ("+translatedSentence+")");
				//TODO: save ratio
			}
			
			TranslationEntity newEntity = new TranslationEntityImpl();
			newEntity.setOriginalText(text);
			newEntity.setKey(text);
			newEntity.setTool(tool);
			newEntity.setTranslatedText(returnValue);
			persistentTranslationEntityService.saveTranslationEntity(newEntity);
			
			return returnValue;
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

}
