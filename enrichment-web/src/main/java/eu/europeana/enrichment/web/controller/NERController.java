package eu.europeana.enrichment.web.controller;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.europeana.enrichment.web.config.swagger.SwaggerSelect;
import eu.europeana.enrichment.web.model.EnrichmentNERRequest;
import eu.europeana.enrichment.web.service.EnrichmentNERService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@RestController
@EnableCaching
@SwaggerSelect
@Api(tags = "NER get entities service", description=" ")
public class NERController extends BaseRest {

	@Resource
	EnrichmentNERService enrichmentNerService;
	
	@ApiOperation(value = "Get entities from text (Stanford_NER_model_3, Stanford_NER_model_4, Stanford_NER_model_7)", nickname = "getNEREntities")
	@RequestMapping(value = "/enrichment/entities", method = {RequestMethod.POST},
			consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getNEREntities(@RequestBody EnrichmentNERRequest nerRequest) {

		String jsonLd = enrichmentNerService.getEntities(nerRequest.text, nerRequest.tool);
		ResponseEntity<String> response = new ResponseEntity<String>(jsonLd, HttpStatus.OK);
		
		return response;
	}
	
}
