package eu.europeana.enrichment.web.controller;

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

import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.web.model.EnrichmentTranslationRequest;
import eu.europeana.enrichment.web.service.EnrichmentTranslationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@EnableCaching
//@SwaggerSelect
@Api(tags = "Translation service", description=" ")
public class TranslationController extends BaseRest {

	@Autowired
	EnrichmentTranslationService enrichmentTranslationService;

	@Autowired
	PersistentItemEntityService persistentItemEntityService;
	
	@Autowired
	PersistentStoryEntityService persistentStoryEntityService;
	
	@Autowired
	PersistentTranslationEntityService persistentTranslationEntityService;

	Logger logger = LogManager.getLogger(getClass());
	
	/**
	 * This method represents the /enrichment/translation/{story} end point,
	 * where a translation request will be processed.
	 * All requests on this end point are processed here.
	 * 
	 * @param wskey
	 * @param storyId
	 * @param translationTool
	 * @param property
	 * @return
	 * @throws Exception 
	 */
	@ApiOperation(value = "Translate text (Google, eTranslation) for Stories", nickname = "postTranslationStory", notes = "This method translates the textual information of transcribathon documents. \"storyId\" represents the identifier of the document in Transcribathon platform.\n"  
			+ " The \"property\" parameter indicates which textual information will be translated, supported values: \"summary\", \"description\" and \"transcription\".\n" + 
			"The \"translationTool\" parameter indicates which machine translation tool will be used for performing the translation, supported value: \"Google\", \"eTranslation\".")
	@RequestMapping(value = "/enrichment/translation/{storyId}", method = {RequestMethod.POST}, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> postTranslationStory(
			@RequestParam(value = "wskey", required = true) String wskey,
			@PathVariable("storyId") String storyId,
			@RequestParam(value = "translationTool", required = true, defaultValue = "Google") String translationTool,
			@RequestParam(value = "property", required = false, defaultValue = "description") String property) throws Exception {
	
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			EnrichmentTranslationRequest body = new EnrichmentTranslationRequest();
			body.setStoryId(storyId);
			body.setItemId("all");
			body.setTranslationTool(translationTool);
			body.setType(property);
			
			enrichmentTranslationService.translate(body, true);
			ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
			
			return response;
	
	}
	
	@ApiOperation(value = "Get translated text (Google, eTranslation) for Stories", nickname = "getTranslationStory", notes = "This method retrieves the translated story elements. \"storyId\" represents the identifier of the document in Transcribathon platform.\n"  
			+ " The \"property\" parameter indicates which textual information has been translated, supported values: \"summary\", \"description\" and \"transcription\".\n" + 
			"The \"translationTool\" parameter indicates which machine translation tool has been used for performing the translation, supported value: \"Google\", \"eTranslation\".")
	@RequestMapping(value = "/enrichment/translation/{storyId}", method = {RequestMethod.GET}, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> getTranslationStory(
			@RequestParam(value = "wskey", required = true) String wskey,
			@PathVariable("storyId") String storyId,
			@RequestParam(value = "translationTool", required = true, defaultValue = "Google") String translationTool,
			@RequestParam(value = "property", required = false, defaultValue = "description") String property) throws Exception {
	
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			EnrichmentTranslationRequest body = new EnrichmentTranslationRequest();
			body.setStoryId(storyId);
			body.setItemId("all");
			body.setTranslationTool(translationTool);
			body.setType(property);
			
			TranslationEntity translation = enrichmentTranslationService.translate(body, false);
			ResponseEntity<String> response = new ResponseEntity<String>(translation.getTranslatedText(), HttpStatus.OK);
			
			return response;
		
	} 
	
	
	
	/**
	 * This method represents the /enrichment/translation/{storyId}/{itemId} end point,
	 * where a translation request for an item will be processed.
	 * All requests on this end point are processed here.
	 * 
	 * @param wskey
	 * @param storyId
	 * @param itemId
	 * @param translationTool
	 * @param property
	 * @return
	 * @throws Exception 
	 */
	@ApiOperation(value = "Translate text (Google, eTranslation) for Items", nickname = "postTranslationItem", notes = "This method translates the textual information of transcribathon documents. \"storyId\" represents the identifier of the document in Transcribathon platform.\n" + 
			" The parameter \"itemId\" further enables considering only specific story item. " +
			" The \"property\" parameter indicates which textual information will be translated, supported values: \"summary\", \"description\" and \"transcription\".\n" + 
			"The \"translationTool\" parameter indicates which machine translation tool will be used for performing the translation, supported value: \"Google\", \"eTranslation\".")
	@RequestMapping(value = "/enrichment/translation/{storyId}/{itemId}", method = {RequestMethod.POST}, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> postTranslationItem(
			@RequestParam(value = "wskey", required = true) String wskey,
			@PathVariable("storyId") String storyId,
			@PathVariable("itemId") String itemId,
			@RequestParam(value = "translationTool", required = true, defaultValue = "Google") String translationTool,
			@RequestParam(value = "property", required = false, defaultValue = "description") String property) throws Exception {
	
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			EnrichmentTranslationRequest body = new EnrichmentTranslationRequest();
			body.setStoryId(storyId);
			body.setItemId(itemId);
			body.setTranslationTool(translationTool);
			body.setType(property);
			
			enrichmentTranslationService.translate(body, true);
			ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
			return response;

	}
	
	@ApiOperation(value = "Get translated text (Google, eTranslation) for Items", nickname = "getTranslationItem", notes = "This method retrieves the translated item elements. \"storyId\" and \"itemId\" enable the identification of the document in Transcribathon platform.\n"  
			+ " The \"property\" parameter indicates which textual information has been translated, supported values: \"summary\", \"description\" and \"transcription\".\n" + 
			"The \"translationTool\" parameter indicates which machine translation tool has been used for performing the translation, supported value: \"Google\", \"eTranslation\".")
	@RequestMapping(value = "/enrichment/translation/{storyId}/{itemId}", method = {RequestMethod.GET}, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> getTranslationItem(
			@RequestParam(value = "wskey", required = true) String wskey,
			@PathVariable("storyId") String storyId,
			@PathVariable("itemId") String itemId,
			@RequestParam(value = "translationTool", required = true, defaultValue = "Google") String translationTool,
			@RequestParam(value = "property", required = false, defaultValue = "description") String property) throws Exception {
	
			// Check client access (a valid “wskey” must be provided) 
			validateApiKey(wskey);
			
			EnrichmentTranslationRequest body = new EnrichmentTranslationRequest();
			body.setStoryId(storyId);
			body.setItemId(itemId);
			body.setTranslationTool(translationTool);
			body.setType(property);
			
			TranslationEntity translation = enrichmentTranslationService.translate(body, false);
			
			ResponseEntity<String> response = new ResponseEntity<String>(translation.getTranslatedText(), HttpStatus.OK);
			
			return response;
		
	} 


}
