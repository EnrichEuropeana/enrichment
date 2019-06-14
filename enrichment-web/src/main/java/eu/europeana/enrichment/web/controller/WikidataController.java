package eu.europeana.enrichment.web.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.json.JSONObject;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.solr.service.SolrWikidataEntityService;
import eu.europeana.enrichment.web.config.swagger.SwaggerSelect;
import eu.europeana.enrichment.web.model.WikidataRequest;
import eu.europeana.enrichment.web.service.EnrichmentNERService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@EnableCaching
@SwaggerSelect
@Api(tags = "Entity preview service", description=" ")
public class WikidataController extends BaseRest {

	@Resource
	SolrWikidataEntityService solrWikidataEntityService;
	
    /**
     * 	 * This method represents the /enrichment/wikidata end point,
	 * where a request with a wikidata URL is send and 
	 * the wikidata entities are retrieved.
	 * All requests on this end point are processed here.
	 * 
     * @param wskey									is the application key which is required
     * @param wikidataRequest						Rest Get Body containing a wikidata URl
     * @return
     * @throws Exception
     * @throws HttpException
     * @throws SolrNamedEntityServiceException
     */
	
	@ApiOperation(value = "Get entity preview", nickname = "getWikidataEntity")
	@RequestMapping(value = "/enrichment/resolve", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getWikidataEntity(
			@RequestParam(value = "wskey", required = false) String wskey,
			@RequestParam(value = "wikidataId", required = true) String wikidataId) throws Exception, HttpException, SolrNamedEntityServiceException {
		try {
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			//String solrResponse = solrWikidataEntityService.searchByWikidataURL(wikidataId);
			String solrResponse = solrWikidataEntityService.searchByWikidataURL_usingJackson(wikidataId);
						
			ResponseEntity<String> response = new ResponseEntity<String>(solrResponse, HttpStatus.OK);			
					
			return response;
		
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw e;
		}
	}
}
