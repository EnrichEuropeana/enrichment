package eu.europeana.enrichment.web.service.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.common.config.I18nConstants;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.mongo.model.ItemEntityImpl;
import eu.europeana.enrichment.mongo.model.NamedEntityImpl;
import eu.europeana.enrichment.mongo.model.PositionEntityImpl;
import eu.europeana.enrichment.mongo.model.StoryEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.ner.service.NERLinkingService;
import eu.europeana.enrichment.ner.service.NERService;
import eu.europeana.enrichment.solr.commons.JavaJSONParser;
import eu.europeana.enrichment.solr.service.SolrEntityPositionsService;
import eu.europeana.enrichment.solr.service.SolrWikidataEntityService;
import eu.europeana.enrichment.translation.service.TranslationService;
import eu.europeana.enrichment.web.exception.ParamValidationException;
import eu.europeana.enrichment.web.model.EnrichmentNERRequest;
import eu.europeana.enrichment.web.service.EnrichmentNERService;

public class EnrichmentNERServiceImpl implements EnrichmentNERService{
	
	/*
	 * Loading Solr service for finding the positions of Entities in the original text
	 */
	@Resource(name = "solrEntityService")
	SolrEntityPositionsService solrEntityService;
	
	@Resource(name = "solrWikidataEntityService")
	SolrWikidataEntityService solrWikidataEntityService;

	
	/*
	 * Loading all translation services
	 */
	@Resource(name = "eTranslationService")
	TranslationService eTranslationService;

	/*
	 * Loading all NER services
	 */
	@Resource(name = "nerLinkingService")
	NERLinkingService nerLinkingService;
	@Resource(name = "stanfordNerModel3Service")
	NERService stanfordNerModel3Service;
//	@Resource(name = "stanfordNerModel4Service")
	NERService stanfordNerModel4Service;
//	@Resource(name = "stanfordNerModel7Service")
	NERService stanfordNerModel7Service;
	@Resource(name = "stanfordNerGermanModelService")
	NERService stanfordNerGermanModelService;
//	@Resource(name = "stanfordNerItalianModelService")
	NERService stanfordNerItalianModelService;
	@Resource(name = "dbpediaSpotlightService")
	NERService dbpediaSpotlightService;
	//@Resource(name = "pythonService")
	NERService pythonService;
	
	@Resource(name = "javaJSONParser")
	JavaJSONParser javaJSONParser;

	/*
	 * Defining the available tools for named entities
	 */
	private static final String stanfordNerModel3 = "Stanford_NER_model_3";
	private static final String stanfordNerModel4 = "Stanford_NER_model_4";
	private static final String stanfordNerModel7 = "Stanford_NER_model_7";
	private static final String stanfordNerModelGerman = "Stanford_NER_model_German";
	private static final String stanfordNerModelItalian = "Stanford_NER_model_Italian";
	private static final String dbpediaSpotlightName = "DBpedia_Spotlight";
	private static final String spaCyName = "spaCy";
	private static final String nltkName = "nltk";
	private static final String flairName = "flair";
	
	Logger logger = LogManager.getLogger(getClass());
	
	@Resource(name = "persistentNamedEntityService")
	PersistentNamedEntityService persistentNamedEntityService;
	@Resource(name = "persistentTranslationEntityService")
	PersistentTranslationEntityService persistentTranslationEntityService;
	@Resource(name = "persistentStoryEntityService")
	PersistentStoryEntityService persistentStoryEntityService;
	@Resource(name = "persistentItemEntityService")
	PersistentItemEntityService persistentItemEntityService;
	
	//@Cacheable("nerResults")
	@Override
	public String getEntities(EnrichmentNERRequest requestParam) throws Exception {
		
		List<String> storyItemIds = requestParam.getStoryItemIds();
		
		if(storyItemIds == null || storyItemIds.isEmpty())
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_ITEM_IDS, null);
		
		
		TreeMap<String, List<NamedEntity>> resultMap = getNamedEntities(requestParam);
		/*
		 * Output preparation
		 */
		if(resultMap == null || resultMap.isEmpty()) {
			return "";
		}
		else
		{
			prepareOutput(resultMap, storyItemIds);
			return new JSONObject(resultMap).toString();
		}
	}
	
	@Override
	public TreeMap<String, List<NamedEntity>> getNamedEntities(EnrichmentNERRequest requestParam) throws Exception {
		
		TreeMap<String, List<NamedEntity>> resultMap = new TreeMap<>();
		
		//TODO: check parameters and return other status code
		String storyId = requestParam.getStoryId();
		String tool = requestParam.getNERTool();
		List<String> linking = requestParam.getLinking();
		String translationTool = requestParam.getTranslationTool();
		String translationLanguage = requestParam.getTranslationLanguage();
		
		List<StoryEntity> tmpStoryEntity = new ArrayList<>();
		
		/*
		 * Retrieve the List<StoryEntity> from the DB
		 */
		if(storyId.compareToIgnoreCase("all")==0)
		{
			tmpStoryEntity = persistentStoryEntityService.getAllStoryEntities();
		}
		else
		{
			findStoryEntitiesFromIds(storyId,tmpStoryEntity);
		}
				
		/*
		 * Check parameters
		 */
		if(tool == null || tool.isEmpty())
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_NER_TOOL, null);
		if(translationTool == null || translationTool.isEmpty())
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_TRANSLATION_TOOL, null);
		if(translationLanguage == null || translationLanguage.isEmpty())
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_TRANSLATION_LANGUAGE, null);
		List<String> invalidLinkinParams = new ArrayList<>();
		for(String newLinkingTool : linking) {
			switch (newLinkingTool) {
			case NERLinkingService.TOOL_EUROPEANA:
			case NERLinkingService.TOOL_WIKIDATA:
				continue;
			default:
				invalidLinkinParams.add(newLinkingTool);
				break;
			}
		}
		if(invalidLinkinParams.size() > 0)
			throw new ParamValidationException(I18nConstants.INVALID_PARAM_VALUE, EnrichmentNERRequest.PARAM_LINKING, String.join(",", invalidLinkinParams));
		
		List<NamedEntity> tmpNamedEntities = new ArrayList<>();
		//check if named entities already exist
		for(StoryEntity dbStoryEntity : tmpStoryEntity) {
			tmpNamedEntities.addAll(persistentNamedEntityService.findNamedEntitiesWithAdditionalInformation(dbStoryEntity.getStoryId(), false));
		}
		//TODO: check if update is need (e.g.: linking tools)
		if(tmpNamedEntities.size() > 0) {
			//TreeMap<String, List<NamedEntity>> resultMap = new TreeMap<>();
			for(int index = tmpNamedEntities.size()-1; index >=0; index--) {
				NamedEntity tmpNamedEntity = tmpNamedEntities.get(index);
				String classificationType = tmpNamedEntity.getType();
				if(!resultMap.containsKey(classificationType))
					resultMap.put(classificationType, new ArrayList<>());
				List<NamedEntity> classificationNamedEntities = resultMap.get(classificationType);
				if(!classificationNamedEntities.stream().anyMatch(x -> x.getKey().equals(tmpNamedEntity.getKey()))) {
					classificationNamedEntities.add(tmpNamedEntity);
				}
			}
			return resultMap;
		}
		
		boolean python = false;
		NERService tmpTool;
		switch(tool){
			case stanfordNerModel3:
				tmpTool = stanfordNerModel3Service;
				break;
			case stanfordNerModel4:
				tmpTool = stanfordNerModel4Service;
				break;
			case stanfordNerModel7:
				tmpTool = stanfordNerModel7Service;
				break;
			case stanfordNerModelGerman:
				tmpTool = stanfordNerGermanModelService;
				break;
			case stanfordNerModelItalian:
				tmpTool = stanfordNerItalianModelService;
				break;
			case dbpediaSpotlightName:
				tmpTool = dbpediaSpotlightService;
				break;
			case spaCyName:
			case nltkName:
			case flairName:
				python = true;
				tmpTool = pythonService;
				break;
			default:
				throw new ParamValidationException(I18nConstants.INVALID_PARAM_VALUE, EnrichmentNERRequest.PARAM_NER_TOOL, tool);
		}			
		
		
		/*
		 * Apply named entity recognition on all story translations
		 */
		for(StoryEntity dbStoryEntity : tmpStoryEntity) {
			TranslationEntity dbTranslationEntity = persistentTranslationEntityService.
					findTranslationEntityWithStoryInformation(dbStoryEntity.getStoryId(), translationTool, translationLanguage);
			
			String text = "";
			if(dbTranslationEntity!=null) text = dbTranslationEntity.getTranslatedText();
			else text = dbStoryEntity.getStoryTranscription();
				
			if(python) {
				JSONObject jsonRequest = new JSONObject();
				jsonRequest.put("tool", tool);
				jsonRequest.put("text", text);
				text = jsonRequest.toString();
			}
			
			TreeMap<String, List<List<String>>> tmpResult = tmpTool.identifyNER(text);
			
			/*
			 * finding the positions of the entities in the original text using Solr 
			 */			
			solrEntityService.findEntitiyOffsetsInOriginalText(true, dbStoryEntity,translationLanguage,text, tmpResult);
			
			for (String classificationType : tmpResult.keySet()) {
				List<NamedEntity> tmpClassificationList = new ArrayList<>();
				/*
				 * Check if already named entities exists from the previous story item
				 */
				if(resultMap.containsKey(classificationType))
					tmpClassificationList = resultMap.get(classificationType);
				else
					resultMap.put(classificationType, tmpClassificationList);
				
				for (List<String> entityLabel : tmpResult.get(classificationType)) {
					NamedEntity dbEntity;
					/*
					 * Check if named entity with the same label was found in the
					 * previous story item or in the database
					 */
					List<NamedEntity> tmpResultNamedEntityList = tmpClassificationList.stream().
							filter(x -> x.getKey().equals(entityLabel.get(0))).collect(Collectors.toList());
					if(tmpResultNamedEntityList.size() > 0)
						dbEntity = tmpResultNamedEntityList.get(0);
					else
						dbEntity = persistentNamedEntityService.findNamedEntity(entityLabel.get(0));
					/*
					 * Create default position
					 */
					PositionEntity defaultPosition = new PositionEntityImpl();
					//defaultPosition.addOfssetPosition(-1);
					defaultPosition.setStoryEntity(dbStoryEntity);
					defaultPosition.setTranslationEntity(dbTranslationEntity);
					defaultPosition.addOfssetsTranslatedText(Integer.parseInt(entityLabel.get(1)));
					defaultPosition.addOfssetsOriginalText(Integer.parseInt(entityLabel.get(2)));
				
					
					if(dbEntity != null) {
						dbEntity.addPositionEntity(defaultPosition);
						/*
						 * Check if named entity is already at the TreeSet
						 */
						if(tmpResultNamedEntityList.size() == 0)
							tmpClassificationList.add(dbEntity);
					}
					else {
						dbEntity = new NamedEntityImpl(entityLabel.get(0));
						dbEntity.setType(classificationType);
						dbEntity.addPositionEntity(defaultPosition);
						tmpClassificationList.add(dbEntity);
					}
					
					/*
					 * Add positions to named entity
					 */
					//tmpTool.getPositions(dbEntity, dbItemEntity, dbTranslationEntity);
					
					/*
					 * Add linking information to named entity
					 */
					nerLinkingService.addLinkingInformation(dbEntity, linking, dbStoryEntity.getStoryLanguage());
				}
			}
		}
		
		
		
		/*
		 * Save and update all named entities as well as save the Wikidata entities to Solr 
		 */
		for (String key : resultMap.keySet()) {
			for (NamedEntity entity : resultMap.get(key)) {				
				//save the wikidata ids to solr
				for(String wikidataId : entity.getWikidataIds())
				{
					solrWikidataEntityService.storeWikidataFromURL(wikidataId, entity.getType());
				}
				//save the named entitiy to the mongo db
				persistentNamedEntityService.saveNamedEntity(entity);
			}
		}

		return resultMap;
	}
	
	private void prepareOutput(TreeMap<String, List<NamedEntity>> resultMap, List<String> storyItemIds) {
		for (String classificationType : resultMap.keySet()) {
			List<NamedEntity> namedEntities = resultMap.get(classificationType);
			for(int index = namedEntities.size()-1; index >= 0; index--) {
				NamedEntity tmpNamedEntity = namedEntities.get(index);
				List<PositionEntity> tmpPositions = tmpNamedEntity.getPositionEntities();
				for(int posIndex = tmpPositions.size()-1; posIndex >= 0; posIndex--) {
					PositionEntity tmpPositionEntity = tmpPositions.get(posIndex);
					String tmpStoryItemId = tmpPositionEntity.getStoryId();
					if(!storyItemIds.contains(tmpStoryItemId))
						tmpPositions.remove(posIndex);
					else {
						tmpPositionEntity.setStoryEntity(null);
						tmpPositionEntity.setStoryId(tmpStoryItemId);
						String tmpTranslationEntityKey = tmpPositionEntity.getTranslationKey();
						tmpPositionEntity.setTranslationEntity(null);
						//tmpPositionEntity.setTranslationKey(tmpTranslationEntityKey);
					}
				}
				tmpNamedEntity.setType(null);
			}
		}
	}
	
		
	private void findStoryEntitiesFromIds(String storyId, List<StoryEntity> result) throws HttpException {
				
		if((storyId == null || storyId.isEmpty()))
		{
			String params = String.join(",", EnrichmentNERRequest.PARAM_STORY_ID, EnrichmentNERRequest.PARAM_STORY_ITEM_IDS);
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, params, null);
		}
		else {
			
			result.add(persistentStoryEntityService.findStoryEntity(storyId));
			
		}
		
	}

	@Override
	public String uploadStories(StoryEntityImpl[] stories) throws HttpException {
		
		for (StoryEntityImpl story : stories) {
			if(story.getStoryId() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_ID, null);
			if(story.getStoryDescription() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_DESCRIPTION, null);
			if(story.getStoryLanguage() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_LANGUAGE, null);
			if(story.getStorySource() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_SOURCE, null);
			if(story.getStorySummary() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_SUMMARY, null);
			if(story.getStoryTitle() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_TITLE, null);
			if(story.getStoryTranscription() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_TRANSCRIPTION, null);

			persistentStoryEntityService.saveStoryEntity(story);
			
		}
		return "Done!";
	}

	@Override
	public String uploadItems(ItemEntityImpl[] items) throws HttpException {
		
		for (ItemEntityImpl item : items) {
			if(item.getStoryId() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_ID, null);
			if(item.getLanguage() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_ITEM_LANGUAGE, null);
			if(item.getTitle() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_ITEM_TITLE, null);
			if(item.getTranscription() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_ITEM_TRANSCRIPTION, null);
			if(item.getItemId() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_ITEM_ID, null);
			if(item.getType() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_ITEM_TYPE, null);


			persistentItemEntityService.saveItemEntity(item);
			
		}
		return "Done!";
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String readStoriesAndItemsFromJson (String jsonStoriesImportPath, String jsonItemsImportPath) {
		
		String result = "Error! Unsuccessfull upload of stories and items!";
		/*
		 * reading stories and items from json file, e.g. C:/java/StoriesExport.json
		 */
		
		BufferedReader brStories = null;
		BufferedReader brItems = null;
		try {
			brStories = new BufferedReader(new FileReader(jsonStoriesImportPath));
			brItems = new BufferedReader(new FileReader(jsonItemsImportPath));
			
			/*
			 * reading stories
			 */		
			
			List<Map<String, Object>> stories = null;			
			List<Map<String, Object>> retMapStories = javaJSONParser.getStoriesAndItemsFromJSON(brStories);
			for(int i=0;i<retMapStories.size();i++)				
			{
				String type = (String) retMapStories.get(i).get("type");
				if(type.compareTo("table")==0) {
					stories = (List<Map<String, Object>>) retMapStories.get(i).get("data");
				}
				
			}
			
			List<StoryEntityImpl> storyEntities = new ArrayList<StoryEntityImpl>();
			
			
			for (int i=0;i<stories.size();i++)
			{
				String storyLanguage = (String)stories.get(i).get("language");
				if(storyLanguage==null) storyLanguage="";
				if(storyLanguage.compareTo("English")==0 || storyLanguage.compareTo("German")==0)
				{
					StoryEntityImpl newStoryEntity = new StoryEntityImpl();
					newStoryEntity.setStoryTitle("");
					newStoryEntity.setStoryDescription("");
					newStoryEntity.setStoryId("");
					newStoryEntity.setStoryLanguage("");
					newStoryEntity.setStorySummary("");
					newStoryEntity.setStoryTranscription("");				
	
					
					if(stories.get(i).get("source")!=null) newStoryEntity.setStorySource((String) stories.get(i).get("source"));
					if(stories.get(i).get("title")!=null) newStoryEntity.setStoryTitle((String) stories.get(i).get("title"));
					if(stories.get(i).get("description")!=null) newStoryEntity.setStoryDescription((String) stories.get(i).get("description"));
					if(stories.get(i).get("story_id")!=null) newStoryEntity.setStoryId((String) stories.get(i).get("story_id"));
					if(stories.get(i).get("language")!=null) newStoryEntity.setStoryLanguage((String) stories.get(i).get("language"));	
					if(stories.get(i).get("summary")!=null)	newStoryEntity.setStorySummary((String) stories.get(i).get("summary"));
				
					storyEntities.add(newStoryEntity);
				}				
				
			}
			
			uploadStories(storyEntities.toArray(new StoryEntityImpl[0]));
						
			
			
			/*
			 * reading items
			 */
			List<Map<String, Object>> items = null;
			List<Map<String, Object>> retMapItems = javaJSONParser.getStoriesAndItemsFromJSON(brItems);
			for(int i=0;i<retMapItems.size();i++)				
			{
				String type = (String) retMapItems.get(i).get("type");
				if(type.compareTo("table")==0) {
					items = (List<Map<String, Object>>) retMapItems.get(i).get("data");
				}
				
			}
			
			List<ItemEntityImpl> itemEntities = new ArrayList<ItemEntityImpl>();
			for (int i=0;i<items.size();i++)
			{
				String itemLanguage = (String)items.get(i).get("language");
				if(itemLanguage==null) itemLanguage="";
				String itemTranscription = (String)items.get(i).get("transcription");				

				if(itemTranscription!=null && (itemLanguage.compareTo("English")==0 || itemLanguage.compareTo("German")==0))
				{
					
					ItemEntityImpl newItemEntity=new ItemEntityImpl();
					newItemEntity.setTitle("");
					newItemEntity.setStoryId("");
					newItemEntity.setLanguage("");
					newItemEntity.setTranscription("");	
					newItemEntity.setType("");	
					newItemEntity.setItemId("");
	
					/*
					 * adapt Item transcriptions for \r\n\n... etc
					 */
					
					
					if(items.get(i).get("title")!=null) newItemEntity.setTitle((String) items.get(i).get("title"));
					if(items.get(i).get("story_id")!=null) newItemEntity.setStoryId((String) items.get(i).get("story_id"));
					if(items.get(i).get("transcription")!=null)
					{
						String itemTrans = (String) items.get(i).get("transcription");
						newItemEntity.setTranscription(adaptTranscriptionText(itemTrans));
					}					
					if(items.get(i).get("language")!=null) newItemEntity.setLanguage((String) items.get(i).get("language"));
					if(items.get(i).get("item_id")!=null) newItemEntity.setItemId((String) items.get(i).get("item_id"));
	
					if(items.get(i).get("story_id")!=null && items.get(i).get("transcription")!=null)
					{		
						String itemStoryId = (String) items.get(i).get("story_id");
						String transcription = newItemEntity.getTranscription();
						/*
						 * adding item transcription to the story transcription
						 */
						StoryEntity dbStoryEntity = persistentStoryEntityService.findStoryEntity(itemStoryId);
						if(dbStoryEntity!=null)
						{
							String storyTranscription = dbStoryEntity.getStoryTranscription();
							storyTranscription += " " + transcription;
							dbStoryEntity.setStoryTranscription(storyTranscription);
							persistentStoryEntityService.saveStoryEntity(dbStoryEntity);
							logger.info("Item for the story_id: " + itemStoryId + "has beeen successfully uploaded to the mongo db.");
						}
					}
					
					itemEntities.add(newItemEntity);
				}
				
			}
			
			uploadItems(itemEntities.toArray(new ItemEntityImpl[0]));
			
			logger.info("Stories and Items are saved to the database from the JSON file!");
			
			result = "Stories and Items are successfully uploaded!";
						
		}
		catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			  if (brStories != null) {
				  try {
					  brStories.close();
				  } catch (IOException e) {
		    		// TODO Auto-generated catch block
		    		e.printStackTrace();
				  }
			  }
			  if (brItems != null) {
				  try {
					  brItems.close();
				  } catch (IOException e) {
		    		// TODO Auto-generated catch block
		    		e.printStackTrace();
				  }
			  }
		}
		
		return result;
		
	}
	
	private String adaptTranscriptionText (String transcriptionText)
	{
		/*
		 * 1. remove "\r" from the String
		 * 2. all "\n\n...", "\n\n   \n...", etc.   replace with \n 
		 */
		
		String dummy1 = transcriptionText.replaceAll("(\\r)+", "");
		logger.info(dummy1 + "\n\n\n\n\n\n\n\n");
		//regex: \n 1+ times followed by space 1+ times and vice versa
		String dummy2 = dummy1.replaceAll("(\\n)+( )+|( )+(\\n)+", "\n");
		String dummy3 = dummy2.replaceAll("(\\n)+", "\n");
		logger.info(dummy3);
		return dummy3;
	}
}
