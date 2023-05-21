package eu.europeana.enrichment.web.controller;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.europeana.api.commons.definitions.vocabulary.CommonApiConstants;
import eu.europeana.api.commons.web.exception.ApplicationAuthenticationException;
import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.api.commons.web.model.vocabulary.Operations;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.definitions.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.definitions.model.impl.StoryEntityImpl;
import eu.europeana.enrichment.translation.exception.TranslationException;
import eu.europeana.enrichment.translation.service.impl.ETranslationEuropaServiceImpl;
import eu.europeana.enrichment.web.common.config.I18nConstants;
import eu.europeana.enrichment.web.exception.ParamValidationException;
import eu.europeana.enrichment.web.model.EnrichmentTranslationRequest;
import eu.europeana.enrichment.web.service.EnrichmentStoryAndItemStorageService;
import eu.europeana.enrichment.web.service.EnrichmentTranslationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@EnableCaching
//@SwaggerSelect
@Api(tags = "Administration service", description=" ")
public class AdministrationController extends BaseRest {

	Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	EnrichmentTranslationService enrichmentTranslationService;
	
	@Autowired
	ETranslationEuropaServiceImpl eTranslationService;
	
    @Autowired
    EnrichmentStoryAndItemStorageService enrichmentStoryAndItemStorageService;
   
	/*
	 * This method represents the /administration/updateStories endpoint,
	 * where a request with an array of StoryEntity to be updated in the database is sent.
	 * All requests on this end point are processed here.
	 * 
	 * @param stories				    an array of StoryEntity to be updated in the database
	 * 
	 * @return							"Done" if everything ok
	 */
	@ApiOperation(value = "Update StoryEntities from input in the database.", nickname = "updateStories", notes = "This method enables updating a set of stories from input in the database."
			+ "directly from the HTTP request, meaning that the story fields are specified as an array of JSON formatted objects directly in the request body. In case the input stories do not exist"
			+ "in the db, they will be stored as new.")
	@RequestMapping(value = "/administration/updateStories", method = {RequestMethod.POST},
			consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> updateStories(
			@RequestBody StoryEntityImpl [] body,
			HttpServletRequest request) throws HttpException {
		
		verifyWriteAccess(Operations.CREATE, request);
		
		if(body==null) {
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentConstants.BODY, null);
		}
		for(StoryEntityImpl story: body) {
			validateStory(story);
		}
		
		enrichmentStoryAndItemStorageService.updateStoriesFromInput(body);
		ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
		return response;	
	}
	
	@ApiOperation(value = "Update stories from Transcribathon using their ids.", nickname = "updateStoriesFromTranscribathon", notes = "This method updates a set of stories from Transcribathon to the db.")
	@RequestMapping(value = "/administration/updateStoriesFromTranscribathon", method = {RequestMethod.POST},
			consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> updateStoriesFromTranscribathon(
			@RequestBody List<String> storyIdsList,
			HttpServletRequest request) throws Exception {
		
		verifyWriteAccess(Operations.CREATE, request);
		List<String> fieldsToUpdate = new ArrayList<>();
		fieldsToUpdate.add(EnrichmentConstants.STORY_ITEM_TRANSCRIPTION);
		fieldsToUpdate.add(EnrichmentConstants.STORY_ITEM_DESCRIPTION);
		fieldsToUpdate.add(EnrichmentConstants.STORY_ITEM_SUMMARY);
		Instant start = Instant.now();
		for (int i = 0; i < storyIdsList.size(); i++) {
			enrichmentStoryAndItemStorageService.updateStoryFromTranscribathon(storyIdsList.get(i), fieldsToUpdate);
		}
		Instant finish = Instant.now();
		long timeElapsed = Duration.between(start, finish).getSeconds();
		logger.debug("Total time: " + timeElapsed + " s.");
		/*
		 * The commented-out code below is for the parallel fetching of stories 
		 */
//		Instant start = Instant.now();
//		List<CompletableFuture<String>> allFutures = new ArrayList<>();
//		for (int i=0; i<storyIdsList.size(); i++) {
//			allFutures.add(transcribathonConcurrentCallServiceImpl.callStoryMinimalService(storyIdsList.get(i)));
//		}
//		CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).join();
//		//fetching the stories that from some reason failed to be fetched
//		int numberInitiallyNotFetchedStories = 0;
//		int numberFinalNotFetchedStories = 0;
//		for (int i = 0; i < storyIdsList.size(); i++) {
//			if(allFutures.get(i).get()!=null) {
//				StoryEntity storyFetchedAgain = enrichmentStoryAndItemStorageService.fetchAndSaveStoryFromTranscribathon(allFutures.get(i).get().toString());
//				if(storyFetchedAgain==null) numberFinalNotFetchedStories++;
//				numberInitiallyNotFetchedStories ++;
//			}				
//		}
//		Instant finish = Instant.now();
//		long timeElapsed = Duration.between(start, finish).getSeconds();
//
//		logger.debug("Total time: " + timeElapsed + " s.");
//		logger.debug("Number initially not fetched stories: " + String.valueOf(numberInitiallyNotFetchedStories) + ".");
//		logger.debug("Number final not fetched stories: " + String.valueOf(numberFinalNotFetchedStories) + ".");
			
		String responseString = "{\"info\": \"Done successfully!\"}";		
		ResponseEntity<String> response = new ResponseEntity<String>(responseString, HttpStatus.OK);
		return response;		
	}
	
	/*
	 * This method represents the /administration/uploadItems end point,
	 * where a request with a ItemEntity information to be saved in the database is sent
	 * All requests on this end point are processed here.
	 * 
	 * @param items				         an array of ItemEntity to be uploaded to the database (each StoryEntity represents a list of ItemEntity)
	 * 
	 * @return							"Done" if everything ok
	 */
	
	@ApiOperation(value = "Update items in the database from input.", nickname = "updateItems", notes = "This method enables updating a set of items in the database."
			+ "directly from the HTTP request, meaning that the item fields are specified as an array of JSON formatted objects directly in the request body. Please "
			+ "note that to create new items (if they do not exist in the db), a story with the given storyId must exist in the db.")
	@RequestMapping(value = "/administration/updateItems", method = {RequestMethod.POST},
			consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> updateItems(
			@RequestBody ItemEntityImpl [] body,
			HttpServletRequest request) throws HttpException, Exception {
		
		verifyWriteAccess(Operations.CREATE, request);
		
		if(body==null) {
			throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentConstants.BODY, null);
		}
		for(ItemEntityImpl item: body) {
			validateItem(item);
		}
		
		enrichmentStoryAndItemStorageService.updateItemsFromInput(body);
		ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.OK);
		return response;
	}
	
	@ApiOperation(value = "Upload translated text (Google, eTranslation)", nickname = "uploadTranslation", notes = "This method enables uploading already translated text of the story or item"
			+ "to the database, by specifying the required translation fields directly in the request body. In case of story translation upload, please specify the \"itemId\" field to be \"all\". In case the translation of the given story or item does not exist in the system at all, "
			+ "please first translate the story or item using the given translation API (i.e. either /enrichment/translation/{storyId}/{itemId} or /enrichment/translation/{storyId}). Example: <br /> "
			+ "[ <br />" + 
			"  { <br />" + 
			"  \"originalText\":\"Franz Joseph I., Kaiser von Österreich, zusammen mit seiner Frau : Kaiserin Elisabeth von Österreich, Königin von Ungarn.\", <br />" +
			"  \"text\":\"Franz Joseph I, an Emperor of Austria, along with his wife: Empress Elizabeth of Austria, Queen of Hungary.\", <br />" +  
			"  \"storyId\":\"1\", <br />" + 
			"  \"itemId\":\"1\", <br />" + 
			"  \"translationTool\":\"Google\", <br />" + 
			"  \"type\":\"transcription\" <br />" +
			"  } <br />" + 
			"  ] <br />")
	@RequestMapping(value = "/administration/uploadTranslation", method = {RequestMethod.POST},
			consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> uploadTranslation(
			@RequestBody EnrichmentTranslationRequest [] body,
			HttpServletRequest request) throws HttpException  {

		verifyWriteAccess(Operations.CREATE, request);
		int i=1;
		String translation = "{info: ";
		for (EnrichmentTranslationRequest translationRequest : body)
		{
			translation += enrichmentTranslationService.uploadTranslation(translationRequest, i);	
		}
			
		translation += "}";
		ResponseEntity<String> response = new ResponseEntity<String>(translation, HttpStatus.OK);
		return response;
	}
	
	/*
	 * This method represents the /enrichment/eTranslation end point,
	 * where a translation response from eTranslation will be processed.
	 * All requests on this end point are processed here.
	 * 
	 * @param translationRequest		is the Rest Post body with the original
	 * 									text for translation into English
	 * return 							the translated text or for eTranslation
	 * 									only an ID
	 */
	@ApiOperation(value = "Receive translated text from eTranslation", nickname = "getFromETranslation", notes = "This method represents an endpoint"
			+ "where the callback from the eTranslation service is received. The method is not aimed to be used by an and user.")
	@RequestMapping(value = "/administration/receiveETranslation", method = {RequestMethod.POST},
			produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> getFromETranslation(
			@RequestParam(value = "target-language", required = false) String targetLanguage,
			@RequestParam(value = "translated-text", required = false) String translatedTextSnippet,
			@RequestParam(value = "request-id", required = false) String requestId,
			@RequestParam(value = "external-reference", required = false) String externalReference,
			@RequestBody String body) throws UnsupportedEncodingException 
	{
		
		eTranslationService.eTranslationResponse(targetLanguage,translatedTextSnippet,requestId,externalReference,body);
		ResponseEntity<String> response = new ResponseEntity<String>("{\"info\" : \"eTranslation callback has been executed!\"}", HttpStatus.OK);
		return response;
	}

	@ApiOperation(value = "Get translated text from eTranslation", nickname = "getETranslation", notes = "This method is aimed to be used "
			+ "when translating with eTranslation locally. Namely, the eTranslation services returns response using a callback function, which should be "
			+ "accessible from the Internet. When the app is deployed to the test server, it will be accessible from the internet, so this method can be "
			+ "used to get the eTranslation responses.")
	@RequestMapping(value = "/administration/eTranslation", method = {RequestMethod.POST},
			produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> getETranslation(
			@RequestBody String text,
			@RequestParam(value = "sourceLang") String sourceLang,
			@RequestParam(value = "targetLang") String targetLang,
			@RequestParam(value = CommonApiConstants.PARAM_WSKEY) String wskey,
			HttpServletRequest request) throws TranslationException, UnsupportedEncodingException, InterruptedException, ApplicationAuthenticationException 
	{
		verifyReadAccess(request);
		String resp = eTranslationService.translateText(text, sourceLang, targetLang);
		if(resp==null) {
			ResponseEntity<String> response = new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
			return response;
		}
		else {
			ResponseEntity<String> response = new ResponseEntity<String>(resp, HttpStatus.OK);
			return response;
		}
	}

//	@ApiOperation(value = "Run NER analysis for all items", nickname = "runNERAllItems", notes = "This method performs the Named Entity Recognition (NER) analysis "
//			+ "for all items in the database. It includes both items that are translated and those that have the transcription text in English language.")
//	@RequestMapping(value = "/administration/ner/allitems", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.TEXT_PLAIN_VALUE)
//	public ResponseEntity<String> runNERAllItems(
//			HttpServletRequest request) throws Exception, HttpException, SolrServiceException {
//	
//		verifyWriteAccess(Operations.CREATE, request);
//
//		String linkingStr = "Wikidata";
//		String nerToolsStr = "Stanford_NER,DBpedia_Spotlight";
//		List<String> linking = Arrays.asList(HelperFunctions.toArray(linkingStr,","));
//		List<String> nerTools = Arrays.asList(HelperFunctions.toArray(nerToolsStr,","));						
//		
//		//run the ner analysis for all items
//		List<ItemEntity> all_item_entities = persistentItemEntityService.getAllItemEntities();
//
//		if(all_item_entities!=null)
//		{
//			for(ItemEntity item : all_item_entities) {	
//				if(! StringUtils.isBlank(item.getTranscriptionText()))
//				{
//					enrichmentNerService.createNamedEntities(item.getStoryId(), item.getItemId(), EnrichmentConstants.STORY_ITEM_TRANSCRIPTION, nerTools, true, linking, EnrichmentConstants.defaultTranslationTool, false, false);
//				}
//			}
//		}
//		
//		ResponseEntity<String> response = new ResponseEntity<String>("all-items-ner-done", HttpStatus.OK);
//		return response;
//	
//	}

}
