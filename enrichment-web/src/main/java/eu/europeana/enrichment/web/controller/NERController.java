package eu.europeana.enrichment.web.controller;

import java.util.ArrayList;
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
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.common.serializer.JsonLdSerializer;
import eu.europeana.enrichment.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.model.vocabulary.NerTools;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.solr.exception.SolrServiceException;
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
	
	@Autowired 
	JsonLdSerializer jsonLdSerializer;
	
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
	@ApiOperation(value = "Create named entities for a story", nickname = "createNamedEntitiesStory", notes = "This method performs the Named Entity Recognition (NER) analysis "
			+ "for stories using the given set of parameters. Please note that if the given story is not in the language it can be analysed (English or German)" 
			+ "it should be first translated using the given API. The possible values for the parameters are: \"translationTool\"=Google or eTranslation, "
			+ "\"linking\"=Wikidata, \"nerTools\"=Stanford_NER or DBpedia_Spotlight (or both, comma separated),"
			+ "\"original\":true or false (meaning the analysis will be done on the original story or on the corresponding translation).")
	@RequestMapping(value = "/enrichment/ner/{storyId}", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createNamedEntitiesStory(
			@PathVariable("storyId") String storyId,
			@RequestParam(value = "translationTool", required = false) String translationTool,
			@RequestParam(value = "property", required = false) String property,
			@RequestParam(value = "linking", required = false) String linking,
			@RequestParam(value = "nerTools", required = false) String nerTools,
			@RequestParam(value = "original", required = false, defaultValue = "false") Boolean original,
			@RequestParam(value = "force", required = false,defaultValue = "false") Boolean force,
			HttpServletRequest request) throws Exception, HttpException, SolrServiceException {
	
		verifyWriteAccess(Operations.CREATE, request);
		
		if(translationTool==null) translationTool=EnrichmentConstants.defaultTranslationTool;
		if(property==null) property=EnrichmentConstants.STORY_ITEM_DESCRIPTION;
		if(linking==null) linking=EnrichmentConstants.WIKIDATA_LINKING;
		if(nerTools==null) nerTools=NerTools.Dbpedia.getStringValue() + "," + NerTools.Stanford.getStringValue();
		if(original==null) original=false;
		if(force==null) force=false;
		
		List<String> linkingList=new ArrayList<>(Arrays.asList(HelperFunctions.toArray(linking,",")));
		List<String> nerToolsList=new ArrayList<>(Arrays.asList(HelperFunctions.toArray(nerTools,",")));
		validateTranslationParams(storyId, null, translationTool, property, false);
		validateNERTools(nerToolsList);
		validateNERLinking(linkingList);
		
		String resultJsonLd = null;
		if(force) {
			List<NamedEntityImpl> result = enrichmentNerService.createNamedEntitiesForStory(storyId, property, nerToolsList, linkingList, translationTool, original, false);
			resultJsonLd=jsonLdSerializer.serializeObject(result);
		}
		else {
			List<NamedEntityImpl> result = enrichmentNerService.getEntities(storyId, null, property, nerToolsList);			
			if(!result.isEmpty()) {
				resultJsonLd=jsonLdSerializer.serializeObject(result);
			}
			else {	
				result = enrichmentNerService.createNamedEntitiesForStory(storyId, property, nerToolsList, linkingList, translationTool, original, false);
				resultJsonLd=jsonLdSerializer.serializeObject(result);
			}
		}
		ResponseEntity<String> response = new ResponseEntity<String>(resultJsonLd, HttpStatus.OK);
		return response;
	}
	
	@ApiOperation(value = "Get named entities for a story", nickname = "getNamedEntitiesStory", notes = "This method retrieves the Named Entity (NER) objects "
			+ "that are stored in the database by the given POST method: /enrichment/ner/{storyId}. For the description of parameters, please see the "
			+ "corresponding POST method.")
	@RequestMapping(value = "/enrichment/ner/{storyId}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getNamedEntitiesStory(
			@PathVariable("storyId") String storyId,
			@RequestParam(value = "property", required = false) String property,
			@RequestParam(value = "nerTools", required = false) String nerTools,
			@RequestParam(value = CommonApiConstants.PARAM_WSKEY) String wskey,
			HttpServletRequest request) throws Exception, HttpException, SolrServiceException {
		
		verifyReadAccess(request);
		
		if(property==null) property=EnrichmentConstants.STORY_ITEM_DESCRIPTION;
		if(nerTools==null) nerTools=NerTools.Dbpedia.getStringValue() + "," + NerTools.Stanford.getStringValue();
	
		List<String> nerToolsList=new ArrayList<>(Arrays.asList(HelperFunctions.toArray(nerTools,",")));
		validateBaseParamsForNEROrTranslation(storyId, null, property, false);
		validateNERTools(nerToolsList);
		
		List<NamedEntityImpl> result = enrichmentNerService.getEntities(storyId, null, property, nerToolsList);
		String resultJsonLd = null;
		if(result.isEmpty()) {
			resultJsonLd="{\"info\" : \"No found NamedEntity-s for the given input parameters!\"}";
		}
		else
		{
			resultJsonLd=jsonLdSerializer.serializeObject(result);
		}
		
		ResponseEntity<String> response = new ResponseEntity<String>(resultJsonLd, HttpStatus.OK);
		return response;
		
	}
	
	/*
	 * NER services for items
	 */
	
	@ApiOperation(value = "Create named entities for an item", nickname = "createNamedEntitiesItem", notes = "This method performs the Named Entity Recognition (NER) analysis "
			+ "for items using the given set of parameters. Please note that if the text of the given item is not in the language it can be analysed (English or German)" 
			+ "it should be first translated using the given API. The possible values for the parameters are: \"translationTool\"=Google or eTranslation, "
			+ "\"linking\"=Wikidata, \"nerTools\"=Stanford_NER or DBpedia_Spotlight (or both, comma separated),"
			+ "\"original\":true or false (meaning the analysis will be done on the original item, or on the corresponding translation).")
	@RequestMapping(value = "/enrichment/ner/{storyId}/{itemId}", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createNamedEntitiesItem(
			@PathVariable("storyId") String storyId,
			@PathVariable("itemId") String itemId,
			@RequestParam(value = "translationTool", required = false) String translationTool,
			@RequestParam(value = "property", required = false) String property,
			@RequestParam(value = "linking", required = false) String linking,
			@RequestParam(value = "nerTools", required = false) String nerTools,
			@RequestParam(value = "original", required = false, defaultValue = "false") Boolean original,
			@RequestParam(value = "force", required = false, defaultValue = "false") Boolean force,
			HttpServletRequest request) throws Exception, HttpException, SolrServiceException {

		verifyWriteAccess(Operations.CREATE, request);
		
		if(translationTool==null) translationTool=EnrichmentConstants.defaultTranslationTool;
		if(property==null) property=EnrichmentConstants.STORY_ITEM_TRANSCRIPTION;
		if(linking==null) linking=EnrichmentConstants.WIKIDATA_LINKING;
		if(nerTools==null) nerTools=NerTools.Dbpedia.getStringValue() + "," + NerTools.Stanford.getStringValue();
		if(original==null) original=false;
		if(force==null) force=false;
		
		List<String> linkingList=new ArrayList<>(Arrays.asList(HelperFunctions.toArray(linking,",")));
		List<String> nerToolsList=new ArrayList<>(Arrays.asList(HelperFunctions.toArray(nerTools,",")));
		validateTranslationParams(storyId, itemId, translationTool, property, true);
		validateNERTools(nerToolsList);
		validateNERLinking(linkingList);
	
		String resultJsonLd = null;
		if(force) {
			List<NamedEntityImpl> result = enrichmentNerService.createNamedEntitiesForItem(storyId, itemId, property, nerToolsList, linkingList, translationTool, original, false);
			resultJsonLd=jsonLdSerializer.serializeObject(result);			
		}
		else {
			List<NamedEntityImpl> result = enrichmentNerService.getEntities(storyId, itemId, property, nerToolsList);
			if(!result.isEmpty()) {
				resultJsonLd=jsonLdSerializer.serializeObject(result);
			}
			else {	
				result = enrichmentNerService.createNamedEntitiesForItem(storyId, itemId, property, nerToolsList, linkingList, translationTool, original, false);
				resultJsonLd=jsonLdSerializer.serializeObject(result);
			}
		}
		
		ResponseEntity<String> response = new ResponseEntity<String>(resultJsonLd, HttpStatus.OK);
		return response;		
	}
	
	@ApiOperation(value = "Get named entities for an item", nickname = "getNamedEntitiesItem", notes = "This method retrieves the Named Entity (NER) objects "
			+ "that are stored in the database by the given POST method: /enrichment/ner/{itemId}. For the description of parameters, please see the" 
			+ " corresponding POST method." )
	@RequestMapping(value = "/enrichment/ner/{storyId}/{itemId}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getNamedEntitiesItem(
			@PathVariable("storyId") String storyId,
			@PathVariable("itemId") String itemId,
			@RequestParam(value = "property", required = false) String property,
			@RequestParam(value = "nerTools", required = false) String nerTools,
			@RequestParam(value = CommonApiConstants.PARAM_WSKEY) String wskey,
			HttpServletRequest request) throws Exception, HttpException, SolrServiceException {
		
		verifyReadAccess(request);

		if(property==null) property=EnrichmentConstants.STORY_ITEM_DESCRIPTION;
		if(nerTools==null) nerTools=NerTools.Dbpedia.getStringValue() + "," + NerTools.Stanford.getStringValue();
	
		List<String> nerToolsList=new ArrayList<>(Arrays.asList(HelperFunctions.toArray(nerTools,",")));
		validateBaseParamsForNEROrTranslation(storyId, itemId, property, true);
		validateNERTools(nerToolsList);
		
		List<NamedEntityImpl> result = enrichmentNerService.getEntities(storyId, itemId, property, nerToolsList);
		String resultJsonLd = null;
		if(result.isEmpty()) {
			resultJsonLd="{\"info\" : \"No found NamedEntity-s for the given input parameters!\"}";
		}
		else
		{
			resultJsonLd=jsonLdSerializer.serializeObject(result);
		}

		ResponseEntity<String> response = new ResponseEntity<String>(resultJsonLd, HttpStatus.OK);
		
		return response;
		
	}	
	
//	@ApiOperation(value = "Compute named entities for all stories descriptions")
//	@RequestMapping(value = "/enrichment/ner/allStories", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<String> getNEREntitiesAllStories(
//			HttpServletRequest request) throws Exception, HttpException, SolrServiceException {
//	
//		verifyWriteAccess(Operations.CREATE, request);
//
//		String linking=EnrichmentConstants.defaultLinkingTool;
//		String nerTools=EnrichmentConstants.dbpediaSpotlightName + "," + EnrichmentConstants.stanfordNer;
//		List<String> linkingList=new ArrayList<>(Arrays.asList(HelperFunctions.toArray(linking,",")));
//		List<String> nerToolsList=new ArrayList<>(Arrays.asList(HelperFunctions.toArray(nerTools,",")));
//
//		List<StoryEntity> stories = persistentStoryEntityService.getAllStoryEntities();	
//		for(StoryEntity story : stories) {
//			logger.info("NER analysis for the storyId: " + story.getStoryId());
//			enrichmentNerService.createNamedEntitiesForStory(story.getStoryId(), EnrichmentConstants.STORY_ITEM_DESCRIPTION, nerToolsList, true, linkingList, EnrichmentConstants.defaultTranslationTool, false, false);
//		}
//
//		ResponseEntity<String> response = new ResponseEntity<String>("{\"Result\":\"Done.\"}", HttpStatus.OK);
//		return response;
//	}

}
