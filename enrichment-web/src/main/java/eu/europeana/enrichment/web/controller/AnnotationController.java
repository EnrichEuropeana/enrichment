package eu.europeana.enrichment.web.controller;

import java.util.Arrays;

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

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
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
	
	/**
	 * This method represents the /enrichment/annotation end point, where the annotations for all NamedEntities 
     * of a story/item are retrieved using the class NamedEntityAnnotationCollection.
	 * All requests on this end point are processed here.
	 * @param wskey
	 * @param nerTools
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
			+ "a story please do not specify any value). The \"property\" parameter refers to the part of the story/item being analyzed, i.e. description or transcription, etc."
			+ " The \"nerTools\" refers to the annotations for the named entity that is found by the given NER tools.")
	@RequestMapping(value = "/enrichment/annotation", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAnnotations(
			@RequestParam(value="wskey", required=true) String wskey,
			@RequestParam(value="nerTools", required=true) String nerTools,
			@RequestParam(value="property", required=true) String property,
			@RequestParam(value="storyId", required=true) String storyId,			
			@RequestParam(value="itemId", required=false) String itemId) throws Exception, HttpException {

			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			String result = enrichmentNerService.getAnnotations(storyId, itemId, property, Arrays.asList(nerTools.trim().split("\\s*,\\s*")));
			ResponseEntity<String> response = new ResponseEntity<String>(result, HttpStatus.OK);
			return response;
	}
	
	/**
	 * This method represents the /enrichment/annotation end point, where the annotations for all NamedEntities 
     * of an item are saved to the db using the class NamedEntityAnnotationCollection.
	 * All requests on this end point are processed here.
	 * @param wskey
	 * @param nerTools
	 * @param property
	 * @param storyId
	 * @param itemId
	 * @return
	 * @throws Exception
	 * @throws HttpException
	 */
	@ApiOperation(value = "Create annotations", nickname = "createAnnotations", notes = "This method stores the annotations of "
			+ "stories or items	to the database. The parameter \"storyId\" enables considering the annotations that are only realted to the given story."
			+ " The parameter \"itemId\" further restricts saving of the annotations to the given story item (in case of all items of a story please do not specify any value)."
			+ " The \"property\" parameter refers to the part of the story/item being analyzed, either description or transcription, etc."
			+ " The parameter \"nerTools\" refers to the annotations which named entity is found by the given NER tools.")
	@RequestMapping(value = "/enrichment/annotation", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createAnnotations(
			@RequestParam(value = "wskey", required = true) String wskey,
			@RequestParam(value="nerTools", required=true) String nerTools,
			@RequestParam(value="property", required=true) String property,
			@RequestParam(value="storyId", required=true) String storyId,			
			@RequestParam(value="itemId", required=false) String itemId) throws Exception, HttpException {

			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			String result = enrichmentNerService.createAnnotations(storyId, itemId, property, Arrays.asList(nerTools.trim().split("\\s*,\\s*")));
			ResponseEntity<String> response = new ResponseEntity<String>(result, HttpStatus.OK);
			return response;		
	}
	
   /**
    * This method represents the /enrichment/annotation/{storyId}/{itemId}/{wikidataIdentifier} end point,
	* where the annotations for some NamedEntity of the given item for the specified story are retrieved using the class NamedEntityAnnotationImpl.
	* All requests on this end point are processed here. 
    * @param wskey
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
			@RequestParam(value = "wskey", required = true) String wskey,
			@PathVariable("storyId") String storyId,
			@PathVariable("itemId") String itemId,
			@PathVariable("wikidataIdentifier") String wikidataIdentifier) throws Exception, HttpException {
		
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			String result = enrichmentNerService.getStoryOrItemAnnotation(storyId, itemId, wikidataIdentifier);
						
			ResponseEntity<String> response = new ResponseEntity<String>(result, HttpStatus.OK);			
					
			return response;
		
		
	} 
	
	   /**
	    * This method represents the /enrichment/annotation/{storyId}/{wikidataIdentifier} end point,
		 * where the annotations for a single NamedEntity of a story are retrieved using the class NamedEntityAnnotationImpl.
		 * All requests on this end point are processed here.
		 * 
	    * @param wskey
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
				@RequestParam(value = "wskey", required = true) String wskey,
				@PathVariable("storyId") String storyId,
				@PathVariable("wikidataIdentifier") String wikidataIdentifier) throws Exception, HttpException {
			
				// Check client access (a valid “wskey” must be provided)
				validateApiKey(wskey);
				
				String result = enrichmentNerService.getStoryOrItemAnnotation(storyId, null, wikidataIdentifier);
							
				ResponseEntity<String> response = new ResponseEntity<String>(result, HttpStatus.OK);			
						
				return response;
			
		} 
} 
