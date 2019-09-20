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
import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.mongo.model.DBTranslationEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.translation.exception.TranslationException;
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
			else if(itemId == null || itemId.isEmpty())
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentTranslationRequest.PARAM_STORY_ITEM_ID, null);
			else if(translationTool == null || translationTool.isEmpty())
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentTranslationRequest.PARAM_TRANSLATION_TOOL, null);
			
			if(type == null || type.isEmpty())
				type = "transcription";
			else if(!(type.equals("summary") || type.equals("description") || type.equals("transcription")))
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentTranslationRequest.PARAM_TYPE, null);
			
			StoryEntity dbStoryEntity = null;
			ItemEntity dbItemEntity = null;
			String sourceLanguage = null;
			
			//TODO: the "if" part below takes into account story and item, can be improved not to hardcode it in if 
			//translate text for the whole story including all items
			if(itemId.compareTo("all")==0)
			{		
				dbStoryEntity = persistentStoryEntityService.findStoryEntity(storyId);
				if(dbStoryEntity != null)
				{
					sourceLanguage = dbStoryEntity.getLanguage();
					//Reuse of StoryEntity text if original text is not given
					if(originalText == null || originalText.isEmpty())
					{
						if(type.toLowerCase().equals("transcription") && !(dbStoryEntity.getTranscription() == null || dbStoryEntity.getTranscription().isEmpty())) {
							
							originalText = dbStoryEntity.getTranscription();
						}
						else if(type.toLowerCase().equals("summary") && !(dbStoryEntity.getSummary() == null || dbStoryEntity.getSummary().isEmpty())) {
							
							originalText = dbStoryEntity.getSummary();
						}
						else if(type.toLowerCase().equals("description") && !(dbStoryEntity.getDescription() == null || dbStoryEntity.getDescription().isEmpty())) {
							
							originalText = dbStoryEntity.getDescription();
						}
					}

				}
				else
				{
					throw new ParamValidationException(I18nConstants.RESOURCE_NOT_FOUND, EnrichmentTranslationRequest.PARAM_STORY_ID, null);
				}
			}
			//translate text for the specific item
			else
			{
				dbItemEntity = persistentItemEntityService.findItemEntityFromStory(storyId, itemId);
				if(dbItemEntity!=null)
				{
					sourceLanguage = dbItemEntity.getLanguage();
					//Reuse of ItemEntity text if original text is not given
					if(originalText == null || originalText.isEmpty())
					{
						if(type.toLowerCase().equals("transcription") && !(dbItemEntity.getTranscription() == null || dbItemEntity.getTranscription().isEmpty())) {
							
							originalText = dbItemEntity.getTranscription();
						}
						else if(type.toLowerCase().equals("description") && !(dbItemEntity.getDescription() == null || dbItemEntity.getDescription().isEmpty())) {
							
							originalText = dbItemEntity.getDescription();
						}
					}
				}
				else
				{
					throw new ParamValidationException(I18nConstants.RESOURCE_NOT_FOUND, EnrichmentTranslationRequest.PARAM_STORY_ITEM_ID, null);
				}

			}
			
			/*
			 * Check if story / storyItem already exist and
			 * if there is a translation
			 */

			String translationKey = HelperFunctions.generateHashFromText(originalText);

			TranslationEntity dbTranslationEntity = persistentTranslationEntityService.findTranslationEntityWithAllAditionalInformation(storyId, itemId, translationTool, defaultTargetLanguage, type, translationKey);
			if(dbTranslationEntity != null) {
				return dbTranslationEntity.getTranslatedText();
			}

						
			if(!process) {
				//TODO: proper exception (like EnrichmentNERServiceImpl
				//TODO: throw exception 404
				throw new HttpException(null, "The translation of the required property was not performed yet. Please invoke the POST method, using the same parameters first.", HttpStatus.PRECONDITION_REQUIRED);
				
			}
			
			if(originalText == null || originalText.isEmpty())
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentTranslationRequest.PARAM_TEXT, null);
			
			
			TranslationEntity tmpTranslationEntity = new DBTranslationEntityImpl();
			tmpTranslationEntity.setStoryEntity(dbStoryEntity);
			tmpTranslationEntity.setItemEntity(dbItemEntity);
			tmpTranslationEntity.setLanguage(defaultTargetLanguage);
			tmpTranslationEntity.setTool(translationTool);
			tmpTranslationEntity.setStoryId(storyId);
			tmpTranslationEntity.setItemId(itemId);
			tmpTranslationEntity.setType(type);
			tmpTranslationEntity.setKey(originalText);
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
				//tmpTranslationEntity.setKey(returnValue);
				tmpTranslationEntity.setTranslatedText(returnValue);
				break;
			case eTranslationToolName:
				if(sendRequest) {
					List<String> textArray = textSplitter(originalText);
					returnValue = eTranslationService.translateText(textArray, sourceLanguage, defaultTargetLanguage);
				}
				//tmpTranslationEntity.setKey(returnValue);
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
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException | TranslationException | InterruptedException e) {
			throw new InternalServerException(e);
		}
	}
	
	private List<String> textSplitter(String originaltext){
		List<String> textArray = new ArrayList<>();
		List<String> sentences = translationLanguageTool.sentenceSplitter(originaltext);
		String tmpString = "";
		for (String tmpSentence : sentences) {
			tmpString += tmpSentence + " ";
			if(tmpString.length() > 4000) {
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
		String originalText = requestParam.getOriginalText();
		
		if(storyId.isEmpty() || storyId==null) {
			throw new ParamValidationException(I18nConstants.INVALID_PARAM_VALUE, EnrichmentTranslationRequest.PARAM_STORY_ID, null);
		}
		else if(itemId.isEmpty() || itemId==null) {
			throw new ParamValidationException(I18nConstants.INVALID_PARAM_VALUE, EnrichmentTranslationRequest.PARAM_STORY_ITEM_ID, null);
		}
		else if(translatedText.isEmpty() || translatedText==null) {
			throw new ParamValidationException(I18nConstants.INVALID_PARAM_VALUE, EnrichmentTranslationRequest.PARAM_TEXT, null);
		}
		else if(translationTool.isEmpty() || translationTool==null) {
			throw new ParamValidationException(I18nConstants.INVALID_PARAM_VALUE, EnrichmentTranslationRequest.PARAM_TRANSLATION_TOOL, null);
		}
		else if(type.isEmpty() || type==null) {
			throw new ParamValidationException(I18nConstants.INVALID_PARAM_VALUE, EnrichmentTranslationRequest.PARAM_TYPE, null);
		}
		else if(originalText.isEmpty() || originalText==null) {
			throw new ParamValidationException(I18nConstants.INVALID_PARAM_VALUE, EnrichmentTranslationRequest.PARAM_ORIGINAL_TEXT, null);
		}
		
		TranslationEntity dbTranslationEntity = persistentTranslationEntityService.
				findTranslationEntityWithAditionalInformation(storyId, itemId, translationTool, language, type);
		if(dbTranslationEntity == null) {
			//TODO: proper exception handling
			return "";
		}
		
		try {
			
			dbTranslationEntity.setKey(originalText);

			dbTranslationEntity.setTranslatedText(translatedText);
			
			persistentTranslationEntityService.saveTranslationEntity(dbTranslationEntity);

		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO proper exception handling
			e.printStackTrace();
			return "";
		}
		
		return "Done";
	}
	
}
