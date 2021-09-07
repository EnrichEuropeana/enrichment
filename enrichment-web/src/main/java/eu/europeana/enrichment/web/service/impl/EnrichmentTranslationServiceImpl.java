package eu.europeana.enrichment.web.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.api.commons.web.exception.InternalServerException;
import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.model.impl.ItemEntityTranscribathonImpl;
import eu.europeana.enrichment.model.impl.StoryEntityImpl;
import eu.europeana.enrichment.model.impl.StoryEntityTranscribathonImpl;
import eu.europeana.enrichment.model.impl.TranslationEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.translation.exception.TranslationException;
import eu.europeana.enrichment.translation.internal.TranslationLanguageTool;
import eu.europeana.enrichment.translation.service.TranslationService;
import eu.europeana.enrichment.translation.service.impl.TranslationGoogleServiceImpl;
import eu.europeana.enrichment.web.common.config.I18nConstants;
import eu.europeana.enrichment.web.exception.ParamValidationException;
import eu.europeana.enrichment.web.model.EnrichmentTranslationRequest;
import eu.europeana.enrichment.web.service.EnrichmentNERService;
import eu.europeana.enrichment.web.service.EnrichmentTranslationService;

@Service(AppConfigConstants.BEAN_ENRICHMENT_TRANSLATION_SERVICE)
public class EnrichmentTranslationServiceImpl implements EnrichmentTranslationService {

	/*
	 * Loading all translation services
	 */
	//@Resource(name = "googleTranslationService")
	@Autowired
	TranslationService googleTranslationService;
	//@Resource(name = "eTranslationService")
	@Autowired
	TranslationService eTranslationService;
	
	Logger logger = LogManager.getLogger(getClass());
	
	/*
	 * Defining the available tools for translation
	 */
	private static final String googleToolName = "Google";
	private static final String eTranslationToolName = "eTranslation";
	
    //Transcribathon URL for getting the item information
    private static final String transcribathonBaseURLItems = "https://europeana.fresenia.man.poznan.pl/tp-api/items/";
    private static final String transcribathonBaseURLStories = "https://europeana.fresenia.man.poznan.pl/tp-api/stories/";

	
	//@Resource(name = "translationLanguageTool")
    @Autowired
	TranslationLanguageTool translationLanguageTool;
	
	//@Resource(name = "persistentTranslationEntityService")
    @Autowired
	PersistentTranslationEntityService persistentTranslationEntityService;
	//@Resource(name = "persistentStoryEntityService")
    @Autowired
	PersistentStoryEntityService persistentStoryEntityService;
	//@Resource(name = "persistentItemEntityService")
    @Autowired
	PersistentItemEntityService persistentItemEntityService;

	//@Resource(name = "enrichmentNerService")
    @Autowired
	EnrichmentNERService enrichmentNerService;

	
	//@Cacheable("translationResults")
	@SuppressWarnings("unused")
	@Override
	public String translate(EnrichmentTranslationRequest requestParam, boolean process) throws Exception{
		try {
			//TODO: check parameters and return other status code
			String defaultTargetLanguage = "en";
			String storyId = requestParam.getStoryId();			
			String itemId = requestParam.getItemId();			
			String originalText = requestParam.getText();
			String translationTool = requestParam.getTranslationTool();
			String type = requestParam.getType();
			String newText = requestParam.getText();
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
			
			
			Object foundStoryOrItem = fetchItemOrStory(type, newText, storyId, itemId);
			
			//TODO: the "if" part below takes into account story and item, can be improved not to hardcode it in if 
			//translate text for the whole story including all items
			if(itemId.compareTo("all")==0)
			{		
				dbStoryEntity = (StoryEntity) foundStoryOrItem;
				if(dbStoryEntity != null)
				{
					sourceLanguage = dbStoryEntity.getLanguageTranscription();
					//Reuse of StoryEntity text if original text is not given
					if(originalText == null || originalText.isEmpty())
					{
						if(type.toLowerCase().equals("transcription") && !(dbStoryEntity.getTranscriptionText() == null || dbStoryEntity.getTranscriptionText().isEmpty())) {
							
							originalText = dbStoryEntity.getTranscriptionText();
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
				dbItemEntity = (ItemEntity) foundStoryOrItem;
				
				if(dbItemEntity!=null)
				{
					sourceLanguage = dbItemEntity.getLanguage();
					//Reuse of ItemEntity text if original text is not given
					if(originalText == null || originalText.isEmpty())
					{
						if(type.toLowerCase().equals("transcription") && !(dbItemEntity.getTranscriptionText() == null || dbItemEntity.getTranscriptionText().isEmpty())) {
							
							originalText = dbItemEntity.getTranscriptionText();
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
			
			
			if(originalText == null || originalText.isEmpty() || originalText.compareToIgnoreCase("-")==0)
			{
				logger.info("The original text is empty or null");
				return "";
			}
				//throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentTranslationRequest.PARAM_TEXT, null);

			logger.info("The original text is NOT empty or null.");
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
			
			TranslationEntity tmpTranslationEntity = new TranslationEntityImpl();
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
					logger.info("Callling eTranslation translateText method.");
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

	/*
	 * This function checks if the given story or item is in the db and if not it will be fetched from the
	 * Transcribathon platform.
	 */
	private Object fetchItemOrStory(String type, String newText, String storyId, String itemId) throws Exception
	{
		ItemEntity item=null;
		StoryEntity story=null;
		
		if(itemId.compareTo("all")==0)
		{		
			//first check if the item or story is present in the db and if not fetch it from the Transcribathon platform
			story = persistentStoryEntityService.findStoryEntity(storyId);
			if(story == null )
			{
				String response = HelperFunctions.createHttpRequest(null, transcribathonBaseURLStories+storyId);
				ObjectMapper objectMapper = new ObjectMapper();		
				List<StoryEntityTranscribathonImpl> listStoryTranscribathon = objectMapper.readValue(response, new TypeReference<List<StoryEntityTranscribathonImpl>>(){});

				if(listStoryTranscribathon!=null && !listStoryTranscribathon.isEmpty())
				{
					StoryEntity [] newStories = new StoryEntity [1];
					newStories[0] = new StoryEntityImpl();
					
					if(type.compareToIgnoreCase("description")==0 && (newText!=null && !newText.isEmpty())) newStories[0].setDescription(newText);
					else newStories[0].setDescription(listStoryTranscribathon.get(0).getDcDescription());
					
					newStories[0].setLanguageTranscription(listStoryTranscribathon.get(0).getEdmLanguage());
					newStories[0].setSource("");
					newStories[0].setStoryId(storyId);
					
					if(type.compareToIgnoreCase("summary")==0 && (newText!=null && !newText.isEmpty())) newStories[0].setSummary(newText);
					else newStories[0].setSummary("");
					
					newStories[0].setTitle(listStoryTranscribathon.get(0).getDcTitle());
					
					if(type.compareToIgnoreCase("transcription")==0 && (newText!=null && !newText.isEmpty())) newStories[0].setTranscriptionText(newText);
					else newStories[0].setTranscriptionText("");
					
					enrichmentNerService.uploadStories(newStories);
					return newStories[0];
				}
				else {
					return null;
				}
			}
			else if (newText!=null && !newText.isEmpty())
			{
				StoryEntity [] newStories = new StoryEntity [1];
				newStories[0] = new StoryEntityImpl(story);
				
				if(type.compareToIgnoreCase("transcription")==0 && story.getTranscriptionText().compareTo(newText)!=0)
				{					
					newStories[0].setTranscriptionText(newText);
				}
				else if(type.compareToIgnoreCase("description")==0 && story.getDescription().compareTo(newText)!=0)
				{
					newStories[0].setDescription(newText);
				}
				else if(type.compareToIgnoreCase("summary")==0 && story.getSummary().compareTo(newText)!=0)
				{
					newStories[0].setSummary(newText);
				}
				
				enrichmentNerService.uploadStories(newStories);
				return newStories[0];
			}
			else {
				return story;
			}
		}
		else
		{
			List<ItemEntityTranscribathonImpl> listItemTranscribathon=null;
			
			story = persistentStoryEntityService.findStoryEntity(storyId);
			
			if(story == null )
			{
				String response = HelperFunctions.createHttpRequest(null, transcribathonBaseURLItems+itemId);
				ObjectMapper objectMapper = new ObjectMapper();		
				listItemTranscribathon = objectMapper.readValue(response, new TypeReference<List<ItemEntityTranscribathonImpl>>(){});

				if(listItemTranscribathon!=null && !listItemTranscribathon.isEmpty())
				{
					StoryEntity [] newStories = new StoryEntity [1];
					newStories[0] = new StoryEntityImpl(); 
					newStories[0].setDescription(listItemTranscribathon.get(0).getStoryDcDescription());
					newStories[0].setLanguageTranscription(listItemTranscribathon.get(0).getStoryEdmLanguage());
					newStories[0].setSource("");
					newStories[0].setStoryId(storyId);
					newStories[0].setSummary("");
					newStories[0].setTitle(listItemTranscribathon.get(0).getStoryDcTitle());
					//getting the transcription text
					List<Map<String,Object>> transcriptions = listItemTranscribathon.get(0).getTranscriptions();
					for (Map<String,Object> transc : transcriptions) {
						if(transc.containsKey("TextNoTags"))
						{
							newStories[0].setTranscriptionText((String)transc.get("TextNoTags"));
						}
					}
					
					enrichmentNerService.uploadStories(newStories);
				}

			}
			
			item = persistentItemEntityService.findItemEntityFromStory(storyId, itemId);
			if(item == null )
			{
				
				if(story!=null)
				{
					String response = HelperFunctions.createHttpRequest(null, transcribathonBaseURLItems+itemId);
					ObjectMapper objectMapper = new ObjectMapper();		
					listItemTranscribathon = objectMapper.readValue(response, new TypeReference<List<ItemEntityTranscribathonImpl>>(){});
					//getting the transcription text
					List<Map<String,Object>> transcriptions = listItemTranscribathon.get(0).getTranscriptions();
					for (Map<String,Object> transc : transcriptions) {
						if(transc.containsKey("TextNoTags"))
						{
							story.setTranscriptionText(story.getTranscriptionText() + " " + (String)transc.get("TextNoTags"));
						}
					}
					StoryEntity [] updateStories = new StoryEntity [1];
					updateStories[0]=story;
					enrichmentNerService.uploadStories(updateStories);

				}
				
				if(listItemTranscribathon!=null && !listItemTranscribathon.isEmpty())
				{
					ItemEntity [] newItems = new ItemEntity [1];
					newItems[0] = new ItemEntityImpl();
					
					if(type.compareToIgnoreCase("description")==0 && (newText!=null && !newText.isEmpty())) newItems[0].setDescription(newText);
					else newItems[0].setDescription(listItemTranscribathon.get(0).getDescription());
					
					newItems[0].setItemId(itemId);
					
					newItems[0].setLanguage(listItemTranscribathon.get(0).getStoryEdmLanguage());
					newItems[0].setSource("");
					newItems[0].setStoryId(storyId);
					newItems[0].setTitle(listItemTranscribathon.get(0).getTitle());
					
					if(type.compareToIgnoreCase("transcription")==0 && (newText!=null && !newText.isEmpty()))
					{
						newItems[0].setTranscriptionText(newText);
						newItems[0].setKey(newText);
					}
					else 
					{
						//getting the transcription text
						List<Map<String,Object>> transcriptions = listItemTranscribathon.get(0).getTranscriptions();
						for (Map<String,Object> transc : transcriptions) {
							if(transc.containsKey("TextNoTags"))
							{
								newItems[0].setTranscriptionText((String)transc.get("TextNoTags"));
							}
						}
						newItems[0].setKey("");
					}
						
					newItems[0].setType("");
					
					enrichmentNerService.uploadItems(newItems);
				}
				
			}
			else if (newText!=null && !newText.isEmpty())
			{
				ItemEntity [] newItems = new ItemEntity [1];
				newItems[0] = new ItemEntityImpl(item);
				
				//newText is only for the type "transcription" and not other types like "description" etc.
				if(type.compareToIgnoreCase("transcription")==0 && item.getTranscriptionText().compareTo(newText)!=0)
				{					
					newItems[0].setTranscriptionText(newText);
				}
				else if(type.compareToIgnoreCase("description")==0 && item.getDescription().compareTo(newText)!=0)
				{
					newItems[0].setDescription(newText);
				}

								
				enrichmentNerService.uploadItems(newItems);
			}
			
			//getting a new item from the db
			return persistentItemEntityService.findItemEntityFromStory(storyId, itemId);
			
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
			e.printStackTrace();
			return "Item " + i + " failed to be uploaded!";
		}
		
		return "Item " + i + " uploaded successfully!";
	}
	
}
