package eu.europeana.enrichment.web.controller;

import java.util.Arrays;

import javax.annotation.Resource;

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
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.web.config.swagger.SwaggerSelect;
import eu.europeana.enrichment.web.model.EnrichmentNERRequest;
import eu.europeana.enrichment.web.service.EnrichmentNERService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@EnableCaching
@SwaggerSelect
@Api(tags = "Enrichment service", description=" ")
public class NERController extends BaseRest { 

	@Resource
	EnrichmentNERService enrichmentNerService;
	
	/**
	 * This method represents the /enrichment/ner/{storyId} end point,
	 * where a request with a translated text is send and the named entities based on this text are retrieved.
	 * All requests on this end point are processed here.
	 * 
	 * @param wskey
	 * @param storyId
	 * @param translationTool
	 * @param property
	 * @param linking
	 * @param nerTools
	 * @param original
	 * @return											a list of named entities converted to a json format
	 * @throws Exception
	 * @throws HttpException
	 * @throws SolrNamedEntityServiceException
	 */
	@ApiOperation(value = "Get named entities for a story", nickname = "getNEREntitiesStory")
	@RequestMapping(value = "/enrichment/ner/{storyId}", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getNEREntitiesStory(
			@RequestParam(value = "wskey", required = true) String wskey,
			@PathVariable("storyId") String storyId,
			@RequestParam(value = "translationTool", required = false) String translationTool,
			@RequestParam(value = "property", required = false) String property,
			@RequestParam(value = "linking", required = false) String linking,
			@RequestParam(value = "nerTools", required = true) String nerTools,
			@RequestParam(value = "original", required = false) Boolean original) throws Exception, HttpException, SolrNamedEntityServiceException {
		try {
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			EnrichmentNERRequest body = new EnrichmentNERRequest();
			body.setStoryId(storyId);
			body.setItemId("all");
			body.setTranslationTool(translationTool);
			body.setProperty(property);
			body.setLinking(Arrays.asList(linking.split(",")));
			body.setNerTools(Arrays.asList(nerTools.split(",")));
			body.setOriginal(original);
			
			String jsonLd = enrichmentNerService.getEntities(body, true);
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
	
	@ApiOperation(value = "Get named entities for a story", nickname = "getEntitiesStory")
	@RequestMapping(value = "/enrichment/ner/{storyId}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getEntitiesStory(
			@RequestParam(value = "wskey", required = true) String wskey,
			@PathVariable("storyId") String storyId,
			@RequestParam(value = "translationTool", required = false) String translationTool,
			@RequestParam(value = "property", required = false) String property,
			@RequestParam(value = "linking", required = false) String linking,
			@RequestParam(value = "nerTools", required = true) String nerTools,
			@RequestParam(value = "original", required = false) Boolean original) throws Exception, HttpException, SolrNamedEntityServiceException {
		try {
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			EnrichmentNERRequest body = new EnrichmentNERRequest();
			body.setStoryId(storyId);
			body.setItemId("all");
			body.setTranslationTool(translationTool);
			body.setProperty(property);
			body.setLinking(Arrays.asList(linking.split(",")));
			body.setNerTools(Arrays.asList(nerTools.split(",")));
			body.setOriginal(original);
			
			String jsonLd = enrichmentNerService.getEntities(body, false);
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
	
	/*
	 * NER services for items
	 */
	
	@ApiOperation(value = "Get named entities for an item", nickname = "getNEREntitiesItem")
	@RequestMapping(value = "/enrichment/ner/{storyId}/{itemId}", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getNEREntitiesItem(
			@RequestParam(value = "wskey", required = true) String wskey,
			@PathVariable("storyId") String storyId,
			@PathVariable("itemId") String itemId,
			@RequestParam(value = "linking", required = false) String linking,
			@RequestParam(value = "nerTools", required = true) String nerTools,
			@RequestParam(value = "original", required = false) Boolean original) throws Exception, HttpException, SolrNamedEntityServiceException {
		try {
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			EnrichmentNERRequest body = new EnrichmentNERRequest();
			body.setStoryId(storyId);
			body.setItemId(itemId);
			body.setTranslationTool("Google");
			body.setLinking(Arrays.asList(linking.split(",")));
			body.setNerTools(Arrays.asList(nerTools.split(",")));
			body.setOriginal(original);
			
			String jsonLd = enrichmentNerService.getEntities(body, true);
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
	
	@ApiOperation(value = "Get named entities for an item", nickname = "getEntitiesItem")
	@RequestMapping(value = "/enrichment/ner/{storyId}/{itemId}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getEntitiesItem(
			@RequestParam(value = "wskey", required = true) String wskey,
			@PathVariable("storyId") String storyId,
			@PathVariable("itemId") String itemId,
			@RequestParam(value = "property", required = false) String property,
			@RequestParam(value = "linking", required = false) String linking,
			@RequestParam(value = "nerTools", required = true) String nerTools,
			@RequestParam(value = "original", required = false) Boolean original) throws Exception, HttpException, SolrNamedEntityServiceException {
		try {
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			EnrichmentNERRequest body = new EnrichmentNERRequest();
			body.setStoryId(storyId);
			body.setItemId(itemId);
			body.setTranslationTool("Google");
			body.setProperty(property);
			body.setLinking(Arrays.asList(linking.split(",")));
			body.setNerTools(Arrays.asList(nerTools.split(",")));
			body.setOriginal(original);
			
			String jsonLd = enrichmentNerService.getEntities(body, false);
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
