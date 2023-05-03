package eu.europeana.enrichment.web.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.model.impl.NamedEntityAnnotationCollection;
import eu.europeana.enrichment.model.impl.NamedEntityAnnotationImpl;
import eu.europeana.enrichment.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.model.impl.PositionEntityImpl;
import eu.europeana.enrichment.model.impl.StoryEntityImpl;
import eu.europeana.enrichment.model.utils.ModelUtils;
import eu.europeana.enrichment.model.vocabulary.NerTools;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityAnnotationService;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityService;
import eu.europeana.enrichment.mongo.service.PersistentPositionEntityServiceImpl;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.ner.enumeration.NERClassification;
import eu.europeana.enrichment.ner.linking.WikidataService;
import eu.europeana.enrichment.ner.service.NERLinkingService;
import eu.europeana.enrichment.ner.service.NERService;
import eu.europeana.enrichment.solr.service.SolrWikidataEntityService;
import eu.europeana.enrichment.web.common.config.I18nConstants;
import eu.europeana.enrichment.web.exception.ParamValidationException;
import eu.europeana.enrichment.web.service.EnrichmentStoryAndItemStorageService;
import eu.europeana.enrichment.web.service.EnrichmentTranslationService;

@Service(EnrichmentConstants.BEAN_ENRICHMENT_NER_SERVICE)
public class EnrichmentNERServiceImpl {
	
	Logger logger = LogManager.getLogger(getClass());
	
	/*
	 * Loading Solr service for finding the positions of Entities in the original text
	 */
	//@Resource(name = "enrichmentTranslationService")
	@Autowired
	EnrichmentTranslationService enrichmentTranslationService;
	
	//@Resource(name = "solrWikidataEntityService")
	@Autowired
	SolrWikidataEntityService solrWikidataEntityService;

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
		
	@Autowired
	PersistentStoryEntityService persistentStoryEntityService;
	
	@Autowired
	PersistentItemEntityService persistentItemEntityService;

    @Autowired
    EnrichmentStoryAndItemStorageService enrichmentStoryAndItemStorageService;
    
    @Autowired
    PersistentPositionEntityServiceImpl persistentPositionEntityService;

	//@Resource(name = "persistentNamedEntityService")
	@Autowired
	PersistentNamedEntityService persistentNamedEntityService;
	//@Resource(name = "persistentTranslationEntityService")
	@Autowired
	PersistentTranslationEntityService persistentTranslationEntityService;
	
	@Autowired
	WikidataService wikidataService;

	//@Resource(name = "persistentNamedEntityAnnotationService")
	@Autowired
	PersistentNamedEntityAnnotationService persistentNamedEntityAnnotationService;	
	@Autowired
	@Qualifier(EnrichmentConstants.BEAN_ENRICHMENT_CONFIGURATION)
	EnrichmentConfiguration configuration;
	
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
		
	//@Cacheable("nerResults")
	public List<NamedEntityImpl> getEntities(String storyId, String itemId, String property, List<String> nerTools) throws Exception {
		return persistentNamedEntityService.findNamedEntitiesWithAdditionalInformation(
						storyId,
						itemId,
						property,
						nerTools,
						false
				);
	}
	
	/**
	 * This function should pnly be called when updating the named entities of a single story. To compute the named entities
	 * for all stories use the function updatedNamedEntitiesForText, followed by the addLinkingInformation and computePreferredWikidataIds.
	 * @param storyId
	 * @param type
	 * @param nerTools
	 * @param linking
	 * @param translationTool
	 * @param original
	 * @param updateStory
	 * @param removeExistingEntities
	 * @throws Exception
	 */
	public List<NamedEntityImpl> createNamedEntitiesForStory(String storyId, String property, List<String> nerTools, List<String> linking, String translationTool, boolean original, boolean updateStory) throws Exception {
		List<NamedEntityImpl> result = new ArrayList<>();

		//delete existing position, named entities and annotations
		persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(storyId, null, property);
		persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(storyId, null, property, EnrichmentConstants.MONGO_SKIP_FIELD);

		Map<String, String> textAndLanguage = getStoryTextForNER(storyId, translationTool, property, original, updateStory);
		if(StringUtils.isBlank(textAndLanguage.get(EnrichmentConstants.POJOFieldText)) || StringUtils.isBlank(textAndLanguage.get(EnrichmentConstants.POJOFieldLanguage))) {
			return result;
		}
		String textForNer = textAndLanguage.get(EnrichmentConstants.POJOFieldText);
		String languageForNer = textAndLanguage.get(EnrichmentConstants.POJOFieldLanguage);
		
		//sometimes some fields for NER can be empty for items which causes problems in the method applyNERTools
		Set<ObjectId> namedEntitiesToUpdateLinking = updatedNamedEntitiesForText(nerTools, textForNer, languageForNer, property, storyId, null, linking, true);
		//add linking to the named entities
		for(ObjectId neId : namedEntitiesToUpdateLinking) {
			NamedEntityImpl ne = persistentNamedEntityService.findNamedEntity(neId);
			result.add(ne);
			List<PositionEntityImpl> positions = persistentPositionEntityService.findPositionEntities(neId);
			boolean neUpdatedLinking = nerLinkingService.addLinkingInformation(ne, positions, linking);
			boolean neUpdatedPrefWikiId=wikidataService.computePreferredWikidataIds(ne, positions, true);
			if(neUpdatedLinking || neUpdatedPrefWikiId) {
				persistentNamedEntityService.saveNamedEntity(ne);
			}
		}
		
		return result;
	}

	/**
	 * This function should only be called when updating the named entities of a single story. To compute the named entities
	 * for all stories use the function updatedNamedEntitiesForText, followed by the addLinkingInformation and computePreferredWikidataIds.
	 * @param storyId
	 * @param itemId
	 * @param type
	 * @param nerTools
	 * @param linking
	 * @param translationTool
	 * @param original
	 * @param updateItem
	 * @param removeExistingEntities
	 * @throws Exception
	 */
	public List<NamedEntityImpl> createNamedEntitiesForItem(String storyId, String itemId, String property, List<String> nerTools, List<String> linking, String translationTool, boolean original, boolean updateItem) throws Exception {
		List<NamedEntityImpl> result = new ArrayList<>();

		//delete existing position and named entities for the given item
		persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(storyId, itemId, property);
		persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(storyId, itemId, property, EnrichmentConstants.MONGO_SKIP_FIELD);
		
		Map<String, String> textAndLanguage = getItemTextForNER(storyId, itemId, translationTool, property, original, updateItem);
		if(StringUtils.isBlank(textAndLanguage.get(EnrichmentConstants.POJOFieldText)) || StringUtils.isBlank(textAndLanguage.get(EnrichmentConstants.POJOFieldLanguage))) {
			return result;
		}
		String textForNer = textAndLanguage.get(EnrichmentConstants.POJOFieldText);
		String languageForNer = textAndLanguage.get(EnrichmentConstants.POJOFieldLanguage);
		
		//sometimes some fields for NER can be empty for items which causes problems in the method applyNERTools
		Set<ObjectId> namedEntitiesToUpdateLinking = updatedNamedEntitiesForText(nerTools, textForNer, languageForNer, property, storyId, itemId, linking, true);
		//add linking to the named entities
		for(ObjectId neId : namedEntitiesToUpdateLinking) {
			NamedEntityImpl ne = persistentNamedEntityService.findNamedEntity(neId);
			result.add(ne);
			List<PositionEntityImpl> positions = persistentPositionEntityService.findPositionEntities(neId);
			boolean neUpdatedLinking = nerLinkingService.addLinkingInformation(ne, positions, linking);
			boolean neUpdatedPrefWikiId=wikidataService.computePreferredWikidataIds(ne, positions, true);
			if(neUpdatedLinking || neUpdatedPrefWikiId) {
				persistentNamedEntityService.saveNamedEntity(ne);
			}
		}
		return result;

	}
	
	@Async
	public CompletableFuture<Boolean> parallelLinking(NamedEntityImpl ne, List<String> linking) throws Exception {
		List<PositionEntityImpl> peOfNe = persistentPositionEntityService.findPositionEntities(ne.get_id());
		nerLinkingService.addLinkingInformation(ne, peOfNe, linking);
		wikidataService.computePreferredWikidataIds(ne, peOfNe, true);
		persistentNamedEntityService.saveNamedEntity(ne);
		return CompletableFuture.completedFuture(true);
	}

	/**
 	 * When this function is called for multiple ner tools one after another, make sure it is first called with the nerTool="DBpedia_Spotlight". 
 	 * For each ner tool the analysis is done separately because different tools may find
	 * the same entities on different positions in the text and we would like to separate those results,
	 * otherwise all positions of the entities would be in the same list and it cannot be clear which positions
	 * belong to which ner tool analyser.
	 * @param nerTool
	 * @param textForNer
	 * @param languageForNer
	 * @param fieldType
	 * @param storyId
	 * @param itemId
	 * @param linking
	 * @param matchType
	 * @throws Exception
	 */
	public Set<ObjectId> updatedNamedEntitiesForText(List<String> nerTools, String textForNer, String languageForNer, String fieldType, String storyId, String itemId, List<String> linking, boolean matchType) throws Exception {
		List<String> nerToolsReordered = new ArrayList<String>(nerTools);
		//the first analyzed NER tool must be DBpedia_Spotlight
		if(nerToolsReordered.contains(NerTools.Dbpedia.getStringValue())) {
			int itemPos = nerToolsReordered.indexOf(NerTools.Dbpedia.getStringValue());
			nerToolsReordered.remove(itemPos);
			nerToolsReordered.add(0, NerTools.Dbpedia.getStringValue());
		}
		
		Set<ObjectId> namedEntityObjectsToRecomputeLinking = new HashSet<>();
		for(String nerTool:nerToolsReordered) {
			TreeMap<String, List<NamedEntityImpl>> tmpResult = applyNERTools(nerTool, textForNer, languageForNer, fieldType, storyId, itemId);
			if(tmpResult==null) {
				continue;
			}
			for (String classificationType : tmpResult.keySet()) {
				
				if (isRestrictedClassificationType(classificationType)) continue;
				
				for (NamedEntityImpl tmpNamedEntity : tmpResult.get(classificationType)) {
					
					//agent entities should have at least 2 parts to be linked (name and surname)
					if(tmpNamedEntity.getType().equalsIgnoreCase(NERClassification.AGENT.toString()) && HelperFunctions.toArray(tmpNamedEntity.getLabel(),null).length<2) {
						continue;
					}
	
					NamedEntityImpl dbNamedEntity = persistentNamedEntityService.findEqualNamedEntity(tmpNamedEntity);
					saveNamedEntityAndPositions(tmpNamedEntity, dbNamedEntity, nerTool, linking, namedEntityObjectsToRecomputeLinking);					
				}	
			}
		}
		return namedEntityObjectsToRecomputeLinking;
	}
	
	private boolean isRestrictedClassificationType(String type) {
		if(!type.equalsIgnoreCase(NERClassification.AGENT.toString()) 
			&& !type.equalsIgnoreCase(NERClassification.PLACE.toString())) {
			return true;
		}
		else return false;
	}
	
	private void saveNamedEntityAndPositions (NamedEntityImpl newNamedEntity, NamedEntityImpl existingNamedEntity, String nerTool, List<String> linking, Set<ObjectId> namedEntityObjectsToRecomputeLinking) throws Exception {
		if(existingNamedEntity != null) {
			boolean foundMatchingPosition=false;
			//update existing position entity if it exists
			List<PositionEntityImpl> existingPosition = 
					persistentPositionEntityService.findPositionEntities(
							existingNamedEntity.get_id(),
							newNamedEntity.getPositionEntity().getStoryId(),
							newNamedEntity.getPositionEntity().getItemId(),
							newNamedEntity.getPositionEntity().getFieldUsedForNER()
							);
			
			if(! existingPosition.isEmpty()) {
				boolean positionEntityHasChanged=false;
				//update positions and ner tools
				for(Map.Entry<Integer, String> newOffset : newNamedEntity.getPositionEntity().getOffsetsTranslatedText().entrySet()) {
					Integer newOffsetKey = newOffset.getKey();
					String newOffsetValue = newOffset.getValue();
					if(existingPosition.get(0).getOffsetsTranslatedText().containsKey(newOffsetKey)) {
						String existingOffsetNerTools = existingPosition.get(0).getOffsetsTranslatedText().get(newOffsetKey);
						if(! existingOffsetNerTools.contains(newOffsetValue)) {
							existingPosition.get(0).getOffsetsTranslatedText().put(newOffsetKey, existingOffsetNerTools + "," + newOffsetValue);
							positionEntityHasChanged=true;
						}
					}
					else {
						existingPosition.get(0).getOffsetsTranslatedText().put(newOffsetKey, newOffsetValue);
						positionEntityHasChanged=true;
					}
				}
				
				if(positionEntityHasChanged) {
					persistentPositionEntityService.savePositionEntity(existingPosition.get(0));
					namedEntityObjectsToRecomputeLinking.add(existingNamedEntity.get_id());
				}
				
				foundMatchingPosition=true;
				//update the dbpedia id in case that existing entity did not have one
				if(existingNamedEntity.getDBpediaId()==null && newNamedEntity.getDBpediaId()!=null) {
					existingNamedEntity.setDBpediaId(newNamedEntity.getDBpediaId());
					persistentNamedEntityService.saveNamedEntity(existingNamedEntity);
				}	
			}	
			//if no  existing position is found, add the position entity as new to the existing named entity 
			if(! foundMatchingPosition) {
				newNamedEntity.getPositionEntity().setNamedEntityId(existingNamedEntity.get_id());
				persistentPositionEntityService.savePositionEntity(newNamedEntity.getPositionEntity());
				namedEntityObjectsToRecomputeLinking.add(existingNamedEntity.get_id());
				if(existingNamedEntity.getDBpediaId()==null && newNamedEntity.getDBpediaId()!=null) {
					existingNamedEntity.setDBpediaId(newNamedEntity.getDBpediaId());
					persistentNamedEntityService.saveNamedEntity(existingNamedEntity);
				}				
			}
		}
		else {
			persistentNamedEntityService.saveNamedEntity(newNamedEntity);
			newNamedEntity.getPositionEntity().setNamedEntityId(newNamedEntity.get_id());
			persistentPositionEntityService.savePositionEntity(newNamedEntity.getPositionEntity());
			namedEntityObjectsToRecomputeLinking.add(persistentNamedEntityService.findNamedEntities(newNamedEntity.getLabel(), newNamedEntity.getType(), newNamedEntity.getDBpediaId()).get(0).get_id());
		}
	}

	public Map<String, String> getStoryTextForNER (String storyId, String translationTool, String type, boolean original, boolean updateStoryBool) throws Exception
	{
		Map<String, String> results = new HashMap<>();
		List<String> fiedlsToUpdate = new ArrayList<String>();
		fiedlsToUpdate.add(type);

		if(original) {
			//update story from Transcribathon
			StoryEntityImpl updatedStory=null;
			if(updateStoryBool) {
				updatedStory = enrichmentStoryAndItemStorageService.updateStoryFromTranscribathon(storyId, fiedlsToUpdate);
			}	
			else {
				updatedStory = persistentStoryEntityService.findStoryEntity(storyId);
			}
			
			if(updatedStory==null) {
				return results;
			}
			
			if(EnrichmentConstants.STORY_ITEM_DESCRIPTION.equalsIgnoreCase(type)) 
			{
				results.put(EnrichmentConstants.POJOFieldText, updatedStory.getDescription());
				results.put(EnrichmentConstants.POJOFieldLanguage, updatedStory.getLanguageDescription());				
			}
			else if(EnrichmentConstants.STORY_ITEM_SUMMARY.equalsIgnoreCase(type))
			{
				results.put(EnrichmentConstants.POJOFieldText, updatedStory.getSummary());
				results.put(EnrichmentConstants.POJOFieldLanguage, updatedStory.getLanguageSummary());
			}
			else if(EnrichmentConstants.STORY_ITEM_TRANSCRIPTION.equalsIgnoreCase(type))
			{
				results.put(EnrichmentConstants.POJOFieldText, updatedStory.getTranscriptionText());
				results.put(EnrichmentConstants.POJOFieldLanguage, ModelUtils.getMainTranslationLanguage(updatedStory));
			}
			return results;	
		}
		
		//update story from Transcribathon
		StoryEntityImpl updatedStory=null;
		if(updateStoryBool) {
			updatedStory = enrichmentStoryAndItemStorageService.updateStoryFromTranscribathon(storyId, fiedlsToUpdate);
		}	
		else {
			updatedStory = persistentStoryEntityService.findStoryEntity(storyId);
		}

		if(updatedStory==null) {
			return results;
		}
		
		String translatedText=enrichmentTranslationService.translateStory(updatedStory, type, translationTool);
		if(! StringUtils.isBlank(translatedText))
		{
			results.put(EnrichmentConstants.POJOFieldText, translatedText);
			results.put(EnrichmentConstants.POJOFieldLanguage, EnrichmentConstants.defaultTargetTranslationLang2Letter);
		}
		return results;
	}

	public Map<String, String> getItemTextForNER (String storyId, String itemId, String translationTool, String property, boolean original, boolean updateItemBool) throws Exception
	{
		Map<String, String> results = new HashMap<>();
		if(original) {
			//update item from Transcribathon
			ItemEntityImpl updatedItem=null;
			if(updateItemBool) {
				updatedItem = enrichmentStoryAndItemStorageService.updateItemFromTranscribathon(storyId, itemId);
			}		
			else {
				updatedItem = persistentItemEntityService.findItemEntity(storyId, itemId);
			}
			
			if(updatedItem==null) {
				return results;
			}
	
			results.put(EnrichmentConstants.POJOFieldText, updatedItem.getTranscriptionText());
			results.put(EnrichmentConstants.POJOFieldLanguage, ModelUtils.getOnlyTranscriptionLanguage(updatedItem));

			return results;
		}

		//update item from Transcribathon
		ItemEntityImpl updatedItem=null;
		if(updateItemBool) {
			updatedItem = enrichmentStoryAndItemStorageService.updateItemFromTranscribathon(storyId, itemId);
		}		
		else {
			updatedItem = persistentItemEntityService.findItemEntity(storyId, itemId);
		}
		
		if(updatedItem==null) {
			return results;
		}

		String translatedText = enrichmentTranslationService.translateItem(updatedItem, property, translationTool);
		if(! StringUtils.isBlank(translatedText))
		{
			results.put(EnrichmentConstants.POJOFieldText, translatedText);
			results.put(EnrichmentConstants.POJOFieldLanguage, EnrichmentConstants.defaultTargetTranslationLang2Letter);
		}
		return results;
	}

	private TreeMap<String, List<NamedEntityImpl>> applyNERTools (String nerTool, String text, String language, String fieldUsedForNER, String storyId, String itemId) throws Exception {
		NERService tmpTool=null;
		
		if(NerTools.Stanford.getStringValue().equals(nerTool)) {
			tmpTool = nerStanfordService;
		}
		else if(NerTools.Dbpedia.getStringValue().equals(nerTool)) {
				tmpTool = nerDBpediaSpotlightService;
		}
		else {
			throw new ParamValidationException(I18nConstants.INVALID_PARAM_VALUE, EnrichmentConstants.NER_TOOLS, nerTool);
		}
		
		adaptNERServiceEndpointBasedOnLanguage(tmpTool, language);
		
		TreeMap<String, List<NamedEntityImpl>> currentResult = tmpTool.identifyNER(text);
		updateNamedEntityPositionEntity(currentResult, storyId, itemId, fieldUsedForNER, nerTool);
		return currentResult;		
	}
	
	private void updateNamedEntityPositionEntity(TreeMap<String, List<NamedEntityImpl>> mapCurrentResult,
		String storyId, String itemId, String fieldUsedForNER, String tool_string) {
		
		if(mapCurrentResult==null || mapCurrentResult.isEmpty()) return;
		
		for(Map.Entry<String, List<NamedEntityImpl>> categoryList : mapCurrentResult.entrySet()) {
			for(NamedEntityImpl entity : categoryList.getValue()) {
				if(entity.getPositionEntity()==null) continue;
				PositionEntityImpl pos = entity.getPositionEntity();
				pos.setStoryId(storyId);
				pos.setItemId(itemId);
				pos.setFieldUsedForNER(fieldUsedForNER);
			}
		}
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
		if(EnrichmentConstants.SUPPORTED_NER_LANGUAGES.contains(languageForNER)) {
			service.setEndpoint(endpoint.replaceAll("/en/", "/"+languageForNER+"/"));
		}
	}

	public NamedEntityAnnotationCollection getAnnotations(String storyId, String itemId, String property) throws Exception {
		List<NamedEntityAnnotationImpl> entities = persistentNamedEntityAnnotationService.findAnnotations(storyId, itemId, property, EnrichmentConstants.MONGO_SKIP_FIELD, EnrichmentConstants.MONGO_SKIP_LIST_FIELD);
		return new NamedEntityAnnotationCollection(configuration.getAnnotationsIdBaseUrl(), configuration.getAnnotationsTargetStoriesBaseUrl(), configuration.getAnnotationsTargetItemsBaseUrl() , configuration.getAnnotationsCreator(), entities, storyId, itemId);
	}
	
	public NamedEntityAnnotationCollection createAnnotationsForStoryOrItem(String storyId, String itemId, String property) throws Exception {
		List<PositionEntityImpl> positionEntities = persistentPositionEntityService.findPositionEntities(EnrichmentConstants.MONGO_SKIP_OBJECT_ID_FIELD, storyId, itemId, property);
		List<NamedEntityAnnotationImpl> namedEntityAnnos = new ArrayList<>();
		for(PositionEntityImpl pe : positionEntities) {
			NamedEntityAnnotationImpl ne = createAndSaveAnnotationsForPosition(pe);
			if(ne!=null) {
				namedEntityAnnos.add(ne);
			}
		}

		return new NamedEntityAnnotationCollection(configuration.getAnnotationsIdBaseUrl(), configuration.getAnnotationsTargetStoriesBaseUrl(), configuration.getAnnotationsTargetItemsBaseUrl(), configuration.getAnnotationsCreator(), namedEntityAnnos, storyId, itemId);
	}
	
	public NamedEntityAnnotationImpl createAndSaveAnnotationsForPosition(PositionEntityImpl pe) throws Exception {		
		List<String> foundByNer=new ArrayList<>();
		List<String> linkedByNer=new ArrayList<>();
		NamedEntityImpl ne = persistentNamedEntityService.findNamedEntity(pe.getNamedEntityId());
		String preferredWikiId=choosePreferredWikiId(pe, ne, foundByNer, linkedByNer);
		
		//create annotation
		if(preferredWikiId!=null) {
			//compute the annotation fields
			String wikidataJSONLocal = HelperFunctions.getWikidataJsonFromLocalFileCache(configuration.getEnrichWikidataDirectory(), preferredWikiId);
			String wikidataJSON=wikidataJSONLocal;
			if(StringUtils.isBlank(wikidataJSON)) 	
			{
				logger.info("Wikidata entity does not exist in a local file cache!");
				wikidataJSON = wikidataService.getWikidataJSONFromRemote(preferredWikiId);
				HelperFunctions.saveWikidataJsonToLocalFileCache(configuration.getEnrichWikidataDirectory(), preferredWikiId, wikidataJSON);					
			}
			
			//compute the pref label
			String entityPrefLabel = ne.getLabel();
			Map<String,List<String>> prefLabelMap = null;
			List<List<String>> jsonElement = null;
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(wikidataJSON,EnrichmentConstants.PREFLABEL_JSONPROP);
			if(!jsonElement.isEmpty())
			{ 
				prefLabelMap = HelperFunctions.convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
				if(prefLabelMap!=null && prefLabelMap.get("en")!=null && prefLabelMap.get("en").size()>0)
					entityPrefLabel = prefLabelMap.get("en").get(0);
			}

			//compute other body fields
			String body_description = null;
			String body_givenName = "";
			String body_familyName = "";
			List<String> body_professionOrOccupation = new ArrayList<>();
			Float body_lat = null;
			Float body_long = null;
			
			//body description
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(wikidataJSON,EnrichmentConstants.DESCRIPTION_JSONPROP);
			if(!jsonElement.isEmpty())
			{
				for(int i=0;i<jsonElement.size();i++) {
					if("en".equalsIgnoreCase(jsonElement.get(i).get(0))) {
						body_description=jsonElement.get(i).get(1);
						break;
					}
				}
			}
			
			//getting the given name and family name for agents
			if(ne.getType().equalsIgnoreCase(NERClassification.AGENT.toString())) {
				//getting the given name
				jsonElement = wikidataService.getJSONFieldFromWikidataJSON(wikidataJSON,EnrichmentConstants.GIVEN_NAME_JSONPROP);
				if(!jsonElement.isEmpty()) 
				{
					//for each collected name in the form of wikidata Q-identifier, fetch the english label for that wikidata id
					for(int i=0;i<jsonElement.size();i++)
					{
						String givenNameWikiId=EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL + jsonElement.get(i).get(0);
						String givenNameWikiJson=wikidataService.getWikidataJSONFromRemote(givenNameWikiId);
						List<List<String>> givenNameJsonElement = wikidataService.getJSONFieldFromWikidataJSON(givenNameWikiJson,"labels.en.value");
						if(!givenNameJsonElement.isEmpty()) {
							body_givenName=body_givenName + givenNameJsonElement.get(0).get(0) + " ";
						}
					}					
					if(! StringUtils.isBlank(body_givenName)) {
						body_givenName = StringUtils.substring(body_givenName, 0, body_givenName.length()-1);
					}
				}
				
				//getting the family name
				jsonElement = wikidataService.getJSONFieldFromWikidataJSON(wikidataJSON,EnrichmentConstants.FAMILY_NAME_JSONPROP);
				if(!jsonElement.isEmpty()) 
				{
					//for each collected family name in the form of wikidata Q-identifier, fetch the english label for that wikidata id
					for(int i=0;i<jsonElement.size();i++)
					{
						String familyNameWikiId=EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL + jsonElement.get(i).get(0);
						String familyNameWikiJson=wikidataService.getWikidataJSONFromRemote(familyNameWikiId);
						List<List<String>> familyNameJsonElement = wikidataService.getJSONFieldFromWikidataJSON(familyNameWikiJson,"labels.en.value");
						if(!familyNameJsonElement.isEmpty()) {
							body_familyName=body_familyName + familyNameJsonElement.get(0).get(0) + " ";
						}
					}
					if(! StringUtils.isBlank(body_familyName)) {
						body_familyName = StringUtils.substring(body_familyName, 0, body_familyName.length()-1);
					}					
				}
				
				//getting the profession or occupation
				jsonElement = wikidataService.getJSONFieldFromWikidataJSON(wikidataJSON,EnrichmentConstants.PROFESSIONOROCCUPATION_JSONPROP);
				if(!jsonElement.isEmpty()) 
				{
					for(int i=0;i<jsonElement.size();i++)
					{
						String occupationWikiId=EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL + jsonElement.get(i).get(0);
						String occupationWikiJson=wikidataService.getWikidataJSONFromRemote(occupationWikiId);
						List<List<String>> occupationJsonElement = wikidataService.getJSONFieldFromWikidataJSON(occupationWikiJson,"labels.en.value");
						if(!occupationJsonElement.isEmpty()) {
							body_professionOrOccupation.add(occupationJsonElement.get(0).get(0));
						}
					}
				}				
			}
			else if(ne.getType().equalsIgnoreCase(NERClassification.PLACE.toString())) {
				jsonElement = wikidataService.getJSONFieldFromWikidataJSON(wikidataJSON,EnrichmentConstants.LATITUDE_JSONPROP);
				if(!jsonElement.isEmpty()) 
				{
					body_lat = Float.valueOf(jsonElement.get(0).get(0));
				}
				
				jsonElement = wikidataService.getJSONFieldFromWikidataJSON(wikidataJSON,EnrichmentConstants.LONGITUDE_JSONPROP);
				if(!jsonElement.isEmpty()) 
				{
					body_long = Float.valueOf(jsonElement.get(0).get(0));
				}
			}
			
			//computing score
			double score=computeScoreForAnnotations(foundByNer, linkedByNer);
									
			NamedEntityAnnotationImpl newAnno = new NamedEntityAnnotationImpl(configuration.getAnnotationsIdBaseUrl(),configuration.getAnnotationsTargetItemsBaseUrl(),pe.getStoryId(), pe.getItemId(), preferredWikiId, ne.getLabel(), entityPrefLabel, pe.getFieldUsedForNER(), ne.getType(), score, foundByNer, linkedByNer,
					body_description, body_givenName, body_familyName, body_professionOrOccupation, body_lat, body_long);
			persistentNamedEntityAnnotationService.saveNamedEntityAnnotation(newAnno);
			return newAnno;
		}
		return null;
	}

	@Async
	public CompletableFuture<Boolean> createAndSaveAnnotationsForPosition_parallel(PositionEntityImpl pe) throws Exception {		
		List<String> foundByNer=new ArrayList<>();
		List<String> linkedByNer=new ArrayList<>();
		NamedEntityImpl ne = persistentNamedEntityService.findNamedEntity(pe.getNamedEntityId());
		String preferredWikiId=choosePreferredWikiId(pe, ne, foundByNer, linkedByNer);
		
		//create annotation
		if(preferredWikiId!=null) {
			//compute the annotation fields
			String wikidataJSONLocal = HelperFunctions.getWikidataJsonFromLocalFileCache(configuration.getEnrichWikidataDirectory(), preferredWikiId);
			String wikidataJSON=wikidataJSONLocal;
			if(StringUtils.isBlank(wikidataJSON)) 	
			{
				logger.info("Wikidata entity does not exist in a local file cache!");
				wikidataJSON = wikidataService.getWikidataJSONFromRemote(preferredWikiId);
				HelperFunctions.saveWikidataJsonToLocalFileCache(configuration.getEnrichWikidataDirectory(), preferredWikiId, wikidataJSON);					
			}
			
			//compute the pref label
			String entityPrefLabel = ne.getLabel();
			Map<String,List<String>> prefLabelMap = null;
			List<List<String>> jsonElement = null;
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(wikidataJSON,EnrichmentConstants.PREFLABEL_JSONPROP);
			if(!jsonElement.isEmpty())
			{ 
				prefLabelMap = HelperFunctions.convertListOfListOfStringToMapOfStringAndListOfString(jsonElement);
				if(prefLabelMap!=null && prefLabelMap.get("en")!=null && prefLabelMap.get("en").size()>0)
					entityPrefLabel = prefLabelMap.get("en").get(0);
			}

			//compute other body fields
			String body_description = null;
			String body_givenName = "";
			String body_familyName = "";
			List<String> body_professionOrOccupation = new ArrayList<>();
			Float body_lat = null;
			Float body_long = null;
			
			//body description
			jsonElement = wikidataService.getJSONFieldFromWikidataJSON(wikidataJSON,EnrichmentConstants.DESCRIPTION_JSONPROP);
			if(!jsonElement.isEmpty())
			{
				for(int i=0;i<jsonElement.size();i++) {
					if("en".equalsIgnoreCase(jsonElement.get(i).get(0))) {
						body_description=jsonElement.get(i).get(1);
						break;
					}
				}
			}
			
			//getting the given name and family name for agents
			if(ne.getType().equalsIgnoreCase(NERClassification.AGENT.toString())) {
				//getting the given name
				jsonElement = wikidataService.getJSONFieldFromWikidataJSON(wikidataJSON,EnrichmentConstants.GIVEN_NAME_JSONPROP);
				if(!jsonElement.isEmpty()) 
				{
					//for each collected name in the form of wikidata Q-identifier, fetch the english label for that wikidata id
					for(int i=0;i<jsonElement.size();i++)
					{
						String givenNameWikiId=EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL + jsonElement.get(i).get(0);
						String givenNameWikiJson=wikidataService.getWikidataJSONFromRemote(givenNameWikiId);
						List<List<String>> givenNameJsonElement = wikidataService.getJSONFieldFromWikidataJSON(givenNameWikiJson,"labels.en.value");
						if(!givenNameJsonElement.isEmpty()) {
							body_givenName=body_givenName + givenNameJsonElement.get(0).get(0) + " ";
						}
					}					
					if(! StringUtils.isBlank(body_givenName)) {
						body_givenName = StringUtils.substring(body_givenName, 0, body_givenName.length()-1);
					}
				}
				
				//getting the family name
				jsonElement = wikidataService.getJSONFieldFromWikidataJSON(wikidataJSON,EnrichmentConstants.FAMILY_NAME_JSONPROP);
				if(!jsonElement.isEmpty()) 
				{
					//for each collected family name in the form of wikidata Q-identifier, fetch the english label for that wikidata id
					for(int i=0;i<jsonElement.size();i++)
					{
						String familyNameWikiId=EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL + jsonElement.get(i).get(0);
						String familyNameWikiJson=wikidataService.getWikidataJSONFromRemote(familyNameWikiId);
						List<List<String>> familyNameJsonElement = wikidataService.getJSONFieldFromWikidataJSON(familyNameWikiJson,"labels.en.value");
						if(!familyNameJsonElement.isEmpty()) {
							body_familyName=body_familyName + familyNameJsonElement.get(0).get(0) + " ";
						}
					}
					if(! StringUtils.isBlank(body_familyName)) {
						body_familyName = StringUtils.substring(body_familyName, 0, body_familyName.length()-1);
					}					
				}
				
				//getting the profession or occupation
				jsonElement = wikidataService.getJSONFieldFromWikidataJSON(wikidataJSON,EnrichmentConstants.PROFESSIONOROCCUPATION_JSONPROP);
				if(!jsonElement.isEmpty()) 
				{
					for(int i=0;i<jsonElement.size();i++)
					{
						String occupationWikiId=EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL + jsonElement.get(i).get(0);
						String occupationWikiJson=wikidataService.getWikidataJSONFromRemote(occupationWikiId);
						List<List<String>> occupationJsonElement = wikidataService.getJSONFieldFromWikidataJSON(occupationWikiJson,"labels.en.value");
						if(!occupationJsonElement.isEmpty()) {
							body_professionOrOccupation.add(occupationJsonElement.get(0).get(0));
						}
					}
				}				
			}
			else if(ne.getType().equalsIgnoreCase(NERClassification.PLACE.toString())) {
				jsonElement = wikidataService.getJSONFieldFromWikidataJSON(wikidataJSON,EnrichmentConstants.LATITUDE_JSONPROP);
				if(!jsonElement.isEmpty()) 
				{
					body_lat = Float.valueOf(jsonElement.get(0).get(0));
				}
				
				jsonElement = wikidataService.getJSONFieldFromWikidataJSON(wikidataJSON,EnrichmentConstants.LONGITUDE_JSONPROP);
				if(!jsonElement.isEmpty()) 
				{
					body_long = Float.valueOf(jsonElement.get(0).get(0));
				}
			}
			
			//computing score
			double score=computeScoreForAnnotations(foundByNer, linkedByNer);
									
			NamedEntityAnnotationImpl newAnno = new NamedEntityAnnotationImpl(configuration.getAnnotationsIdBaseUrl(),configuration.getAnnotationsTargetItemsBaseUrl(),pe.getStoryId(), pe.getItemId(), preferredWikiId, ne.getLabel(), entityPrefLabel, pe.getFieldUsedForNER(), ne.getType(), score, foundByNer, linkedByNer,
					body_description, body_givenName, body_familyName, body_professionOrOccupation, body_lat, body_long);
			persistentNamedEntityAnnotationService.saveNamedEntityAnnotation(newAnno);
			return CompletableFuture.completedFuture(true);
		}
		return CompletableFuture.completedFuture(false);
	}
	
	public String choosePreferredWikiId(PositionEntityImpl pe, NamedEntityImpl ne, List<String> foundByNer, List<String> linkedByNer) {
		//finds the position which is found by both stanford and dbpedia
		Optional<Integer> optNerToolsBothFound = pe.getOffsetsTranslatedText().entrySet().stream()
			.filter(e -> (e.getValue().contains(NerTools.Stanford.getStringValue()) && e.getValue().contains(NerTools.Dbpedia.getStringValue())))
			.map(Map.Entry::getKey)
			.findFirst();
		//find the position which is found only by dbpedia tool
		Optional<Integer> optNerToolsDbpedia = pe.getOffsetsTranslatedText().entrySet().stream()
				.filter(e -> e.getValue().contains(NerTools.Dbpedia.getStringValue()))
				.map(Map.Entry::getKey)
				.findFirst();
		//find the position which is found only by stanford tool
		Optional<Integer> optNerToolsStanford = pe.getOffsetsTranslatedText().entrySet().stream()
				.filter(e -> e.getValue().contains(NerTools.Stanford.getStringValue()))
				.map(Map.Entry::getKey)
				.findFirst();

		/*
		 * Here we check if the given named entity is found by stanford, dbpedia or both,
		 * and if it is linked by stanford, dbpedia, or both. Based on that the annotations are generated,
		 * by assigning the proper score.
		 */
		String preferredWikiId=null;
		if(optNerToolsBothFound.isPresent()) {
			foundByNer.add(NerTools.Stanford.getStringValue());
			foundByNer.add(NerTools.Dbpedia.getStringValue());
			if(ne.getPrefWikiIdBothStanfordAndDbpedia()!=null) {
				linkedByNer.add(NerTools.Stanford.getStringValue());
				linkedByNer.add(NerTools.Dbpedia.getStringValue());
				preferredWikiId=ne.getPrefWikiIdBothStanfordAndDbpedia();
			}	
			//this is probably an impossible case, but still we check it
			else if(ne.getPrefWikiIdOnlyDbpedia()!=null) {
				linkedByNer.add(NerTools.Dbpedia.getStringValue());
				preferredWikiId=ne.getPrefWikiIdOnlyDbpedia();
			}
			/*
			 * this is also impossible case, taking into account the algorithm, but we keep the code,
			 * for the same of completeness
			 */
			else if(ne.getPrefWikiIdOnlyStanford()!=null) {
				linkedByNer.add(NerTools.Stanford.getStringValue());
				preferredWikiId=ne.getPrefWikiIdOnlyStanford();					
			}
		}
		else {
			if(optNerToolsDbpedia.isPresent()) {
				if(ne.getPrefWikiIdOnlyDbpedia()!=null) {
					foundByNer.add(NerTools.Dbpedia.getStringValue());
					linkedByNer.add(NerTools.Dbpedia.getStringValue());
					preferredWikiId=ne.getPrefWikiIdOnlyDbpedia();
				}
			}
			if(preferredWikiId==null && optNerToolsStanford.isPresent()) {
				if(ne.getPrefWikiIdOnlyStanford()!=null) {
					foundByNer.add(NerTools.Stanford.getStringValue());
					linkedByNer.add(NerTools.Stanford.getStringValue());
					preferredWikiId=ne.getPrefWikiIdOnlyStanford();
				}
			}
		}
		return preferredWikiId;		
	}

	
	public List<NamedEntityAnnotationImpl> getStoryOrItemAnnotation(String storyId, String itemId, String wikidataEntity) throws HttpException, IOException {
		String wikidataIdGenerated=null;
		wikidataIdGenerated = WikidataUtils.getWikidataEntityUri(wikidataEntity);
		return persistentNamedEntityAnnotationService.findAnnotations(storyId, itemId, EnrichmentConstants.MONGO_SKIP_FIELD, wikidataIdGenerated, EnrichmentConstants.MONGO_SKIP_LIST_FIELD);
	}
	
	private double computeScoreForAnnotations(List<String> foundByNer, List<String> linkedByNer) {
		int foundByDbpedia=0;
		int foundByStanford=0;
		int linkedByDbpedia=0;
		int linkedByStanford=0;
		if(! foundByNer.isEmpty()) {
			if(foundByNer.contains(NerTools.Dbpedia.getStringValue()) && foundByNer.contains(NerTools.Stanford.getStringValue()) ) {
				foundByDbpedia=1;
				foundByStanford=1;
			}
			else if(foundByNer.contains(NerTools.Dbpedia.getStringValue())) {
				foundByDbpedia=1;
			}
			else if(foundByNer.contains(NerTools.Stanford.getStringValue())) {
				foundByStanford=1;
			}
		}
		if(! linkedByNer.isEmpty()) {
			if(linkedByNer.contains(NerTools.Dbpedia.getStringValue()) && linkedByNer.contains(NerTools.Stanford.getStringValue()) ) {
				linkedByDbpedia=1;
				linkedByStanford=1;
			}
			else if(linkedByNer.contains(NerTools.Dbpedia.getStringValue())) {
				linkedByDbpedia=1;
			}
			else if(linkedByNer.contains(NerTools.Stanford.getStringValue())) {
				linkedByStanford=1;
			}
		}
		
		return 0.2*foundByDbpedia + 0.1*foundByStanford + 0.4*linkedByDbpedia + 0.3*linkedByStanford;
	}
}
