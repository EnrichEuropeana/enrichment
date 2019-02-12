package eu.europeana.enrichment.web.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.StoryItemEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.mongo.model.StoryEntityImpl;
import eu.europeana.enrichment.mongo.model.StoryItemEntityImpl;
import eu.europeana.enrichment.mongo.model.TranslationEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.translation.internal.TranslationLanguageTool;
import eu.europeana.enrichment.translation.service.TranslationService;
import eu.europeana.enrichment.web.model.EnrichmentTranslationRequest;
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
	@Resource(name = "persistentStoryEntityService")
	PersistentStoryEntityService persistentStoryEntityService;
	@Resource(name = "persistentStoryItemEntityService")
	PersistentStoryItemEntityService persistentStoryItemEntityService;

	
	//@Cacheable("translationResults")
	@Override
	public String translate(EnrichmentTranslationRequest requestParam) {
		try {
			String defaultTargetLanguage = "en";
			String storyId = requestParam.getStoryId();
			String storyItemId = requestParam.getStoryItemId();
			String originalText = requestParam.getText();
			String textType = requestParam.getType();
			String tool = requestParam.getTool();
			String sourceLanguage = requestParam.getSourceLanguage();
			
			/*
			 * Check if story / storyItem already exist and
			 * if there is a translation
			 */
			StoryEntity dbStoryEntity = persistentStoryEntityService.findStoryEntity(storyId);
			StoryEntity tmpStoryEntity = null;
			StoryItemEntity tmpStoryItemEntity = null;
			if(dbStoryEntity != null) {
				tmpStoryEntity = dbStoryEntity;
				StoryItemEntity dbStoryItemEntity = persistentStoryItemEntityService.findStoryItemEntity(storyItemId);
				if(dbStoryItemEntity != null) {
					tmpStoryItemEntity = dbStoryItemEntity;
					//TODO: compare text hashes if there are equal, if not throw exception
					//Check if translation already exists
					TranslationEntity dbTranslationEntity = persistentTranslationEntityService.
							findTranslationEntityWithStoryInformation(storyItemId, tool, sourceLanguage);
					if(dbTranslationEntity != null)
						return dbTranslationEntity.getTranslatedText();
					
					if(originalText.isEmpty() && !dbStoryItemEntity.getText().isEmpty()) {
						// Reuse of dbStoryItemEntity text if original text is not given
						originalText = dbStoryItemEntity.getText();
					}
				}
			}
			
			if(tmpStoryEntity == null) {
				tmpStoryEntity = new StoryEntityImpl();
				tmpStoryEntity.setStoryId(storyId);
				persistentStoryEntityService.saveStoryEntity(tmpStoryEntity);
			}
			if(tmpStoryItemEntity == null) {
				tmpStoryItemEntity = new StoryItemEntityImpl();
				tmpStoryItemEntity.setStoryEntity(tmpStoryEntity);
				tmpStoryItemEntity.setKey(originalText);
				tmpStoryItemEntity.setLanguage(sourceLanguage);
				tmpStoryItemEntity.setText(originalText);
				tmpStoryItemEntity.setType(textType);
				persistentStoryItemEntityService.saveStoryItemEntity(tmpStoryItemEntity);
			}
			
			//TODO: throw exveption
			if(originalText.isEmpty())
				return "";
			
			TranslationEntity tmpTranslationEntity = new TranslationEntityImpl();
			tmpTranslationEntity.setStoryItemEntity(tmpStoryItemEntity);
			tmpTranslationEntity.setLanguage(defaultTargetLanguage);
			tmpTranslationEntity.setTool(tool);
			//Empty string because of callback
			tmpTranslationEntity.setTranslatedText("");

			String returnValue;
			switch (tool) {
			case googleToolName:
				returnValue = googleTranslationService.translateText(originalText, sourceLanguage);
				tmpTranslationEntity.setKey(returnValue);
				tmpTranslationEntity.setTranslatedText(returnValue);
				break;
			case eTranslationToolName:
				returnValue = eTranslationService.translateText(originalText, sourceLanguage);
				tmpTranslationEntity.setKey(returnValue);
				break;
			default:
				//TODO: Exception handling
				returnValue = "";
				break;
			}
			
			persistentTranslationEntityService.saveTranslationEntity(tmpTranslationEntity);
			
			//TODO: throw exception
			if(returnValue.equals(""))
				return returnValue;
			
			/*
			 * Check English word ratio based on sentences
			 */
			/*
			List<String> sentences = translationLanguageTool.sentenceSplitter(translatedText);
			for (String translatedSentence : sentences) {
				double ratio = translationLanguageTool.getLanguageRatio(translatedSentence);
				System.out.println("Sentence ratio: " + ratio + " ("+translatedSentence+")");
				//TODO: save ratio
			}*/
			
			return returnValue;
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

}
