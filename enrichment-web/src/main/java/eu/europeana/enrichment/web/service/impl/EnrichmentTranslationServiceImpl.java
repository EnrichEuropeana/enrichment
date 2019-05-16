package eu.europeana.enrichment.web.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import javax.annotation.Resource;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.api.commons.web.exception.InternalServerException;
import eu.europeana.enrichment.common.config.I18nConstants;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.mongo.model.DBTranslationEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.translation.internal.TranslationLanguageTool;
import eu.europeana.enrichment.translation.service.TranslationService;
import eu.europeana.enrichment.web.exception.ParamValidationException;
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
	@Resource(name = "persistentItemEntityService")
	PersistentItemEntityService persistentItemEntityService;

	
	//@Cacheable("translationResults")
	@Override
	public String translate(EnrichmentTranslationRequest requestParam) throws HttpException{
		try {
			//TODO: check parameters and return other status code
			String defaultTargetLanguage = "en";
			String storyId = requestParam.getStoryId();			
			String originalText = requestParam.getText();
			String translationTool = requestParam.getTranslationTool();
			String sourceLanguage = requestParam.getSourceLanguage();
			Boolean sendRequest = requestParam.getSendRequest() == null? true : requestParam.getSendRequest();
			
			/*
			 * Parameter check
			 */
			if(storyId == null || storyId.isEmpty())
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentTranslationRequest.PARAM_STORY_ID, null);
			else if(translationTool == null || translationTool.isEmpty())
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentTranslationRequest.PARAM_TRANSLATION_TOOL, null);
			else if(sourceLanguage == null || sourceLanguage.isEmpty())
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentTranslationRequest.PARAM_SOURCE_LANGUAGE, null);
			
			/*
			 * Check if story / storyItem already exist and
			 * if there is a translation
			 */
			StoryEntity dbStoryEntity = persistentStoryEntityService.findStoryEntity(storyId);						
			StoryEntity tmpStoryEntity = null;
			if(dbStoryEntity != null) {
			
				tmpStoryEntity = dbStoryEntity;
				TranslationEntity dbTranslationEntity = persistentTranslationEntityService.
							findTranslationEntityWithStoryInformation(storyId, translationTool, sourceLanguage);
				if(dbTranslationEntity != null)
					return dbTranslationEntity.getTranslatedText();
				
				if((originalText == null || originalText.isEmpty()) && 
						!(dbStoryEntity.getStoryTranscription() == null || dbStoryEntity.getStoryTranscription().isEmpty())) {
					// Reuse of dbItemEntity text if original text is not given
					originalText = dbStoryEntity.getStoryTranscription();
				}
			
			}
			
			if(originalText == null || originalText.isEmpty())
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentTranslationRequest.PARAM_TEXT, null);
			
			if(tmpStoryEntity == null) {
				throw new ParamValidationException(I18nConstants.RESOURCE_NOT_FOUND, EnrichmentTranslationRequest.PARAM_STORY_ID, null);
//				tmpStoryEntity = new StoryEntityImpl();
//				tmpStoryEntity.setStoryId(storyId);
//				persistentStoryEntityService.saveStoryEntity(tmpStoryEntity);
			}
			
			
			TranslationEntity tmpTranslationEntity = new DBTranslationEntityImpl();
			tmpTranslationEntity.setStoryEntity(tmpStoryEntity);
			tmpTranslationEntity.setLanguage(defaultTargetLanguage);
			tmpTranslationEntity.setTool(translationTool);
			tmpTranslationEntity.setStoryId(storyId);
			//Empty string because of callback
			tmpTranslationEntity.setTranslatedText("");

			String returnValue = "-1";
			switch (translationTool) {
			case googleToolName:
				if(sendRequest)
					returnValue = googleTranslationService.translateText(originalText, sourceLanguage, defaultTargetLanguage);
				tmpTranslationEntity.setKey(returnValue);
				tmpTranslationEntity.setTranslatedText(returnValue);
				break;
			case eTranslationToolName:
				if(sendRequest)
					returnValue = eTranslationService.translateText(originalText, sourceLanguage, defaultTargetLanguage);
				tmpTranslationEntity.setKey(returnValue);
				break;
			default:
				throw new ParamValidationException(I18nConstants.INVALID_PARAM_VALUE, EnrichmentTranslationRequest.PARAM_TRANSLATION_TOOL, translationTool);
			}
			
			persistentTranslationEntityService.saveTranslationEntity(tmpTranslationEntity);
			
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
			throw new InternalServerException(e);
		}
	}

	@Override
	public String uploadTranslation(EnrichmentTranslationRequest requestParam) throws HttpException{
		String storyId = requestParam.getStoryId();
		String translatedText = requestParam.getText();		
		String translationTool = requestParam.getTranslationTool();
		String language = requestParam.getSourceLanguage();
		
		if(storyId.isEmpty() || translatedText.isEmpty() || translationTool.isEmpty() || language.isEmpty()) {
			//TODO: proper exception handling
			return "";
		}
		TranslationEntity dbTranslationEntity = persistentTranslationEntityService.
				findTranslationEntityWithStoryInformation(storyId, translationTool, language);
		if(dbTranslationEntity == null) {
			//TODO: proper exception handling
			return "";
		}
		try {
			dbTranslationEntity.setKey(translatedText);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO proper exception handling
			e.printStackTrace();
			return "";
		}
		dbTranslationEntity.setTranslatedText(translatedText);
		persistentTranslationEntityService.saveTranslationEntity(dbTranslationEntity);
		return "Done";
	}
	
}
