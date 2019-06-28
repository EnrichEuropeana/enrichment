package eu.europeana.enrichment.web.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.jsoup.Jsoup;
import org.springframework.http.HttpStatus;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.api.commons.web.exception.InternalServerException;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.mongo.model.DBTranslationEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.translation.internal.TranslationLanguageTool;
import eu.europeana.enrichment.translation.service.TranslationService;
import eu.europeana.enrichment.web.common.config.I18nConstants;
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
	public String translate(EnrichmentTranslationRequest requestParam, boolean process) throws HttpException{
		try {
			//TODO: check parameters and return other status code
			String defaultTargetLanguage = "en";
			String storyId = requestParam.getStoryId();			
			String itemId = requestParam.getItemId();			
			String originalText = requestParam.getText();
			String translationTool = requestParam.getTranslationTool();
			String type = requestParam.getType();
			Boolean sendRequest = true;//requestParam.getSendRequest() == null? true : requestParam.getSendRequest();
			
			/*
			 * Parameter check
			 */
			if(storyId == null || storyId.isEmpty())
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentTranslationRequest.PARAM_STORY_ID, null);
			else if(translationTool == null || translationTool.isEmpty())
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentTranslationRequest.PARAM_TRANSLATION_TOOL, null);
			
			if(type == null || type.isEmpty())
				type = "transcription";
			else if(!(type.equals("summary") || type.equals("description") || type.equals("transcription")))
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentTranslationRequest.PARAM_TYPE, null);
			
			/*
			 * Check if story / storyItem already exist and
			 * if there is a translation
			 */
			StoryEntity dbStoryEntity = persistentStoryEntityService.findStoryEntity(storyId);						
			StoryEntity tmpStoryEntity = null;
			String sourceLanguage = null;
			if(dbStoryEntity != null) {
				sourceLanguage = dbStoryEntity.getLanguage();
				tmpStoryEntity = dbStoryEntity;
				TranslationEntity dbTranslationEntity = persistentTranslationEntityService.
							findTranslationEntityWithStoryAndItemInformation(storyId, itemId, translationTool, defaultTargetLanguage, type);
				if(dbTranslationEntity != null) {
					return dbTranslationEntity.getTranslatedText();
				}
				
				if(originalText == null || originalText.isEmpty())
				{
					if(type.toLowerCase().equals("transcription") && !(dbStoryEntity.getTranscription() == null || dbStoryEntity.getTranscription().isEmpty())) {
						// Reuse of dbItemEntity text if original text is not given
						originalText = dbStoryEntity.getTranscription();
					}
					else if(type.toLowerCase().equals("summary") && !(dbStoryEntity.getSummary() == null || dbStoryEntity.getSummary().isEmpty())) {
						// Reuse of dbItemEntity text if original text is not given
						originalText = dbStoryEntity.getSummary();
					}
					else if(type.toLowerCase().equals("description") && !(dbStoryEntity.getDescription() == null || dbStoryEntity.getDescription().isEmpty())) {
						// Reuse of dbItemEntity text if original text is not given
						originalText = dbStoryEntity.getDescription();
					}
				}
			} else {
				//TODO: throw exception
				throw new ParamValidationException(I18nConstants.RESOURCE_NOT_FOUND, EnrichmentTranslationRequest.PARAM_STORY_ID, null);
			}
			
			if(!process) {
				//TODO: proper exception (like EnrichmentNERServiceImpl
				//TODO: throw exception 404
				throw new HttpException(null, "The translation of the required property was not performed yet. Please invoke the POST method, using the same parameters first.", HttpStatus.PRECONDITION_REQUIRED);
				
			}
			
			if(originalText == null || originalText.isEmpty())
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentTranslationRequest.PARAM_TEXT, null);
			
//			if(tmpStoryEntity == null) {
//				throw new ParamValidationException(I18nConstants.RESOURCE_NOT_FOUND, EnrichmentTranslationRequest.PARAM_STORY_ID, null);
////				tmpStoryEntity = new StoryEntityImpl();
////				tmpStoryEntity.setStoryId(storyId);
////				persistentStoryEntityService.saveStoryEntity(tmpStoryEntity);
//			}
			
			
			TranslationEntity tmpTranslationEntity = new DBTranslationEntityImpl();
			tmpTranslationEntity.setStoryEntity(tmpStoryEntity);
			tmpTranslationEntity.setLanguage(defaultTargetLanguage);
			tmpTranslationEntity.setTool(translationTool);
			tmpTranslationEntity.setStoryId(storyId);
			tmpTranslationEntity.setType(type);
			//Empty string because of callback
			tmpTranslationEntity.setTranslatedText("");
			
			String returnValue = "-1";
			switch (translationTool) {
			case googleToolName:
				if(sendRequest) {
					List<String> textArray = textSplitter(originalText);
					returnValue = googleTranslationService.translateText(textArray, sourceLanguage, defaultTargetLanguage);
					returnValue = Jsoup.parse(returnValue).text();
				}
				tmpTranslationEntity.setKey(returnValue);
				tmpTranslationEntity.setTranslatedText(returnValue);
				break;
			case eTranslationToolName:
				if(sendRequest) {
					List<String> textArray = textSplitter(originalText);
					returnValue = eTranslationService.translateText(textArray, sourceLanguage, defaultTargetLanguage);
				}
				tmpTranslationEntity.setKey(returnValue);
				tmpTranslationEntity.setTranslatedText(returnValue);
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
	
	private List<String> textSplitter(String originaltext){
		List<String> textArray = new ArrayList<>();
		List<String> sentences = translationLanguageTool.sentenceSplitter(originaltext);
		String tmpString = "";
		for (String tmpSentence : sentences) {
			tmpString += tmpSentence + " ";
			if(tmpString.length() > 50000) {
				textArray.add(tmpString);
				tmpString = "";
			}
		}
		if(!tmpString.equals(""))
			textArray.add(tmpString);
			
		return textArray;
	}

	@Override
	public String uploadTranslation(EnrichmentTranslationRequest requestParam) throws HttpException{
		String storyId = requestParam.getStoryId();
		String itemId = requestParam.getItemId();			
		String translatedText = requestParam.getText();		
		String translationTool = requestParam.getTranslationTool();
		String language = "en";
		String type = requestParam.getType();
		
		if(storyId.isEmpty() || translatedText.isEmpty() || translationTool.isEmpty() || language.isEmpty() || type.isEmpty()) {
			//TODO: proper exception handling
			return "";
		}
		TranslationEntity dbTranslationEntity = persistentTranslationEntityService.
				findTranslationEntityWithStoryAndItemInformation(storyId, itemId, translationTool, language, type);
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
