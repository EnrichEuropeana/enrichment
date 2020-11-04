package eu.europeana.enrichment.web.controller;

import java.util.Arrays;

import javax.annotation.Resource;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
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
	
	@Resource(name = "persistentTranslationEntityService")
	PersistentTranslationEntityService persistentTranslationEntityService;

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
	@ApiOperation(value = "Get named entities for a story", nickname = "getNEREntitiesStory", notes = "This method performs the Named Entity Recognition (NER) analysis "
			+ "for stories using the given set of parameters. Please note that if the given story is not in the language it can be analysed (English or German)" 
			+ "it should be first translated using the given API. The possible values for the parameters are: \"translationTool\"=Google or eTranslation, "
			+ "\"linking\"=Wikidata, \"nerTools\"=Stanford_NER or DBpedia_Spotlight (or both, comma separated),"
			+ "\"original\":true or false (meaning the analysis will be done on the original story or on the corresponding translation)."
			+ "\"text\": a new text for the given \"property\" provided by the user to be analysed. If the provided non-empty body is different from the text of "
			+ "the given field of the story, it will be changed accordingly.")
	@RequestMapping(value = "/enrichment/ner/{storyId}", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> getNEREntitiesStory(
			@RequestParam(value = "wskey", required = true) String wskey,
			@PathVariable("storyId") String storyId,
			@RequestParam(value = "translationTool", required = true) String translationTool,
			@RequestParam(value = "property", required = false) String property,
			@RequestParam(value = "linking", required = true) String linking,
			@RequestParam(value = "nerTools", required = true) String nerTools,
			@RequestParam(value = "original", required = false, defaultValue = "false") Boolean original,
			@RequestBody(required = false) String text) throws Exception, HttpException, SolrNamedEntityServiceException {
	
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
//			if(storyId.compareToIgnoreCase("nerallitems")==0)
//			{
//				EnrichmentNERRequest body = new EnrichmentNERRequest();
//				String linking_local = "Wikidata";
//				String nerTools_local = "Stanford_NER,DBpedia_Spotlight";
//				String jsonLd;
//				
//				List<TranslationEntity> all_translation_entities = persistentTranslationEntityService.getAllTranslationEntities();
//
//				if(all_translation_entities!=null)
//				{
//					for(TranslationEntity tr_entity : all_translation_entities) {	
//						
//						if(tr_entity.getItemId().compareToIgnoreCase("all")!=0)
//						{
//						
//							body.setStoryId(tr_entity.getStoryId());
//							body.setItemId(tr_entity.getItemId());
//							body.setTranslationTool("Google");
//							body.setLinking(Arrays.asList(linking_local.split(",")));
//							body.setNerTools(Arrays.asList(nerTools_local.split(",")));
//							body.setOriginal(false);
//												
//							if(tr_entity.getTranslatedText()!=null && tr_entity.getTranslatedText().compareToIgnoreCase("")!=0)
//							{
//								jsonLd = enrichmentNerService.getEntities(body,null,true);
//							}
//						}				
//					}
//				}
//				
//				ResponseEntity<String> response = new ResponseEntity<String>("all-items-ner-done", HttpStatus.OK);
//				return response;
//			}
			
				EnrichmentNERRequest body = new EnrichmentNERRequest();
				body.setStoryId(storyId);
				body.setItemId("all");
				body.setTranslationTool(translationTool);
				body.setProperty(property);
				body.setLinking(Arrays.asList(linking.split(",")));
				body.setNerTools(Arrays.asList(nerTools.split(",")));
				body.setOriginal(original);
				
				if(text!=null && text.compareToIgnoreCase("{}")==0) text="";
				String jsonLd = enrichmentNerService.getEntities(body,text, true);
				ResponseEntity<String> response = new ResponseEntity<String>(jsonLd, HttpStatus.OK);
				
				return response;
		
	}
	
	@ApiOperation(value = "Get named entities for a story", nickname = "getEntitiesStory", notes = "This method retrieves the Named Entity (NER) objects "
			+ "that are stored in the database by the given POST method: /enrichment/ner/{storyId}. For the description of parameters, please see the "
			+ "corresponding POST method.")
	@RequestMapping(value = "/enrichment/ner/{storyId}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getEntitiesStory(
			@RequestParam(value = "wskey", required = true) String wskey,
			@PathVariable("storyId") String storyId,
			@RequestParam(value = "translationTool", required = true) String translationTool,
			@RequestParam(value = "property", required = false) String property,
			@RequestParam(value = "linking", required = true) String linking,
			@RequestParam(value = "nerTools", required = true) String nerTools
			//@RequestParam(value = "original", required = true) Boolean original
			) throws Exception, HttpException, SolrNamedEntityServiceException {
		
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			EnrichmentNERRequest body = new EnrichmentNERRequest();
			body.setStoryId(storyId);
			body.setItemId("all");
			body.setTranslationTool(translationTool);
			body.setProperty(property);
			body.setLinking(Arrays.asList(linking.split(",")));
			body.setNerTools(Arrays.asList(nerTools.split(",")));
			body.setOriginal(false);
			
			String jsonLd = enrichmentNerService.getEntities(body,null, false);
			ResponseEntity<String> response = new ResponseEntity<String>(jsonLd, HttpStatus.OK);
			
			return response;
		
	}
	
	/*
	 * NER services for items
	 */
	
	@ApiOperation(value = "Get named entities for an item", nickname = "getNEREntitiesItem", notes = "This method performs the Named Entity Recognition (NER) analysis "
			+ "for items using the given set of parameters. Please note that if the text of the given item is not in the language it can be analysed (English or German)" 
			+ "it should be first translated using the given API. The possible values for the parameters are: \"translationTool\"=Google or eTranslation, "
			+ "\"linking\"=Wikidata, \"nerTools\"=Stanford_NER or DBpedia_Spotlight (or both, comma separated),"
			+ "\"original\":true or false (meaning the analysis will be done on the original item, or on the corresponding translation)."
			+ "\"text\": a new text for the given \"property\" provided by the user to be analysed. If the provided non-empty body is different from the text of" 
			+ " the given field of the item, it will be changed accordingly.")
	@RequestMapping(value = "/enrichment/ner/{storyId}/{itemId}", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> getNEREntitiesItem(
			@RequestParam(value = "wskey", required = true) String wskey,
			@PathVariable("storyId") String storyId,
			@PathVariable("itemId") String itemId,
			@RequestParam(value = "property", required = false) String property,
			@RequestParam(value = "linking", required = true) String linking,
			@RequestParam(value = "nerTools", required = true) String nerTools,
			@RequestParam(value = "original", required = false,defaultValue = "false") Boolean original,
			@RequestBody(required = false) String text) throws Exception, HttpException, SolrNamedEntityServiceException {
		
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
			
			if(text!=null && text.compareToIgnoreCase("{}")==0) text="";
			String jsonLd = enrichmentNerService.getEntities(body,text, true);
			ResponseEntity<String> response = new ResponseEntity<String>(jsonLd, HttpStatus.OK);
			
			return response;
		
	}
	
	@ApiOperation(value = "Get named entities for an item", nickname = "getEntitiesItem", notes = "This method retrieves the Named Entity (NER) objects "
			+ "that are stored in the database by the given POST method: /enrichment/ner/{itemId}. For the description of parameters, please see the" 
			+ " corresponding POST method." )
	@RequestMapping(value = "/enrichment/ner/{storyId}/{itemId}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getEntitiesItem(
			@RequestParam(value = "wskey", required = true) String wskey,
			@PathVariable("storyId") String storyId,
			@PathVariable("itemId") String itemId,
			@RequestParam(value = "property", required = false) String property,
			@RequestParam(value = "linking", required = true) String linking,
			@RequestParam(value = "nerTools", required = true) String nerTools
			//@RequestParam(value = "original", required = true) Boolean original
			) throws Exception, HttpException, SolrNamedEntityServiceException {
		
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			EnrichmentNERRequest body = new EnrichmentNERRequest();
			body.setStoryId(storyId);
			body.setItemId(itemId);
			body.setTranslationTool("Google");
			body.setProperty(property);
			body.setLinking(Arrays.asList(linking.split(",")));
			body.setNerTools(Arrays.asList(nerTools.split(",")));
			body.setOriginal(false);
			
			String jsonLd = enrichmentNerService.getEntities(body,null, false);
			ResponseEntity<String> response = new ResponseEntity<String>(jsonLd, HttpStatus.OK);
			
			return response;
		
	}
}
