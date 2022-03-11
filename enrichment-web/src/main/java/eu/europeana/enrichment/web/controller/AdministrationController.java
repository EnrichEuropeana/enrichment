package eu.europeana.enrichment.web.controller;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.model.impl.StoryEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.translation.service.impl.ETranslationEuropaServiceImpl;
import eu.europeana.enrichment.web.model.EnrichmentNERRequest;
import eu.europeana.enrichment.web.model.EnrichmentTranslationRequest;
import eu.europeana.enrichment.web.service.EnrichmentStoryAndItemStorageService;
import eu.europeana.enrichment.web.service.EnrichmentTranslationService;
import eu.europeana.enrichment.web.service.impl.EnrichmentNERServiceImpl;
import eu.europeana.enrichment.web.service.impl.TranscribathonConcurrentCallServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@EnableCaching
//@SwaggerSelect
@Api(tags = "Administration service", description=" ")
public class AdministrationController extends BaseRest {

	Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	EnrichmentNERServiceImpl enrichmentNerService;
	@Autowired
	EnrichmentTranslationService enrichmentTranslationService;
	@Autowired
	PersistentTranslationEntityService persistentTranslationEntityService;
	@Autowired
	PersistentItemEntityService persistentItemEntityService;
	
	@Autowired
	ETranslationEuropaServiceImpl eTranslationService;
	
    @Autowired
    EnrichmentStoryAndItemStorageService enrichmentStoryAndItemStorageService;
    
    @Autowired
    TranscribathonConcurrentCallServiceImpl transcribathonConcurrentCallServiceImpl;
	
	/*
	 * This method represents the /administration/uploadStories end point,
	 * where a request with an array of StoryEntity to be saved to the database is sent
	 * All requests on this end point are processed here.
	 * 
	 * @param wskey						is the application key which is required
	 * 
	 * @param stories				    an array of StoryEntity to be uploaded to the database (each StoryEntity represents a list of ItemEntity)
	 * 
	 * @return							"Done" if everything ok
	 */
	@ApiOperation(value = "Upload StoryEntities to the database", nickname = "uploadStories", notes = "This method enables uploading a set of stories to the database"
			+ "directly from the HTTP request, meaning that the story fields are specified as an array of JSON formatted objects directly in the request body. Example: <br /> "
			+ "[ <br />" + 
			"  { <br />" + 
			"  \"transcriptionText\":\"Franz Joseph I was Emperor of Austria along with his wife: Empress Elizabeth of Austria, Queen of Hungary.\", <br />" + 
			"  \"title\":\"Franz Joseph I Emperor\", <br />" + 
			"  \"storyId\":\"1\", <br />" + 
			"  \"source\":\"http:\\/\\/www.europeana1914-1918.eu\\/en\\/contributions\\/1494\", <br />" + 
			"  \"description\":\"The story about Franz Joseph I Emperor\", <br />" + 
			"  \"summary\":\"\", <br />" + 
			"  \"language\":\"en\" <br /> " + 
			"  } <br />" + 
			"  ] <br />")
	@RequestMapping(value = "/administration/uploadStories", method = {RequestMethod.POST},
			consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> uploadStories(
			@RequestParam(value = "wskey", required = false) String wskey,
			@RequestBody StoryEntityImpl [] body) throws HttpException {
		
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			String uploadStoriesStatus = enrichmentNerService.uploadStories(body);
			
			ResponseEntity<String> response = new ResponseEntity<String>(uploadStoriesStatus, HttpStatus.OK);
		
			return response;
			
	}
	
	@ApiOperation(value = "Upload stories from Transcribathon using their ids.", nickname = "uploadStoriesFromTranscribathon", notes = "This method uploads a set of stories from Transcribathon to the db.")
	@RequestMapping(value = "/administration/uploadStoriesFromTranscribathon", method = {RequestMethod.POST},
			consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> uploadStoriesFromTranscribathon(
			@RequestParam(value = "wskey", required = false) String wskey,
			@RequestBody String storiesIds) throws Exception {
		
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			List<String> storyIdsList = new ArrayList<String>(Arrays.asList(storiesIds.split(",")));
			Instant start = Instant.now();
			String notFetchedStoryIds = "";
			int numberNotFetchedStories = 0;
			for (int i = 0; i < storyIdsList.size(); i++) {
				StoryEntity storyFetchedAgain = enrichmentStoryAndItemStorageService.fetchAndSaveStoryFromTranscribathon(storyIdsList.get(i));
				if(storyFetchedAgain==null) {
					notFetchedStoryIds += " " + storyIdsList.get(i);
					numberNotFetchedStories ++;
				}	
			}
			Instant finish = Instant.now();
			long timeElapsed = Duration.between(start, finish).getSeconds();
			logger.debug("Total time: " + timeElapsed + " s.");
			if(numberNotFetchedStories>0) {
				logger.debug("Number not fetched stories: " + String.valueOf(numberNotFetchedStories) + ".");
				logger.debug("Not fetched storyIds: " + String.valueOf(notFetchedStoryIds) + ".");
			}
			/*
			 * The commented-out code below is for the parallel fetching of stories 
			 */
//			Instant start = Instant.now();
//			List<CompletableFuture<String>> allFutures = new ArrayList<>();
//			for (int i=0; i<storyIdsList.size(); i++) {
//				allFutures.add(transcribathonConcurrentCallServiceImpl.callStoryMinimalService(storyIdsList.get(i)));
//			}
//			CompletableFuture.allOf(allFutures.toArray(new CompletableFuture[0])).join();
//			//fetching the stories that from some reason failed to be fetched
//			int numberInitiallyNotFetchedStories = 0;
//			int numberFinalNotFetchedStories = 0;
//			for (int i = 0; i < storyIdsList.size(); i++) {
//				if(allFutures.get(i).get()!=null) {
//					StoryEntity storyFetchedAgain = enrichmentStoryAndItemStorageService.fetchAndSaveStoryFromTranscribathon(allFutures.get(i).get().toString());
//					if(storyFetchedAgain==null) numberFinalNotFetchedStories++;
//					numberInitiallyNotFetchedStories ++;
//				}				
//			}
//			Instant finish = Instant.now();
//			long timeElapsed = Duration.between(start, finish).getSeconds();
//
//			logger.debug("Total time: " + timeElapsed + " s.");
//			logger.debug("Number initially not fetched stories: " + String.valueOf(numberInitiallyNotFetchedStories) + ".");
//			logger.debug("Number final not fetched stories: " + String.valueOf(numberFinalNotFetchedStories) + ".");
			
			String responseString = "{\"info\": \"Done successfully!\"}";
			
			ResponseEntity<String> response = new ResponseEntity<String>(responseString, HttpStatus.OK);
		
			return response;
			
	}
	
	/*
	 * This method represents the /administration/uploadItems end point,
	 * where a request with a ItemEntity information to be saved in the database is sent
	 * All requests on this end point are processed here.
	 * 
	 * @param wskey						is the application key which is required
	 * 
	 * @param items				         an array of ItemEntity to be uploaded to the database (each StoryEntity represents a list of ItemEntity)
	 * 
	 * @return							"Done" if everything ok
	 */
	
	@ApiOperation(value = "Upload ItemEntities to the database", nickname = "uploadItems", notes = "This method enables uploading a set of items to the database"
			+ "directly from the HTTP request, meaning that the item fields are specified as an array of JSON formatted objects directly in the request body. Please "
			+ "note that to upload new items, a story with the given storyId must exist. Example: <br /> "
			+ "[ <br />" + 
			"  { <br />" + 
			"  \"transcriptionText\":\"Franz Joseph I was Emperor of Austria along with his wife: Empress Elizabeth of Austria, Queen of Hungary.\", <br />" + 
			"  \"title\":\"Franz Joseph I Emperor\", <br />" + 
			"  \"storyId\":\"1\", <br />" + 
			"  \"source\":\"http:\\/\\/www.europeana1914-1918.eu\\/en\\/contributions\\/1494\", <br />" + 
			"  \"itemId\":\"1\", <br />" + 
			"  \"description\":\"The story about Franz Joseph I Emperor\", <br />" + 
			"  \"type\":\"text\", <br />" + 
			"  \"language\":\"en\" <br /> " + 
			"  } <br />" + 
			"  ] <br />")
	@RequestMapping(value = "/administration/uploadItems", method = {RequestMethod.POST},
			consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> uploadItems(
			@RequestParam(value = "wskey", required = false) String wskey,
			@RequestBody ItemEntityImpl [] body) throws HttpException, Exception {
	
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			String uploadItemsStatus = enrichmentNerService.uploadItems(body);
			
			ResponseEntity<String> response = new ResponseEntity<String>(uploadItemsStatus, HttpStatus.OK);
		
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
			@RequestParam(value = "wskey", required = false) String wskey,
			@RequestBody EnrichmentTranslationRequest [] body) throws HttpException  {

			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
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
	@ApiOperation(value = "Get translated text from eTranslation", nickname = "getETranslation", notes = "This method represents an endpoint"
			+ "where the callback from the eTranslation service is received. The method is not aimed to be used by an and user.")
	@RequestMapping(value = "/administration/eTranslation", method = {RequestMethod.POST},
			produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> getETranslation(
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

	@ApiOperation(value = "Run NER analysis for all items", nickname = "runNERAllItems", notes = "This method performs the Named Entity Recognition (NER) analysis "
			+ "for all items in the database. It includes both items that are translated and those that have the transcription text in English language.")
	@RequestMapping(value = "/administration/ner/allitems", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> runNERAllItems(
			@RequestParam(value = "wskey", required = true) String wskey) throws Exception, HttpException, SolrNamedEntityServiceException {
	
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);

			EnrichmentNERRequest body = new EnrichmentNERRequest();
			String linking_local = "Wikidata";
			String nerTools_local = "Stanford_NER,DBpedia_Spotlight";
			String jsonLd;
			
			List<TranslationEntity> all_translation_entities = persistentTranslationEntityService.getAllTranslationEntities();

			if(all_translation_entities!=null)
			{
				for(TranslationEntity tr_entity : all_translation_entities) {	
					
					if(!tr_entity.getItemId().equalsIgnoreCase("all"))
					{
					
						body.setStoryId(tr_entity.getStoryId());
						body.setItemId(tr_entity.getItemId());
						body.setTranslationTool("Google");
						body.setLinking(Arrays.asList(linking_local.split(",")));
						body.setNerTools(Arrays.asList(nerTools_local.split(",")));
						body.setOriginal(false);
											
						if(tr_entity.getTranslatedText()!=null && !tr_entity.getTranslatedText().equalsIgnoreCase(""))
						{
							jsonLd = enrichmentNerService.getEntities(body, true);
						}
					}				
				}
			}
			
			//run the analysis for the items that have original language "en"
			List<ItemEntity> all_item_entities = persistentItemEntityService.getAllItemEntities();

			if(all_item_entities!=null)
			{
				for(ItemEntity item_entity : all_item_entities) {	
				
					body.setStoryId(item_entity.getStoryId());
					body.setItemId(item_entity.getItemId());
					body.setTranslationTool("Google");
					body.setLinking(Arrays.asList(linking_local.split(",")));
					body.setNerTools(Arrays.asList(nerTools_local.split(",")));
					body.setOriginal(true);
										
					if(item_entity.getTranscriptionText()!=null && !item_entity.getTranscriptionText().equalsIgnoreCase(""))
					{
						jsonLd = enrichmentNerService.getEntities(body, true);
					}				
				}
			}
			
			ResponseEntity<String> response = new ResponseEntity<String>("all-items-ner-done", HttpStatus.OK);
			return response;
	
	}
	

}
