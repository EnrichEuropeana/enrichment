package eu.europeana.enrichment.web.controller;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

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
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.model.impl.StoryEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.translation.service.TranslationService;
import eu.europeana.enrichment.web.config.swagger.SwaggerSelect;
import eu.europeana.enrichment.web.exception.ApplicationAuthenticationException;
import eu.europeana.enrichment.web.exception.ParamValidationException;
import eu.europeana.enrichment.web.model.EnrichmentNERRequest;
import eu.europeana.enrichment.web.model.EnrichmentTranslationRequest;
import eu.europeana.enrichment.web.service.EnrichmentNERService;
import eu.europeana.enrichment.web.service.EnrichmentTranslationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@EnableCaching
@SwaggerSelect
@Api(tags = "Administration service", description=" ")
public class AdministrationController extends BaseRest {

	@Resource
	EnrichmentNERService enrichmentNerService;
	@Resource
	EnrichmentTranslationService enrichmentTranslationService;
	@Resource(name = "persistentTranslationEntityService")
	PersistentTranslationEntityService persistentTranslationEntityService;
	@Resource(name = "persistentItemEntityService")
	PersistentItemEntityService persistentItemEntityService;
	
	@Resource(name = "eTranslationService")
	TranslationService eTranslationService;
	
	/*
	 * This method represents the /administration/uploadStoriesAndItemsFromJson end point,
	 * where a request with 2 json files (one for stories and one for items) to be read and 
	 * saved to the database is sent
	 * 
	 * All requests on this end point are processed here.
	 * 
	 * @param wskey						is the application key which is required
	 * 
	 * @param jsonFileStoriesPath		the path to the json file for stories
	 * 
	 * @param jsonFileItemsPath		    the path to the json file for items
	 * 
	 * @return							"Done" if everything ok
	 */
	@ApiOperation(value = "Upload Story and Item entries from the json file to the database", nickname = "uploadStoriesAndItemsFromJson", notes = "This method reads the stories and items"
			+ "from the given JSON files and saves them to the database. The files for reading the stories and items need to be specified as parameters to the request in the form of a proper full path to the files (e.g. C:/java/stories.json)")
	@RequestMapping(value = "/administration/uploadStoriesAndItemsFromJson", method = {RequestMethod.POST} , produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> uploadStories(
			@RequestParam(value = "wskey", required = false) String wskey,
			@RequestParam(value = "jsonFileStories", required = true) String jsonStories,
			@RequestParam(value = "jsonFileItems", required = true) String jsonItems
			) throws ParamValidationException, NoSuchAlgorithmException, UnsupportedEncodingException, HttpException {
		
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			String uploadStoriesStatus = enrichmentNerService.readStoriesAndItemsFromJson(jsonStories, jsonItems);
			
			ResponseEntity<String> response = new ResponseEntity<String>(uploadStoriesStatus, HttpStatus.OK);
		
			return response;
		
	}
	
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
					
					if(tr_entity.getItemId().compareToIgnoreCase("all")!=0)
					{
					
						body.setStoryId(tr_entity.getStoryId());
						body.setItemId(tr_entity.getItemId());
						body.setTranslationTool("Google");
						body.setLinking(Arrays.asList(linking_local.split(",")));
						body.setNerTools(Arrays.asList(nerTools_local.split(",")));
						body.setOriginal(false);
											
						if(tr_entity.getTranslatedText()!=null && tr_entity.getTranslatedText().compareToIgnoreCase("")!=0)
						{
							jsonLd = enrichmentNerService.getEntities(body,null,true);
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
										
					if(item_entity.getTranscriptionText()!=null && item_entity.getTranscriptionText().compareToIgnoreCase("")!=0)
					{
						jsonLd = enrichmentNerService.getEntities(body,null,true);
					}				
				}
			}
			
			ResponseEntity<String> response = new ResponseEntity<String>("all-items-ner-done", HttpStatus.OK);
			return response;
	
	}
	

}
