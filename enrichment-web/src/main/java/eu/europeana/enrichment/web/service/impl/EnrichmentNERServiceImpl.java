package eu.europeana.enrichment.web.service.impl;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.NamedEntityAnnotation;
import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.model.impl.NamedEntityAnnotationCollection;
import eu.europeana.enrichment.model.impl.NamedEntityAnnotationImpl;
import eu.europeana.enrichment.model.utils.ModelUtils;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityAnnotationService;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.ner.enumeration.NERClassification;
import eu.europeana.enrichment.ner.service.NERLinkingService;
import eu.europeana.enrichment.ner.service.NERService;
import eu.europeana.enrichment.solr.commons.JavaJSONParser;
import eu.europeana.enrichment.solr.model.vocabulary.EntitySolrFields;
import eu.europeana.enrichment.solr.service.SolrEntityPositionsService;
import eu.europeana.enrichment.solr.service.SolrWikidataEntityService;
import eu.europeana.enrichment.translation.service.impl.ETranslationEuropaServiceImpl;
import eu.europeana.enrichment.web.common.config.I18nConstants;
import eu.europeana.enrichment.web.commons.StoryWikidataEntitySerializer;
import eu.europeana.enrichment.web.exception.ParamValidationException;
import eu.europeana.enrichment.web.model.EnrichmentNERRequest;
import eu.europeana.enrichment.web.model.EnrichmentTranslationRequest;
import eu.europeana.enrichment.web.service.EnrichmentNERService;
import eu.europeana.enrichment.web.service.EnrichmentStoryAndItemStorageService;
import eu.europeana.enrichment.web.service.EnrichmentTranslationService;

@Service(AppConfigConstants.BEAN_ENRICHMENT_NER_SERVICE)
public class EnrichmentNERServiceImpl implements EnrichmentNERService{
	
	/*
	 * Loading Solr service for finding the positions of Entities in the original text
	 */
	//@Resource(name = "enrichmentTranslationService")
	@Autowired
	EnrichmentTranslationService enrichmentTranslationService;

	//@Resource(name = "solrEntityService")
	@Autowired
	SolrEntityPositionsService solrEntityService;
	
	//@Resource(name = "solrWikidataEntityService")
	@Autowired
	SolrWikidataEntityService solrWikidataEntityService;
	

	//@Resource(name = "storyEntitySerializer")
	@Autowired
	StoryWikidataEntitySerializer storyEntitySerializer;
	
	/*
	 * Loading all translation services
	 */
	//@Resource(name = "eTranslationService")
	@Autowired
	ETranslationEuropaServiceImpl eTranslationService;

	/*
	 * Loading all NER services
	 */
	//@Resource(name = "nerLinkingService")
	@Autowired
	NERLinkingService nerLinkingService;
	//@Resource(name = "stanfordNerService")
	@Autowired
	NERService nerStanfordService;
	//@Resource(name = "dbpediaSpotlightService")
	@Autowired
	NERService nerDBpediaSpotlightService;
	
	//@Resource(name = "javaJSONParser")
	@Autowired
	JavaJSONParser javaJSONParser;
	
    @Autowired
    EnrichmentStoryAndItemStorageService enrichmentStoryAndItemStorageService;

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
    
    //Transcribathon URL for getting the item information
    private static final String transcribathonBaseURLItems = "https://europeana.fresenia.man.poznan.pl/tp-api/items/";
    private static final String transcribathonBaseURLStories = "https://europeana.fresenia.man.poznan.pl/tp-api/stories/";
    private static final String transcribathonBaseURLStoriesMinimal = "https://europeana.fresenia.man.poznan.pl/tp-api/storiesMinimal/";
    private static int cascadeCall = 0;
	
	Logger logger = LogManager.getLogger(getClass());
	
	//@Resource(name = "persistentNamedEntityService")
	@Autowired
	PersistentNamedEntityService persistentNamedEntityService;
	//@Resource(name = "persistentTranslationEntityService")
	@Autowired
	PersistentTranslationEntityService persistentTranslationEntityService;
	//@Resource(name = "persistentStoryEntityService")
	@Autowired
	PersistentStoryEntityService persistentStoryEntityService;
	//@Resource(name = "persistentItemEntityService")
	@Autowired
	PersistentItemEntityService persistentItemEntityService;
	//@Resource(name = "persistentNamedEntityAnnotationService")
	@Autowired
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
	
	/*
	 * TODO: refactor this method, is too long 
	 */
	@Override
	public TreeMap<String, List<NamedEntity>> getNamedEntities(EnrichmentNERRequest requestParam, boolean process) throws Exception {
		
		TreeMap<String, List<NamedEntity>> resultMap = new TreeMap<>();
		
		//TODO: check parameters and return other status code
		String storyId = requestParam.getStoryId();
		String itemId = requestParam.getItemId();
		String type = requestParam.getProperty();
		if(type == null || type.isEmpty())
			type = "transcription";
		else if(!(type.equals("all") || type.equals("summary") || type.equals("description") || type.equals("transcription")))
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

		//fetching NamedEntity-ies to check if they are found with the required ner tools
		tmpNamedEntities.addAll(persistentNamedEntityService.findNamedEntitiesWithAdditionalInformation(storyId, itemId, type));
	
		int numberNERTools = tools.size();
		//check if for the given story/item the NER anaylysis for all required NER tools is already pursued, returned is the number of 
		//ner tools for which the analysis remained undone (the list "tools" is also updated, i.e. only the not analyzed tools remian in the list)
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
				if(tmpNamedEntity.getLabel()!=null 
						&& !classificationNamedEntities.stream().anyMatch(x -> tmpNamedEntity.getLabel().equals(x.getLabel()))) {
					classificationNamedEntities.add(tmpNamedEntity);
				}
			}
		}
		
		//in case of GET API (process==FALSE) or POST API where all ner tools have been processed and no new provided text is given
		if(!process || numberNERToolsFound==numberNERTools) {
			//TODO: throw exception 404
			//throw new HttpException("");
			return resultMap;
		}

		//from this part down only POST method is executed and the NER analysis is done for all story or item fields
		List<String> allNERFieldTypes = new ArrayList<String>();
		if(itemId.compareToIgnoreCase("all")==0 && type.compareToIgnoreCase("all")==0)
		{
			allNERFieldTypes.add("transcription");
			allNERFieldTypes.add("description");
			allNERFieldTypes.add("summary");
		}
		else if (type.compareToIgnoreCase("all")==0) {
			allNERFieldTypes.add("transcription");
		}
		else {
			allNERFieldTypes.add(type);
		}
		
		for(String typeNERField : allNERFieldTypes)
		{

			type=typeNERField; 
			
			TranslationEntity dbTranslationEntity = null;
			String [] textAndLanguage = updateStoryOrItem(original, storyId, itemId, translationTool, translationLanguage, type, dbTranslationEntity);
			String textForNer = textAndLanguage[0];
			String languageForNer = textAndLanguage[1];
//			if(languageForNer==null)
//				throw new HttpException("The original language of the story or item is null.", null, HttpStatus.PRECONDITION_REQUIRED);

			//sometimes some fields for NER can be empty for items which causes problems in the method applyNERTools
			if(textForNer!=null && !textForNer.isBlank() && languageForNer!=null)
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
					
					TreeMap<String, List<NamedEntity>> tmpResult = applyNERTools(newNERTools, textForNer, type, storyId, itemId, languageForNer, dbTranslationEntity);
					
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
							NamedEntity dbEntity = null;
							/*
							 * Check if named entity with the same label was found in the
							 * previous story item or in the database
							 */
							List<NamedEntity> tmpResultNamedEntityList = null;
							if(tmpNamedEntity.getLabel()!=null) {
								tmpResultNamedEntityList = tmpClassificationList.stream().
									filter(x -> tmpNamedEntity.getLabel().equals(x.getLabel())).collect(Collectors.toList());
							}
							if(tmpResultNamedEntityList!=null && tmpResultNamedEntityList.size() > 0)
								dbEntity = tmpResultNamedEntityList.get(0);
							else if (tmpNamedEntity.getLabel()!=null)
								dbEntity = persistentNamedEntityService.findNamedEntity(tmpNamedEntity.getLabel());
						
							
							if(dbEntity != null) {
								//check if there are new position entities to be added
								int addPositionEntitiesCheck = 1;
								if(dbEntity.getPositionEntities()!=null) {
									for(PositionEntity pe : dbEntity.getPositionEntities())
									{
										if(tmpNamedEntity.getPositionEntities()!=null && tmpNamedEntity.getPositionEntities().get(0).equals(pe))
										{
											addPositionEntitiesCheck = 0;
											/*
											 * only if all fields of the position entities are the same including the positions in the translated text
											 * 2 or more ner tools are added to the same position entity
											 */
											if(pe.getNERTools()!=null && !pe.getNERTools().contains(NERTool)) pe.getNERTools().add(NERTool);
											break;
										}
									}
								}
								if(addPositionEntitiesCheck==1 && tmpNamedEntity.getPositionEntities()!=null) dbEntity.addPositionEntity(tmpNamedEntity.getPositionEntities().get(0));
	
								if(tmpNamedEntity.getDBpediaIds()!=null) {
									for(int dbpediaIndex = 0; dbpediaIndex < tmpNamedEntity.getDBpediaIds().size(); dbpediaIndex++) {
										int tmpIndex = dbpediaIndex;
										boolean found = dbEntity.getDBpediaIds().stream().anyMatch(x -> x.equals(tmpNamedEntity.getDBpediaIds().get(tmpIndex)));
										if(!found){
											dbEntity.addDBpediaId(tmpNamedEntity.getDBpediaIds().get(tmpIndex));
										}
									}
								}
								
								/*
								 * Check if named entity is already at the TreeSet
								 */
								if(tmpResultNamedEntityList==null || tmpResultNamedEntityList.size() == 0)
									tmpClassificationList.add(dbEntity);
							}
							else {
								dbEntity = tmpNamedEntity;	
								/*
								 * Add linking information to named entity
								 */
								nerLinkingService.addLinkingInformation(dbEntity, linking, languageForNer);
								tmpClassificationList.add(dbEntity);
							}
						}
					}
				}
			}
		}
		
		/*
		 * Save and update all named entities
		 */
		int numberFoundNamedEntity = 0;
		if(resultMap.size()>0)
		{
			for (String key : resultMap.keySet()) {
				List<NamedEntity> entities = resultMap.get(key);
				for (NamedEntity entity : entities) {
					//save the wikidata ids to solr
					//for(String wikidataId : entity.getPreferredWikidataIds())
					if(entity.getPreferredWikidataIds()!=null) {
						for(String wikidataId : entity.getPreferredWikidataIds())
						{
							solrWikidataEntityService.storeWikidataFromURL(wikidataId, entity.getType());
						} 
					}
					//save the NamedEntity to mongo db
					persistentNamedEntityService.saveNamedEntity(entity);
					numberFoundNamedEntity+=1;
				}
			}
		}

		logger.info("The NER analysis has been finished. The number of found NamedEntity-ies for storyId: " + storyId +
				" and itemId: " + itemId + " is: " + Integer.toString(numberFoundNamedEntity));
		
		return resultMap;
	}
	/*
	 * This function checks if the given story or item is present in the db and if not it fetches it from the Transcribathon platform.
	 * Additionally, if there is not proper translation, it is first done here and the translated text is returned for the NER analysis.
	 */
	private String [] updateStoryOrItem (boolean original, String storyId, String itemId, String translationTool, String translationLanguage, String type, TranslationEntity returnTranslationEntity) throws Exception
	{
		String [] results =  new String [2];
		results[0]=null;
		results[1]=null;
		
		if(original) {
			if(itemId.compareTo("all")==0)
			{		
				
				StoryEntity story = persistentStoryEntityService.findStoryEntity(storyId);
				if(story==null) story = enrichmentStoryAndItemStorageService.fetchAndSaveStoryFromTranscribathon(storyId);
				if (story==null) return results;
				
				if(type.toLowerCase().equals("description")) 
				{
					results[0] = story.getDescription();
					results[1] = story.getLanguageDescription();
					
				}
				else if(type.toLowerCase().equals("summary"))
				{
					results[0] = story.getSummary();
					results[1] = story.getLanguageSummary();
				}
				else if(type.toLowerCase().equals("transcription"))
				{
					results[0] = story.getTranscriptionText();
					results[1] = ModelUtils.getSingleTranslationLanguage(story);
				}
				return results;
			}
			else
			{	
				ItemEntity item = persistentItemEntityService.findItemEntity(storyId, itemId);
				if(item==null) item = enrichmentStoryAndItemStorageService.fetchAndSaveItemFromTranscribathon(storyId, itemId);
				if (item==null) return results;
				
				results[0] = item.getTranscriptionText();
				results[1] = ModelUtils.getSingleTranslationLanguage(item);
				return results;
			}
		}
		
		
		//checking TranslationEntity
		returnTranslationEntity = persistentTranslationEntityService.findTranslationEntityWithAditionalInformation(storyId, itemId, translationTool, translationLanguage, type);
		
		if(returnTranslationEntity!=null)
		{
			results[0] = returnTranslationEntity.getTranslatedText();
			results[1] = returnTranslationEntity.getLanguage();
			
		}
		else
		{
			EnrichmentTranslationRequest body = new EnrichmentTranslationRequest();
			body.setStoryId(storyId);
			body.setItemId(itemId);
			body.setTranslationTool(translationTool);
			body.setType(type);
			enrichmentTranslationService.translate(body, true);
			returnTranslationEntity = persistentTranslationEntityService.findTranslationEntityWithAditionalInformation(storyId, itemId, translationTool, translationLanguage, type);
			if(returnTranslationEntity!=null)
			{
				results[0] = returnTranslationEntity.getTranslatedText();
				results[1] = returnTranslationEntity.getLanguage();
			}

		}			
		
		return results;

	}
	
	/*
	 * This function returns the number of NER-tools that are present in the found NamedEntities  
	 */
	private int checkAllNerToolsAlreadyCompleted (List<String> tools, List<NamedEntity> tmpNamedEntities)
	{		
		Set<String> nerToolsForStoryOrItem = new HashSet<String>();
		if(tmpNamedEntities!=null) {
			for (NamedEntity ne : tmpNamedEntities)
			{
				if(ne.getPositionEntities()!=null) {
					for(PositionEntity pe : ne.getPositionEntities())
					{
						nerToolsForStoryOrItem.addAll(pe.getNERTools());
					}
				}
			}
		}
				
		List<String> toolsToRemove = new ArrayList<String>();
		if(tools!=null) {
			for (String nerToolsNew : tools)
			{
				if(nerToolsForStoryOrItem.contains(nerToolsNew)) toolsToRemove.add(nerToolsNew);
			}
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
					tmpTool = nerStanfordService;
					break;
				case dbpediaSpotlightName:
					tmpTool = nerDBpediaSpotlightService;
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
			if(tmpResult!=null && !tmpResult.isEmpty())
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
					if(entity.getPositionEntities()==null || entity.getPositionEntities().size()==0) continue;
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
					if(entity.getPositionEntities()==null || entity.getPositionEntities().size()==0) continue;
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
					if(endResultCategoryValues.get(resultIndex).getLabel()!=null
							&& endResultCategoryValues.get(resultIndex).getLabel().equals(tmpNamedEntity.getLabel())) 
					{
						NamedEntity resultNamedEntity = endResultCategoryValues.get(resultIndex);
						if(resultNamedEntity!=null && resultNamedEntity.getPositionEntities()!=null && resultNamedEntity.getPositionEntities().size()>0) {
							PositionEntity pos = resultNamedEntity.getPositionEntities().get(0);
							pos.setStoryId(storyId);
							pos.setItemId(itemId);
							pos.setFieldUsedForNER(fieldUsedForNER);
							pos.addNERTool(tool_string);
							if(dbTranslationEntity != null)
								pos.setTranslationKey(dbTranslationEntity.getKey());
							
							if(tmpNamedEntity.getDBpediaIds()!=null) {
								for(String dbpediaId : tmpNamedEntity.getDBpediaIds())
								{
									resultNamedEntity.addDBpediaId(dbpediaId);
								}
							}
							
							List<Integer> tmpOffsets = tmpNamedEntity.getPositionEntities().get(0).getOffsetsTranslatedText();
							List<Integer> resultOffsets = pos.getOffsetsTranslatedText();
							if(tmpOffsets!=null) resultOffsets.addAll(tmpOffsets);
							Set<Integer> uniqueList = new HashSet<Integer>(resultOffsets);
							pos.setOffsetsTranslatedText(new ArrayList<Integer>(uniqueList));
							//updated new NamedEntity added to the list
							//combinedResult.get(categoryKey).add(resultNamedEntity);
							foundNamedEntityInPreviousResults = true;
							break;
						}
					}
				}
				if(!foundNamedEntityInPreviousResults)
				{
					//add new entity
					// Only first PositionEntity is set, because NER tools create new NamedEntities
					if(tmpNamedEntity.getPositionEntities()!=null && tmpNamedEntity.getPositionEntities().size()>0) {
						PositionEntity pos = tmpNamedEntity.getPositionEntities().get(0);
						pos.setStoryId(storyId);
						pos.setItemId(itemId);
						pos.setFieldUsedForNER(fieldUsedForNER);
						pos.addNERTool(tool_string);
						if(dbTranslationEntity != null) pos.setTranslationKey(dbTranslationEntity.getKey());
					}
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
			if(namedEntities==null) continue;
			for(int index = namedEntities.size()-1; index >= 0; index--) {
				NamedEntity tmpNamedEntity = namedEntities.get(index);
				List<PositionEntity> tmpPositions = tmpNamedEntity.getPositionEntities();
				if(tmpPositions==null) continue;
				for(int posIndex = tmpPositions.size()-1; posIndex >= 0; posIndex--) {
					PositionEntity tmpPositionEntity = tmpPositions.get(posIndex);
					String tmpStoryId = tmpPositionEntity.getStoryId();
					String tmpItemId = tmpPositionEntity.getItemId();
					if(storyId.compareTo(tmpStoryId)!=0 || itemId.compareTo(tmpItemId)!=0)
						tmpPositions.remove(posIndex);
					else {
						tmpPositionEntity.setStoryEntity(null);
						tmpPositionEntity.setStoryId(tmpStoryId);
						tmpPositionEntity.setItemEntity(null);
						tmpPositionEntity.setItemId(tmpItemId);
						tmpPositionEntity.setTranslationEntity(null);
						tmpPositionEntity.setTranslationKey(null);
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
		
		logger.info("Uploading new stories to the Mongo DB.");
		
		for (StoryEntity story : stories) {
			if(story.getStoryId() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_ID, null);
			if(story.getDescription() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_DESCRIPTION, null);
			if(story.getTranscriptionLanguages() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_TRANSCRIPTION_LANGUAGES, null);
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
					persistentNamedEntityService.deletePositionEntitiesFromNamedEntity(story.getStoryId(), "all" , "description");
					persistentTranslationEntityService.deleteTranslationEntity(story.getStoryId(), "all", "description");
				}
				if(dbStoryEntity.getSummary().compareTo(story.getSummary())!=0)
				{
					someStoryPartChanged=true;
					persistentNamedEntityService.deletePositionEntitiesFromNamedEntity(story.getStoryId(), "all" , "summary");
					persistentTranslationEntityService.deleteTranslationEntity(story.getStoryId(), "all", "summary");
				}
				if(dbStoryEntity.getTranscriptionText().compareTo(story.getTranscriptionText())!=0)
				{
					someStoryPartChanged=true;
					persistentNamedEntityService.deletePositionEntitiesFromNamedEntity(story.getStoryId(), "all" , "transcription");
					persistentTranslationEntityService.deleteTranslationEntity(story.getStoryId(), "all", "transcription");
				}		
				
				if(someStoryPartChanged)
				{
					persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(story.getStoryId(), "all");
				}
				
				dbStoryEntity.copyFromStory(story);
				persistentStoryEntityService.saveStoryEntity(dbStoryEntity);
			}
			else {
				persistentStoryEntityService.saveStoryEntity(story);
			}
			
		}
		return "{\"info\": \"Done successfully!\"}";
	}
	
	@Override
	public String uploadItems(ItemEntity[] items) throws HttpException, NoSuchAlgorithmException, UnsupportedEncodingException {
		
		logger.info("Uploading new items to the Mongo DB.");
		
		for (ItemEntity item : items) {
			if(item.getStoryId() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_ID, null);
			if(item.getTranscriptionLanguages() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_TRANSCRIPTION_LANGUAGES, null);
			if(item.getTitle() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_ITEM_TITLE, null);
			if(item.getTranscriptionText() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_ITEM_TRANSCRIPTION, null);
			if(item.getItemId() == null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_ITEM_ID, null);
			
			//remove html markup from the transcription and decription texts
			String itemTranscriptionText = parseHTMLWithJsoup(item.getTranscriptionText());
			item.setTranscriptionText(itemTranscriptionText);
			
			//comparing the new and the already existing item and deleting old NamedEntities if there are changes
			ItemEntity dbItemEntity = persistentItemEntityService.findItemEntity(item.getStoryId(), item.getItemId());			
			if (dbItemEntity!=null)
			{
				boolean transcriptionChanged = false;
				if(dbItemEntity.getTranscriptionText().compareTo(item.getTranscriptionText())!=0)
				{
					logger.info("Uploading new items : deleting old NamedEntity and TranslationEntity for transcription.");
					transcriptionChanged = true;
					persistentNamedEntityService.deletePositionEntitiesFromNamedEntity(item.getStoryId(), item.getItemId() , "transcription");
					persistentTranslationEntityService.deleteTranslationEntity(item.getStoryId(), item.getItemId() , "transcription");
				}						
				if(transcriptionChanged)
				{
					persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(item.getStoryId(), item.getItemId());
				}
				
				dbItemEntity.copyFromItem(item);
				persistentItemEntityService.saveItemEntity(dbItemEntity);
			}
			else {
				persistentItemEntityService.saveItemEntity(item);
			}
			
			//add item's transcription text to the story's transcription
			if(item.getStoryId()!=null && item.getTranscriptionText()!=null)
			{
				StoryEntity dbStoryEntity = persistentStoryEntityService.findStoryEntity(item.getStoryId());
			
				if(dbStoryEntity!=null)
				{
					String storyTranscription = dbStoryEntity.getTranscriptionText();
					if(storyTranscription!=null && dbItemEntity==null)
					{
						storyTranscription += " " + item.getTranscriptionText();
					}
					else if(storyTranscription!=null)
					{
						storyTranscription = storyTranscription.replace(dbItemEntity.getTranscriptionText(), item.getTranscriptionText());
					}

					if(storyTranscription!=null && !storyTranscription.isBlank()) {
						dbStoryEntity.setTranscriptionText(storyTranscription);
						persistentStoryEntityService.saveStoryEntity(dbStoryEntity);
					}
				}
				
			}

			
			
		}
		return "{\"info\": \"Done successfully!\"}";
	}
	
	private String parseHTMLWithJsoup (String htmlText)
	{
//		StringBuilder response = new StringBuilder ();

		//https://stackoverflow.com/questions/5640334/how-do-i-preserve-line-breaks-when-using-jsoup-to-convert-html-to-plain-text
		String response;
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
	public String getStoryOrItemAnnotationCollection(String storyId, String itemId, boolean saveEntity, boolean crosschecked, String property) throws Exception {
		
		List<NamedEntityAnnotationImpl> namedEntityAnnoList = new ArrayList<NamedEntityAnnotationImpl> ();
		
		//try first to retrieve the entities from the db in case of the GET request (if NO entries are found the POST must be called first)
		List<NamedEntityAnnotation> entities = persistentNamedEntityAnnotationService.findNamedEntityAnnotationWithStoryItemIdAndProperty(storyId, itemId, property);
		if(entities!=null && !entities.isEmpty())
		{
			for(NamedEntityAnnotation anno : entities )
			{
				if(!crosschecked || (crosschecked && anno.getWikidataId().contains("www.wikidata.org")))
				{
					namedEntityAnnoList.add(new NamedEntityAnnotationImpl(anno));
				}

			}
			
			return storyEntitySerializer.serializeCollection(new NamedEntityAnnotationCollection(namedEntityAnnoList, storyId, itemId));
		}
		else if(!saveEntity)
		{
			logger.info("No valid entries found! Please use the POST method first to save the data to the database.");
			return "{\"info\" : \"No valid entries found! Please use the POST method first to save the data to the database.\"}";
		}
			
		
		String source = null;
		if(itemId.compareTo("all")==0)
		{	
			StoryEntity story = persistentStoryEntityService.findStoryEntity(storyId);
			if(story==null) throw new ParamValidationException(I18nConstants.INVALID_PARAM_VALUE, EnrichmentNERRequest.PARAM_STORY_ID, storyId);
			source = story.getSource();				
		}
		else
		{
			ItemEntity item = persistentItemEntityService.findItemEntityFromStory(storyId, itemId);
			if(item==null) throw new ParamValidationException(I18nConstants.INVALID_PARAM_VALUE, EnrichmentNERRequest.PARAM_ITEM_ID, itemId); 
			source = item.getSource();
		}
	
		Set<NamedEntity> NESet = new HashSet<NamedEntity>();
	
		String allPropertyFields [] = {"description","transcription","summary"}; 
		
		for (String propertyField : allPropertyFields)
		{
			//taking NamedEntitiy-ies for the given field
			NESet.clear();
			NESet.addAll(persistentNamedEntityService.findNamedEntitiesWithAdditionalInformation(storyId,itemId, propertyField));

			if(NESet!=null && !NESet.isEmpty())
			{
				for (NamedEntity entity : NESet)
				{

					if(entity.getPreferredWikidataIds()!=null) {
						for(String wikidataId : entity.getPreferredWikidataIds())
						{				
							//getting Solr WikidataEntity prefLabel
							WikidataEntity wikiEntity = solrWikidataEntityService.getWikidataEntity(wikidataId, entity.getType());
							String entityPrefLabel = entity.getLabel();
							if(wikiEntity!=null)
							{
								Map<String, List<String>> prefLabelMap = wikiEntity.getPrefLabel();
								if(prefLabelMap!=null && prefLabelMap.get(EntitySolrFields.PREF_LABEL+".en")!=null 
										&& prefLabelMap.get(EntitySolrFields.PREF_LABEL+".en").size()>0)
									entityPrefLabel = prefLabelMap.get(EntitySolrFields.PREF_LABEL+".en").get(0);
							}
													
							NamedEntityAnnotationImpl tmpNamedEntityAnnotation = new NamedEntityAnnotationImpl(storyId,itemId, wikidataId, source, entity.getLabel(),entityPrefLabel, propertyField, entity.getType()); 
							
							if(!namedEntityAnnoList.contains(tmpNamedEntityAnnotation))
							{
								namedEntityAnnoList.add(tmpNamedEntityAnnotation);					
								//saving the entity to the db
								persistentNamedEntityAnnotationService.saveNamedEntityAnnotation(tmpNamedEntityAnnotation);
							}
						}
					}
					
					//in case of annotations for the whole story take only cross-checked wikidata and dbpedia entities
					//in case of annotations for a specific item take into account additionally all named entities labels found by Stanford_NER
					if(itemId.compareTo("all")!=0 && (entity.getDBpediaIds()==null || entity.getDBpediaIds().isEmpty()))
					{
						NamedEntityAnnotationImpl tmpNamedEntityAnnotation = new NamedEntityAnnotationImpl(storyId,itemId, entity.getLabel(), source, entity.getLabel(), entity.getLabel(), propertyField, entity.getType()); 
						
						if(!namedEntityAnnoList.contains(tmpNamedEntityAnnotation) && !crosschecked)
						{
							namedEntityAnnoList.add(tmpNamedEntityAnnotation);
						}
						
						//saving the entity to the db
						persistentNamedEntityAnnotationService.saveNamedEntityAnnotation(tmpNamedEntityAnnotation);
	
					}
				
				}
			}
		}
		
		if(namedEntityAnnoList!=null && !namedEntityAnnoList.isEmpty())
		{
			return storyEntitySerializer.serializeCollection(new NamedEntityAnnotationCollection(namedEntityAnnoList, storyId, itemId));
		}
		else
		{
			
			//calling the enrichment NER service for doing the NER analysis first
			EnrichmentNERRequest body = new EnrichmentNERRequest();
			body.setStoryId(storyId);
			body.setItemId(itemId);
			body.setTranslationTool("Google");
			body.setProperty(property);
			String linking = "Wikidata";
			String nerTools = "Stanford_NER,DBpedia_Spotlight";
			body.setLinking(Arrays.asList(linking.split(",")));
			body.setNerTools(Arrays.asList(nerTools.split(",")));
			body.setOriginal(false);
			
			getEntities(body, true);
			
			//calling the NER analysis first but just once
			cascadeCall += 1;
			if(cascadeCall>1) {
				logger.info("No valid entries found! There are no entries for the given storyId to be generated even after doing the NER analysis first.");
				return "{\"info\" : \"No valid entries found! There are no entries for the given storyId to be generated even after doing the NER analysis first.\"}";
			}
			else
			{
				return getStoryOrItemAnnotationCollection(storyId, itemId, true, crosschecked, null);
			}
		}
		
	}

	@Override
	public String getStoryOrItemAnnotation(String storyId, String itemId, String wikidataEntity) throws HttpException, IOException {
		
		String wikidataIdGenerated=null;
		if(wikidataEntity.startsWith("Q")) wikidataIdGenerated = "http://www.wikidata.org/entity/" + wikidataEntity;
		else wikidataIdGenerated = wikidataEntity;		
		
		NamedEntityAnnotation entityAnno = persistentNamedEntityAnnotationService.findNamedEntityAnnotationWithStoryIdItemIdAndWikidataId(storyId, itemId, wikidataIdGenerated);
		if(entityAnno!=null)
		{
			return storyEntitySerializer.serialize(new NamedEntityAnnotationImpl(entityAnno));
		}
		else
		{
			logger.info("No valid entries found! Please use the POST method first to save the data to the database or provide a valid Wikidata identifier.");
			return "{\"info\" : \"No valid entries found! Please use the POST method first to save the data to the database.\"}";
		}
	}
}
