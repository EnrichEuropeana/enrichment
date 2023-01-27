package eu.europeana.enrichment.web.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.common.serializer.JsonLdSerializer;
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
import eu.europeana.enrichment.solr.commons.JavaGsonJSONParser;
import eu.europeana.enrichment.solr.exception.SolrServiceException;
import eu.europeana.enrichment.solr.model.vocabulary.EntitySolrFields;
import eu.europeana.enrichment.solr.service.SolrEntityPositionsService;
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

	//@Resource(name = "solrEntityService")
	@Autowired
	SolrEntityPositionsService solrEntityService;
	
	//@Resource(name = "solrWikidataEntityService")
	@Autowired
	SolrWikidataEntityService solrWikidataEntityService;
	

	@Autowired 
	JsonLdSerializer jsonLdSerializer;

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
	JavaGsonJSONParser javaJSONParser;
	
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
	//@Resource(name = "persistentStoryEntityService")
	@Autowired
	PersistentStoryEntityService persistentStoryEntityService;
	//@Resource(name = "persistentItemEntityService")
	@Autowired
	PersistentItemEntityService persistentItemEntityService;
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
	public String getEntities(String storyId, String itemId, String property, List<String> nerTools) throws Exception {

		List<NamedEntityImpl> result = 
				persistentNamedEntityService.findNamedEntitiesWithAdditionalInformation(
						storyId,
						itemId,
						property,
						nerTools,
						false
				);

		if(result == null || result.isEmpty()) {
			return "{\"info\" : \"No found NamedEntity-s for the given input parameters!\"}";
		}
		else
		{
			return jsonLdSerializer.serializeObject(result);
		}
	}
	
	public void createNamedEntities(String storyId, String itemId, String type, List<String> nerTools, boolean nerPossiblyDoneForSomeTools ,List<String> linking, String translationTool, boolean original, boolean updateStoryOrItem) throws Exception {
		
		if(nerPossiblyDoneForSomeTools) {
			boolean allNERToolsCompleted = checkAllNerToolsAlreadyCompleted(storyId, itemId, type, nerTools);
			if(allNERToolsCompleted) {
				return;
			}
		}
		
		String [] textAndLanguage = getStoryOrItemTextForNER(storyId, itemId, translationTool, type, original, updateStoryOrItem);
		if(textAndLanguage[0]==null || textAndLanguage[1]==null) {
			return;
		}
		String textForNer = textAndLanguage[0];
		String languageForNer = textAndLanguage[1];
		//sometimes some fields for NER can be empty for items which causes problems in the method applyNERTools
		for(String tool : nerTools) {
			updatedNamedEntitiesForText(tool, textForNer, languageForNer, type, storyId, itemId, linking, true);
		}
	}
	
	private boolean checkAllNerToolsAlreadyCompleted(String storyId, String itemId, String fieldForNer, List<String> nerTools) {
		List<String> toolsToRemove = new ArrayList<String>();
		for(String tool : nerTools) {
			if(persistentPositionEntityService.findPositionEntitiesForNerTool(storyId, itemId, fieldForNer, tool)!=null) {
				toolsToRemove.add(tool);
			}
		}
		nerTools.removeAll(toolsToRemove);
		return nerTools.size()==0;
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
	public void updatedNamedEntitiesForText(String nerTool, String textForNer, String languageForNer, String fieldType, String storyId, String itemId, List<String> linking, boolean matchType) throws Exception {
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

	/*
	 * This function checks if the given story or item is present in the db and if not it fetches it from the Transcribathon platform.
	 * Additionally, if there is not proper translation, it is first done here and the translated text is returned for the NER analysis.
	 */
	private String [] getStoryOrItemTextForNER (String storyId, String itemId, String translationTool, String type, boolean original, boolean updateStoryOrItem) throws Exception
	{
		String [] results =  new String [2];
		results[0]=null;
		results[1]=null;

		if(original) {
			if(itemId==null)
			{		
				StoryEntity story = persistentStoryEntityService.findStoryEntity(storyId);
				if(updateStoryOrItem) {
					enrichmentStoryAndItemStorageService.updateStoryFromTranscribathon(story);
				}
				if(story==null) {
					return results;
				}
				
				if(EnrichmentConstants.STORY_ITEM_DESCRIPTION.equalsIgnoreCase(type)) 
				{
					results[0] = story.getDescription();
					results[1] = story.getLanguageDescription();
					
				}
				else if(EnrichmentConstants.STORY_ITEM_SUMMARY.equalsIgnoreCase(type))
				{
					results[0] = story.getSummary();
					results[1] = story.getLanguageSummary();
				}
				else if(EnrichmentConstants.STORY_ITEM_TRANSCRIPTION.equalsIgnoreCase(type))
				{
					results[0] = story.getTranscriptionText();
					results[1] = ModelUtils.getMainTranslationLanguage(story);
				}
				return results;
	
			}
			else
			{	
				ItemEntity item = persistentItemEntityService.findItemEntity(storyId, itemId);
				if(updateStoryOrItem) {
					enrichmentStoryAndItemStorageService.updateItemFromTranscribathon(item);
				}
				if(item==null) {
					return results;
				}
				
				results[0] = item.getTranscriptionText();
				results[1] = ModelUtils.getMainTranslationLanguage(item);
				return results;
			}
		}
		
		String translatedText=null;
		if(itemId==null) {
			translatedText = enrichmentTranslationService.translateStory(storyId, type, translationTool, updateStoryOrItem);
		}
		else {
			translatedText = enrichmentTranslationService.translateItem(storyId, itemId, type, translationTool, updateStoryOrItem);
		}

		if(translatedText!=null)
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

	public String getAnnotations(String storyId, String itemId, String property) throws Exception {
		List<NamedEntityAnnotation> entities = persistentNamedEntityAnnotationService.findNamedEntityAnnotation(storyId, itemId, property, null);
		if(!entities.isEmpty())
		{
			return jsonLdSerializer.serializeObject(new NamedEntityAnnotationCollection(configuration.getAnnotationsIdBaseUrl(), configuration.getAnnotationsTargetStoriesBaseUrl(), configuration.getAnnotationsTargetItemsBaseUrl() , configuration.getAnnotationsCreator(), entities, storyId, itemId));
		}
		else
		{
			return "{\"info\" : \"No valid entries found! Please check that the annotations for the given story are created.\"}";
		}
	}
	
	public String createAnnotations(String storyId, String itemId, String property) throws SolrServiceException, IOException {
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
		return jsonLdSerializer.serializeObject(new NamedEntityAnnotationCollection(configuration.getAnnotationsIdBaseUrl(), configuration.getAnnotationsTargetStoriesBaseUrl(), configuration.getAnnotationsTargetItemsBaseUrl(), configuration.getAnnotationsCreator(), namedEntityAnnos, storyId, itemId));
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

	public String getStoryOrItemAnnotation(String storyId, String itemId, String wikidataEntity) throws HttpException, IOException {
		
		String wikidataIdGenerated=null;
		if(wikidataEntity.startsWith("Q")) wikidataIdGenerated = EnrichmentConstants.WIKIDATA_ENTITY_BASE_URL + wikidataEntity;
		else wikidataIdGenerated = wikidataEntity;		
		
		NamedEntityAnnotation entityAnno = persistentNamedEntityAnnotationService.findNamedEntityAnnotationWithStoryIdItemIdAndWikidataId(storyId, itemId, wikidataIdGenerated);
		if(entityAnno!=null)
		{
			return jsonLdSerializer.serializeObject(entityAnno);
		}
		else
		{
			logger.debug("No valid entries found! Please use the POST method first to save the data to the database or provide a valid Wikidata identifier.");
			return "{\"info\" : \"No valid entries found! Please use the POST method first to save the data to the database.\"}";
		}
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
