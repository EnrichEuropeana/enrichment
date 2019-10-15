package eu.europeana.enrichment.web.controller;

import java.util.ArrayList;
import java.util.List;

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
import eu.europeana.enrichment.solr.service.SolrWikidataEntityService;
import eu.europeana.enrichment.web.config.swagger.SwaggerSelect;
import eu.europeana.enrichment.web.service.EnrichmentNERService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@EnableCaching
@SwaggerSelect
@Api(tags = "Enrichment service", description=" ")
public class AnnotationController extends BaseRest {

	@Resource
	EnrichmentNERService enrichmentNerService;
	
    /**
     * This method represents the /enrichment/annotation/{storyId} end point,
	 * where the annotations for all NamedEntities of a story are retrieved using the class NamedEntityAnnotationCollection.
	 * All requests on this end point are processed here.
     * @param wskey
     * @param storyId
     * @return
     * @throws Exception
     * @throws HttpException
     */
	@ApiOperation(value = "Get annotation collection preview", nickname = "getAnnotationCollection", notes = "This method retrieves the annotations of "
			+ "stories or items, that are stored using the corresponding POST request (/enrichment/annotation/{storyId}/{itemId}). The parameter \"storyId\" enables considering the annotations that are only realted to the given story."
			+ " The parameter \"itemId\" further restricts retrieving the annotations related to the given story item. If \"itemId\" is set to \"all\", then all annotations"
			+ " that belong to the given story are retrieved.")
	@RequestMapping(value = "/enrichment/annotation/{storyId}/{itemId}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAnnotationCollection(
			@RequestParam(value = "wskey", required = false) String wskey,
			@PathVariable("storyId") String storyId,
			@PathVariable("itemId") String itemId) throws Exception, HttpException {

			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			String result = enrichmentNerService.getStoryOrItemAnnotationCollection(storyId, itemId, false);
						
			ResponseEntity<String> response = new ResponseEntity<String>(result, HttpStatus.OK);			
					
			return response;
		
		
	}
	
    /**
     * This method represents the /enrichment/annotation/{storyId} end point,
	 * where the annotations for all NamedEntities of a story are saved to the db using the class NamedEntityAnnotationCollection.
	 * All requests on this end point are processed here.
     * @param wskey
     * @param storyId
     * @return
     * @throws Exception
     * @throws HttpException
     */
	@ApiOperation(value = "Get annotation collection preview", nickname = "getAnnotationCollectionPOST", notes = "This method stores the annotations of "
			+ "stories or items	to the database. The parameter \"storyId\" enables considering the annotations that are only realted to the given story."
			+ " The parameter \"itemId\" further restricts saving of the annotations to the given story item. If \"itemId\" is set to \"all\", then all annotations"
			+ " that belong to the given story are stored without  the information to which story item which annotation belongs. Therefore, in order to retrieve the annotations"
			+ " for the specific story item, please use \"itemId\" parameter.")
	@RequestMapping(value = "/enrichment/annotation/{storyId}/{itemId}", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAnnotationCollectionPOST(
			@RequestParam(value = "wskey", required = false) String wskey,
			@PathVariable("storyId") String storyId,
			@PathVariable("itemId") String itemId) throws Exception, HttpException {

			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			String result = enrichmentNerService.getStoryOrItemAnnotationCollection(storyId, itemId, true);
						
			ResponseEntity<String> response = new ResponseEntity<String>(result, HttpStatus.OK);			
					
			return response;
		
		
	}
	
   /**
    * This method represents the /enrichment/annotation/{storyId}/{wikidataIdentifier} end point,
	 * where the annotations for a single NamedEntity of a story are retrieved using the class NamedEntityAnnotationImpl.
	 * All requests on this end point are processed here.
	 * 
    * @param wskey
    * @param storyId
    * @return
    * @throws Exception
    * @throws HttpException
    */
	
	@ApiOperation(value = "Get annotation preview", nickname = "getAnnotation", notes = "This method retrieves the annotations of "
			+ "a single wikidata entity from the whole collection of entities that can be retrieved using the GET method: /enrichment/annotation/{storyId}/{itemId}"
			+ " The parameter \"wikidataIdentifier\" specifies the wikidata entity we want to retrieve (e.g. Q1569850).")
	@RequestMapping(value = "/enrichment/annotation/{storyId}/{itemId}/{wikidataIdentifier}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAnnotation(
			@RequestParam(value = "wskey", required = false) String wskey,
			@PathVariable("storyId") String storyId,
			@PathVariable("itemId") String itemId,
			@PathVariable("wikidataIdentifier") String wikidataIdentifier) throws Exception, HttpException {
		
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			String result = enrichmentNerService.getStoryOrItemAnnotation(storyId, itemId, wikidataIdentifier);
						
			ResponseEntity<String> response = new ResponseEntity<String>(result, HttpStatus.OK);			
					
			return response;
		
		
	} 
} 
