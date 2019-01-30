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

import eu.europeana.enrichment.web.config.swagger.SwaggerSelect;
import eu.europeana.enrichment.web.model.EnrichmentTranslationRequest;
import eu.europeana.enrichment.web.service.EnrichmentTranslationService;
import eu.europeana.enrichment.web.service.impl.EnrichmentNERServiceImpl;
import eu.europeana.enrichment.web.service.impl.EnrichmentTranslationServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@EnableCaching
@SwaggerSelect
@Api(tags = "Translation service", description=" ")
public class TranslationController extends BaseRest {

	@Resource
	EnrichmentTranslationService enrichmentTranslationService;
	
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
	@ApiOperation(value = "Translate text (Google, eTranslation)", nickname = "getTranslation")
	@RequestMapping(value = "/enrichment/translation", method = {RequestMethod.POST},
			consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> getTranslation(
			@RequestParam(value = "wskey", required = false) String wskey,
			@RequestBody EnrichmentTranslationRequest translationRequest) {

		String translation = enrichmentTranslationService.translate(translationRequest.text, translationRequest.sourceLanguage, translationRequest.tool);
		ResponseEntity<String> response = new ResponseEntity<String>(translation, HttpStatus.OK);
		
		return response;
	}
	
}
