package eu.europeana.enrichment.web.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.api.commons.web.exception.InternalServerException;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.definitions.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.definitions.model.impl.StoryEntityImpl;
import eu.europeana.enrichment.definitions.model.impl.TranslationEntityImpl;
import eu.europeana.enrichment.definitions.model.utils.ModelUtils;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.translation.exception.TranslationException;
import eu.europeana.enrichment.translation.internal.TranslationLanguageTool;
import eu.europeana.enrichment.translation.service.impl.DeeplTranslationServiceImpl;
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
	
	@Autowired
	DeeplTranslationServiceImpl deeplTranslationService;
	
	Logger logger = LogManager.getLogger(getClass());
		
    @Autowired
	TranslationLanguageTool translationLanguageTool;
	
    @Autowired
	PersistentTranslationEntityService persistentTranslationEntityService;
		
	@Override
	public String translateStory(StoryEntityImpl story, String type, String translationTool, boolean translate) throws Exception {
		List<TranslationEntityImpl> dbTranslationEntity = persistentTranslationEntityService.findTranslationEntitiesWithAditionalInformation(story.getStoryId(), null, translationTool, EnrichmentConstants.defaultTargetTranslationLang2Letter, type);
		if(! dbTranslationEntity.isEmpty()) {
			return dbTranslationEntity.get(0).getTranslatedText();
		}						

		String textToTranslate = null;
		String sourceLanguage = null;
		if(EnrichmentConstants.STORY_ITEM_TRANSCRIPTION.equalsIgnoreCase(type) && !StringUtils.isBlank(story.getTranscriptionText())) {
			textToTranslate = story.getTranscriptionText();
			sourceLanguage = ModelUtils.getOnlyTranscriptionLanguage(story.getTranscriptionLanguages());
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
		else if(sourceLanguage!=null && EnrichmentConstants.transcribathonEnglishLangValues.contains(sourceLanguage)) {
			return textToTranslate;
		}		
		
		if(translate) {
			TranslationEntityImpl newTranslation = translateAndSave(story.getStoryId(), null, type, translationTool, sourceLanguage, textToTranslate);
			if(newTranslation==null) return null;
			return newTranslation.getTranslatedText();
		}
		else {
			return null;
		}
	}

	@Override
	public String translateItem(ItemEntityImpl item, String property, String translationTool, boolean translate) throws Exception {
		List<TranslationEntityImpl> dbTranslationEntity = persistentTranslationEntityService.findTranslationEntitiesWithAditionalInformation(item.getStoryId(), item.getItemId(), translationTool, EnrichmentConstants.defaultTargetTranslationLang2Letter, property);
		if(! dbTranslationEntity.isEmpty()) {
			return dbTranslationEntity.get(0).getTranslatedText();
		}						

		String textToTranslate = null;
		String sourceLanguage = null;
		if(EnrichmentConstants.ITEM_HTRDATA.equalsIgnoreCase(property) && !StringUtils.isBlank(item.getHtrdataTranscription())) {
			textToTranslate = item.getHtrdataTranscription();
			sourceLanguage = ModelUtils.getOnlyTranscriptionLanguage(item.getHtrdataTranscriptionLangs());
		}
		else if(EnrichmentConstants.STORY_ITEM_TRANSCRIPTION.equalsIgnoreCase(property) && !StringUtils.isBlank(item.getTranscriptionText())) {
			textToTranslate = item.getTranscriptionText();
			sourceLanguage = ModelUtils.getOnlyTranscriptionLanguage(item.getTranscriptionLanguages());
		}

		if(StringUtils.isBlank(textToTranslate))
		{
			logger.debug("The text of the item to be translated is empty!");
			return null;
		}
		else if(sourceLanguage!=null && EnrichmentConstants.transcribathonEnglishLangValues.contains(sourceLanguage)) {
			return textToTranslate;
		}
		
		if(translate) {
			TranslationEntityImpl newTranslation = translateAndSave(item.getStoryId(), item.getItemId(), property, translationTool, sourceLanguage, textToTranslate);
			if(newTranslation==null) return null;
			return newTranslation.getTranslatedText();
		}
		else {
			return null;
		}
	}

	private TranslationEntityImpl translateAndSave(String storyId, String itemId, String property, String translationTool, String sourceLanguage, String textToTranslate) throws Exception{
		try {
			TranslationEntityImpl tmpTranslationEntity = new TranslationEntityImpl();
			tmpTranslationEntity.setLanguage(EnrichmentConstants.defaultTargetTranslationLang2Letter);
			tmpTranslationEntity.setTool(translationTool);
			tmpTranslationEntity.setStoryId(storyId);
			tmpTranslationEntity.setItemId(itemId);
			tmpTranslationEntity.setType(property);
			tmpTranslationEntity.setKey(textToTranslate);

			switch (translationTool) {
			case EnrichmentConstants.defaultTranslationTool:
				if(googleTranslationService==null) {
					logger.info("The google translation service is currently disabled.");
					return null;
				}
				List<String> googleTransTextResp = new ArrayList<>();
				List<String> googleTransDetectedLangResp = new ArrayList<>();
				googleTranslationService.translateText(textToTranslate, null, EnrichmentConstants.defaultTargetTranslationLang2Letter, googleTransTextResp, googleTransDetectedLangResp);
				if(googleTransTextResp.size()>0) {
					tmpTranslationEntity.setTranslatedText(googleTransTextResp.get(0));
					tmpTranslationEntity.setOriginLangGoogle(googleTransDetectedLangResp.get(0));
				}
				break;
			case EnrichmentConstants.eTranslationTool:
				if(StringUtils.isBlank(sourceLanguage)) {
					logger.info("The eTranslation source language is empty! Skipping translation!");
					return null;					
				}
				String eTranslationResponse = eTranslationService.translateText(textToTranslate, sourceLanguage, EnrichmentConstants.defaultTargetTranslationLang2Letter);
				if(! eTranslationResponse.equals(EnrichmentConstants.eTranslationFailedSign)) {
					tmpTranslationEntity.setTranslatedText(eTranslationResponse);
				}
				break;
			case EnrichmentConstants.deeplTranslationTool:
				String deeplResp = deeplTranslationService.translateText(textToTranslate, EnrichmentConstants.defaultTargetTranslationLang2Letter);
				tmpTranslationEntity.setTranslatedText(deeplResp);
				break;
			default:
				throw new ParamValidationException(I18nConstants.INVALID_PARAM_VALUE, EnrichmentTranslationRequest.PARAM_TRANSLATION_TOOL, translationTool);
			}

			return persistentTranslationEntityService.saveTranslationEntity(tmpTranslationEntity);
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
		
		List<TranslationEntityImpl> dbTranslationEntity = persistentTranslationEntityService.findTranslationEntitiesWithAditionalInformation(storyId, itemId, translationTool, language, type);
		if(dbTranslationEntity.isEmpty()) {
			//TODO: proper exception handling
			return "Item " + i + " failed to be uploaded!";
		}		
		try {
			
			Date now = new Date();
			dbTranslationEntity.get(0).setCreated(now);
			dbTranslationEntity.get(0).setModified(now);
			dbTranslationEntity.get(0).setKey(originalText);
			dbTranslationEntity.get(0).setTranslatedText(translatedText);
			
			persistentTranslationEntityService.saveTranslationEntity(dbTranslationEntity.get(0));

		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO proper exception handling
			logger.log(Level.ERROR, "Exception during setting the key in the process of storing the translation entity.", e);
			return "Item " + i + " failed to be uploaded!";
		}
		
		return "Item " + i + " uploaded successfully!";
	}
	
}
