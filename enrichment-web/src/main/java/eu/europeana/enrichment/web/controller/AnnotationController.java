package eu.europeana.enrichment.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.europeana.api.commons.definitions.vocabulary.CommonApiConstants;
import eu.europeana.api.commons.nosql.entity.ApiWriteLock;
import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.api.commons.web.model.vocabulary.Operations;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.common.serializer.JsonLdSerializer;
import eu.europeana.enrichment.definitions.model.impl.NamedEntityAnnotationCollection;
import eu.europeana.enrichment.definitions.model.impl.NamedEntityAnnotationImpl;
import eu.europeana.enrichment.mongo.service.PersistentApiWriteLockService;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentPositionEntityServiceImpl;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.web.common.config.I18nConstants;
import eu.europeana.enrichment.web.service.EnrichmentStoryAndItemStorageService;
import eu.europeana.enrichment.web.service.impl.EnrichmentNERServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@EnableCaching
//@SwaggerSelect
@Api(tags = "Enrichment service", description=" ")
public class AnnotationController extends BaseRest {

	@Autowired
	EnrichmentNERServiceImpl enrichmentNerService;

	@Autowired
	PersistentItemEntityService persistentItemEntityService;

	@Autowired
	PersistentStoryEntityService persistentStoryEntityService;

    @Autowired
    EnrichmentStoryAndItemStorageService enrichmentStoryAndItemStorageService;
	
    @Autowired
    PersistentPositionEntityServiceImpl persistentPositionEntityService;
    
    @Autowired
    PersistentApiWriteLockService persistentApiWriteLockService;

	@Autowired 
	JsonLdSerializer jsonLdSerializer;
	
	Logger logger = LogManager.getLogger(getClass());
	
	/**
	 * This method represents the /enrichment/annotation end point, where the annotations for all NamedEntities 
     * of a story/item are retrieved using the class NamedEntityAnnotationCollection.
	 * All requests on this end point are processed here.
	 * @param property
	 * @param storyId
	 * @param itemId
	 * @return
	 * @throws Exception
	 * @throws HttpException
	 */
	@ApiOperation(value = "Get annotations", nickname = "getAnnotations", notes = "This method retrieves the annotations of "
			+ "stories/items. The parameter \"storyId\" enables considering the annotations that are only realted to the given story."
			+ " The \"itemId\" parameter further restricts retrieving the annotations related to the given story item (in case of all items of "
			+ "a story please do not specify any value). The \"property\" parameter refers to the part of the story/item being analyzed, i.e. description or transcription, etc.")
	@RequestMapping(value = "/enrichment/annotation", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAnnotations(
			@RequestParam(value="property", required=false) String property,
			@RequestParam(value="storyId", required=true) String storyId,			
			@RequestParam(value="itemId", required=false) String itemId,
			@RequestParam(value = CommonApiConstants.PARAM_WSKEY) String wskey,
			HttpServletRequest request) throws Exception, HttpException {

			verifyReadAccess(request);
			
			if(property==null) {
				property = itemId!=null ? EnrichmentConstants.STORY_ITEM_TRANSCRIPTION : EnrichmentConstants.STORY_ITEM_DESCRIPTION;
			}

			NamedEntityAnnotationCollection result = enrichmentNerService.getAnnotations(storyId, itemId, property);
			if(result.getItems()!=null) {
				removeGivenNameInResponse(result.getItems());
			}
			String resultJson=jsonLdSerializer.serializeObject(result);
			ResponseEntity<String> response = new ResponseEntity<String>(resultJson, HttpStatus.OK);
			return response;
	}
	
	@ApiOperation(value = "Create annotations for an item", nickname = "createAnnotationsForItem", notes = "This method creates and stores the annotations of "
			+ "an item to the database. The \"property\" parameter refers to the part of the item being analyzed (e.g. transcription)."
			+ "Please note that this method also updats the item from Transcribathon and performs the Named Entity Recognition analysis if needed.")
	@RequestMapping(value = "/enrichment/annotation/{storyId}/{itemId}", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createAnnotationsForItem(
			@PathVariable("storyId") String storyId,
			@PathVariable("itemId") String itemId,
			@RequestParam(value="property", required = false) String property,
			HttpServletRequest request) throws Exception, HttpException {
		
		if(property==null) {
			property=EnrichmentConstants.STORY_ITEM_TRANSCRIPTION;
		}
		
		//check if the api is already locked (i.e. the analysis has been started)
//		ApiWriteLock annoDbWiteLock = persistentApiWriteLockService.getActiveLock(storyId, itemId, property, EnrichmentConstants.ENTITY_TYPE_ANNOTATION);
//		if(annoDbWiteLock!=null) {
//			throw new HttpException(null, I18nConstants.WRITE_LOCK_EXISTS, HttpStatus.ACCEPTED);
//		}
//		ApiWriteLock annoWriteLock = persistentApiWriteLockService.lock(storyId, itemId, property, EnrichmentConstants.ENTITY_TYPE_ANNOTATION);
		
		verifyWriteAccess(Operations.CREATE, request);

		NamedEntityAnnotationCollection annosCollection = enrichmentNerService.createAnnotationsFullWorkflow(storyId, itemId, property, null);
				
		if(annosCollection!=null && annosCollection.getItems()!=null) {
			removeGivenNameInResponse(annosCollection.getItems());
		}
		String resultJson = jsonLdSerializer.serializeObject(annosCollection);
		ResponseEntity<String> response = new ResponseEntity<String>(resultJson, HttpStatus.OK);
		
//		persistentApiWriteLockService.unlock(annoWriteLock);

		return response;		
	}

	@ApiOperation(value = "Create annotations for a story", nickname = "createAnnotationsForStory", notes = "This method stores the annotations of "
			+ "a story to the database. The \"property\" parameter refers to the part of the story being analyzed (e.g. description or transcription). "
			+ "Please note that this method also updats a story from Transcribathon and performs the Named Entity Recognition analysis if needed.")
	@RequestMapping(value = "/enrichment/annotation/{storyId}", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createAnnotationsForStory(
			@PathVariable("storyId") String storyId,
			@RequestParam(value="property", required = false) String property,
			HttpServletRequest request) throws Exception, HttpException {

		if(property==null) {
			property=EnrichmentConstants.STORY_ITEM_DESCRIPTION;
		}

		//check if the api is already locked (i.e. the analysis has been started)
//		ApiWriteLock annoDbWiteLock = persistentApiWriteLockService.getActiveLock(storyId, null, property, EnrichmentConstants.ENTITY_TYPE_ANNOTATION);
//		if(annoDbWiteLock!=null) {
//			throw new HttpException(null, I18nConstants.WRITE_LOCK_EXISTS, HttpStatus.ACCEPTED);
//		}
//		ApiWriteLock annoWriteLock = persistentApiWriteLockService.lock(storyId, null, property, EnrichmentConstants.ENTITY_TYPE_ANNOTATION);
		
		verifyWriteAccess(Operations.CREATE, request);

		List<String> fieldsToUpdate = new ArrayList<>();
		fieldsToUpdate.add(property);
		
		NamedEntityAnnotationCollection annosCollection = enrichmentNerService.createAnnotationsFullWorkflow(storyId, null, property, fieldsToUpdate);
		
		if(annosCollection!=null && annosCollection.getItems()!=null) {
			removeGivenNameInResponse(annosCollection.getItems());
		}
		String resultJson = jsonLdSerializer.serializeObject(annosCollection);
		ResponseEntity<String> response = new ResponseEntity<String>(resultJson, HttpStatus.OK);
		
//		persistentApiWriteLockService.unlock(annoWriteLock);
		
		return response;		
	}
	
	
//	@ApiOperation(value = "Create annotations for all items", nickname = "createAnnotationsAllItems")
//	@RequestMapping(value = "/enrichment/annotation-all-items", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<String> createAnnotationsAllItems(
//			HttpServletRequest request) throws Exception, HttpException {
//
//			verifyWriteAccess(Operations.CREATE, request);
//			
//			String linking_local = "Wikidata";
//			String nerTools_local = "DBpedia_Spotlight,Stanford_NER";
//			List<String> linking = new ArrayList<>(Arrays.asList(HelperFunctions.toArray(linking_local,",")));
//			List<String> nerTools = new ArrayList<>(Arrays.asList(HelperFunctions.toArray(nerTools_local,",")));						
//
//			List<ItemEntity> items = persistentItemEntityService.getAllItemEntities();
//			boolean startFrom=false;
//			boolean check=true;
//			int itemCount=12692;
//			for(ItemEntity item : items) {	
//				//start from specified item, to continue the analysis in case in breaks
//				if(check && "1243601".equals(item.getItemId()) && "108619".equals(item.getStoryId()))
//				{
//					startFrom=true;
//					check=false;
//				}
//				if(startFrom && !StringUtils.isBlank(item.getTranscriptionText()))
//				{
//					logger.info("NER analysis and annotation generation for the item number:" + itemCount);
//					itemCount++;
//					logger.info("NER analysis and annotation generation for the item: storyId=" + item.getStoryId() + ", itemId=" + item.getItemId());
//					enrichmentNerService.createNamedEntitiesForItem(item.getStoryId(), item.getItemId(), EnrichmentConstants.STORY_ITEM_TRANSCRIPTION, nerTools, false, linking, EnrichmentConstants.defaultTranslationTool, false, true);
//					enrichmentNerService.createAnnotations(item.getStoryId(), item.getItemId(), EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
//				}
//			}
//
//			ResponseEntity<String> response = new ResponseEntity<String>("{\"result\" : \"Annotations for all items are created.\"}", HttpStatus.OK);
//			return response;		
//	}

   /**
    * This method represents the /enrichment/annotation/{storyId}/{itemId}/{wikidataIdentifier} end point,
	* where the annotations for some NamedEntity of the given item for the specified story are retrieved using the class NamedEntityAnnotationImpl.
	* All requests on this end point are processed here. 
    * @param storyId
    * @return
    * @throws Exception
    * @throws HttpException
    */
	@ApiOperation(value = "Get annotation preview for items", nickname = "getAnnotationItem", notes = "This method retrieves the annotations of "
			+ "a single wikidata entity from the whole collection of entities that can be retrieved using the GET method: /enrichment/annotation/{storyId}/{itemId}"
			+ " The parameter \"wikidataIdentifier\" specifies the wikidata entity we want to retrieve (e.g. Q1569850).")
	@RequestMapping(value = "/enrichment/annotation/entity/{storyId}/{itemId}/{wikidataIdentifier}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAnnotationItem(
			@PathVariable("storyId") String storyId,
			@PathVariable("itemId") String itemId,
			@PathVariable("wikidataIdentifier") String wikidataIdentifier,
			@RequestParam(value = CommonApiConstants.PARAM_WSKEY) String wskey,
			HttpServletRequest request) throws Exception, HttpException {
		
		verifyReadAccess(request);
		List<NamedEntityAnnotationImpl> result = enrichmentNerService.getStoryOrItemAnnotation(storyId, itemId, wikidataIdentifier);
		removeGivenNameInResponse(result);
		String resultJson=jsonLdSerializer.serializeObject(result);
		ResponseEntity<String> response = new ResponseEntity<String>(resultJson, HttpStatus.OK);			
		return response;
	} 
	
   /**
    * This method represents the /enrichment/annotation/{storyId}/{wikidataIdentifier} end point,
	* where the annotations for a single NamedEntity of a story are retrieved using the class NamedEntityAnnotationImpl.
	* All requests on this end point are processed here.
    * @param storyId
    * @return
    * @throws Exception
    * @throws HttpException
    */
	
	@ApiOperation(value = "Get annotation preview for stories", nickname = "getAnnotationStory", notes = "This method retrieves the annotations of "
			+ "a single wikidata entity from the whole collection of entities that can be retrieved using the GET method: /enrichment/annotation/{storyId}"
			+ " The parameter \"wikidataIdentifier\" specifies the wikidata entity we want to retrieve (e.g. Q1569850).")
	@RequestMapping(value = "/enrichment/annotation/entity/{storyId}/{wikidataIdentifier}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAnnotationStory(
			@PathVariable("storyId") String storyId,
			@PathVariable("wikidataIdentifier") String wikidataIdentifier,
			@RequestParam(value = CommonApiConstants.PARAM_WSKEY) String wskey,
			HttpServletRequest request) throws Exception, HttpException {
		
		verifyReadAccess(request);
		List<NamedEntityAnnotationImpl> result = enrichmentNerService.getStoryOrItemAnnotation(storyId, null, wikidataIdentifier);
		removeGivenNameInResponse(result);
		String resultJson=jsonLdSerializer.serializeObject(result);
		ResponseEntity<String> response = new ResponseEntity<String>(resultJson, HttpStatus.OK);			
		return response;
	}
	
	private void removeGivenNameInResponse(List<NamedEntityAnnotationImpl> annos) {
		for(NamedEntityAnnotationImpl anno : annos) {
			anno.getBody().remove(EnrichmentConstants.GIVEN_NAME_ANNO);
		}
	}

} 
