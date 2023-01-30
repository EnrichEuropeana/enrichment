package eu.europeana.enrichment.web.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import eu.europeana.api.commons.definitions.vocabulary.CommonApiConstants;
import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.api.commons.web.model.vocabulary.Operations;
import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.solr.exception.SolrServiceException;
import eu.europeana.enrichment.web.model.EnrichmentNERRequest;
import eu.europeana.enrichment.web.service.impl.EnrichmentNERServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@EnableCaching
//@SwaggerSelect
@Api(tags = "Enrichment service", description=" ")
public class NERController extends BaseRest { 

	@Autowired
	EnrichmentNERServiceImpl enrichmentNerService;
	
	@Autowired
	PersistentStoryEntityService persistentStoryEntityService;
	
	Logger logger = LogManager.getLogger(getClass());
	
	/**
	 * This method represents the /enrichment/ner/{storyId} end point,
	 * where a request with a translated text is send and the named entities based on this text are retrieved.
	 * All requests on this end point are processed here.
	 * 
	 * @param storyId
	 * @param translationTool
	 * @param property
	 * @param linking
	 * @param nerTools
	 * @param original
	 * @return											a list of named entities converted to a json format
	 * @throws Exception
	 * @throws HttpException
	 * @throws SolrServiceException
	 */
	@ApiOperation(value = "Create named entities for a story", nickname = "createNEREntitiesStory", notes = "This method performs the Named Entity Recognition (NER) analysis "
			+ "for stories using the given set of parameters. Please note that if the given story is not in the language it can be analysed (English or German)" 
			+ "it should be first translated using the given API. The possible values for the parameters are: \"translationTool\"=Google or eTranslation, "
			+ "\"linking\"=Wikidata, \"nerTools\"=Stanford_NER or DBpedia_Spotlight (or both, comma separated),"
			+ "\"original\":true or false (meaning the analysis will be done on the original story or on the corresponding translation).")
	@RequestMapping(value = "/enrichment/ner/{storyId}", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getNEREntitiesStory(
			@PathVariable("storyId") String storyId,
			@RequestParam(value = "translationTool", required = true) String translationTool,
			@RequestParam(value = "property", required = false) String property,
			@RequestParam(value = "linking", required = true) String linking,
			@RequestParam(value = "nerTools", required = true) String nerTools,
			@RequestParam(value = "original", required = false, defaultValue = "false") Boolean original,
			HttpServletRequest request) throws Exception, HttpException, SolrServiceException {
	
		verifyWriteAccess(Operations.CREATE, request);

		EnrichmentNERRequest body = new EnrichmentNERRequest();
		body.setStoryId(storyId);
		body.setTranslationTool(translationTool);
		body.setProperty(property);
		body.setLinking(Arrays.asList(HelperFunctions.toArray(linking,",")));
		body.setNerTools(Arrays.asList(HelperFunctions.toArray(nerTools,",")));
		body.setOriginal(original);
		
		enrichmentNerService.createNamedEntities(body);
		ResponseEntity<String> response = new ResponseEntity<String>("{\"Result\":\"The NER analysis has been successfully executed.\"}", HttpStatus.OK);
		
		return response;
	}
	
	@ApiOperation(value = "Get named entities for a story", nickname = "getEntitiesStory", notes = "This method retrieves the Named Entity (NER) objects "
			+ "that are stored in the database by the given POST method: /enrichment/ner/{storyId}. For the description of parameters, please see the "
			+ "corresponding POST method.")
	@RequestMapping(value = "/enrichment/ner/{storyId}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getEntitiesStory(
			@PathVariable("storyId") String storyId,
			@RequestParam(value = "translationTool", required = true) String translationTool,
			@RequestParam(value = "property", required = false) String property,
			@RequestParam(value = "linking", required = true) String linking,
			@RequestParam(value = "nerTools", required = true) String nerTools,
			@RequestParam(value = CommonApiConstants.PARAM_WSKEY) String wskey,
			HttpServletRequest request) throws Exception, HttpException, SolrServiceException {
		
		verifyReadAccess(request);
		
		EnrichmentNERRequest body = new EnrichmentNERRequest();
		body.setStoryId(storyId);
		body.setTranslationTool(translationTool);
		body.setProperty(property);
		body.setLinking(Arrays.asList(HelperFunctions.toArray(linking,",")));
		body.setNerTools(Arrays.asList(HelperFunctions.toArray(nerTools,",")));
		body.setOriginal(false);
		
		String jsonLd = enrichmentNerService.getEntities(body);
		ResponseEntity<String> response = new ResponseEntity<String>(jsonLd, HttpStatus.OK);
		
		return response;
		
	}
	
	/*
	 * NER services for items
	 */
	
	@ApiOperation(value = "Create named entities for an item", nickname = "createNEREntitiesItem", notes = "This method performs the Named Entity Recognition (NER) analysis "
			+ "for items using the given set of parameters. Please note that if the text of the given item is not in the language it can be analysed (English or German)" 
			+ "it should be first translated using the given API. The possible values for the parameters are: \"translationTool\"=Google or eTranslation, "
			+ "\"linking\"=Wikidata, \"nerTools\"=Stanford_NER or DBpedia_Spotlight (or both, comma separated),"
			+ "\"original\":true or false (meaning the analysis will be done on the original item, or on the corresponding translation).")
	@RequestMapping(value = "/enrichment/ner/{storyId}/{itemId}", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getNEREntitiesItem(
			@PathVariable("storyId") String storyId,
			@PathVariable("itemId") String itemId,
			@RequestParam(value = "property", required = false) String property,
			@RequestParam(value = "linking", required = true) String linking,
			@RequestParam(value = "nerTools", required = true) String nerTools,
			@RequestParam(value = "original", required = false,defaultValue = "false") Boolean original,
			HttpServletRequest request) throws Exception, HttpException, SolrServiceException {

		verifyWriteAccess(Operations.CREATE, request);

		EnrichmentNERRequest body = new EnrichmentNERRequest();
		body.setStoryId(storyId);
		body.setItemId(itemId);
		body.setTranslationTool("Google");
		body.setProperty(property);
		body.setLinking(Arrays.asList(HelperFunctions.toArray(linking,",")));
		body.setNerTools(Arrays.asList(HelperFunctions.toArray(nerTools,",")));
		body.setOriginal(original);
		
		enrichmentNerService.createNamedEntities(body);
		ResponseEntity<String> response = new ResponseEntity<String>("{\"Result\":\"The NER analysis has been successfully executed.\"}", HttpStatus.OK);
		
		return response;		
	}
	
	@ApiOperation(value = "Get named entities for an item", nickname = "getEntitiesItem", notes = "This method retrieves the Named Entity (NER) objects "
			+ "that are stored in the database by the given POST method: /enrichment/ner/{itemId}. For the description of parameters, please see the" 
			+ " corresponding POST method." )
	@RequestMapping(value = "/enrichment/ner/{storyId}/{itemId}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getEntitiesItem(
			@PathVariable("storyId") String storyId,
			@PathVariable("itemId") String itemId,
			@RequestParam(value = "property", required = false) String property,
			@RequestParam(value = "linking", required = true) String linking,
			@RequestParam(value = "nerTools", required = true) String nerTools,
			@RequestParam(value = CommonApiConstants.PARAM_WSKEY) String wskey,
			HttpServletRequest request) throws Exception, HttpException, SolrServiceException {
		
		verifyReadAccess(request);

		EnrichmentNERRequest body = new EnrichmentNERRequest();
		body.setStoryId(storyId);
		body.setItemId(itemId);
		body.setTranslationTool("Google");
		body.setProperty(property);
		body.setLinking(Arrays.asList(HelperFunctions.toArray(linking,",")));
		body.setNerTools(Arrays.asList(HelperFunctions.toArray(nerTools,",")));
		body.setOriginal(false);
		
		String jsonLd = enrichmentNerService.getEntities(body);
		ResponseEntity<String> response = new ResponseEntity<String>(jsonLd, HttpStatus.OK);
		
		return response;
		
	}	
	
	@ApiOperation(value = "Compute named entities for all stories descriptions")
	@RequestMapping(value = "/enrichment/ner/allStories", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getNEREntitiesAllStories(
			HttpServletRequest request) throws Exception, HttpException, SolrServiceException {
	
		verifyWriteAccess(Operations.CREATE, request);

		String linking_local = "Wikidata";
		List<StoryEntity> stories = persistentStoryEntityService.getAllStoryEntities();	
		for(StoryEntity story : stories) {
			if(story.getDescriptionEn()!=null) {
				logger.info("NER analysis for the storyId: " + story.getStoryId());
				enrichmentNerService.updatedNamedEntitiesForText("DBpedia_Spotlight", story.getDescriptionEn(), "en", "description", story.getStoryId(), null, Arrays.asList(HelperFunctions.toArray(linking_local,",")), true);
				enrichmentNerService.updatedNamedEntitiesForText("Stanford_NER", story.getDescriptionEn(), "en", "description", story.getStoryId(), null, Arrays.asList(HelperFunctions.toArray(linking_local,",")), true);
				
			}			
		}

		ResponseEntity<String> response = new ResponseEntity<String>("{\"Result\":\"Done.\"}", HttpStatus.OK);
		return response;
	}

}
