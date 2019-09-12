package eu.europeana.enrichment.web.controller;

import javax.annotation.Resource;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.translation.service.TranslationService;
import eu.europeana.enrichment.web.config.swagger.SwaggerSelect;
import eu.europeana.enrichment.web.model.EnrichmentTranslationRequest;
import eu.europeana.enrichment.web.service.EnrichmentTranslationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@EnableCaching
@SwaggerSelect
@Api(tags = "Translation service", description=" ")
public class TranslationController extends BaseRest {

	@Resource
	EnrichmentTranslationService enrichmentTranslationService;
	
	@Resource(name = "eTranslationService")
	TranslationService eTranslationService;

	
	/*
	 * This method represents the /enrichment/translation end point,
	 * where a translation request will be processed.
	 * All requests on this end point are processed here.
	 * 
	 * @param wskey						is the application key which is required
	 * @param translationRequest		is the Rest Post body with the original
	 * 									text for translation into English
	 * return 							the translated text or for eTranslation
	 * 									only an ID
	 */
	@ApiOperation(value = "Translate text (Google, eTranslation)", nickname = "postTranslation", notes = "This method translates the textual information of transcribathon documents. \"storyId\" represents the identifier of the document in Transcribathon platform.\n" + 
			" The parameter \"itemId\" further enables considering only specific story item. If \"itemId\" is set to \"all\", then the text of the whole"
			+ " story is taken into account. " +
			" The \"property\" parameter indicates which textual information will be translated, supported values: \"summary\", \"description\" and \"transcription\".\n" + 
			"The \"translationTool\" parameter indicates which machine translation tool will be used for performing the translation, supported value: \"Google\", \"eTranslation\".")
	@RequestMapping(value = "/enrichment/translation", method = {RequestMethod.POST}, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> postTranslation(
			@RequestParam(value = "wskey", required = true) String wskey,
			@RequestParam(value = "storyId", required = true) String storyId,
			@RequestParam(value = "itemId", required = true) String itemId,
			@RequestParam(value = "translationTool", required = true, defaultValue = "Google") String translationTool,
			@RequestParam(value = "property", required = false, defaultValue = "description") String property) throws HttpException {
	
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			EnrichmentTranslationRequest body = new EnrichmentTranslationRequest();
			body.setStoryId(storyId);
			body.setItemId(itemId);
			body.setTranslationTool(translationTool);
			body.setType(property);
			
			String translation = enrichmentTranslationService.translate(body, true);
			ResponseEntity<String> response = new ResponseEntity<String>(translation, HttpStatus.OK);
			
			return response;
	
	}
	
	@ApiOperation(value = "Get translated text (Google, eTranslation)", nickname = "getTranslation")
	@RequestMapping(value = "/enrichment/translation", method = {RequestMethod.GET}, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> getTranslation(
			@RequestParam(value = "wskey", required = true) String wskey,
			@RequestParam(value = "storyId", required = true) String storyId,
			@RequestParam(value = "itemId", required = true) String itemId,
			@RequestParam(value = "translationTool", required = true, defaultValue = "Google") String translationTool,
			@RequestParam(value = "property", required = false, defaultValue = "description") String property) throws HttpException {
	
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			EnrichmentTranslationRequest body = new EnrichmentTranslationRequest();
			body.setStoryId(storyId);
			body.setItemId(itemId);
			body.setTranslationTool(translationTool);
			body.setType(property);
			
			String translation = enrichmentTranslationService.translate(body, false);
			ResponseEntity<String> response = new ResponseEntity<String>(translation, HttpStatus.OK);
			
			return response;
		
	} 

}
