package eu.europeana.enrichment.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@Api(tags = "Annotation preview service", description=" ")
public class AnnotationController extends BaseRest {

	@Resource
	EnrichmentNERService enrichmentNerService;
	
    /**
     * 	 * This method represents the /enrichment/annotation end point,
	 * where a request with a storyId parameter is sent and 
	 * the story annotations are retrieved using the class NamedEntityAnnotationCollection.
	 * All requests on this end point are processed here.
	 * 
     * @param wskey									is the application key which is required
     * @param wikidataRequest						Rest Get Body containing a wikidata URl
     * @return
     * @throws Exception
     * @throws HttpException
     * @throws SolrNamedEntityServiceException
     */
	
	@ApiOperation(value = "Get annotation preview", nickname = "getAnnotation")
	@RequestMapping(value = "/enrichment/annotation", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getAnnotation(
			@RequestParam(value = "wskey", required = false) String wskey,
			@RequestParam(value = "storyId", required = true) String storyId) throws Exception, HttpException {
		try {
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			String result = enrichmentNerService.getStoryAnnotation(storyId);
						
			ResponseEntity<String> response = new ResponseEntity<String>(result, HttpStatus.OK);			
					
			return response;
		
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
}
