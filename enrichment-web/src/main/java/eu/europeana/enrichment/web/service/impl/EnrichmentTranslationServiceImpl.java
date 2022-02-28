package eu.europeana.enrichment.web.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.cloud.translate.Translation;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.api.commons.web.exception.InternalServerException;
import eu.europeana.enrichment.common.commons.AppConfigConstants;
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
import eu.europeana.enrichment.translation.service.TranslationService;
import eu.europeana.enrichment.translation.service.impl.ETranslationEuropaServiceImpl;
import eu.europeana.enrichment.translation.service.impl.TranslationGoogleServiceImpl;
import eu.europeana.enrichment.web.common.config.I18nConstants;
import eu.europeana.enrichment.web.exception.ParamValidationException;
import eu.europeana.enrichment.web.model.EnrichmentTranslationRequest;
import eu.europeana.enrichment.web.service.EnrichmentStoryAndItemStorageService;
import eu.europeana.enrichment.web.service.EnrichmentTranslationService;

@Service(AppConfigConstants.BEAN_ENRICHMENT_TRANSLATION_SERVICE)
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
	public void translate(EnrichmentTranslationRequest requestParam, boolean process) throws Exception{
		try {
			//TODO: check parameters and return other status code
			String defaultTargetLanguage = "en";
			String storyId = requestParam.getStoryId();			
			String itemId = requestParam.getItemId();			
			String translationTool = requestParam.getTranslationTool();
			String type = requestParam.getType();
			Boolean sendRequest = true;//requestParam.getSendRequest() == null? true : requestParam.getSendRequest();
			String textToTranslate = null;
			/*
			 * Parameter check
			 */
			if(storyId == null || storyId.isBlank())
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentTranslationRequest.PARAM_STORY_ID, null);
			else if(itemId == null || itemId.isBlank())
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentTranslationRequest.PARAM_STORY_ITEM_ID, null);
			else if(translationTool == null || translationTool.isBlank())
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentTranslationRequest.PARAM_TRANSLATION_TOOL, null);

			if(type == null || type.isBlank())
				type = "transcription";
			else if(!(type.equals("summary") || type.equals("description") || type.equals("transcription")))
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentTranslationRequest.PARAM_TYPE, null);

			TranslationEntity dbTranslationEntity = persistentTranslationEntityService.findTranslationEntityWithAditionalInformation(storyId, itemId, translationTool, defaultTargetLanguage, type);
			if(dbTranslationEntity != null) {
				return;
			}						
			else if(!process) {
				//TODO: proper exception (like EnrichmentNERServiceImpl
				//TODO: throw exception 404
				throw new HttpException(null, "The translation of the required property was not performed yet. Please invoke the POST method, using the same parameters first.", HttpStatus.PRECONDITION_REQUIRED);				
			}

			StoryEntity dbStoryEntity = null;
			ItemEntity dbItemEntity = null;
			String sourceLanguage = null;			
			
			Object foundStoryOrItem = null;
			if(itemId.compareTo("all")==0)
			{		
				foundStoryOrItem = persistentStoryEntityService.findStoryEntity(storyId);
				if(foundStoryOrItem==null) foundStoryOrItem = enrichmentStoryAndItemStorageService.fetchAndSaveStoryFromTranscribathon(storyId);
			}
			else
			{	
				foundStoryOrItem = persistentItemEntityService.findItemEntity(storyId, itemId);
				if(foundStoryOrItem==null) foundStoryOrItem = enrichmentStoryAndItemStorageService.fetchAndSaveItemFromTranscribathon(storyId, itemId);
			}
			
			//TODO: the "if" part below takes into account story and item, can be improved not to hardcode it in if 
			//translate text for the whole story including all items
			if(itemId.compareTo("all")==0)
			{		
				dbStoryEntity = (StoryEntity) foundStoryOrItem;
				if(dbStoryEntity != null)
				{
					if(type.toLowerCase().equals("transcription") && dbStoryEntity.getTranscriptionText()!=null && !dbStoryEntity.getTranscriptionText().isBlank()) {
						
						textToTranslate = dbStoryEntity.getTranscriptionText();
						sourceLanguage = ModelUtils.getSingleTranslationLanguage(dbStoryEntity);
					}
					else if(type.toLowerCase().equals("summary") && dbStoryEntity.getSummary()!=null && !dbStoryEntity.getSummary().isBlank()) {
						
						textToTranslate = dbStoryEntity.getSummary();
						sourceLanguage = dbStoryEntity.getLanguageSummary();
					}
					else if(type.toLowerCase().equals("description") && dbStoryEntity.getDescription()!=null && !dbStoryEntity.getDescription().isBlank()) {
						
						textToTranslate = dbStoryEntity.getDescription();
						sourceLanguage = dbStoryEntity.getLanguageDescription();
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
				dbItemEntity = (ItemEntity) foundStoryOrItem;
				if(dbItemEntity!=null)
				{
					textToTranslate = dbItemEntity.getTranscriptionText();
					sourceLanguage = ModelUtils.getSingleTranslationLanguage(dbItemEntity);
				}
				else
				{
					throw new ParamValidationException(I18nConstants.RESOURCE_NOT_FOUND, EnrichmentTranslationRequest.PARAM_STORY_ITEM_ID, null);
				}

			}
			
			
			if(textToTranslate == null || textToTranslate.isBlank())
			{
				logger.info("The original text is empty or null");
				return;
			}
				//throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentTranslationRequest.PARAM_TEXT, null);

			logger.info("The original text is NOT empty or null.");
			
			TranslationEntity tmpTranslationEntity = new TranslationEntityImpl();
			tmpTranslationEntity.setLanguage(defaultTargetLanguage);
			tmpTranslationEntity.setTool(translationTool);
			tmpTranslationEntity.setStoryId(storyId);
			tmpTranslationEntity.setItemId(itemId);
			tmpTranslationEntity.setType(type);
			tmpTranslationEntity.setKey(textToTranslate);
			//Empty string because of callback

			switch (translationTool) {
			case googleToolName:
				if(sendRequest) {
					if(googleTranslationService==null) {
						logger.info("The google translation service is currently disabled.");
						return;
					}
					Translation googleResponse = googleTranslationService.translateText(textToTranslate, sourceLanguage, defaultTargetLanguage);
					if(googleResponse!=null) {
						String googleResponseText = Jsoup.parse(googleResponse.getTranslatedText()).text();
						tmpTranslationEntity.setTranslatedText(googleResponseText);
						tmpTranslationEntity.setOriginLangGoogle(googleResponse.getSourceLanguage());
					}
				}
				break;
			case eTranslationToolName:
				if(sendRequest) {
					String eTranslationResponse = eTranslationService.translateText(textToTranslate, sourceLanguage, defaultTargetLanguage);
					if(eTranslationResponse!=null)
						tmpTranslationEntity.setTranslatedText(eTranslationResponse);
				}
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
				logger.info("Sentence ratio: " + ratio + " ("+translatedSentence+")");
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
