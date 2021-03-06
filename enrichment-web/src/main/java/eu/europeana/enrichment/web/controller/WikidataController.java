package eu.europeana.enrichment.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.model.impl.NamedEntitySolrCollection;
import eu.europeana.enrichment.ner.linking.WikidataService;
import eu.europeana.enrichment.solr.commons.JacksonSerializer;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.solr.service.SolrWikidataEntityService;
import eu.europeana.enrichment.web.config.swagger.SwaggerSelect;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@EnableCaching
@SwaggerSelect
@Api(tags = "Entity preview service", description=" ")
public class WikidataController extends BaseRest {

	Logger logger = LogManager.getLogger(getClass());
	
	@Resource
	SolrWikidataEntityService solrWikidataEntityService;
	
	@Resource(name = "wikidataService")
	WikidataService wikidataService;
	
	@Resource(name = "jacksonSerializer")
	JacksonSerializer jacksonSerializer;

	
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
	
	@ApiOperation(value = "Get entity preview", nickname = "getWikidataEntity", notes = "This method retrives the wikidata objects (including their wikidata ids, labels, etc.)"
			+ "based on the provided \"wikidataId\" request parameter (e.g. http://www.wikidata.org/entity/Q2677) and its type (agent or place) from the Solr local storage.")
	@RequestMapping(value = "/enrichment/resolve", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getWikidataEntity(
			@RequestParam(value = "wskey", required = false) String wskey,
			@RequestParam(value = "wikidataId", required = true) String wikidataId,
			@RequestParam(value = "type", required = true) String type) throws Exception, HttpException, SolrNamedEntityServiceException {
		
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			//String solrResponse = solrWikidataEntityService.searchByWikidataURL(wikidataId);
			String solrResponse = solrWikidataEntityService.searchByWikidataURL_usingJackson(wikidataId,type);
						
			ResponseEntity<String> response = new ResponseEntity<String>(solrResponse, HttpStatus.OK);			
					
			return response;
		
		
	} 
	
	/**
	 * This method is used to get a collection of NamedEntity from Solr based on some search parameters.
	 * The result is serialized to JSON using Jackson Jsonld serialization library.
	 * All requests on this end point are processed here.
	 * 
	 * @param wskey
	 * @param query									Solr query to search for entities (e.g. "Glas*","Glasgow", etc.)
	 * @param type									comma separated list, used to indicate which entity types (i.e. place,person) should be included in the results. If the parameter is not provided, all entities should be searched
	 * @param lang									list of comma separated values for language filtering, if not provided “en” is used as default
	 * @param qf									solr query for filtering results
	 * @param sort									sorting criteria according to solr specs
	 * @param pageSize								the number of results returned, if not provided defaults to 5
	 * @param page									the results page, if not provided defaults to 0 
	 * @return
	 * @throws Exception
	 * @throws HttpException
	 * @throws SolrNamedEntityServiceException
	 */
	
	@ApiOperation(value = "Get named entities from Solr", nickname = "getNamedEntitiesFromSolr", notes = "This method retrives the wikidata objects (including their wikidata ids, labels, etc.)"
			+ "based on the provided input parameters: \"query\"= query word to search for (e.g. London, Lond*, etc.), \"type\"= comma separated list used to indicate which entity types (i.e. place,person)"
			+ " will be included in the result, \"qf\" = Solr specific query for filtering the results, \"sort\" = sorting criteria according to solr specifications, "
			+ "\"pageSize\" = the number of results returned, if not provided defaults to 5, \"page\" = the results page, if not provided defaults to 0.")
	@RequestMapping(value = "/enrichment/search", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getNamedEntitiesFromSolr(
			@RequestParam(value = "wskey", required = false) String wskey,
			@RequestParam(value = "query", required = true) String query,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "lang", required = false) String lang,
			@RequestParam(value = "qf", required = false) String qf,
			@RequestParam(value = "sort", required = false) String sort,
			@RequestParam(value = "pageSize", required = false) String pageSize,
			@RequestParam(value = "page", required = false) String page
			) throws Exception, HttpException, SolrNamedEntityServiceException {
		
			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);
			
			//String solrResponse = solrWikidataEntityService.searchByWikidataURL(wikidataId);
			String solrResponse = solrWikidataEntityService.searchNamedEntities_usingJackson(wskey, query, type, lang, qf, sort, pageSize, page);
						
			ResponseEntity<String> response = new ResponseEntity<String>(solrResponse, HttpStatus.OK);			
					
			return response;
		
		
	} 
	
	
	@ApiOperation(value = "Get places from Wikidata", nickname = "getPlacesFromWikidata", notes = "This method retrives the wikidata places (including their wikidata ids, labels, etc.)"
			+ "based on the provided input parameters: \"query\"= query word to search for (e.g. London, Lond*, etc.), \"type\"= entity type (i.e. \"place\"), "
			+ "\"lang\" = the language filtration for the place labels and other multi-lingual fields, "
			+ "\"pageSize\" = the number of results returned, if not provided defaults to 5, \"page\" = the results page, if not provided defaults to 0.")
	@RequestMapping(value = "/enrichment/places", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getPlacesFromWikidata(
			@RequestParam(value = "wskey", required = false) String wskey,
			@RequestParam(value = "query", required = true) String query,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "lang", required = false) String lang,
			@RequestParam(value = "pageSize", required = false) String pageSize,
			@RequestParam(value = "page", required = false) String page
			) throws Exception, HttpException, SolrNamedEntityServiceException {
		

			// Check client access (a valid “wskey” must be provided)
			validateApiKey(wskey);

			if(pageSize==null || pageSize.isEmpty())
			{	
				pageSize="5";
			}
			
			if(page==null || page.isEmpty())
			{	
				page="0";
			}

			if(lang==null || lang.isEmpty())
			{
				lang="en";
			}

			if(type==null || type.isEmpty())
			{
				type="place";
			}

			List<WikidataEntity> items = new ArrayList<WikidataEntity>();

			List<String> wikidataIDs = new ArrayList<String>();
			
			wikidataIDs = wikidataService.getWikidataPlaceIdWithLabelAltLabel(query, lang);
			
			String URLPage = "http://dsi-demo.ait.ac.at/enrichment-web/entity/places?wskey=" + wskey + "&query=" + query + "&type=" + type + "&lang="+ lang;
			String URLWithoutPage = "http://dsi-demo.ait.ac.at/enrichment-web/entity/places?wskey=" + wskey + "&query=" + query + "&type=" + type + "&lang="+ lang;
			URLPage+="&page="+ page +"&pageSize=" + pageSize;
			
			int totalResultsAll = wikidataIDs.size();
			int totalResultsPerPage = (totalResultsAll < Integer.valueOf(pageSize)) ? totalResultsAll : Integer.valueOf(pageSize);

			for(int i=0;i<wikidataIDs.size();i++)
			{
				int startIndex = totalResultsPerPage*Integer.valueOf(page);
				int endIndex = totalResultsPerPage*Integer.valueOf(page) + totalResultsPerPage;
				
				if(i>=startIndex && i<endIndex)
				{
					//getting WikidataEntity, either from local cache or from the wikidata
					WikidataEntity wikidataEntity = wikidataService.getWikidataEntityUsingLocalCache(wikidataIDs.get(i), type);
					items.add(wikidataEntity);
					
					logger.info("Wikidata place found is: ");
					
					
					//adjust for languages, i.e. remove the fields for other not required languages
					HelperFunctions.removeDataForLanguages(wikidataEntity.getPrefLabel(),null, lang);
					HelperFunctions.removeDataForLanguages(wikidataEntity.getAltLabel(),null,lang);
					HelperFunctions.removeDataForLanguages(wikidataEntity.getDescription(),null,lang);

				}
			}
			

			NamedEntitySolrCollection neColl = new NamedEntitySolrCollection(items, URLPage, URLWithoutPage, totalResultsPerPage, totalResultsAll);
			
			String serializedNamedEntityCollection=null;
	    	
	    	serializedNamedEntityCollection = jacksonSerializer.serializeNamedEntitySolrCollection(neColl);
					
			ResponseEntity<String> response = new ResponseEntity<String>(serializedNamedEntityCollection, HttpStatus.OK);			

			return response;
					
		
	}
}
