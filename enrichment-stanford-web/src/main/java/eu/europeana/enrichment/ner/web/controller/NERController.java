package eu.europeana.enrichment.ner.web.controller;

import javax.annotation.Resource;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.europeana.enrichment.ner.web.NERStanfordServiceImpl;
import eu.europeana.enrichment.ner.web.config.swagger.SwaggerSelect;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@EnableCaching
@SwaggerSelect
@Api(tags = "NER get entities service", description=" ")
public class NERController {

	@Resource
	NERStanfordServiceImpl enrichmentNerService;
	
	/*
	 * This method represents the /enrichment/entities end point,
	 * where a request with a translated text is send and 
	 * the named entities based on this text is retrieved.
	 * All requests on this end point are processed here.
	 * 
	 * @param wskey						is the application key which is required
	 * @param nerRequest				is the Rest Post body which contains 
	 * 									the text for the named entity recognition tools
	 * @return							a map of all named entities including 
	 * 									their classification types 
	 */
	@ApiOperation(value = "Get named entities from text with Stanford NER", nickname = "getNEREntities")
	@RequestMapping(value = "namedEntityRecognition", method = {RequestMethod.POST},
			consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getNEREntities(
			@RequestBody StanfordRequest requestBody) {
			
			String jsonLd = enrichmentNerService.getEntities(requestBody.getText());
			ResponseEntity<String> response = new ResponseEntity<String>(jsonLd, HttpStatus.OK);
			
			return response;

	}
}
