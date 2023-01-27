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
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.translation.exception.TranslationException;
import eu.europeana.enrichment.translation.internal.TranslationLanguageTool;
import eu.europeana.enrichment.translation.service.impl.ETranslationEuropaServiceImpl;
import eu.europeana.enrichment.translation.service.impl.TranslationGoogleServiceImpl;
import eu.europeana.enrichment.web.common.config.I18nConstants;
import eu.europeana.enrichment.web.exception.ParamValidationException;
import eu.europeana.enrichment.web.model.EnrichmentTranslationRequest;
import eu.europeana.enrichment.web.service.EnrichmentStoryAndItemStorageService;
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

    @Autowired
    EnrichmentStoryAndItemStorageService enrichmentStoryAndItemStorageService;
    
	@Autowired
	PersistentStoryEntityService persistentStoryEntityService;
	
	@Autowired
	PersistentItemEntityService persistentItemEntityService;
	
	@Override
	public String getTranslation(String storyId, String itemId, String translationTool, String type) {
		TranslationEntity dbTranslationEntity = persistentTranslationEntityService.findTranslationEntityWithAditionalInformation(storyId, itemId, translationTool, EnrichmentConstants.defaultTargetTranslationLanguage, type);
		if(dbTranslationEntity==null) return null;
		return dbTranslationEntity.getTranslatedText();
	}
	
	@Override
	public String translateStory(String storyId, String type, String translationTool, boolean update) throws Exception {
		String textToTranslate = null;
		String sourceLanguage = null;
		StoryEntity storyUpdated = persistentStoryEntityService.findStoryEntity(storyId);
		
		boolean isUpdated=false;
		//check if the story is updated with new data from Transcribathon
		if(update) {
			isUpdated = enrichmentStoryAndItemStorageService.updateStoryFromTranscribathon(storyUpdated);
		}		
		if(storyUpdated==null) {
			logger.debug("The story for the translation does not exist!");
			return null;
//			throw new ParamValidationException(I18nConstants.RESOURCE_NOT_FOUND, EnrichmentTranslationRequest.PARAM_STORY_ID, "The story for the translation does not exist!");
		}
		
		if(! isUpdated) {
			TranslationEntity dbTranslationEntity = persistentTranslationEntityService.findTranslationEntityWithAditionalInformation(storyId, null, translationTool, EnrichmentConstants.defaultTargetTranslationLanguage, type);
			if(dbTranslationEntity != null) {
				return dbTranslationEntity.getTranslatedText();
			}						
		}

		if(EnrichmentConstants.STORY_ITEM_TRANSCRIPTION.equalsIgnoreCase(type) && !StringUtils.isBlank(storyUpdated.getTranscriptionText())) {
			textToTranslate = storyUpdated.getTranscriptionText();
			sourceLanguage = ModelUtils.getMainTranslationLanguage(storyUpdated);
		}
		else if(EnrichmentConstants.STORY_ITEM_DESCRIPTION.equalsIgnoreCase(type) && !StringUtils.isBlank(storyUpdated.getDescription())) {
			textToTranslate = storyUpdated.getDescription();
			sourceLanguage = storyUpdated.getLanguageDescription();
		}
		else if(EnrichmentConstants.STORY_ITEM_SUMMARY.equalsIgnoreCase(type) && !StringUtils.isBlank(storyUpdated.getSummary())) {
			textToTranslate = storyUpdated.getSummary();
			sourceLanguage = storyUpdated.getLanguageSummary();
		}		
		if(StringUtils.isBlank(textToTranslate))
		{
			logger.debug("The text of the story to be translated is empty!");
			return null;
		}
		else if(EnrichmentConstants.defaultTargetTranslationLanguage.equalsIgnoreCase(sourceLanguage)) {
			return textToTranslate;
		}
		
		
		TranslationEntity newTranslation = translateAndSave(storyId, null, type, translationTool, sourceLanguage, textToTranslate);
		if(newTranslation==null) return null;
		return newTranslation.getTranslatedText();
	}

	@Override
	public String translateItem(String storyId, String itemId, String type, String translationTool, boolean update) throws Exception {
		String textToTranslate = null;
		String sourceLanguage = null;
		ItemEntity itemUpdated = persistentItemEntityService.findItemEntity(storyId, itemId);
		
		boolean isUpdated=false;
		//check if the item is updated with new data from Transcribathon
		if(update) {
			isUpdated = enrichmentStoryAndItemStorageService.updateItemFromTranscribathon(itemUpdated);
		}		
		if(itemUpdated==null) {
			logger.debug("The item for the translation does not exist!");
			return null;
//			throw new ParamValidationException(I18nConstants.RESOURCE_NOT_FOUND, EnrichmentTranslationRequest.PARAM_ITEM_ID, "The item for the translation does not exist!");
		}
		
		if(! isUpdated) {
			TranslationEntity dbTranslationEntity = persistentTranslationEntityService.findTranslationEntityWithAditionalInformation(storyId, itemId, translationTool, EnrichmentConstants.defaultTargetTranslationLanguage, type);
			if(dbTranslationEntity != null) {
				return dbTranslationEntity.getTranslatedText();
			}						
		}

		textToTranslate = itemUpdated.getTranscriptionText();
		sourceLanguage = ModelUtils.getMainTranslationLanguage(itemUpdated);
		if(StringUtils.isBlank(textToTranslate))
		{
			logger.debug("The text of the item to be translated is empty!");
			return null;
		}
		else if(EnrichmentConstants.defaultTargetTranslationLanguage.equalsIgnoreCase(sourceLanguage)) {
			return textToTranslate;
		}
		
		TranslationEntity newTranslation = translateAndSave(storyId, itemId, type, translationTool, sourceLanguage, textToTranslate);
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
