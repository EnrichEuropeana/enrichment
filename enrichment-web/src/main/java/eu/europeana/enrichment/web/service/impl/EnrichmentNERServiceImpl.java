package eu.europeana.enrichment.web.service.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Whitelist;

import com.neovisionaries.i18n.LanguageCode;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.NamedEntityAnnotation;
import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.model.impl.NamedEntityAnnotationCollection;
import eu.europeana.enrichment.model.impl.NamedEntityAnnotationImpl;
import eu.europeana.enrichment.mongo.model.DBItemEntityImpl;
import eu.europeana.enrichment.mongo.model.DBStoryEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityAnnotationService;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.ner.enumeration.NERClassification;
import eu.europeana.enrichment.ner.service.NERLinkingService;
import eu.europeana.enrichment.ner.service.NERService;
import eu.europeana.enrichment.solr.commons.JavaJSONParser;
import eu.europeana.enrichment.solr.service.SolrEntityPositionsService;
import eu.europeana.enrichment.solr.service.SolrWikidataEntityService;
import eu.europeana.enrichment.translation.service.TranslationService;
import eu.europeana.enrichment.web.common.config.I18nConstants;
import eu.europeana.enrichment.web.commons.StoryWikidataEntitySerializer;
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
	

	@Resource(name = "storyEntitySerializer")
	StoryWikidataEntitySerializer storyEntitySerializer;
	
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
	private static final Map<String, String> languageCodeMap;
    static {
        Map<String, String> aMap = new HashMap<String, String>();
        aMap.put("German", "de");
        aMap.put("English", "en");
        aMap.put("Italian", "it");
        aMap.put("Romanian", "ro");
        aMap.put("French", "fr");
        languageCodeMap = Collections.unmodifiableMap(aMap);
    }
	
	Logger logger = LogManager.getLogger(getClass());
	
	@Resource(name = "persistentNamedEntityService")
	PersistentNamedEntityService persistentNamedEntityService;
	@Resource(name = "persistentTranslationEntityService")
	PersistentTranslationEntityService persistentTranslationEntityService;
	@Resource(name = "persistentStoryEntityService")
	PersistentStoryEntityService persistentStoryEntityService;
	@Resource(name = "persistentItemEntityService")
	PersistentItemEntityService persistentItemEntityService;
	@Resource(name = "persistentNamedEntityAnnotationService")
	PersistentNamedEntityAnnotationService persistentNamedEntityAnnotationService;	
	
	//@Cacheable("nerResults")
	@Override
	public String getEntities(EnrichmentNERRequest requestParam, boolean process) throws Exception {
		
		String storyId = requestParam.getStoryId();
		String itemId = requestParam.getItemId();
		
		TreeMap<String, List<NamedEntity>> resultMap = getNamedEntities(requestParam, process);
		/*
		 * Output preparation
		 */
		if(resultMap == null || resultMap.isEmpty()) {
			return "{\"info\" : \"No found NamedEntity-s for the given input parameters!\"}";
		}
		else
		{
			prepareOutput(resultMap, storyId, itemId);
			return new JSONObject(resultMap).toString();
		}
	}
	
	@Override
	public TreeMap<String, List<NamedEntity>> getNamedEntities(EnrichmentNERRequest requestParam, boolean process) throws Exception {
		
		TreeMap<String, List<NamedEntity>> resultMap = new TreeMap<>();
		
		//TODO: check parameters and return other status code
		String storyId = requestParam.getStoryId();
		String itemId = requestParam.getItemId();
		String type = requestParam.getProperty();
		if(type == null || type.isEmpty())
			type = "transcription";
		else if(!(type.equals("summary") || type.equals("description") || type.equals("transcription")))
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_ITEM_TYPE, null);
		//TODO: add if description or summary or transcription or items
		boolean original = requestParam.getOriginal();		
		List<String> tools = new ArrayList<String>();
		tools.addAll(requestParam.getNerTools());
		List<String> linking = requestParam.getLinking();
		String translationTool = requestParam.getTranslationTool();
		String translationLanguage = "en";
		
		if(storyId == null || storyId.isEmpty())
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_ID, null);
		if(itemId == null || itemId.isEmpty())
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_ITEM_ID, null);
		
			
		/*
		 * Check parameters
		 */
		if(tools == null || tools.isEmpty())
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_NER_TOOL, null);
		if(!original && (translationTool == null || translationTool.isEmpty()))
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_TRANSLATION_TOOL, null);
		if(!original && (translationLanguage == null || translationLanguage.isEmpty()))
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
		
		
		/*
		 * This part from here down only executes for POST requests.
		 * Here in case of items we run the analysis for all types of the NER field (summary, description, or transcription)
		 */
		List<NamedEntity> tmpNamedEntities = new ArrayList<>();
		
		List<String> allNERFieldTypes = new ArrayList<String>();
		if(itemId == "all")
		{
			allNERFieldTypes.add(type);
			//Named entities should also get the type where it was found
			tmpNamedEntities.addAll(persistentNamedEntityService.findNamedEntitiesWithAdditionalInformation(storyId, itemId, type, false));

		}
		else
		{
			allNERFieldTypes.add("transcription");
			allNERFieldTypes.add("description");
			allNERFieldTypes.add("summary");
			//Named entities should also get the type where it was found
			tmpNamedEntities.addAll(persistentNamedEntityService.findNamedEntitiesWithAdditionalInformation(storyId, itemId, false));

		}
	
			
		int numberNERTools = tools.size();
		//check if for the given story/item the NER anaylysis for all required NER tools is already pursued, returned is the number of 
		//ner tools for which the analysis is done
		int numberNERToolsFound = checkAllNerToolsAlreadyCompleted(tools, tmpNamedEntities);
		
		//TODO: check if update is need (e.g.: linking tools)
		if(tmpNamedEntities.size() > 0 && numberNERToolsFound > 0) {
			//TreeMap<String, List<NamedEntity>> resultMap = new TreeMap<>();
			for(int index = tmpNamedEntities.size()-1; index >=0; index--) {
				NamedEntity tmpNamedEntity = tmpNamedEntities.get(index);
				String classificationType = tmpNamedEntity.getType();
				if(!resultMap.containsKey(classificationType))
					resultMap.put(classificationType, new ArrayList<>());
				List<NamedEntity> classificationNamedEntities = resultMap.get(classificationType);
				if(!classificationNamedEntities.stream().anyMatch(x -> x.getLabel().equals(tmpNamedEntity.getLabel()))) {
					classificationNamedEntities.add(tmpNamedEntity);
				}
			}
		}
		
		if(!process || numberNERToolsFound==numberNERTools) {
			//TODO: throw exception 404
			//throw new HttpException("");
			return resultMap;
		}

		
		
		for(String typeNERField : allNERFieldTypes)
		{
			type=typeNERField;
			
			TranslationEntity dbTranslationEntity = null;
			if(!original) {
				dbTranslationEntity = persistentTranslationEntityService.
						findTranslationEntityWithAditionalInformation(storyId, itemId, translationTool, translationLanguage, type);
				
				if(dbTranslationEntity==null) continue;
			}
		
			
			String textForNer = "";
			String originalLanguage = null;
			StoryEntity tmpStoryEntity = null;
			ItemEntity tmpItemEntity = null;
			
			if(itemId.compareTo("all")==0)
			{		
				tmpStoryEntity = persistentStoryEntityService.findStoryEntity(storyId);
				if(tmpStoryEntity == null )
					throw new ParamValidationException(I18nConstants.RESOURCE_NOT_FOUND, EnrichmentNERRequest.PARAM_STORY_ID, null);
			}
			else
			{
				tmpItemEntity = persistentItemEntityService.findItemEntityFromStory(storyId, itemId);
				if(tmpItemEntity == null )
					throw new ParamValidationException(I18nConstants.RESOURCE_NOT_FOUND, EnrichmentNERRequest.PARAM_ITEM_ID, null);
			}
			

			/*
			 * Getting the specified story field to do the NER (summary, description, or traslationText) using Java reflection
			 * When nothing is specified, the traslationText field is taken.
			 */
			if(dbTranslationEntity!=null) textForNer = dbTranslationEntity.getTranslatedText();
			else if(type.toLowerCase().equals("summary") && dbTranslationEntity==null)
			{
				if(tmpStoryEntity!=null) textForNer = tmpStoryEntity.getSummary();					
			}
			else if(type.toLowerCase().equals("description") && dbTranslationEntity==null) 
			{
				if(tmpStoryEntity!=null) textForNer = tmpStoryEntity.getDescription();
				else textForNer = tmpItemEntity.getDescription();
			}
			else
			{
				if(tmpStoryEntity!=null) textForNer = tmpStoryEntity.getTranscriptionText();
				else textForNer = tmpItemEntity.getTranscriptionText();
			}
			
			if(tmpStoryEntity!=null) originalLanguage=tmpStoryEntity.getLanguage();
			else originalLanguage=tmpItemEntity.getLanguage();

			
			//sometimes some fields for NER can be empty for items which causes problems in the method applyNERTools
			if(textForNer!=null && !textForNer.isEmpty())
			{
				/*
				 * Get all named entities
				 */
				
				for(String NERTool : tools)
				{
					/*
					 * Here for each ner tool the analysis is done separately because different tools may find
					 * the same entities on different positions in the text and we would like to separate those results,
					 * otherwise all positions of the entities would be in the same list and it cannot be clear which positions
					 * belong to which ner tool analyser.
					 */
					List<String> newNERTools = new ArrayList<String>();
					newNERTools.add(NERTool);
					
					TreeMap<String, List<NamedEntity>> tmpResult = applyNERTools(newNERTools, textForNer, type, storyId, itemId, originalLanguage, dbTranslationEntity);
					
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
									filter(x -> x.getLabel().equals(tmpNamedEntity.getLabel())).collect(Collectors.toList());
							if(tmpResultNamedEntityList.size() > 0)
								dbEntity = tmpResultNamedEntityList.get(0);
							else
								dbEntity = persistentNamedEntityService.findNamedEntity(tmpNamedEntity.getLabel());
						
							
							if(dbEntity != null) {
								//check if there are new position entities to be added
								int addPositionEntitiesCheck = 1;
								for(PositionEntity pe : dbEntity.getPositionEntities())
								{
									if(tmpNamedEntity.getPositionEntities().get(0).equals(pe))
									{
										addPositionEntitiesCheck = 0;
										/*
										 * only if all fields of the position entities are the same including the positions in the translated text
										 * 2 or more ner tools are added to the same position entity
										 */
										pe.getNERTools().add(NERTool);
										break;
									}
								}
								if(addPositionEntitiesCheck==1) dbEntity.addPositionEntity(tmpNamedEntity.getPositionEntities().get(0));
								
								
								
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
								dbEntity = tmpNamedEntity;
								
								tmpClassificationList.add(dbEntity);
							}
							
							
							/*
							 * Add linking information to named entity
							 */
							nerLinkingService.addLinkingInformation(dbEntity, linking, originalLanguage);
						}
					}
				}
			}
		}
		
		/*
		 * Save and update all named entities
		 */
		for (String key : resultMap.keySet()) {
			List<NamedEntity> entities = resultMap.get(key);
			for (NamedEntity entity : entities) {
				//save the wikidata ids to solr
				//for(String wikidataId : entity.getPreferredWikidataIds())
				for(String wikidataId : entity.getPreferredWikidataIds())
				{
					solrWikidataEntityService.storeWikidataFromURL(wikidataId, entity.getType());
				}
				//save the NamedEntity to mongo db
				persistentNamedEntityService.saveNamedEntity(entity);
			}
		}

		return resultMap;
	}
	
	/*
	 * This function returns the number of NER-tools that are present in the found NamedEntities  
	 */
	private int checkAllNerToolsAlreadyCompleted (List<String> tools, List<NamedEntity> tmpNamedEntities)
	{		
		Set<String> nerToolsForStoryOrItem = new HashSet<String>();
		for (NamedEntity ne : tmpNamedEntities)
		{
			for(PositionEntity pe : ne.getPositionEntities())
			{
				
				nerToolsForStoryOrItem.addAll(pe.getNERTools());
				
			}
			
		}
				
		List<String> toolsToRemove = new ArrayList<String>();
		for (String nerToolsNew : tools)
		{
			if(nerToolsForStoryOrItem.contains(nerToolsNew)) toolsToRemove.add(nerToolsNew);
		}
		
		tools.removeAll(toolsToRemove);
		
		return toolsToRemove.size();
	}
	
	private TreeMap<String, List<NamedEntity>> applyNERTools(List<String> tools, String text, String fieldUsedForNER,
			String storyId, String itemId, String originalLanguage, TranslationEntity dbTranslationEntity) throws ParamValidationException, IOException{
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
			
			String language = originalLanguage;
			if(dbTranslationEntity != null)
				language = dbTranslationEntity.getLanguage();
			adaptNERServiceEndpointBasedOnLanguage(tmpTool, language);
			
			TreeMap<String, List<NamedEntity>> tmpResult = tmpTool.identifyNER(text);
			//comparison and update of previous values
			mapResult = mapResultCombination(mapResult, tmpResult, storyId, itemId, fieldUsedForNER, dbTranslationEntity, tool_string);
			
		}
		return mapResult;
	}
	
	private TreeMap<String, List<NamedEntity>> mapResultCombination(TreeMap<String, List<NamedEntity>> mapPreResult, TreeMap<String, List<NamedEntity>> mapCurrentResult,
			String storyId, String itemId, String fieldUsedForNER, TranslationEntity dbTranslationEntity, String tool_string) {
		TreeMap<String, List<NamedEntity>> combinedResult = new TreeMap<>();
		if(mapPreResult.size() == 0)
		{
			for(Map.Entry<String, List<NamedEntity>> categoryList : mapCurrentResult.entrySet()) {
				for(NamedEntity entity : categoryList.getValue()) {
					PositionEntity pos = entity.getPositionEntities().get(0);
					pos.setStoryId(storyId);
					pos.setItemId(itemId);
					pos.setFieldUsedForNER(fieldUsedForNER);
					pos.addNERTool(tool_string);
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
					pos.setStoryId(storyId);
					pos.setItemId(itemId);
					pos.setFieldUsedForNER(fieldUsedForNER);
					pos.addNERTool(tool_string);
					if(dbTranslationEntity != null)
						pos.setTranslationKey(dbTranslationEntity.getKey());
				}
				combinedResult.put(categoryKey, tmpCategoryValues);
				continue;
			}
			combinedResult.put(categoryKey, new ArrayList<>());
			List<NamedEntity> endResultCategoryValues = mapPreResult.get(categoryKey);
			//add all NamedEntities found in the previous result
			combinedResult.get(categoryKey).addAll(endResultCategoryValues);
			
			for(int index = 0; index < tmpCategoryValues.size(); index++) {
				NamedEntity tmpNamedEntity = tmpCategoryValues.get(index);
				boolean foundNamedEntityInPreviousResults = false;
				for(int resultIndex = 0; resultIndex < endResultCategoryValues.size(); resultIndex++) {
					if(endResultCategoryValues.get(resultIndex).getLabel().equals(tmpNamedEntity.getLabel())) {
						NamedEntity resultNamedEntity = endResultCategoryValues.get(resultIndex);
						PositionEntity pos = resultNamedEntity.getPositionEntities().get(0);
						pos.setStoryId(storyId);
						pos.setItemId(itemId);
						pos.setFieldUsedForNER(fieldUsedForNER);
						pos.addNERTool(tool_string);
						if(dbTranslationEntity != null)
							pos.setTranslationKey(dbTranslationEntity.getKey());
						
						for(String dbpediaId : tmpNamedEntity.getDBpediaIds())
						{
							resultNamedEntity.addDBpediaId(dbpediaId);
						}
						
						List<Integer> tmpOffsets = tmpNamedEntity.getPositionEntities().get(0).getOffsetsTranslatedText();
						List<Integer> resultOffsets = pos.getOffsetsTranslatedText();
						resultOffsets.addAll(tmpOffsets);
						Set<Integer> uniqueList = new HashSet<Integer>(resultOffsets);
						pos.setOffsetsTranslatedText(new ArrayList<Integer>(uniqueList));
						//updated new NamedEntity added to the list
						//combinedResult.get(categoryKey).add(resultNamedEntity);
						foundNamedEntityInPreviousResults = true;
						break;
					}
				}
				if(!foundNamedEntityInPreviousResults)
				{
					//add new entity
					// Only first PositionEntity is set, because NER tools create new NamedEntities
					PositionEntity pos = tmpNamedEntity.getPositionEntities().get(0);
					pos.setStoryId(storyId);
					pos.setItemId(itemId);
					pos.setFieldUsedForNER(fieldUsedForNER);
					pos.addNERTool(tool_string);
					if(dbTranslationEntity != null) pos.setTranslationKey(dbTranslationEntity.getKey());
					combinedResult.get(categoryKey).add(tmpNamedEntity);
				}
			}
		}
		return combinedResult;
	}
	
	private void prepareOutput(TreeMap<String, List<NamedEntity>> resultMap, String storyId, String itemId) {
		List<String> classificationTypeForRemoval = new ArrayList<>();
		for (String classificationType : resultMap.keySet()) {
			if(!(classificationType.equals(NERClassification.AGENT.toString()) || 
					classificationType.equals(NERClassification.PLACE.toString())))
			{
				classificationTypeForRemoval.add(classificationType);
				continue;
			}
			List<NamedEntity> namedEntities = resultMap.get(classificationType);
			for(int index = namedEntities.size()-1; index >= 0; index--) {
				NamedEntity tmpNamedEntity = namedEntities.get(index);
				//set all empty values to be null
				if(tmpNamedEntity.getDBpediaIds().isEmpty()) tmpNamedEntity.setDBpediaIds(null);
				if(tmpNamedEntity.getDbpediaWikidataIds().isEmpty()) tmpNamedEntity.setDbpediaWikidataIds(null);
				if(tmpNamedEntity.getEuropeanaIds().isEmpty()) tmpNamedEntity.setEuropeanaIds(null);
				if(tmpNamedEntity.getPositionEntities().isEmpty()) tmpNamedEntity.setPositionEntities(null);
				if(tmpNamedEntity.getPreferredWikidataIds().isEmpty()) tmpNamedEntity.setPreferredWikidataIds(null);
				if(tmpNamedEntity.getWikidataIds().isEmpty()) tmpNamedEntity.setWikidataIds(null);				
				
				List<PositionEntity> tmpPositions = tmpNamedEntity.getPositionEntities();
				for(int posIndex = tmpPositions.size()-1; posIndex >= 0; posIndex--) {
					PositionEntity tmpPositionEntity = tmpPositions.get(posIndex);
					String tmpStoryId = tmpPositionEntity.getStoryId();
					String tmpItemId = tmpPositionEntity.getItemId();
					List<String> tmpNERTools = tmpPositionEntity.getNERTools();
					if(storyId.compareTo(tmpStoryId)!=0 || itemId.compareTo(tmpItemId)!=0)
						tmpPositions.remove(posIndex);
					else {
						tmpPositionEntity.setStoryEntity(null);
						tmpPositionEntity.setStoryId(tmpStoryId);
						tmpPositionEntity.setItemEntity(null);
						tmpPositionEntity.setItemId(tmpItemId);
						String tmpTranslationEntityKey = tmpPositionEntity.getTranslationKey();
						tmpPositionEntity.setTranslationEntity(null);
						tmpPositionEntity.setTranslationKey(null);
						
						//set all empty values to be null
						if(tmpPositionEntity.getOffsetsOriginalText().isEmpty()) tmpPositionEntity.setOffsetsOriginalText(null);
						if(tmpPositionEntity.getOffsetsTranslatedText().isEmpty()) tmpPositionEntity.setOffsetsTranslatedText(null);
						if(tmpPositionEntity.getFieldUsedForNER().isEmpty()) tmpPositionEntity.setFieldUsedForNER(null);
						if(tmpPositionEntity.getNERTools().isEmpty()) tmpPositionEntity.setNERTools(null);
					}
				}
				tmpNamedEntity.setType(null);
			}
		}
		
		for(String type : classificationTypeForRemoval) {
			resultMap.remove(type);
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
//			if(story.getTranscription() == null)
//				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_TRANSCRIPTION, null);
			
			//some stories have html markup in the description 
			String storyDescriptionText = parseHTMLWithJsoup(story.getDescription());
			story.setDescription(storyDescriptionText);
			
			//comparing the new and the already existing story and deleting old NamedEntities, TranslationEntities and NamedEntityAnnotations if there are changes
			StoryEntity dbStoryEntity = persistentStoryEntityService.findStoryEntity(story.getStoryId());
			if (dbStoryEntity!=null)
			{
				boolean someStoryPartChanged = false;
				if(dbStoryEntity.getDescription().compareTo(story.getDescription())!=0)
				{
					someStoryPartChanged=true;
					persistentNamedEntityService.deleteListNamedEntity(story.getStoryId(), "all" , "description");
					persistentTranslationEntityService.deleteTranslationEntity(story.getStoryId(), "all", "description");
				}
				if(dbStoryEntity.getSummary().compareTo(story.getSummary())!=0)
				{
					someStoryPartChanged=true;
					persistentNamedEntityService.deleteListNamedEntity(story.getStoryId(), "all" , "summary");
					persistentTranslationEntityService.deleteTranslationEntity(story.getStoryId(), "all", "summary");
				}
				if(dbStoryEntity.getTranscriptionText().compareTo(story.getTranscriptionText())!=0)
				{
					someStoryPartChanged=true;
					persistentNamedEntityService.deleteListNamedEntity(story.getStoryId(), "all" , "transcription");
					persistentTranslationEntityService.deleteTranslationEntity(story.getStoryId(), "all", "transcription");
				}		
				
				if(someStoryPartChanged)
				{
					persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(story.getStoryId(), "all");
				}
			}
			
			persistentStoryEntityService.saveStoryEntity(story);
			
		}
		return "{\"info\": \"Done successfully!\"}";
	}

	@Override
	public String uploadItems(ItemEntity[] items) throws HttpException, NoSuchAlgorithmException, UnsupportedEncodingException {
		
		for (ItemEntity item : items) {
			if(item.getStoryId() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_ID, null);
			if(item.getLanguage() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_ITEM_LANGUAGE, null);
			if(item.getTitle() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_ITEM_TITLE, null);
			if(item.getTranscriptionText() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_ITEM_TRANSCRIPTION, null);
			if(item.getItemId() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_ITEM_ID, null);
			if(item.getDescription() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_ITEM_DESCRIPTION, null);

			
			
			//remove html markup from the transcription and decription texts
			String itemTranscriptionText = parseHTMLWithJsoup(item.getTranscriptionText());
			item.setTranscriptionText(itemTranscriptionText);
			
			String itemDescriptionText = parseHTMLWithJsoup(item.getDescription());
			item.setDescription(itemDescriptionText);
			
			//comparing the new and the already existing item and deleting old NamedEntities if there are changes
			ItemEntity dbItemEntity = persistentItemEntityService.findItemEntity(item.getItemId());			
			if (dbItemEntity!=null)
			{
				boolean someItemPartChanged = false;
				if(dbItemEntity.getDescription().compareTo(item.getDescription())!=0)
				{
					someItemPartChanged = true;
					persistentNamedEntityService.deleteListNamedEntity(item.getStoryId(), item.getItemId() , "description");
					persistentTranslationEntityService.deleteTranslationEntity(item.getStoryId(), item.getItemId(), "description");
				}
				if(dbItemEntity.getTranscriptionText().compareTo(item.getTranscriptionText())!=0)
				{
					someItemPartChanged = true;
					persistentNamedEntityService.deleteListNamedEntity(item.getStoryId(), item.getItemId() , "transcription");
					persistentTranslationEntityService.deleteTranslationEntity(item.getStoryId(), item.getItemId() , "transcription");
				}		
				
				if(someItemPartChanged)
				{
					persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(item.getStoryId(), item.getItemId());
				}
			}

			persistentItemEntityService.saveItemEntity(item);
			
			//add item's transcription text to the story's transcription
			if(item.getStoryId()!=null && item.getTranscriptionText()!=null)
			{
				StoryEntity dbStoryEntity = persistentStoryEntityService.findStoryEntity(item.getStoryId());
			
				if(dbStoryEntity!=null)
				{
					String storyTranscription = dbStoryEntity.getTranscriptionText();
					if(dbItemEntity==null)
					{
						storyTranscription += " " + item.getTranscriptionText();
					}
					else
					{
						storyTranscription = storyTranscription.replace(dbItemEntity.getTranscriptionText(), item.getTranscriptionText());
					}
					
					dbStoryEntity.setTranscriptionText(storyTranscription);
					persistentStoryEntityService.saveStoryEntity(dbStoryEntity);
				}
				
			}

			
			
		}
		return "{\"info\": \"Done successfully!\"}";
	}

	@SuppressWarnings("finally")
	@Override
	public String readStoriesAndItemsFromJson(String jsonStoriesImportPath, String jsonItemsImportPath) {
		
		/*
		 * reading stories and items from json
		 */
		
		String resultString = "{\"info\" : \"Done successfully!\"}";
		
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
				String storyId = (String) stories.get(i).get("story_id");
				boolean found = false;
				for(DBStoryEntityImpl tmp : storyEntities) {
					if(tmp.getStoryId().equals(storyId)) {
						found = true;
						break;
					}
				}
				if(found)
					continue;
				String storyLanguage = (String)stories.get(i).get("language");
				if(storyLanguage==null) 
					storyLanguage="";
				else {
					List<LanguageCode> languageCodes = LanguageCode.findByName(storyLanguage);
					if(languageCodes.size() > 0)
						storyLanguage = languageCodes.get(0).toString();
				}
				
				DBStoryEntityImpl newStoryEntity = new DBStoryEntityImpl();
				newStoryEntity.setTitle("");
				newStoryEntity.setDescription("");
				newStoryEntity.setStoryId("");
				newStoryEntity.setLanguage("");
				newStoryEntity.setSummary("");
				newStoryEntity.setTranscriptionText("");
				newStoryEntity.setLanguage(storyLanguage);
				
				if(stories.get(i).get("source")!=null) newStoryEntity.setSource((String) stories.get(i).get("source"));
				
				if(stories.get(i).get("title")!=null) newStoryEntity.setTitle((String) stories.get(i).get("title"));
				
				if(stories.get(i).get("description")!=null) newStoryEntity.setDescription((String) stories.get(i).get("description"));
				
				if(stories.get(i).get("story_id")!=null) newStoryEntity.setStoryId((String) stories.get(i).get("story_id"));
				
				if(stories.get(i).get("summary")!=null)	newStoryEntity.setSummary((String) stories.get(i).get("summary"));
				
				//if(stories.get(i).get("language")!=null) newStoryEntity.setLanguage((String) stories.get(i).get("language"));	
			
				storyEntities.add(newStoryEntity);
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

				String itemTranscription = (String)items.get(i).get("transcription");				

				if(itemTranscription!=null)
				{
					
					DBItemEntityImpl newItemEntity=new DBItemEntityImpl();
					newItemEntity.setTitle("");
					newItemEntity.setStoryId("");
					newItemEntity.setLanguage("");
					newItemEntity.setDescription("");
					newItemEntity.setTranscriptionText("");	
					newItemEntity.setType("");	
					newItemEntity.setItemId("");
					newItemEntity.setSource("");
	
					
					if(items.get(i).get("title")!=null) newItemEntity.setTitle((String) items.get(i).get("title"));
					if(items.get(i).get("story_id")!=null) newItemEntity.setStoryId((String) items.get(i).get("story_id"));
					if(items.get(i).get("transcription")!=null) newItemEntity.setTranscriptionText((String) items.get(i).get("transcription"));
					if(items.get(i).get("description")!=null) newItemEntity.setDescription((String) items.get(i).get("description"));
					if(items.get(i).get("source")!=null) newItemEntity.setSource((String) items.get(i).get("source"));
					
					String itemLanguage = (String) items.get(i).get("language");
					if(items.get(i).get("language")!=null) {
						List<LanguageCode> languageCodes = LanguageCode.findByName(itemLanguage);
						if(languageCodes.size() > 0)
							newItemEntity.setLanguage(languageCodes.get(0).toString());
						else
							newItemEntity.setLanguage("");
					} else {
						newItemEntity.setLanguage("");
					}
					
					if(items.get(i).get("item_id")!=null) newItemEntity.setItemId((String) items.get(i).get("item_id"));
					
					itemEntities.add(newItemEntity);
				}
				
			}
			
			
			String uploadItemsStatus = uploadItems(itemEntities.toArray(new DBItemEntityImpl[0]));
			
			
			logger.info("Stories and Items are saved to the database from the JSON file!");
			resultString = "{\"info\" : \"Done successfully!\"}";
		}
		catch (FileNotFoundException e) {

		    resultString = "{\"info\":\"Fail! File not found!\"}";
		} catch (HttpException e) {
			resultString = "{\"info\":\"Fail! Http Exception!\"}";
		} catch (NoSuchAlgorithmException e) {
			resultString = "{\"info\":\"Fail! No Such Algorithm for setting the security key for the item!\"}";
		} catch (UnsupportedEncodingException e) {
			resultString = "{\"info\":\"Fail! Unsupported encoding for setting the security key of the item!\"}";
		} finally {
			  if (brStories != null) {
				  try {
					  brStories.close();
				  } catch (IOException e) {

		    		resultString = "{\"info\":\"Fail! Cannot close the file!\"}";
				  }
				 
			  }
			  if (brItems != null) {
				  try {
					  brItems.close();
				  } catch (IOException e) {

		    		resultString = "{\"info\":\"Fail! Cannot close the file!\"}";
				  }
			  }
			  //TODO: throw httpexception
		}	
		
		return resultString;
	}
	
	private String parseHTMLWithJsoup (String htmlText)
	{
//		StringBuilder response = new StringBuilder ();

		//https://stackoverflow.com/questions/5640334/how-do-i-preserve-line-breaks-when-using-jsoup-to-convert-html-to-plain-text
		String response = "";
		Document doc = Jsoup.parse(htmlText);		
		doc.outputSettings(new Document.OutputSettings().prettyPrint(false));//makes html() preserve linebreaks and spacing
	    doc.select("br").append("\\n");
	    doc.select("p").prepend("\\n\\n");
	    String s = doc.html().replaceAll("\\\\n", "\n");
	    /*
	     * By passing it Whitelist.none() we make sure that all HTML is removed.
	     * By passsing new OutputSettings().prettyPrint(false) we make sure that the output is not reformatted and line breaks are preserved.
	     */
	    String whole = Jsoup.clean(s, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));

	    /*
	     * These are used to escape characters that are markup sensitive in certain contexts:
		 *	&amp; → & (ampersand, U+0026)
		 *	&lt; → < (less-than sign, U+003C)
		 *	&gt; → > (greater-than sign, U+003E)
		 *	&quot; → " (quotation mark, U+0022)
		 *	&apos; → ' (apostrophe, U+0027)
		 *  &nbsp;  → " " (space)
	     */
	    response = Parser.unescapeEntities(whole, false);
	    //System.out.print(response);
	    //System.out.print(response);
	    return response;

//	    Elements allParagraphs = doc.getElementsByTag("p");
//		allParagraphs.forEach(paragraph -> response.append(paragraph.text()));
//		logger.info(whole);
//		System.out.print(whole);
	}
	
	/**
	 * This function adapts the endpoint of the NER service Stanford
	 * @param endpointToAdapt
	 * @param languageForNER
	 * @return
	 */
	private void adaptNERServiceEndpointBasedOnLanguage (NERService service, String languageForNER)
	{
		String endpoint = service.getEnpoint();
		service.setEndpoint(endpoint.replaceAll("/en/", "/"+languageForNER+"/"));
	}

	@Override
	public String getStoryOrItemAnnotationCollection(String storyId, String itemId, boolean saveEntity) throws HttpException, IOException {
		
		List<NamedEntityAnnotationImpl> namedEntityAnnoList = new ArrayList<NamedEntityAnnotationImpl> ();
		//just retrieve the entities from the db
		if(!saveEntity)
		{
			List<NamedEntityAnnotation> entities = persistentNamedEntityAnnotationService.findNamedEntityAnnotationWithStoryAndItemId(storyId, itemId);
			if(entities!=null && !entities.isEmpty())
			{
				for(NamedEntityAnnotation anno : entities )
				{
					namedEntityAnnoList.add(new NamedEntityAnnotationImpl(anno));
				}
				
				return storyEntitySerializer.serializeCollection(new NamedEntityAnnotationCollection(namedEntityAnnoList, storyId, itemId));
			}
			else
			{
				logger.info("No valid entries found! Please use the POST method first to save the data to the database.");
				return "{\"info\" : \"No valid entries found! Please use the POST method first to save the data to the database.\"}";
			}
			
		}
		else
		{			
			String source = null;
			if(itemId.compareTo("all")==0)
			{		
				source = persistentStoryEntityService.findStoryEntity(storyId).getSource();
			}
			else
			{
				source = persistentItemEntityService.findItemEntityFromStory(storyId, itemId).getSource();
			}
			
			
			Set<NamedEntity> NESet = new HashSet<NamedEntity>();
			
			//taking NamedEntitiy-ies for the story "description" and "transcription" 
			NESet.addAll(persistentNamedEntityService.findNamedEntitiesWithAdditionalInformation(storyId,itemId, "description", false));
			NESet.addAll(persistentNamedEntityService.findNamedEntitiesWithAdditionalInformation(storyId,itemId,"transcription", false));
			
			for (NamedEntity entity : NESet)
			{
				

				for(String wikidataId : entity.getPreferredWikidataIds())
				{				
					NamedEntityAnnotationImpl tmpNamedEntityAnnotation = new NamedEntityAnnotationImpl(storyId,itemId, wikidataId, source); 
					namedEntityAnnoList.add(tmpNamedEntityAnnotation);
					
					//saving the entity to the db
					persistentNamedEntityAnnotationService.saveNamedEntityAnnotation(tmpNamedEntityAnnotation);
				}
				
				//in case of annotations for the whole story take only cross-checked wikidata and dbpedia entities
				//in case of annotations for a specific item take into account additionally all named entities labels found by Stanford_NER
				if(itemId.compareTo("all")!=0 && (entity.getDBpediaIds().isEmpty() || entity.getDBpediaIds()==null))
				{
					NamedEntityAnnotationImpl tmpNamedEntityAnnotation = new NamedEntityAnnotationImpl(storyId,itemId, entity.getLabel(), source); 
					namedEntityAnnoList.add(tmpNamedEntityAnnotation);
					
					//saving the entity to the db
					persistentNamedEntityAnnotationService.saveNamedEntityAnnotation(tmpNamedEntityAnnotation);

				}
			
			}
			
			if(namedEntityAnnoList!=null && !namedEntityAnnoList.isEmpty())
			{
				return storyEntitySerializer.serializeCollection(new NamedEntityAnnotationCollection(namedEntityAnnoList, storyId, itemId));
			}
			else
			{
				logger.info("No valid entries found! There are no entries for the given storyId to be generated.");
				return "{\"info\" : \"No valid entries found! There are no entries for the given storyId to be generated.\"}";
			}
		}
		
	}

	@Override
	public String getStoryOrItemAnnotation(String storyId, String itemId, String wikidataEntity) throws HttpException, IOException {
		
		String wikidataIdGenerated = "http://www.wikidata.org/entity/" + wikidataEntity;
		NamedEntityAnnotation entityAnno = persistentNamedEntityAnnotationService.findNamedEntityAnnotationWithStoryIdItemIdAndWikidataId(storyId, itemId, wikidataIdGenerated);
		if(entityAnno!=null)
		{
			return storyEntitySerializer.serialize(new NamedEntityAnnotationImpl(entityAnno));
		}
		else
		{
			logger.info("No valid entries found! Please use the POST method first to save the data to the database.");
			return "{\"info\" : \"No valid entries found! Please use the POST method first to save the data to the database.\"}";
		}
	}
}
