package eu.europeana.enrichment.web.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.common.serializer.JsonLdSerializer;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.NamedEntityAnnotation;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.model.impl.NamedEntityAnnotationCollection;
import eu.europeana.enrichment.model.impl.NamedEntityAnnotationImpl;
import eu.europeana.enrichment.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.model.impl.PositionEntityImpl;
import eu.europeana.enrichment.model.utils.ModelUtils;
import eu.europeana.enrichment.model.vocabulary.NERConstants;
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
import eu.europeana.enrichment.translation.service.impl.ETranslationEuropaServiceImpl;
import eu.europeana.enrichment.web.common.config.I18nConstants;
import eu.europeana.enrichment.web.exception.ParamValidationException;
import eu.europeana.enrichment.web.model.EnrichmentNERRequest;
import eu.europeana.enrichment.web.model.EnrichmentTranslationRequest;
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
	public String getEntities(EnrichmentNERRequest requestParam, boolean process) throws Exception {

		List<NamedEntityImpl> result = getNamedEntities(requestParam, process);

		if(result == null || result.isEmpty()) {
			return "{\"info\" : \"No found NamedEntity-s for the given input parameters!\"}";
		}
		else
		{
//			return new JSONObject(result).toString();
			return jsonLdSerializer.serializeObject(result);
		}
	}
	
	/*
	 * TODO: refactor this method, is too long 
	 */
	public List<NamedEntityImpl> getNamedEntities(EnrichmentNERRequest requestParam, boolean process) throws Exception {
		
		List<NamedEntityImpl> result = new ArrayList<NamedEntityImpl>();
		
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
		List<String> tools = new ArrayList<String>(requestParam.getNerTools());
		List<String> linking = requestParam.getLinking();
		String translationTool = requestParam.getTranslationTool();
		String translationLanguage = "en";
		
		if(storyId == null || storyId.isEmpty())
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentNERRequest.PARAM_STORY_ID, null);
			
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
		List<NamedEntityImpl> tmpNamedEntities = new ArrayList<>();

		//fetching NamedEntity-ies to check if they are found with the required ner tools
		tmpNamedEntities.addAll(persistentNamedEntityService.findNamedEntitiesWithAdditionalInformation(storyId, itemId, type, requestParam.getNerTools()));
	
		int numberNERTools = tools.size();
		//check if for the given story/item the NER anaylysis for all required NER tools is already pursued, returned is the number of 
		//ner tools for which the analysis remained undone (the list "tools" is also updated, i.e. only the not analyzed tools remain in the list)
		int numberNERToolsFound = checkAllNerToolsAlreadyCompleted(tools, tmpNamedEntities);
				
		//in case of GET API (process==FALSE) or POST API where all ner tools have been processed and no new provided text is given
		if(!process || numberNERToolsFound==numberNERTools) {
			return tmpNamedEntities;
		}

		//from this part down only POST method is executed and the NER analysis is done for all story or item fields
		List<String> allNERFieldTypes = new ArrayList<String>();
		allNERFieldTypes.add(type);
		
		for(String typeNERField : allNERFieldTypes)
		{
			type=typeNERField; 
			
			String [] textAndLanguage = updateStoryOrItem(original, storyId, itemId, translationTool, translationLanguage, type);
			String textForNer = textAndLanguage[0];
			String languageForNer = textAndLanguage[1];

			//sometimes some fields for NER can be empty for items which causes problems in the method applyNERTools
			for(String tool : tools) {
				List<NamedEntityImpl> tmpResult = getUpdatedNamedEntitiesForText(tool, textForNer, languageForNer, typeNERField, storyId, itemId, linking);
				updateNamedEntitiesAndPositionsInDbAndSolr(tmpResult);
				updateNamedEntityResultList (result, tmpResult);
			}
	
		}	
		
		return result;
	}
	
	private void updateNamedEntityResultList (List<NamedEntityImpl> main, List<NamedEntityImpl> newOnes) {
		if(main==null) {
			if(newOnes!=null) {
				main=newOnes;
				return;
			}
			else return;
		}
		else {
			if(newOnes==null) {
				return;
			}
		}
		//update the main result with new elements (the new list is extended for the entities that are not inside from the previous list, because the new entities are the updated ones)
		List<NamedEntityImpl> toAdd = new ArrayList<NamedEntityImpl>();
		for(NamedEntityImpl mainEntity : main) {
			boolean found = false;
			for(NamedEntityImpl newEntity : newOnes) {
				if(newEntity.getLabel().equals(mainEntity.getLabel()) && newEntity.getType().equals(mainEntity.getType())) {
					found=true;
					break;
				}
			}
			if(!found) {
				toAdd.add(mainEntity);
			}
		}
		newOnes.addAll(toAdd);
		main=newOnes;
	}
	
	public List<NamedEntityImpl> getUpdatedNamedEntitiesForText(String nerTool, String textForNer, String languageForNer, String fieldType, String storyId, String itemId, List<String> linking) throws Exception {
		/*
		 * Here for each ner tool the analysis is done separately because different tools may find
		 * the same entities on different positions in the text and we would like to separate those results,
		 * otherwise all positions of the entities would be in the same list and it cannot be clear which positions
		 * belong to which ner tool analyser.
		 */
		TreeMap<String, List<NamedEntityImpl>> tmpResult = applyNERTools(nerTool, textForNer, languageForNer, fieldType, storyId, itemId);
		
		if(tmpResult==null || tmpResult.isEmpty()) return null;
		
		List<NamedEntityImpl> result = new ArrayList<NamedEntityImpl>();
		for (String classificationType : tmpResult.keySet()) {
			
			if (isRestrictedClassificationType(classificationType)) continue;
			
			for (NamedEntityImpl tmpNamedEntity : tmpResult.get(classificationType)) {
				NamedEntityImpl dbEntity = null;
				boolean oldNamedEntityChanged = false;
				if (tmpNamedEntity.getLabel()!=null) {
					dbEntity = persistentNamedEntityService.findNamedEntityByLabelAndType(tmpNamedEntity.getLabel(), tmpNamedEntity.getType());
				}
				
				if(dbEntity != null) {
					//check if there are new position entities to be added						
					if(tmpNamedEntity.getPositionEntities()!=null) {
						List<PositionEntityImpl> existingPositions = persistentPositionEntityService.findPositionEntities(dbEntity.get_id());
						if(existingPositions.size()>0) {
							boolean addNewPositionEntity = true;
							for(PositionEntityImpl pe : existingPositions)
							{
								if(tmpNamedEntity.getPositionEntities().get(0).equals(pe))
								{
									addNewPositionEntity=false;
									/*
									 * only if all fields of the position entities are the same including the positions in the translated text
									 * 2 or more ner tools are added to the same position entity
									 */
									if(pe.getNerTools()!=null && !pe.getNerTools().contains(nerTool)) { 
										pe.getNerTools().add(nerTool);
										persistentPositionEntityService.savePositionEntity(pe);
									}
									break;
								}
							}
							if(addNewPositionEntity) {
								//saving an existing, changed position, first set the reference to the named entity id
								tmpNamedEntity.getPositionEntities().get(0).setNamedEntityId(dbEntity.get_id());
								persistentPositionEntityService.savePositionEntity(tmpNamedEntity.getPositionEntities().get(0));
							}
						}
						else {		
							//saving a new position, first set the reference to the named entity id
							tmpNamedEntity.getPositionEntities().get(0).setNamedEntityId(dbEntity.get_id());
							persistentPositionEntityService.savePositionEntity(tmpNamedEntity.getPositionEntities().get(0));
						}							
					}

					if(tmpNamedEntity.getDBpediaIds()!=null) {
						if(dbEntity.getDBpediaIds()!=null) {
							for(int dbpediaIndex = 0; dbpediaIndex < tmpNamedEntity.getDBpediaIds().size(); dbpediaIndex++) {
								int tmpIndex = dbpediaIndex;
								boolean found = dbEntity.getDBpediaIds().stream().anyMatch(x -> x.equals(tmpNamedEntity.getDBpediaIds().get(tmpIndex)));
								if(!found){
									dbEntity.addDBpediaId(tmpNamedEntity.getDBpediaIds().get(tmpIndex));
									oldNamedEntityChanged=true;
								}
							}
						}
						else {
							List<String> newDbpediaIds = new ArrayList<String>(tmpNamedEntity.getDBpediaIds());
							dbEntity.setDBpediaIds(newDbpediaIds);
							oldNamedEntityChanged=true;
						}
					}
					
					/*
					 * Add the entity to the new ones if it is found in the db and it is changed
					 */
					if(oldNamedEntityChanged) {
						result.add(dbEntity);
					}
				}
				else {
					result.add(tmpNamedEntity);
				}
			}	
		}
		/*
		 * Add linking information to named entity
		 */
		addLinkingInformation(result, linking, languageForNer, nerTool);
		
		return result;
	}
	
	private boolean isRestrictedClassificationType(String type) {
		if(!type.equalsIgnoreCase(NERClassification.AGENT.toString()) 
			&& !type.equalsIgnoreCase(NERClassification.PLACE.toString())) {
			return true;
		}
		else return false;
	}
	
	public void updateNamedEntitiesAndPositionsInDbAndSolr (List<NamedEntityImpl> result) {
		if(result==null) return;
		/*
		 * Save and update all named entities
		 */
		for (NamedEntityImpl entity : result) {
			//save the wikidata ids to solr
			List<String> wikidataIdsForSolr = getWikidataIdsSolr(entity);
			if(wikidataIdsForSolr!=null) {
				for(String wikidataId : wikidataIdsForSolr)
				{
					try {
						solrWikidataEntityService.storeWikidataFromURL(wikidataId, entity.getType());
					} catch (SolrServiceException | IOException e) {
						logger.log(Level.ERROR, "Exception during storing the wikidata entity to Solr.", e);
					}
				} 
			}
			
			//save the NamedEntity to the db
			persistentNamedEntityService.saveNamedEntity(entity);
			//save the new positions to the db
			if(entity.getPositionEntities()!=null) {
				for(PositionEntityImpl position: entity.getPositionEntities()) {
					//saving a new position, first set the reference to the named entity id
					position.setNamedEntityId(entity.get_id());
					persistentPositionEntityService.savePositionEntity(position);
				}
			}
		}
			
	}
	
	private List<String> getWikidataIdsSolr(NamedEntityImpl ne) {
		if(ne.getPreferredWikidataIds()!=null) {
			return ne.getPreferredWikidataIds();
		}
		else {
			return null;
		}
	}
	
	private void addLinkingInformation(List<NamedEntityImpl> result, List<String> linking, String languageForNer, String nerTool) throws IOException {
		if(result==null) return;
		for (NamedEntityImpl namedEntity : result) {
			nerLinkingService.addLinkingInformation(namedEntity, linking, languageForNer, nerTool);
		}
	}
	
	/*
	 * This function checks if the given story or item is present in the db and if not it fetches it from the Transcribathon platform.
	 * Additionally, if there is not proper translation, it is first done here and the translated text is returned for the NER analysis.
	 */
	private String [] updateStoryOrItem (boolean original, String storyId, String itemId, String translationTool, String translationLanguage, String type) throws Exception
	{
		String [] results =  new String [2];
		results[0]=null;
		results[1]=null;
		
		if(original) {
			if(itemId==null)
			{		
				StoryEntity story = persistentStoryEntityService.findStoryEntity(storyId);
				if(story==null) story = enrichmentStoryAndItemStorageService.fetchAndSaveStoryFromTranscribathon(storyId);
				if (story==null) return results;
				
				if(type.toLowerCase().equals("description")) 
				{
					results[0] = story.getDescriptionEn();
					results[1] = "en";
					
				}
				else if(type.toLowerCase().equals("summary"))
				{
					results[0] = story.getSummaryEn();
					results[1] = "en";
				}
				else if(type.toLowerCase().equals("transcription"))
				{
					results[0] = story.getTranscriptionText();
					results[1] = ModelUtils.getMainTranslationLanguage(story);
				}
				return results;
			}
			else
			{	
				ItemEntity item = persistentItemEntityService.findItemEntity(storyId, itemId);
				if(item==null) item = enrichmentStoryAndItemStorageService.fetchAndSaveItemFromTranscribathon(storyId, itemId);
				if (item==null) return results;
				
				results[0] = item.getTranscriptionText();
				results[1] = ModelUtils.getMainTranslationLanguage(item);
				return results;
			}
		}
		
		EnrichmentTranslationRequest body = new EnrichmentTranslationRequest();
		body.setStoryId(storyId);
		body.setItemId(itemId);
		body.setTranslationTool(translationTool);
		body.setType(type);
		TranslationEntity returnTranslationEntity = enrichmentTranslationService.translate(body, true);
		if(returnTranslationEntity!=null)
		{
			results[0] = returnTranslationEntity.getTranslatedText();
			results[1] = returnTranslationEntity.getLanguage();
		}

		return results;

	}
	
	/*
	 * This function returns the number of NER-tools that are present in the found NamedEntities  
	 */
	private int checkAllNerToolsAlreadyCompleted (List<String> tools, List<NamedEntityImpl> tmpNamedEntities)
	{		
		Set<String> nerToolsForStoryOrItem = new HashSet<String>();
		if(tmpNamedEntities!=null) {
			for (NamedEntityImpl ne : tmpNamedEntities)
			{
				if(ne.getPositionEntities()!=null) {
					for(PositionEntityImpl pe : ne.getPositionEntities())
					{
						nerToolsForStoryOrItem.addAll(pe.getNerTools());
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

	private TreeMap<String, List<NamedEntityImpl>> applyNERTools (String nerTool, String text, String language, String fieldUsedForNER, String storyId, String itemId) throws Exception {
		
		if(text==null || text.isBlank() || language==null) {
			return null;
		}
			
		NERService tmpTool;
		switch(nerTool){
			case NERConstants.stanfordNer:
				tmpTool = nerStanfordService;
				break;
			case NERConstants.dbpediaSpotlightName:
				tmpTool = nerDBpediaSpotlightService;
				break;
			default:
				throw new ParamValidationException(I18nConstants.INVALID_PARAM_VALUE, EnrichmentNERRequest.PARAM_NER_TOOL, nerTool);
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
				if(entity.getPositionEntities()==null || entity.getPositionEntities().size()==0) continue;
				PositionEntityImpl pos = entity.getPositionEntities().get(0);
				pos.setStoryId(storyId);
				pos.setItemId(itemId);
				pos.setFieldUsedForNER(fieldUsedForNER);
			}
		}
	}
	
	public String uploadStories(StoryEntity[] stories) throws HttpException {
		
		logger.debug("Uploading new stories to the Mongo DB.");
		
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
					persistentNamedEntityService.deletePositionEntitiesAndNamedEntity(story.getStoryId(), null, "description");
					persistentTranslationEntityService.deleteTranslationEntity(story.getStoryId(), null, "description");
				}
				if(dbStoryEntity.getSummary().compareTo(story.getSummary())!=0)
				{
					someStoryPartChanged=true;
					persistentNamedEntityService.deletePositionEntitiesAndNamedEntity(story.getStoryId(), null, "summary");
					persistentTranslationEntityService.deleteTranslationEntity(story.getStoryId(), null, "summary");
				}
				if(dbStoryEntity.getTranscriptionText().compareTo(story.getTranscriptionText())!=0)
				{
					someStoryPartChanged=true;
					persistentNamedEntityService.deletePositionEntitiesAndNamedEntity(story.getStoryId(), null, "transcription");
					persistentTranslationEntityService.deleteTranslationEntity(story.getStoryId(), null, "transcription");
				}		
				
				if(someStoryPartChanged)
				{
					persistentNamedEntityAnnotationService.deleteNamedEntityAnnotation(story.getStoryId(), null);
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
	
	public String uploadItems(ItemEntity[] items) throws HttpException, NoSuchAlgorithmException, UnsupportedEncodingException {
		
		logger.debug("Uploading new items to the Mongo DB.");
		
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
					logger.debug("Uploading new items : deleting old NamedEntity and TranslationEntity for transcription.");
					transcriptionChanged = true;
					persistentNamedEntityService.deletePositionEntitiesAndNamedEntity(item.getStoryId(), item.getItemId() , "transcription");
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
	    //logger.debug(response);
	    //logger.debug(response);
	    return response;

//	    Elements allParagraphs = doc.getElementsByTag("p");
//		allParagraphs.forEach(paragraph -> response.append(paragraph.text()));
//		logger.debug(whole);
//		logger.debug(whole);
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

	public String getAnnotations(String storyId, String itemId, String property, List<String> nerTools) throws Exception {
		List<NamedEntityAnnotation> entities = persistentNamedEntityAnnotationService.findNamedEntityAnnotation(storyId, itemId, property, nerTools);
		if(!entities.isEmpty())
		{
			return jsonLdSerializer.serializeObject(new NamedEntityAnnotationCollection(configuration.getAnnotationsIdBaseUrl(), configuration.getAnnotationsCreator(), entities, storyId, itemId));
		}
		else
		{
			return "{\"info\" : \"No valid entries found! Please check that the annotations for the given story are created.\"}";
		}
	}
	
	public String createAnnotations(String storyId, String itemId, String property, List<String> nerTools) throws SolrServiceException, IOException {
		List<NamedEntityAnnotation> namedEntityAnnos = persistentNamedEntityAnnotationService.findNamedEntityAnnotation(storyId, itemId, property, nerTools);
		if(namedEntityAnnos.isEmpty()) {
			namedEntityAnnos = new ArrayList<NamedEntityAnnotation> ();
			List<NamedEntityImpl> namedEntities = persistentNamedEntityService.findNamedEntitiesWithAdditionalInformation(storyId, itemId, property, nerTools);
			for(NamedEntityImpl ne : namedEntities) {
				if(ne.getPreferedWikidataId()!=null) {
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
					float score;
					if(ne.getWikidataLabelAltLabelMatchIds()!=null && ne.getWikidataLabelAltLabelMatchIds().contains(ne.getPreferedWikidataId()) 
							&& ne.getDbpediaWikidataIds()!=null && ne.getDbpediaWikidataIds().contains(ne.getPreferedWikidataId())) {
						score=1;
					}
					else {
						score=2;
					}
											
					NamedEntityAnnotationImpl tmpNamedEntityAnnotation = new NamedEntityAnnotationImpl(configuration.getAnnotationsIdBaseUrl(),configuration.getAnnotationsTargetItemsBaseUrl(),storyId,itemId, ne.getPreferedWikidataId(), ne.getLabel(), entityPrefLabel, property, ne.getType(), score, nerTools); 
					namedEntityAnnos.add(tmpNamedEntityAnnotation);					
					//saving the entity to the db
					persistentNamedEntityAnnotationService.saveNamedEntityAnnotation(tmpNamedEntityAnnotation);
				}
			}
		}
		return jsonLdSerializer.serializeObject(new NamedEntityAnnotationCollection(configuration.getAnnotationsIdBaseUrl(), configuration.getAnnotationsCreator(), namedEntityAnnos, storyId, itemId));
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
}
