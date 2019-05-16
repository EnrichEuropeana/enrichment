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
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
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
	@ApiOperation(value = "Get entities from text (Stanford_NER_model_3, Stanford_NER_model_4, Stanford_NER_model_7)", nickname = "getNEREntities")
	@RequestMapping(value = "/enrichment/entities", method = {RequestMethod.POST},
			consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getNEREntities(
			@RequestParam(value = "wskey", required = false) String wskey,
			@RequestBody EnrichmentNERRequest nerRequest) throws Exception, HttpException, SolrNamedEntityServiceException {
		try {
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			String jsonLd = enrichmentNerService.getEntities(nerRequest);
			ResponseEntity<String> response = new ResponseEntity<String>(jsonLd, HttpStatus.OK);
			
			return response;
		} catch (HttpException e) {
			throw e;
		} catch (SolrNamedEntityServiceException e) {
			// TODO Auto-generated catch block
			throw e;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
	}
}
