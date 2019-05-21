package eu.europeana.enrichment.web.service.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.common.config.I18nConstants;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.mongo.model.DBItemEntityImpl;
import eu.europeana.enrichment.mongo.model.DBStoryEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.ner.service.NERLinkingService;
import eu.europeana.enrichment.ner.service.NERService;
import eu.europeana.enrichment.solr.service.SolrEntityPositionsService;
import eu.europeana.enrichment.translation.service.TranslationService;
import eu.europeana.enrichment.web.exception.ParamValidationException;
import eu.europeana.enrichment.web.model.EnrichmentNERRequest;
import eu.europeana.enrichment.web.service.EnrichmentNERService;
import eu.europeana.enrichment.solr.commons.JavaJSONParser;

public class EnrichmentNERServiceImpl implements EnrichmentNERService{
	
	/*
	 * Loading Solr service for finding the positions of Entities in the original text
	 */
	@Resource(name = "solrEntityService")
	SolrEntityPositionsService solrEntityService;
	
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
	@Resource(name = "stanfordNerService")
	NERService stanfordNerService;
	@Resource(name = "dbpediaSpotlightService")
	NERService dbpediaSpotlightService;
	
	@Resource(name = "javaJSONParser")
	JavaJSONParser javaJSONParser;
	
	/*
	 * Defining the available tools for named entities
	 */
	private static final String stanfordNer = "Stanford_NER";
	private static final String dbpediaSpotlightName = "DBpedia_Spotlight";
	
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
		
		String storyId = requestParam.getStoryId();
		
		TreeMap<String, List<NamedEntity>> resultMap = getNamedEntities(requestParam);
		/*
		 * Output preparation
		 */
		if(resultMap == null || resultMap.isEmpty()) {
			return "";
		}
		else
		{
			prepareOutput(resultMap, storyId);
			return new JSONObject(resultMap).toString();
		}
	}
	
	@Override
	public TreeMap<String, List<NamedEntity>> getNamedEntities(EnrichmentNERRequest requestParam) throws Exception {
		
		TreeMap<String, List<NamedEntity>> resultMap = new TreeMap<>();
		
		//TODO: check parameters and return other status code
		String storyId = requestParam.getStoryId();
		//TODO: add if description or summary or transcription or items
		
		List<String> tools = requestParam.getNERTools();
		List<String> linking = requestParam.getLinking();
		String translationTool = requestParam.getTranslationTool();
		String translationLanguage = requestParam.getTranslationLanguage();
		
		List<StoryEntity> tmpStoryEntity = new ArrayList<>();
		/*
		 * Retrieve the List<StoryEntity> from the DB
		 */
		findStoryEntitiesFromIds(storyId,tmpStoryEntity);
				
		/*
		 * Check parameters
		 */
		if(tools == null || tools.isEmpty())
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
			
		/*
		 * Apply named entity recognition on all story translations
		 */
		for(StoryEntity dbStoryEntity : tmpStoryEntity) {
			TranslationEntity dbTranslationEntity = persistentTranslationEntityService.
					findTranslationEntityWithStoryInformation(dbStoryEntity.getStoryId(), translationTool, translationLanguage);
			String text = "";
			if(dbTranslationEntity!=null) text = dbTranslationEntity.getTranslatedText();
			else text = dbStoryEntity.getTranscription();
			
			/*
			 * Get all named entities
			 */
			TreeMap<String, List<NamedEntity>> tmpResult = applyNERTools(tools, text, dbStoryEntity, dbTranslationEntity);
			
			/*
			 * finding the positions of the entities in the original text using Solr 
			 */			
			//solrEntityService.findEntitiyOffsetsInOriginalText(true, dbStoryEntity,translationLanguage,text, tmpResult);
			
			for (String classificationType : tmpResult.keySet()) {
				List<NamedEntity> tmpClassificationList = new ArrayList<>();
				/*
				 * Check if already named entities exists from the previous story item
				 */
				if(resultMap.containsKey(classificationType))
					tmpClassificationList = resultMap.get(classificationType);
				else
					resultMap.put(classificationType, tmpClassificationList);
				
				for (NamedEntity tmpNamedEntity : tmpResult.get(classificationType)) {
					NamedEntity dbEntity;
					/*
					 * Check if named entity with the same label was found in the
					 * previous story item or in the database
					 */
					List<NamedEntity> tmpResultNamedEntityList = tmpClassificationList.stream().
							filter(x -> x.getKey().equals(tmpNamedEntity.getKey())).collect(Collectors.toList());
					if(tmpResultNamedEntityList.size() > 0)
						dbEntity = tmpResultNamedEntityList.get(0);
					else
						dbEntity = persistentNamedEntityService.findNamedEntity(tmpNamedEntity.getKey());
				
					
					if(dbEntity != null) {
						dbEntity.addPositionEntity(tmpNamedEntity.getPositionEntities().get(0));
						for(int dbpediaIndex = 0; dbpediaIndex < tmpNamedEntity.getDBpediaIds().size(); dbpediaIndex++) {
							int tmpIndex = dbpediaIndex;
							boolean found = dbEntity.getDBpediaIds().stream().anyMatch(x -> x.equals(tmpNamedEntity.getDBpediaIds().get(tmpIndex)));
							if(!found){
								dbEntity.addDBpediaId(tmpNamedEntity.getDBpediaIds().get(tmpIndex));
							}
						}
						/*
						 * Check if named entity is already at the TreeSet
						 */
						if(tmpResultNamedEntityList.size() == 0)
							tmpClassificationList.add(dbEntity);
					}
					else {
						dbEntity = tmpNamedEntity; //(DBNamedEntityImpl) tmpNamedEntity;
						tmpClassificationList.add(dbEntity);
					}
					
					
					/*
					 * Add linking information to named entity
					 */
					nerLinkingService.addLinkingInformation(dbEntity, linking, dbStoryEntity.getLanguage());
				}
			}
		}
		
		/*
		 * Save and update all named entities
		 */
		for (String key : resultMap.keySet()) {
			for (NamedEntity entity : resultMap.get(key)) {
				persistentNamedEntityService.saveNamedEntity(entity);
			}
		}

		return resultMap;
	}
	
	private TreeMap<String, List<NamedEntity>> applyNERTools(List<String> tools, String text, 
			StoryEntity dbStoryEntity, TranslationEntity dbTranslationEntity) throws ParamValidationException{
		TreeMap<String, List<NamedEntity>> mapResult = new TreeMap<>();
		for(String tool_string : tools) {
			NERService tmpTool;
			switch(tool_string){
				case stanfordNer:
					tmpTool = stanfordNerService;
					break;
				case dbpediaSpotlightName:
					tmpTool = dbpediaSpotlightService;
					break;
				default:
					throw new ParamValidationException(I18nConstants.INVALID_PARAM_VALUE, EnrichmentNERRequest.PARAM_NER_TOOL, tool_string);
			}
			TreeMap<String, List<NamedEntity>> tmpResult = tmpTool.identifyNER(text);
			//comparison and update of previous values
			mapResult = mapResultCombination(mapResult, tmpResult, dbStoryEntity, dbTranslationEntity);
			
		}
		return mapResult;
	}
	
	private TreeMap<String, List<NamedEntity>> mapResultCombination(TreeMap<String, List<NamedEntity>> mapPreResult, TreeMap<String, List<NamedEntity>> mapCurrentResult,
			StoryEntity dbStoryEntity, TranslationEntity dbTranslationEntity) {
		TreeMap<String, List<NamedEntity>> combinedResult = new TreeMap<>();
		if(mapPreResult.size() == 0)
		{
			for(Map.Entry<String, List<NamedEntity>> categoryList : mapCurrentResult.entrySet()) {
				for(NamedEntity entity : categoryList.getValue()) {
					PositionEntity pos = entity.getPositionEntities().get(0);
					pos.setStoryId(dbStoryEntity.getStoryId());
					if(dbTranslationEntity != null)
						pos.setTranslationKey(dbTranslationEntity.getKey());
				}
			}
			return mapCurrentResult;
		}
		
		for(Map.Entry<String, List<NamedEntity>> tmpCategoryMap : mapCurrentResult.entrySet()) {
			String categoryKey = tmpCategoryMap.getKey();
			List<NamedEntity> tmpCategoryValues = tmpCategoryMap.getValue();
			if(!mapPreResult.containsKey(categoryKey)) {
				for(NamedEntity entity : tmpCategoryValues) {
					// Only first PositionEntity is set, because NER tools create new NamedEntities
					PositionEntity pos = entity.getPositionEntities().get(0);
					pos.setStoryId(dbStoryEntity.getStoryId());
					if(dbTranslationEntity != null)
						pos.setTranslationKey(dbTranslationEntity.getKey());
				}
				combinedResult.put(categoryKey, tmpCategoryValues);
				continue;
			}
			combinedResult.put(categoryKey, new ArrayList<>());
			List<NamedEntity> endResultCategoryValues = mapPreResult.get(categoryKey);
			
			for(int index = 0; index < tmpCategoryValues.size(); index++) {
				NamedEntity tmpNamedEntity = tmpCategoryValues.get(index);
				for(int resultIndex = 0; resultIndex < endResultCategoryValues.size(); resultIndex++) {
					if(endResultCategoryValues.get(resultIndex).getKey().equals(tmpNamedEntity.getKey())) {
						NamedEntity resultNamedEntity = endResultCategoryValues.get(resultIndex);
						PositionEntity pos = resultNamedEntity.getPositionEntities().get(0);
						pos.setStoryId(dbStoryEntity.getStoryId());
						if(dbTranslationEntity != null)
							pos.setTranslationKey(dbTranslationEntity.getKey());
						List<Integer> tmpOffsets = tmpNamedEntity.getPositionEntities().get(0).getOffsetsTranslatedText();
						List<Integer> resultOffsets = pos.getOffsetsTranslatedText();
						resultOffsets.addAll(tmpOffsets);
						Set<Integer> uniqueList = new HashSet<Integer>(resultOffsets);
						pos.setOffsetsTranslatedText(new ArrayList<Integer>(uniqueList));
						combinedResult.get(categoryKey).add(resultNamedEntity);
						break;
					}
				}
			}
		}
		return combinedResult;
	}
	
	private void prepareOutput(TreeMap<String, List<NamedEntity>> resultMap, String storyId) {
		for (String classificationType : resultMap.keySet()) {
			List<NamedEntity> namedEntities = resultMap.get(classificationType);
			for(int index = namedEntities.size()-1; index >= 0; index--) {
				NamedEntity tmpNamedEntity = namedEntities.get(index);
				List<PositionEntity> tmpPositions = tmpNamedEntity.getPositionEntities();
				for(int posIndex = tmpPositions.size()-1; posIndex >= 0; posIndex--) {
					PositionEntity tmpPositionEntity = tmpPositions.get(posIndex);
					String tmpStoryId = tmpPositionEntity.getStoryId();
					if(!storyId.equals(tmpStoryId))
						tmpPositions.remove(posIndex);
					else {
						tmpPositionEntity.setStoryEntity(null);
						tmpPositionEntity.setStoryId(tmpStoryId);
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
				
		if(storyId == null || storyId.isEmpty())
		{
			String params = String.join(",", EnrichmentNERRequest.PARAM_STORY_ID, EnrichmentNERRequest.PARAM_STORY_ITEM_IDS);
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, params, null);
		}
		else {
			
			result.add(persistentStoryEntityService.findStoryEntity(storyId));
			
		}
		
	}

	@Override
	public String uploadStories(StoryEntity[] stories) throws HttpException {
		
		for (StoryEntity story : stories) {
			if(story.getStoryId() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_ID, null);
			if(story.getDescription() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_DESCRIPTION, null);
			if(story.getLanguage() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_LANGUAGE, null);
			if(story.getSource() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_SOURCE, null);
			if(story.getSummary() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_SUMMARY, null);
			if(story.getTitle() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_TITLE, null);
			if(story.getTranscription() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_TRANSCRIPTION, null);

			persistentStoryEntityService.saveStoryEntity(story);
			
		}
		return "Done!";
	}

	@Override
	public String uploadItems(ItemEntity[] items) throws HttpException {
		
		for (ItemEntity item : items) {
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

	@Override
	public String readStoriesAndItemsFromJson(String jsonStoriesImportPath, String jsonItemsImportPath) {
		
		/*
		 * reading stories and items from json
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
			
			List<DBStoryEntityImpl> storyEntities = new ArrayList<DBStoryEntityImpl>();
			
			
			for (int i=0;i<stories.size();i++)
			{
				String storyLanguage = (String)stories.get(i).get("language");
				if(storyLanguage==null) storyLanguage="";
//				if(storyLanguage.compareTo("English")==0 || storyLanguage.compareTo("German")==0)
//				{
					DBStoryEntityImpl newStoryEntity = new DBStoryEntityImpl();
					newStoryEntity.setTitle("");
					newStoryEntity.setDescription("");
					newStoryEntity.setStoryId("");
					newStoryEntity.setLanguage("");
					newStoryEntity.setSummary("");
					newStoryEntity.setTranscription("");				
	
					
					if(stories.get(i).get("source")!=null) newStoryEntity.setSource((String) stories.get(i).get("source"));
					if(stories.get(i).get("title")!=null) newStoryEntity.setTitle((String) stories.get(i).get("title"));
					if(stories.get(i).get("description")!=null) newStoryEntity.setDescription((String) stories.get(i).get("description"));
					if(stories.get(i).get("story_id")!=null) newStoryEntity.setStoryId((String) stories.get(i).get("story_id"));
					if(stories.get(i).get("language")!=null) newStoryEntity.setLanguage((String) stories.get(i).get("language"));	
					if(stories.get(i).get("summary")!=null)	newStoryEntity.setSummary((String) stories.get(i).get("summary"));
				
					storyEntities.add(newStoryEntity);
//				}				
				
			}
			
			String uploadStoriesStatus = uploadStories(storyEntities.toArray(new DBStoryEntityImpl[0]));
			
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
			
			List<DBItemEntityImpl> itemEntities = new ArrayList<DBItemEntityImpl>();
			for (int i=0;i<items.size();i++)
			{
				String itemLanguage = (String)items.get(i).get("language");
				if(itemLanguage==null) itemLanguage="";
				String itemTranscription = (String)items.get(i).get("transcription");				

//				if(itemTranscription!=null && (itemLanguage.compareTo("English")==0 || itemLanguage.compareTo("German")==0))
//				{
					
					DBItemEntityImpl newItemEntity=new DBItemEntityImpl();
					newItemEntity.setTitle("");
					newItemEntity.setStoryId("");
					newItemEntity.setLanguage("");
					newItemEntity.setTranscription("");	
					newItemEntity.setType("");	
					newItemEntity.setItemId("");
	
					
					if(items.get(i).get("title")!=null) newItemEntity.setTitle((String) items.get(i).get("title"));
					if(items.get(i).get("story_id")!=null) newItemEntity.setStoryId((String) items.get(i).get("story_id"));
					if(items.get(i).get("transcription")!=null) newItemEntity.setTranscription((String) items.get(i).get("transcription"));
					if(items.get(i).get("language")!=null) newItemEntity.setLanguage((String) items.get(i).get("language"));
					if(items.get(i).get("item_id")!=null) newItemEntity.setItemId((String) items.get(i).get("item_id"));
	
					if(items.get(i).get("story_id")!=null && items.get(i).get("transcription")!=null)
					{		
						String itemStoryId = (String) items.get(i).get("story_id");
						String transcription = (String) items.get(i).get("transcription");
						/*
						 * adding item transcription to the story transcription
						 */
						StoryEntity dbStoryEntity = persistentStoryEntityService.findStoryEntity(itemStoryId);
						if(dbStoryEntity!=null)
						{
							String storyTranscription = dbStoryEntity.getTranscription();
							storyTranscription += " " + transcription;
							dbStoryEntity.setTranscription(storyTranscription);
							persistentStoryEntityService.saveStoryEntity(dbStoryEntity);
						}
					}
					
					itemEntities.add(newItemEntity);
//				}
				
			}
			
			String uploadItemsStatus = uploadItems(itemEntities.toArray(new DBItemEntityImpl[0]));
			
			logger.info("Stories and Items are saved to the database from the JSON file!");
			return "Done!";
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
			  //TODO: throw httpexception
			  return "Fail";
		}	
		
	}
}
