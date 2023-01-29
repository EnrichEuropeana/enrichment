package eu.europeana.enrichment.web.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.cloud.translate.Translation;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.api.commons.web.exception.InternalServerException;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.model.impl.TranslationEntityImpl;
import eu.europeana.enrichment.model.utils.ModelUtils;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.translation.exception.TranslationException;
import eu.europeana.enrichment.translation.internal.TranslationLanguageTool;
import eu.europeana.enrichment.translation.service.impl.ETranslationEuropaServiceImpl;
import eu.europeana.enrichment.translation.service.impl.TranslationGoogleServiceImpl;
import eu.europeana.enrichment.web.common.config.I18nConstants;
import eu.europeana.enrichment.web.exception.ParamValidationException;
import eu.europeana.enrichment.web.model.EnrichmentTranslationRequest;
import eu.europeana.enrichment.web.service.EnrichmentTranslationService;

@Service(EnrichmentConstants.BEAN_ENRICHMENT_TRANSLATION_SERVICE)
public class EnrichmentTranslationServiceImpl implements EnrichmentTranslationService {

	/*
	 * Loading all translation services
	 */

	@Autowired(required=false)
	TranslationGoogleServiceImpl googleTranslationService;

	@Autowired
	ETranslationEuropaServiceImpl eTranslationService;
	
	Logger logger = LogManager.getLogger(getClass());
	
	/*
	 * Defining the available tools for translation
	 */
	private static final String googleToolName = "Google";
	private static final String eTranslationToolName = "eTranslation";
	
    @Autowired
	TranslationLanguageTool translationLanguageTool;
	
    @Autowired
	PersistentTranslationEntityService persistentTranslationEntityService;
	
	@Override
	public String getTranslation(String storyId, String itemId, String translationTool, String type) {
		TranslationEntity dbTranslationEntity = persistentTranslationEntityService.findTranslationEntityWithAditionalInformation(storyId, itemId, translationTool, EnrichmentConstants.defaultTargetTranslationLanguage, type);
		if(dbTranslationEntity==null) return null;
		return dbTranslationEntity.getTranslatedText();
	}
	
	@Override
	public String translateStory(StoryEntity story, String type, String translationTool) throws Exception {
		TranslationEntity dbTranslationEntity = persistentTranslationEntityService.findTranslationEntityWithAditionalInformation(story.getStoryId(), null, translationTool, EnrichmentConstants.defaultTargetTranslationLanguage, type);
		if(dbTranslationEntity != null) {
			return dbTranslationEntity.getTranslatedText();
		}						

		String textToTranslate = null;
		String sourceLanguage = null;
		if(EnrichmentConstants.STORY_ITEM_TRANSCRIPTION.equalsIgnoreCase(type) && !StringUtils.isBlank(story.getTranscriptionText())) {
			textToTranslate = story.getTranscriptionText();
			sourceLanguage = ModelUtils.getMainTranslationLanguage(story);
		}
		else if(EnrichmentConstants.STORY_ITEM_DESCRIPTION.equalsIgnoreCase(type) && !StringUtils.isBlank(story.getDescription())) {
			textToTranslate = story.getDescription();
			sourceLanguage = story.getLanguageDescription();
		}
		else if(EnrichmentConstants.STORY_ITEM_SUMMARY.equalsIgnoreCase(type) && !StringUtils.isBlank(story.getSummary())) {
			textToTranslate = story.getSummary();
			sourceLanguage = story.getLanguageSummary();
		}		
		if(StringUtils.isBlank(textToTranslate))
		{
			logger.debug("The text of the story to be translated is empty!");
			return null;
		}
		else if(EnrichmentConstants.defaultTargetTranslationLanguage.equalsIgnoreCase(sourceLanguage)) {
			return textToTranslate;
		}		
		
		TranslationEntity newTranslation = translateAndSave(story.getStoryId(), null, type, translationTool, sourceLanguage, textToTranslate);
		if(newTranslation==null) return null;
		return newTranslation.getTranslatedText();
	}

	@Override
	public String translateItem(ItemEntity item, String type, String translationTool) throws Exception {
		TranslationEntity dbTranslationEntity = persistentTranslationEntityService.findTranslationEntityWithAditionalInformation(item.getStoryId(), item.getItemId(), translationTool, EnrichmentConstants.defaultTargetTranslationLanguage, type);
		if(dbTranslationEntity != null) {
			return dbTranslationEntity.getTranslatedText();
		}						

		String textToTranslate = item.getTranscriptionText();
		String sourceLanguage = ModelUtils.getMainTranslationLanguage(item);
		if(StringUtils.isBlank(textToTranslate))
		{
			logger.debug("The text of the item to be translated is empty!");
			return null;
		}
		else if(EnrichmentConstants.defaultTargetTranslationLanguage.equalsIgnoreCase(sourceLanguage)) {
			return textToTranslate;
		}
		
		TranslationEntity newTranslation = translateAndSave(item.getStoryId(), item.getItemId(), type, translationTool, sourceLanguage, textToTranslate);
		if(newTranslation==null) return null;
		return newTranslation.getTranslatedText();
	}

	private TranslationEntity translateAndSave(String storyId, String itemId, String type, String translationTool, String sourceLanguage, String textToTranslate) throws Exception{
		try {
			TranslationEntity tmpTranslationEntity = new TranslationEntityImpl();
			tmpTranslationEntity.setLanguage(EnrichmentConstants.defaultTargetTranslationLanguage);
			tmpTranslationEntity.setTool(translationTool);
			tmpTranslationEntity.setStoryId(storyId);
			tmpTranslationEntity.setItemId(itemId);
			tmpTranslationEntity.setType(type);
			tmpTranslationEntity.setKey(textToTranslate);

			switch (translationTool) {
			case googleToolName:
				if(googleTranslationService==null) {
					logger.debug("The google translation service is currently disabled.");
					return null;
				}
				Translation googleResponse = googleTranslationService.translateText(textToTranslate, EnrichmentConstants.defaultTargetTranslationLanguage);
				if(googleResponse!=null) {
					String googleResponseText = Jsoup.parse(googleResponse.getTranslatedText()).text();
					tmpTranslationEntity.setTranslatedText(googleResponseText);
					tmpTranslationEntity.setOriginLangGoogle(googleResponse.getSourceLanguage());
				}
				break;
			case eTranslationToolName:
				if(StringUtils.isBlank(sourceLanguage)) {
					logger.info("The eTranslation source language is empty! Skipping translation!");
					return null;					
				}
				String eTranslationResponse = eTranslationService.translateText(textToTranslate, sourceLanguage, EnrichmentConstants.defaultTargetTranslationLanguage);
				if(eTranslationResponse!=null)
					tmpTranslationEntity.setTranslatedText(eTranslationResponse);
				break;
			default:
				throw new ParamValidationException(I18nConstants.INVALID_PARAM_VALUE, EnrichmentTranslationRequest.PARAM_TRANSLATION_TOOL, translationTool);
			}

			persistentTranslationEntityService.saveTranslationEntity(tmpTranslationEntity);
			return tmpTranslationEntity;
			/*
			 * Check English word ratio based on sentences
			 */
			/*
			List<String> sentences = translationLanguageTool.sentenceSplitter(translatedText);
			for (String translatedSentence : sentences) {
				double ratio = translationLanguageTool.getLanguageRatio(translatedSentence);
				logger.debug("Sentence ratio: " + ratio + " ("+translatedSentence+")");
				//TODO: save ratio
			}*/
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
	public String uploadTranslation(EnrichmentTranslationRequest requestParam, int i) throws HttpException{
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
			return "Item " + i + " failed to be uploaded!";
		}
		
		try {
			
			dbTranslationEntity.setKey(originalText);

			dbTranslationEntity.setTranslatedText(translatedText);
			
			persistentTranslationEntityService.saveTranslationEntity(dbTranslationEntity);

		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO proper exception handling
			logger.log(Level.ERROR, "Exception during setting the key in the process of storing the translation entity.", e);
			return "Item " + i + " failed to be uploaded!";
		}
		
		return "Item " + i + " uploaded successfully!";
	}
	
}
