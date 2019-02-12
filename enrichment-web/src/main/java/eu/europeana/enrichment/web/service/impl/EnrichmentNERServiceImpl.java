package eu.europeana.enrichment.web.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.StoryItemEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.mongo.model.NamedEntityImpl;
import eu.europeana.enrichment.mongo.model.PositionEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.ner.service.NERLinkingService;
import eu.europeana.enrichment.ner.service.NERService;
import eu.europeana.enrichment.web.model.EnrichmentNERRequest;
import eu.europeana.enrichment.web.service.EnrichmentNERService;
import org.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;

public class EnrichmentNERServiceImpl implements EnrichmentNERService{

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
	
	@Resource(name = "persistentNamedEntityService")
	PersistentNamedEntityService persistentNamedEntityService;
	@Resource(name = "persistentTranslationEntityService")
	PersistentTranslationEntityService persistentTranslationEntityService;
	@Resource(name = "persistentStoryEntityService")
	PersistentStoryEntityService persistentStoryEntityService;
	@Resource(name = "persistentStoryItemEntityService")
	PersistentStoryItemEntityService persistentStoryItemEntityService;
	
	//@Cacheable("nerResults")
	@Override
	public String getEntities(EnrichmentNERRequest requestParam) {
		String storyId = requestParam.getStoryId();
		List<String> storyItemIds = requestParam.getStoryItemIds();
		String tool = requestParam.getTool();
		List<String> linking = requestParam.getLinking();
		String translationTool = requestParam.getTranslationTool();
		String translationLanguage = requestParam.getTranslationLanguage();
		
		List<StoryItemEntity> tmpStoryItemEntity = new ArrayList<>();
		if(storyItemIds.size() == 0 && storyId.isEmpty())
		{
			//TODO: throw exception
			return "";
		}
		else if(storyItemIds.size() == 0) {
			tmpStoryItemEntity = persistentStoryItemEntityService.findStoryItemEntitiesFromStory(storyId);
		}
		else {
			for(String storyItemId : storyItemIds) {
				tmpStoryItemEntity.add(persistentStoryItemEntityService.findStoryItemEntity(storyItemId));
			}
		}
		
		List<NamedEntity> tmpNamedEntities = new ArrayList<>();
		//check if named entities already exists
		for(StoryItemEntity dbStoryItemEntity : tmpStoryItemEntity) {
			tmpNamedEntities.addAll(persistentNamedEntityService.findNamedEntitiesWithAdditionalInformation(dbStoryItemEntity.getStoryItemId(), false));
		}
		//TODO: check if update is need (e.g.: linking tools)
		if(tmpNamedEntities.size() > 0)
			return new JSONObject(tmpNamedEntities).toString();
		
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
				//TODO:Return tool is not supported
				return null;
		}
		
		TreeMap<String, TreeSet<NamedEntity>> resultMap = new TreeMap<>();
		/*
		 * Apply named entity recognition on all story item translations
		 */
		for(StoryItemEntity dbStoryItemEntity : tmpStoryItemEntity) {
			TranslationEntity dbTranslationEntity = persistentTranslationEntityService.
					findTranslationEntityWithStoryInformation(dbStoryItemEntity.getStoryItemId(), translationTool, "en");
			String text = dbTranslationEntity.getTranslatedText();
			if(python) {
				JSONObject jsonRequest = new JSONObject();
				jsonRequest.put("tool", tool);
				jsonRequest.put("text", text);
				text = jsonRequest.toString();
			}
			TreeMap<String, TreeSet<String>> tmpResult = tmpTool.identifyNER(text);

			for (String classificationType : tmpResult.keySet()) {
				TreeSet<NamedEntity> tmpClassificationTreeSet = new TreeSet<>();
				/*
				 * Check if already named entities exists from the previous story item
				 */
				if(resultMap.containsKey(classificationType))
					tmpClassificationTreeSet = resultMap.get(classificationType);
				else
					resultMap.put(classificationType, tmpClassificationTreeSet);
				
				for (String entityLabel : tmpResult.get(classificationType)) {
					NamedEntity dbEntity;
					/*
					 * Check if named entity with the same label was found in the
					 * previous story item or in the database
					 */
					List<NamedEntity> tmpResultNamedEntityList = tmpClassificationTreeSet.stream().
							filter(x -> x.getKey().equals(entityLabel)).collect(Collectors.toList());
					if(tmpResultNamedEntityList.size() > 0)
						dbEntity = tmpResultNamedEntityList.get(0);
					else
						dbEntity = persistentNamedEntityService.findNamedEntity(entityLabel);
					/*
					 * Create default position
					 */
					PositionEntity defaultPosition = new PositionEntityImpl();
					defaultPosition.addOfssetPosition(-1);;
					defaultPosition.setStoryItemEntity(dbStoryItemEntity);
					defaultPosition.setTranslationEntity(dbTranslationEntity);
					if(dbEntity != null) {
						dbEntity.addPositionEntity(defaultPosition);
						/*
						 * Check if named entity is already at the TreeSet
						 */
						if(tmpResultNamedEntityList.size() == 0)
							tmpClassificationTreeSet.add(dbEntity);
					}
					else {
						NamedEntity newNamedEntity = new NamedEntityImpl(entityLabel);
						newNamedEntity.setType(classificationType);
						newNamedEntity.addPositionEntity(defaultPosition);
						tmpClassificationTreeSet.add(newNamedEntity);
					}
				}
			}
		}
		
		//TODO: add position and linking info
		/*
		TreeMap<String, List<NamedEntity>> entitiesWithPositions = stanfordNerModel3Service.getPositions(map, text);
		nerLinkingService.addLinkingInformation(entitiesWithPositions, linking, "en");
		*/

		for (String key : resultMap.keySet()) {
			for (NamedEntity entity : resultMap.get(key)) {
				persistentNamedEntityService.saveNamedEntity(entity);
			}
		}
		return new JSONObject(resultMap).toString();
	}
	
}
