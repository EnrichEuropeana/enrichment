package eu.europeana.enrichment.web.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.NamedEntityAnnotation;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.model.impl.NamedEntityAnnotationCollection;
import eu.europeana.enrichment.model.impl.NamedEntityAnnotationImpl;
import eu.europeana.enrichment.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.model.impl.PositionEntityImpl;
import eu.europeana.enrichment.model.utils.ModelUtils;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityAnnotationService;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityService;
import eu.europeana.enrichment.mongo.service.PersistentPositionEntityServiceImpl;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.ner.enumeration.NERClassification;
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
	
	public void createNamedEntitiesForStory(String storyId, String type, List<String> nerTools, boolean nerPossiblyDoneForSomeTools ,List<String> linking, String translationTool, boolean original, boolean updateStory) throws Exception {
		StoryEntity story = persistentStoryEntityService.findStoryEntity(storyId);
		if(nerPossiblyDoneForSomeTools) {
			List<String> completedNERTools = checkAllNerToolsAlreadyCompleted(story.getStoryId(), null, type, nerTools);
			if(completedNERTools.containsAll(nerTools)) {
				return;
			}
			else {
				if(nerTools.contains(EnrichmentConstants.dbpediaSpotlightName) 
					&& ! completedNERTools.contains(EnrichmentConstants.dbpediaSpotlightName)) {
					persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(story.getStoryId(), null, type);
				}
				else {
					nerTools.removeAll(completedNERTools);
				}
			}
		}
		
		String [] textAndLanguage = getStoryTextForNER(story, translationTool, type, original, updateStory);
		if(StringUtils.isBlank(textAndLanguage[0]) || StringUtils.isBlank(textAndLanguage[1])) {
			return;
		}
		String textForNer = textAndLanguage[0];
		String languageForNer = textAndLanguage[1];
		//sometimes some fields for NER can be empty for items which causes problems in the method applyNERTools
		updatedNamedEntitiesForText(nerTools, textForNer, languageForNer, type, story.getStoryId(), null, linking, true);
	}

	public void createNamedEntitiesForItem(String storyId, String itemId, String type, List<String> nerTools, boolean nerPossiblyDoneForSomeTools ,List<String> linking, String translationTool, boolean original, boolean updateItem) throws Exception {
		ItemEntity item = persistentItemEntityService.findItemEntity(storyId, itemId);
		if(nerPossiblyDoneForSomeTools) {
			List<String> completedNERTools = checkAllNerToolsAlreadyCompleted(item.getStoryId(), item.getItemId(), type, nerTools);
			if(completedNERTools.containsAll(nerTools)) {
				return;
			}
			else {
				if(nerTools.contains(EnrichmentConstants.dbpediaSpotlightName) 
					&& ! completedNERTools.contains(EnrichmentConstants.dbpediaSpotlightName)) {
					persistentNamedEntityService.deletePositionEntitiesAndNamedEntities(item.getStoryId(), item.getItemId(), type);
				}
				else {
					nerTools.removeAll(completedNERTools);
				}
			}
		}
		
		String [] textAndLanguage = getItemTextForNER(item, translationTool, type, original, updateItem);
		if(StringUtils.isBlank(textAndLanguage[0]) || StringUtils.isBlank(textAndLanguage[1])) {
			return;
		}
		String textForNer = textAndLanguage[0];
		String languageForNer = textAndLanguage[1];
		//sometimes some fields for NER can be empty for items which causes problems in the method applyNERTools
		updatedNamedEntitiesForText(nerTools, textForNer, languageForNer, type, item.getStoryId(), item.getItemId(), linking, true);
	}

	private List<String> checkAllNerToolsAlreadyCompleted(String storyId, String itemId, String fieldForNer, List<String> nerTools) {
		List<String> completedTools = new ArrayList<>();
		for(String tool : nerTools) {
			if(persistentPositionEntityService.findPositionEntitiesForNerTool(storyId, itemId, fieldForNer, tool)!=null) {
				completedTools.add(tool);
			}
		}
		return completedTools;
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
	public void updatedNamedEntitiesForText(List<String> nerTools, String textForNer, String languageForNer, String fieldType, String storyId, String itemId, List<String> linking, boolean matchType) throws Exception {
		List<String> nerToolsReordered = new ArrayList<String>(nerTools);
		//the first analyzed NER tool must be DBpedia_Spotlight
		if(nerToolsReordered.contains(EnrichmentConstants.dbpediaSpotlightName)) {
			int itemPos = nerToolsReordered.indexOf(EnrichmentConstants.dbpediaSpotlightName);
			nerToolsReordered.remove(itemPos);
			nerToolsReordered.add(0, EnrichmentConstants.dbpediaSpotlightName);
		}
		
		for(String nerTool:nerToolsReordered) {
			TreeMap<String, List<NamedEntityImpl>> tmpResult = applyNERTools(nerTool, textForNer, languageForNer, fieldType, storyId, itemId);
			if(tmpResult==null) {
				return;
			}
			for (String classificationType : tmpResult.keySet()) {
				
				if (isRestrictedClassificationType(classificationType)) continue;
				
				for (NamedEntityImpl tmpNamedEntity : tmpResult.get(classificationType)) {
					
					//agent entities should have at least 2 parts to be linked (name and surname)
					if(tmpNamedEntity.getType().equalsIgnoreCase(NERClassification.AGENT.toString()) && HelperFunctions.toArray(tmpNamedEntity.getLabel(),null).length<2) {
						continue;
					}
	
					NamedEntityImpl dbEntity = persistentNamedEntityService.findExistingNamedEntity(tmpNamedEntity);
					nerLinkingService.addLinkingInformation(tmpNamedEntity, dbEntity, linking, languageForNer, nerTool, matchType);
					saveNamedEntityAndPositionsToDbAndSolr(tmpNamedEntity, dbEntity);
				}	
			}
		}
	}
	
	private boolean isRestrictedClassificationType(String type) {
		if(!type.equalsIgnoreCase(NERClassification.AGENT.toString()) 
			&& !type.equalsIgnoreCase(NERClassification.PLACE.toString())) {
			return true;
		}
		else return false;
	}
	
	private void saveNamedEntityAndPositionsToDbAndSolr (NamedEntityImpl newNamedEntity, NamedEntityImpl existingNamedEntity) throws SolrServiceException, IOException {
		if(existingNamedEntity!=null) {
			//save the position entity only
			List<Integer> existingOffsets = new ArrayList<Integer>();
			List<Integer> offsetsTranslatedText = newNamedEntity.getPositionEntity().getOffsetsTranslatedText();
			for(int offset : offsetsTranslatedText) {
				PositionEntityImpl existingPosition = 
						persistentPositionEntityService.findPositionEntities(
								existingNamedEntity.get_id(),
								newNamedEntity.getPositionEntity().getStoryId(),
								newNamedEntity.getPositionEntity().getItemId(),
								offset,
								newNamedEntity.getPositionEntity().getFieldUsedForNER()
								);
				//update ner tools
				if(existingPosition!=null) {
					if(!existingPosition.getNerTools().contains(newNamedEntity.getPositionEntity().getNerTools().get(0))) {
						existingPosition.getNerTools().add(newNamedEntity.getPositionEntity().getNerTools().get(0));
						persistentPositionEntityService.savePositionEntity(existingPosition);
					}
					existingOffsets.add(offset);
				}
			}
			if(existingOffsets.size()>0) {
				offsetsTranslatedText.removeAll(existingOffsets);
			}
			if(offsetsTranslatedText.size()>0) {
				newNamedEntity.getPositionEntity().setNamedEntityId(existingNamedEntity.get_id());
				persistentPositionEntityService.savePositionEntity(newNamedEntity.getPositionEntity());
			}
		}
		else {
			persistentNamedEntityService.saveNamedEntity(newNamedEntity);
			if(newNamedEntity.getPreferedWikidataId()!=null) {
				if(!solrWikidataEntityService.existWikidataURL(newNamedEntity.getPreferedWikidataId())) {
					solrWikidataEntityService.storeWikidataFromURL(newNamedEntity.getPreferedWikidataId(), newNamedEntity.getType());
				}
			}
			newNamedEntity.getPositionEntity().setNamedEntityId(newNamedEntity.get_id());
			persistentPositionEntityService.savePositionEntity(newNamedEntity.getPositionEntity());

		}
		
	}

	private String [] getStoryTextForNER (StoryEntity dbStory, String translationTool, String type, boolean original, boolean updateStory) throws Exception
	{
		String [] results =  new String [2];
		results[0]=null;
		results[1]=null;

		if(original) {
			//update story from Transcribathon
			StoryEntity updatedStory=dbStory;
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
		StoryEntity updatedStory=dbStory;
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

	private String [] getItemTextForNER (ItemEntity dbItem, String translationTool, String type, boolean original, boolean updateItem) throws Exception
	{
		String [] results =  new String [2];
		results[0]=null;
		results[1]=null;

		if(original) {
			//update item from Transcribathon
			ItemEntity updatedItem=dbItem;
			if(updateItem) {
				updatedItem = enrichmentStoryAndItemStorageService.updateItemFromTranscribathon(dbItem);
			}		
			if(updatedItem==null) {
				return results;
			}
			
			results[0] = updatedItem.getTranscriptionText();
			results[1] = ModelUtils.getMainTranslationLanguage(updatedItem);
			return results;
		}

		//update item from Transcribathon
		ItemEntity updatedItem=dbItem;
		if(updateItem) {
			updatedItem = enrichmentStoryAndItemStorageService.updateItemFromTranscribathon(dbItem);
		}		
		if(updatedItem==null) {
			return results;
		}

		String translatedText = enrichmentTranslationService.translateItem(updatedItem, type, translationTool);
		if(! StringUtils.isBlank(translatedText))
		{
			results[0] = translatedText;
			results[1] = EnrichmentConstants.defaultTargetTranslationLanguage;
		}
		return results;
	}

	private TreeMap<String, List<NamedEntityImpl>> applyNERTools (String nerTool, String text, String language, String fieldUsedForNER, String storyId, String itemId) throws Exception {
		NERService tmpTool=null;
		switch(nerTool){
			case EnrichmentConstants.stanfordNer:
				tmpTool = nerStanfordService;
				break;
			case EnrichmentConstants.dbpediaSpotlightName:
				tmpTool = nerDBpediaSpotlightService;
				break;
			default:
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
		List<NamedEntityAnnotation> entities = persistentNamedEntityAnnotationService.findNamedEntityAnnotation(storyId, itemId, property, null);
		return new NamedEntityAnnotationCollection(configuration.getAnnotationsIdBaseUrl(), configuration.getAnnotationsTargetStoriesBaseUrl(), configuration.getAnnotationsTargetItemsBaseUrl() , configuration.getAnnotationsCreator(), entities, storyId, itemId);
	}
	
	public NamedEntityAnnotationCollection createAnnotations(String storyId, String itemId, String property) throws SolrServiceException, IOException {
		List<NamedEntityAnnotation> namedEntityAnnos = persistentNamedEntityAnnotationService.findNamedEntityAnnotation(storyId, itemId, property, null);
		if(namedEntityAnnos.isEmpty()) {
			namedEntityAnnos = new ArrayList<NamedEntityAnnotation> ();
			List<String> nerTools = new ArrayList<String>();
			nerTools.add(EnrichmentConstants.dbpediaSpotlightName);
			nerTools.add(EnrichmentConstants.stanfordNer);
			//first the annos for both ner tools are created, which will also have the highest score
			List<NamedEntityImpl> namedEntities = persistentNamedEntityService.findNamedEntitiesWithAdditionalInformation(storyId, itemId, property, nerTools, true);
			createAnnotationsPerNerTool(namedEntities, namedEntityAnnos, nerTools, storyId, itemId, property);
			
			nerTools.clear();
			nerTools.add(EnrichmentConstants.dbpediaSpotlightName);
			//second the annos for the dbpedia ner tool are created (the ones that do not already exist for both ner tools), these annos will have the second highest score
			namedEntities = persistentNamedEntityService.findNamedEntitiesWithAdditionalInformation(storyId, itemId, property, nerTools, true);
			createAnnotationsPerNerTool(namedEntities, namedEntityAnnos, nerTools, storyId, itemId, property);
			
			nerTools.clear();
			nerTools.add(EnrichmentConstants.stanfordNer);
			//third the annos for the stanford ner tool are created (the ones that are not already created before), these annos will have the third highest score
			namedEntities = persistentNamedEntityService.findNamedEntitiesWithAdditionalInformation(storyId, itemId, property, nerTools, true);
			createAnnotationsPerNerTool(namedEntities, namedEntityAnnos, nerTools, storyId, itemId, property);
			
		}
		return new NamedEntityAnnotationCollection(configuration.getAnnotationsIdBaseUrl(), configuration.getAnnotationsTargetStoriesBaseUrl(), configuration.getAnnotationsTargetItemsBaseUrl(), configuration.getAnnotationsCreator(), namedEntityAnnos, storyId, itemId);
	}
	
	private void createAnnotationsPerNerTool(List<NamedEntityImpl> namedEntities, List<NamedEntityAnnotation> annos, List<String> nerTools, String storyId, String itemId, String property) throws SolrServiceException {
		for(NamedEntityImpl ne : namedEntities) {
			if(ne.getPreferedWikidataId()!=null) {
				boolean alreadyExist = annos.stream().filter(el -> el.getWikidataId().equals(ne.getPreferedWikidataId())).findFirst().isPresent();
				if(!alreadyExist) {
					//getting Solr WikidataEntity prefLabel
					WikidataEntity wikiEntity = solrWikidataEntityService.getWikidataEntity(ne.getPreferedWikidataId(), ne.getType());
					String entityPrefLabel = ne.getLabel();
					if(wikiEntity!=null)
					{
						Map<String, List<String>> prefLabelMap = wikiEntity.getPrefLabel();
						if(prefLabelMap!=null && prefLabelMap.get(EntitySolrFields.PREF_LABEL+".en")!=null 
								&& prefLabelMap.get(EntitySolrFields.PREF_LABEL+".en").size()>0)
							entityPrefLabel = prefLabelMap.get(EntitySolrFields.PREF_LABEL+".en").get(0);
					}
					//computing score
					double score=computeScoreForAnnotations(nerTools,ne);
											
					NamedEntityAnnotationImpl tmpNamedEntityAnnotation = new NamedEntityAnnotationImpl(configuration.getAnnotationsIdBaseUrl(),configuration.getAnnotationsTargetItemsBaseUrl(),storyId,itemId, ne.getPreferedWikidataId(), ne.getLabel(), entityPrefLabel, property, ne.getType(), score, nerTools); 
					annos.add(tmpNamedEntityAnnotation);					
					//saving the entity to the db
					persistentNamedEntityAnnotationService.saveNamedEntityAnnotation(tmpNamedEntityAnnotation);
				}
			}
		}

	}

	public NamedEntityAnnotation getStoryOrItemAnnotation(String storyId, String itemId, String wikidataEntity) throws HttpException, IOException {
		
		String wikidataIdGenerated=null;
		if(wikidataEntity.startsWith("Q")) wikidataIdGenerated = EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL + wikidataEntity;
		else wikidataIdGenerated = wikidataEntity;		
		
		return persistentNamedEntityAnnotationService.findNamedEntityAnnotationWithStoryIdItemIdAndWikidataId(storyId, itemId, wikidataIdGenerated);
	}
	
	private double computeScoreForAnnotations(List<String> nerTools, NamedEntityImpl ne) {
		int linkedByDbpedia=0;
		int linkedByWikidataSearch=0;
		if(ne.getDbpediaWikidataIds()!=null && nerTools.contains(EnrichmentConstants.stanfordNer)) {
			linkedByDbpedia=1;
			//if there is a dbpedia wikidata id, we assume it also exist in the wikidata search
			linkedByWikidataSearch=1;
		}
		else if(ne.getDbpediaWikidataIds()!=null) {
			linkedByDbpedia=1;
		}
		else {
			linkedByWikidataSearch=1;
		}
		return 0.3 + 0.4*linkedByDbpedia + 0.3*linkedByWikidataSearch;
	}
}
