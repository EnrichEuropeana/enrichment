package eu.europeana.enrichment.web.controller;

import javax.annotation.Resource;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	
	@Override
	protected void init() {
		super.init();
		//Load all NER tools
		//enrichmentService = new EnrichmentTranslationServiceImpl();
		//enrichmentService.init();
	}
	
	@ApiOperation(value = "Translate text (Google, eTranslation)", nickname = "getTranslation")
	@RequestMapping(value = "/enrichment/translation", method = {RequestMethod.POST},
			consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> getTranslation(@RequestBody EnrichmentTranslationRequest translationRequest) {

		String translation = enrichmentTranslationService.translate(translationRequest.text, translationRequest.sourceLanguage, translationRequest.tool);
		ResponseEntity<String> response = new ResponseEntity<String>(translation, HttpStatus.OK);
		
		return response;
	}
	
}
