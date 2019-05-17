package eu.europeana.enrichment.web.controller;

import javax.annotation.Resource;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.mongo.model.DBItemEntityImpl;
import eu.europeana.enrichment.mongo.model.DBStoryEntityImpl;
import eu.europeana.enrichment.web.config.swagger.SwaggerSelect;
import eu.europeana.enrichment.web.model.EnrichmentTranslationRequest;
import eu.europeana.enrichment.web.service.EnrichmentNERService;
import eu.europeana.enrichment.web.service.EnrichmentTranslationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@EnableCaching
@SwaggerSelect
@Api(tags = "Administrator service", description=" ")
public class AdministrationController extends BaseRest {

	@Resource
	EnrichmentNERService enrichmentNerService;
	@Resource
	EnrichmentTranslationService enrichmentTranslationService;
	
	/*
	 * This method represents the /administration/uploadStoriesAndItemsFromJson end point,
	 * where a request with 2 json files (one for stories and one for items) to be read and 
	 * saved to the database is sent
	 * 
	 * All requests on this end point are processed here.
	 * 
	 * @param wskey						is the application key which is required
	 * 
	 * @param jsonFileStoriesPath		the path to the json file for stories
	 * 
	 * @param jsonFileItemsPath		    the path to the json file for items
	 * 
	 * @return							"Done" if everything ok
	 */
	@ApiOperation(value = "Upload Story and Item entries from the json file to the database", nickname = "uploadStoriesAndItemsFromJson")
	@RequestMapping(value = "/administration/uploadStoriesAndItemsFromJson", method = {RequestMethod.POST},
			consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> uploadStories(
			@RequestParam(value = "wskey", required = false) String wskey,
			@RequestParam(value = "jsonFileStories", required = true) String jsonStories,
			@RequestParam(value = "jsonFileItems", required = true) String jsonItems
			) throws HttpException {
		try {
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			String uploadStoriesStatus = enrichmentNerService.readStoriesAndItemsFromJson(jsonStories, jsonItems);
			
			ResponseEntity<String> response = new ResponseEntity<String>(uploadStoriesStatus, HttpStatus.OK);
		
			return response;
		} catch (HttpException e) {
			throw e;
		}	
	}
	
	/*
	 * This method represents the /administration/uploadStories end point,
	 * where a request with an array of StoryEntity to be saved to the database is sent
	 * All requests on this end point are processed here.
	 * 
	 * @param wskey						is the application key which is required
	 * 
	 * @param stories				    an array of StoryEntity to be uploaded to the database (each StoryEntity represents a list of ItemEntity)
	 * 
	 * @return							"Done" if everything ok
	 */
	@ApiOperation(value = "Upload StoryEntities to the database", nickname = "uploadStories")
	@RequestMapping(value = "/administration/uploadStories", method = {RequestMethod.POST},
			consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> uploadStories(
			@RequestParam(value = "wskey", required = false) String wskey,
			@RequestBody DBStoryEntityImpl [] stories) throws HttpException {
		try {
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			String uploadStoriesStatus = enrichmentNerService.uploadStories(stories);
			
			ResponseEntity<String> response = new ResponseEntity<String>(uploadStoriesStatus, HttpStatus.OK);
		
			return response;
		} catch (HttpException e) {
			throw e;
		}	
	}
	
	/*
	 * This method represents the /administration/uploadItems end point,
	 * where a request with a ItemEntity information to be saved in the database is sent
	 * All requests on this end point are processed here.
	 * 
	 * @param wskey						is the application key which is required
	 * 
	 * @param items				         an array of ItemEntity to be uploaded to the database (each StoryEntity represents a list of ItemEntity)
	 * 
	 * @return							"Done" if everything ok
	 */
	
	@ApiOperation(value = "Upload ItemEntities to the database", nickname = "uploadItems")
	@RequestMapping(value = "/administration/uploadItems", method = {RequestMethod.POST},
			consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> uploadItems(
			@RequestParam(value = "wskey", required = false) String wskey,
			@RequestBody DBItemEntityImpl [] items) throws HttpException {
		try {
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			String uploadItemsStatus = enrichmentNerService.uploadItems(items);
			
			ResponseEntity<String> response = new ResponseEntity<String>(uploadItemsStatus, HttpStatus.OK);
		
			return response;
		} catch (HttpException e) {
			throw e;
		}	
	}
	
	@ApiOperation(value = "Upload translated text (Google, eTranslation)", nickname = "uploadTranslation")
	@RequestMapping(value = "/administration/uploadTranslation", method = {RequestMethod.POST},
			consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> uploadTranslation(
			@RequestParam(value = "wskey", required = false) String wskey,
			@RequestBody EnrichmentTranslationRequest translationRequest) throws HttpException {
		try {
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			String translation = enrichmentTranslationService.uploadTranslation(translationRequest);
			ResponseEntity<String> response = new ResponseEntity<String>(translation, HttpStatus.OK);
		
			return response;
		} catch (HttpException e) {
			throw e;
		}	
	}

}
