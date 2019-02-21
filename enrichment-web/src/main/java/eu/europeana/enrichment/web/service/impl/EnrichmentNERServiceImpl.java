package eu.europeana.enrichment.web.service.impl;

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
import eu.europeana.enrichment.translation.service.TranslationService;
import eu.europeana.enrichment.web.exception.ParamValidationException;
import eu.europeana.enrichment.web.model.EnrichmentNERRequest;
import eu.europeana.enrichment.web.service.EnrichmentNERService;

import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;

public class EnrichmentNERServiceImpl implements EnrichmentNERService{
	
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
	public String getEntities(EnrichmentNERRequest requestParam) throws HttpException {
		
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
	public TreeMap<String, List<NamedEntity>> getNamedEntities(EnrichmentNERRequest requestParam) throws HttpException {
		
		TreeMap<String, List<NamedEntity>> resultMap = new TreeMap<>();
		
		//TODO: check parameters and return other status code
		String storyId = requestParam.getStoryId();
		List<String> storyItemIds = requestParam.getStoryItemIds();
		String tool = requestParam.getNERTool();
		List<String> linking = requestParam.getLinking();
		String translationTool = requestParam.getTranslationTool();
		String translationLanguage = requestParam.getTranslationLanguage();
		
		List<StoryItemEntity> tmpStoryItemEntity = new ArrayList<>();
		/*
		 * Retrieve the List<storyItemEntity> from the DB
		 */
		findStoryItemEntitiesFromIds(storyId,storyItemIds,tmpStoryItemEntity);
				
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
		//check if named entities already exists
		for(StoryItemEntity dbStoryItemEntity : tmpStoryItemEntity) {
			tmpNamedEntities.addAll(persistentNamedEntityService.findNamedEntitiesWithAdditionalInformation(dbStoryItemEntity.getStoryItemId(), false));
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
			TreeMap<String, List<List<String>>> tmpResult = tmpTool.identifyNER(text);

			findEntitiyOffsetsInOriginalText(tmpStoryItemEntity.get(0).getLanguage(),dbTranslationEntity.getLanguage(),tmpStoryItemEntity.get(0).getText(),tmpResult);
			
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
					defaultPosition.setStoryItemEntity(dbStoryItemEntity);
					defaultPosition.setTranslationEntity(dbTranslationEntity);
					defaultPosition.addOfssetPosition(Integer.parseInt(entityLabel.get(1)));
					
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
					//tmpTool.getPositions(dbEntity, dbStoryItemEntity, dbTranslationEntity);
					
					/*
					 * Add linking information to named entity
					 */
					nerLinkingService.addLinkingInformation(dbEntity, linking, "en");
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
					String tmpStoryItemId = tmpPositionEntity.getStoryItemId();
					if(!storyItemIds.contains(tmpStoryItemId))
						tmpPositions.remove(posIndex);
					else {
						tmpPositionEntity.setStoryItemEntity(null);
						tmpPositionEntity.setStoryItemId(tmpStoryItemId);
						String tmpTranslationEntityKey = tmpPositionEntity.getTranslationKey();
						tmpPositionEntity.setTranslationEntity(null);
						//tmpPositionEntity.setTranslationKey(tmpTranslationEntityKey);
					}
				}
				tmpNamedEntity.setType(null);
			}
		}
	}
	
	/**
	 * This function implements finding the positions of the identified entities (using the given NER tool) 
	 * in the original text.
	 * Logic behind:
	 * 1. get all found entities that contain the name of the entity and its position in the translated text
	 * 2. sort all these entities by the position in the translated text
	 * 3. translate all of them using the given translation service by sending the string of all entities separated by commas
	 * where each entity is additionally added a classifier word ("location", "agent", or "organization") in front of it (i.e. string="agent Dumitru, location Pola, agent Pola, location Bistrita, etc.")
	 * so that the translation tool correctly translates the entity from the context (i.e. if we send only the name, the service does not put special characters that exist in the original text, e.g. if we translate Bistrita in 
	 * google into Romanian we would get the same name, but if translate "entity Bistrita" we would get the special character on "t" which is the exact name of the city in Romanian)
	 * 4. take the translated string, divide it in parts using comma as a separator and extract the second word to get the entitiy name in the original text
	 * 5. since the entities are sorted search for each one in the original text and if the same name occurs save the number of previously found entities and 
	 * extract the corresponding one then by extracting the exact offset from a set of found offsets
	 * 6. in searching the text in the original text use Levenshtein's distance (https://en.wikipedia.org/wiki/Levenshtein_distance) to match the owrds that differ a bit (e.g. Bistrita -> Bistritei (original))
	 */
	
	private Map<List<String>,String> findEntitiyOffsetsInOriginalText(String originalLanguage, String targetLanguage, String originalText, TreeMap<String, List<List<String>>> identifiedNER)
	{
		Map<List<String>,String> resultMap = new HashMap<List<String>, String>();
		
		/*
		 * get all entities in one list in order to sort them
		 */
		List<List<String>> sortedListAllEntities = new ArrayList<List<String>>();		
		for (String classificationType : identifiedNER.keySet()) {
			for (List<String> entityList : identifiedNER.get(classificationType)) {
				/*
				 * adding a new element to the list based on the original element, e.g. {"Bistrita", "234"}->{"location Bistrita","234"}
				 * to be easier translated because it contains a bit more context than just a name of the entity
				 */
				List<String> newList = new ArrayList<String>();
				newList.add(classificationType+" "+entityList.get(0));
				newList.add(entityList.get(1));
				sortedListAllEntities.add(newList);
			}
		}
		
		/*
		 * sort the list based on the second element in the inner list, which is the position of the entity in the translated text 
		 */
		Collections.sort(sortedListAllEntities, new Comparator<List<String>>() {
	        @Override
	        public int compare(List<String> o1, List<String> o2) {
	            try {
	                return Integer.valueOf(o1.get(1)).compareTo(Integer.valueOf(o2.get(1)));
	            } catch (NullPointerException e) {
	                return 0;
	            }
	        }
	    });
		
		StringBuilder entitiesText = new StringBuilder(); 
		for(List<String> entityList: sortedListAllEntities) {
			entitiesText.append(entityList.get(0)+",");
		}
		entitiesText.deleteCharAt(entitiesText.length()-1);//delete the last comma
		
		String serviceResult = eTranslationService.translateText(entitiesText.toString(),originalLanguage,targetLanguage);
			
		/*
		 * finding the translated entities in the original text
		 */
		String [] serviceResultWords = serviceResult.split(","); //splitting into words		
		String [] originalTextWords = originalText.split("\\s+"); //splitting into words by one/more spaces
		Map<String, Integer> entityLastFoundIndexes = new HashMap<String, Integer>();
		List<Integer> positionsOriginalText = new ArrayList<Integer> ();
		
		for(int i=0; i<serviceResultWords.length ; i++) {
			
			String [] translatedEntities = serviceResultWords[i].split("\\s+", 2); //splitting the classifier from the entity name
						
			for(int j=0; j<originalTextWords.length ; j++){
			
				String nWords="";				
				for(int m=0; j<translatedEntities[1].split("\\s+").length ; m++) {				
					
					nWords = nWords + " " + originalTextWords[j+m] ;    
            
				}
				
				nWords=nWords.substring(1); //exclude the first space added during concatenation
				
				if(calculateLevenshteinDistance(nWords,translatedEntities[1]) >  0.8) {
					
					int startIndex = !entityLastFoundIndexes.containsKey(translatedEntities[1]) ? 0 : entityLastFoundIndexes.get(translatedEntities[1]);
					int foundPosition = originalText.indexOf(translatedEntities[1], startIndex);
					positionsOriginalText.add(foundPosition);				
					entityLastFoundIndexes.put(translatedEntities[1], foundPosition+translatedEntities[1].length());
					
				}
			}
		}
			
		
		return resultMap;
		
	}
	
	private int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }
	
	private int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }
	
	/*
	 * Normalized similaity between 2 strings based on Levenshtein's distance.
	 * Value: 0-1. 0 - totally different, 1 - the same 
	 */
	private int calculateLevenshteinDistance (String x, String y) {
		
	    int[][] dp = new int[x.length() + 1][y.length() + 1];
	    
	    for (int i = 0; i <= x.length(); i++) {
	        for (int j = 0; j <= y.length(); j++) {
	            if (i == 0) {
	                dp[i][j] = j;
	            }
	            else if (j == 0) {
	                dp[i][j] = i;
	            }
	            else {
	            	dp[i][j] = min(dp[i - 1][j - 1] + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)), 
	                  dp[i - 1][j] + 1, 
	                  dp[i][j - 1] + 1);
	            }
	        }
	    }
	 
	    int levenshteinDistance = dp[x.length()][y.length()];
	    
	    return 1-levenshteinDistance/(x.length()+y.length());
	}
		
	private void findStoryItemEntitiesFromIds(String storyId, List<String> storyItemIds, List<StoryItemEntity> result) throws HttpException {
				
		if((storyId == null || storyId.isEmpty()) && (storyItemIds == null || storyItemIds.size() == 0))
		{
			String params = String.join(",", EnrichmentNERRequest.PARAM_STORY_ID, EnrichmentNERRequest.PARAM_STORY_ITEM_IDS);
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, params, null);
		}
		else if(storyItemIds.size() == 0) {
			result = persistentStoryItemEntityService.findStoryItemEntitiesFromStory(storyId);
		}
		else {
			for(String storyItemId : storyItemIds) {
				result.add(persistentStoryItemEntityService.findStoryItemEntity(storyItemId));
			}
		}
		
	}
}
