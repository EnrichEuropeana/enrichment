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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.model.NamedEntityAnnotation;
import eu.europeana.enrichment.model.WikidataEntity;
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
import eu.europeana.enrichment.solr.exception.SolrServiceException;
import eu.europeana.enrichment.solr.model.vocabulary.EntitySolrFields;
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
	public void createNamedEntitiesForStory(String storyId, String property, List<String> nerTools, List<String> linking, String translationTool, boolean original, boolean updateStory, boolean removeExistingEntities) throws Exception {
		StoryEntityImpl story = persistentStoryEntityService.findStoryEntity(storyId);
		if(removeExistingEntities) {
			//delete existing position and named entities
			persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(story.getStoryId(), null, property);
		}
		String [] textAndLanguage = getStoryTextForNER(story, translationTool, property, original, updateStory);
		if(StringUtils.isBlank(textAndLanguage[0]) || StringUtils.isBlank(textAndLanguage[1])) {
			return;
		}
		String textForNer = textAndLanguage[0];
		String languageForNer = textAndLanguage[1];
		
		//sometimes some fields for NER can be empty for items which causes problems in the method applyNERTools
		Set<ObjectId> namedEntitiesToUpdateLinking = updatedNamedEntitiesForText(nerTools, textForNer, languageForNer, property, story.getStoryId(), null, linking, true);
		//add linking to the named entities
		for(ObjectId neId : namedEntitiesToUpdateLinking) {
			NamedEntityImpl ne = persistentNamedEntityService.findNamedEntity(neId);
			List<PositionEntityImpl> positions = persistentPositionEntityService.findPositionEntities(neId);
			boolean neUpdatedLinking = nerLinkingService.addLinkingInformation(ne, positions, linking);
			boolean neUpdatedPrefWikiId=wikidataService.computePreferredWikidataIds(ne, positions, true);
			if(neUpdatedLinking || neUpdatedPrefWikiId) {
				persistentNamedEntityService.saveNamedEntity(ne);
			}
		}
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
	public void createNamedEntitiesForItem(String storyId, String itemId, String property, List<String> nerTools, List<String> linking, String translationTool, boolean original, boolean updateItem, boolean removeExistingEntities) throws Exception {
		ItemEntityImpl item = persistentItemEntityService.findItemEntity(storyId, itemId);
		if(removeExistingEntities) {
			//delete existing position and named entities for the given item
			persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(item.getStoryId(), item.getItemId(), property);
		}
		String [] textAndLanguage = getItemTextForNER(item, translationTool, property, original, updateItem);
		if(StringUtils.isBlank(textAndLanguage[0]) || StringUtils.isBlank(textAndLanguage[1])) {
			return;
		}
		String textForNer = textAndLanguage[0];
		String languageForNer = textAndLanguage[1];
		
		//sometimes some fields for NER can be empty for items which causes problems in the method applyNERTools
		Set<ObjectId> namedEntitiesToUpdateLinking = updatedNamedEntitiesForText(nerTools, textForNer, languageForNer, property, item.getStoryId(), item.getItemId(), linking, true);
		//add linking to the named entities
		for(ObjectId neId : namedEntitiesToUpdateLinking) {
			NamedEntityImpl ne = persistentNamedEntityService.findNamedEntity(neId);
			List<PositionEntityImpl> positions = persistentPositionEntityService.findPositionEntities(neId);
			boolean neUpdatedLinking = nerLinkingService.addLinkingInformation(ne, positions, linking);
			boolean neUpdatedPrefWikiId=wikidataService.computePreferredWikidataIds(ne, positions, true);
			if(neUpdatedLinking || neUpdatedPrefWikiId) {
				persistentNamedEntityService.saveNamedEntity(ne);
			}
		}

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
	
					NamedEntityImpl dbNamedEntity = persistentNamedEntityService.findNamedEntitiesByNerTool(tmpNamedEntity);
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
			PositionEntityImpl existingPosition = 
					persistentPositionEntityService.findPositionEntity(
							existingNamedEntity.get_id(),
							newNamedEntity.getPositionEntity().getStoryId(),
							newNamedEntity.getPositionEntity().getItemId(),
							newNamedEntity.getPositionEntity().getFieldUsedForNER()
							);
			
			if(existingPosition!=null) {
				boolean positionEntityHasChanged=false;
				//update positions and ner tools
				for(Map.Entry<Integer, String> newOffset : newNamedEntity.getPositionEntity().getOffsetsTranslatedText().entrySet()) {
					Integer newOffsetKey = newOffset.getKey();
					String newOffsetValue = newOffset.getValue();
					if(existingPosition.getOffsetsTranslatedText().containsKey(newOffsetKey)) {
						String existingOffsetNerTools = existingPosition.getOffsetsTranslatedText().get(newOffsetKey);
						if(! existingOffsetNerTools.contains(newOffsetValue)) {
							existingPosition.getOffsetsTranslatedText().put(newOffsetKey, existingOffsetNerTools + "," + newOffsetValue);
							positionEntityHasChanged=true;
						}
					}
					else {
						existingPosition.getOffsetsTranslatedText().put(newOffsetKey, newOffsetValue);
						positionEntityHasChanged=true;
					}
				}
				
				if(positionEntityHasChanged) {
					persistentPositionEntityService.savePositionEntity(existingPosition);
					namedEntityObjectsToRecomputeLinking.add(existingNamedEntity.get_id());
				}
				
				foundMatchingPosition=true;
				//update the dbpedia id in case that existing entity did not have one
				if(existingNamedEntity.getDBpediaId()==null && newNamedEntity.getDBpediaId()!=null) {
					existingNamedEntity.setDBpediaId(newNamedEntity.getDBpediaId());
					persistentNamedEntityService.saveNamedEntity(existingNamedEntity);
				}	
			}	
			//if no  existing position is found, and the position entity as new to the existing named entity 
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
			namedEntityObjectsToRecomputeLinking.add(persistentNamedEntityService.findNamedEntity(newNamedEntity.getLabel(), newNamedEntity.getType(), newNamedEntity.getDBpediaId()).get_id());
		}
	}

	public String [] getStoryTextForNER (StoryEntityImpl dbStory, String translationTool, String type, boolean original, boolean updateStory) throws Exception
	{
		String [] results =  new String [2];
		results[0]=null;
		results[1]=null;

		if(original) {
			//update story from Transcribathon
			StoryEntityImpl updatedStory=dbStory;
			if(updateStory) {
				updatedStory = enrichmentStoryAndItemStorageService.updateStoryFromTranscribathon(dbStory);
			}		
			if(updatedStory==null) {
				return results;
			}
			
			if(EnrichmentConstants.STORY_ITEM_DESCRIPTION.equalsIgnoreCase(type)) 
			{
				results[0] = updatedStory.getDescription();
				results[1] = updatedStory.getLanguageDescription();
				
			}
			else if(EnrichmentConstants.STORY_ITEM_SUMMARY.equalsIgnoreCase(type))
			{
				results[0] = updatedStory.getSummary();
				results[1] = updatedStory.getLanguageSummary();
			}
			else if(EnrichmentConstants.STORY_ITEM_TRANSCRIPTION.equalsIgnoreCase(type))
			{
				results[0] = updatedStory.getTranscriptionText();
				results[1] = ModelUtils.getMainTranslationLanguage(updatedStory);
			}
			return results;	
		}
		
		//update story from Transcribathon
		StoryEntityImpl updatedStory=dbStory;
		if(updateStory) {
			updatedStory = enrichmentStoryAndItemStorageService.updateStoryFromTranscribathon(dbStory);
		}		
		if(updatedStory==null) {
			return results;
		}
		
		String translatedText=enrichmentTranslationService.translateStory(updatedStory, type, translationTool);
		if(! StringUtils.isBlank(translatedText))
		{
			results[0] = translatedText;
			results[1] = EnrichmentConstants.defaultTargetTranslationLanguage;
		}
		return results;
	}

	public String [] getItemTextForNER (ItemEntityImpl dbItem, String translationTool, String property, boolean original, boolean updateItem) throws Exception
	{
		String [] results =  new String [2];
		results[0]=null;
		results[1]=null;

		if(original) {
			//update item from Transcribathon
			ItemEntityImpl updatedItem=dbItem;
			if(updateItem) {
				updatedItem = enrichmentStoryAndItemStorageService.updateItemFromTranscribathon(dbItem);
			}		
			if(updatedItem==null) {
				return results;
			}
			
			results[0] = updatedItem.getTranscriptionText();
			results[1] = ModelUtils.getOnlyTranscriptionLanguage(updatedItem);
			return results;
		}

		//update item from Transcribathon
		ItemEntityImpl updatedItem=dbItem;
		if(updateItem) {
			updatedItem = enrichmentStoryAndItemStorageService.updateItemFromTranscribathon(dbItem);
		}		
		if(updatedItem==null) {
			return results;
		}

		String translatedText = enrichmentTranslationService.translateItem(updatedItem, property, translationTool);
		if(! StringUtils.isBlank(translatedText))
		{
			results[0] = translatedText;
			results[1] = EnrichmentConstants.defaultTargetTranslationLanguage;
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
		service.setEndpoint(endpoint.replaceAll("/en/", "/"+languageForNER+"/"));
	}

	public NamedEntityAnnotationCollection getAnnotations(String storyId, String itemId, String property) throws Exception {
		List<NamedEntityAnnotationImpl> entities = persistentNamedEntityAnnotationService.findNamedEntityAnnotation(storyId, itemId, property, null, null);
		return new NamedEntityAnnotationCollection(configuration.getAnnotationsIdBaseUrl(), configuration.getAnnotationsTargetStoriesBaseUrl(), configuration.getAnnotationsTargetItemsBaseUrl() , configuration.getAnnotationsCreator(), entities, storyId, itemId);
	}
	
	public NamedEntityAnnotationCollection createAnnotationsForStoryOrItem(String storyId, String itemId, String property) throws SolrServiceException, IOException {
		List<PositionEntityImpl> positionEntities = persistentPositionEntityService.findPositionEntities(storyId, itemId, property);
		List<NamedEntityAnnotationImpl> namedEntityAnnos = new ArrayList<>();
		for(PositionEntityImpl pe : positionEntities) {
			NamedEntityAnnotationImpl ne = createAnnotationsForPosition(pe);
			if(ne!=null) {
				namedEntityAnnos.add(ne);
			}
		}

		return new NamedEntityAnnotationCollection(configuration.getAnnotationsIdBaseUrl(), configuration.getAnnotationsTargetStoriesBaseUrl(), configuration.getAnnotationsTargetItemsBaseUrl(), configuration.getAnnotationsCreator(), namedEntityAnnos, storyId, itemId);
	}
	
	public NamedEntityAnnotationImpl createAnnotationsForPosition(PositionEntityImpl pe) throws SolrServiceException {		
		List<String> foundByNer=new ArrayList<>();
		List<String> linkedByNer=new ArrayList<>();
		NamedEntityImpl ne = persistentNamedEntityService.findNamedEntity(pe.getNamedEntityId());
		String preferredWikiId=choosePreferredWikiId(pe, ne, foundByNer, linkedByNer);
		
		//create annotation
		if(preferredWikiId!=null) {
			//getting Solr WikidataEntity prefLabel
			WikidataEntity wikiEntity = solrWikidataEntityService.getWikidataEntity(preferredWikiId, ne.getType());
			String entityPrefLabel = ne.getLabel();
			if(wikiEntity!=null)
			{
				Map<String, List<String>> prefLabelMap = wikiEntity.getPrefLabel();
				if(prefLabelMap!=null && prefLabelMap.get(EntitySolrFields.PREF_LABEL+".en")!=null 
						&& prefLabelMap.get(EntitySolrFields.PREF_LABEL+".en").size()>0)
					entityPrefLabel = prefLabelMap.get(EntitySolrFields.PREF_LABEL+".en").get(0);
			}
			//computing score
			double score=computeScoreForAnnotations(foundByNer, linkedByNer);
									
			return new NamedEntityAnnotationImpl(configuration.getAnnotationsIdBaseUrl(),configuration.getAnnotationsTargetItemsBaseUrl(),pe.getStoryId(), pe.getItemId(), preferredWikiId, ne.getLabel(), entityPrefLabel, pe.getFieldUsedForNER(), ne.getType(), score, foundByNer, linkedByNer); 		
		}
		return null;
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

	
	public NamedEntityAnnotation getStoryOrItemAnnotation(String storyId, String itemId, String wikidataEntity) throws HttpException, IOException {
		
		String wikidataIdGenerated=null;
		wikidataIdGenerated = WikidataUtils.getWikidataEntityUri(wikidataEntity);
		
		return persistentNamedEntityAnnotationService.findNamedEntityAnnotationWithStoryIdItemIdAndWikidataId(storyId, itemId, wikidataIdGenerated);
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
