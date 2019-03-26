package eu.europeana.enrichment.web.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.common.config.I18nConstants;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.mongo.model.ItemEntityImpl;
import eu.europeana.enrichment.mongo.model.NamedEntityImpl;
import eu.europeana.enrichment.mongo.model.PositionEntityImpl;
import eu.europeana.enrichment.mongo.model.StoryEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.ner.service.NERLinkingService;
import eu.europeana.enrichment.ner.service.NERService;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.solr.service.SolrEntityPositionsService;
import eu.europeana.enrichment.translation.service.TranslationService;
import eu.europeana.enrichment.web.exception.ParamValidationException;
import eu.europeana.enrichment.web.model.EnrichmentNERRequest;
import eu.europeana.enrichment.web.service.EnrichmentNERService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;

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
	@Resource(name = "stanfordNerModel3Service")
	NERService stanfordNerModel3Service;
	@Resource(name = "stanfordNerModel4Service")
	NERService stanfordNerModel4Service;
	@Resource(name = "stanfordNerModel7Service")
	NERService stanfordNerModel7Service;
	@Resource(name = "stanfordNerGermanModelService")
	NERService stanfordNerGermanModelService;
	@Resource(name = "dbpediaSpotlightService")
	NERService dbpediaSpotlightService;
	@Resource(name = "pythonService")
	NERService pythonService;
	
	/*
	 * Defining the available tools for named entities
	 */
	private static final String stanfordNerModel3 = "Stanford_NER_model_3";
	private static final String stanfordNerModel4 = "Stanford_NER_model_4";
	private static final String stanfordNerModel7 = "Stanford_NER_model_7";
	private static final String stanfordNerModelGerman = "Stanford_NER_model_German";
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
		findStoryEntitiesFromIds(storyId,tmpStoryEntity);
				
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
					findTranslationEntityWithStoryInformation(dbStoryEntity.getStoryId(), translationTool, "en");
			String text = dbTranslationEntity.getTranslatedText();
			if(python) {
				JSONObject jsonRequest = new JSONObject();
				jsonRequest.put("tool", tool);
				jsonRequest.put("text", text);
				text = jsonRequest.toString();
			}
			TreeMap<String, List<List<String>>> tmpResult = tmpTool.identifyNER(text);
			
			solrEntityService.findEntitiyOffsetsInOriginalText(true, dbStoryEntity.getStoryLanguage(),"en",dbStoryEntity.getStoryTranscription(),text, dbStoryEntity.getStoryId(), tmpResult);
			
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
		 * Save and update all named entities
		 */
		for (String key : resultMap.keySet()) {
			for (NamedEntity entity : resultMap.get(key)) {
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
			if(item.getStoryId() == null || item.getStoryId().isEmpty())
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_ID, null);
			if(item.getLanguage() == null || item.getLanguage().isEmpty())
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_ITEM_LANGUAGE, null);
			if(item.getTitle() == null || item.getTitle().isEmpty())
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_ITEM_TITLE, null);
			if(item.getTranscription() == null || item.getTranscription().isEmpty())
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_ITEM_TRANSCRIPTION, null);
			if(item.getItemId() == null || item.getItemId().isEmpty())
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_ITEM_ID, null);
			if(item.getType() == null || item.getType().isEmpty())
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_ITEM_TYPE, null);


			persistentItemEntityService.saveItemEntity(item);
			
		}
		return "Done!";
	}
}
