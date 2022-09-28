package eu.europeana.enrichment.web.controller;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.europeana.api.commons.web.definitions.WebFields;
import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.api.commons.web.http.HttpHeaders;
import eu.europeana.enrichment.common.serializer.JsonLdSerializer;
import eu.europeana.enrichment.model.Topic;
import eu.europeana.enrichment.model.impl.TopicImpl;
import eu.europeana.enrichment.model.vocabulary.EnrichmentModelFields;
import eu.europeana.enrichment.solr.exception.SolrServiceException;
import eu.europeana.enrichment.web.common.config.I18nConstants;
import eu.europeana.enrichment.web.exception.ParamValidationException;
import eu.europeana.enrichment.web.service.EnrichmentTopicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@RestController
@EnableCaching
//@SwaggerSelect
@Api(tags = "Topic service", description=" ")
public class TopicController extends BaseRest{
	
	@Autowired
	EnrichmentTopicService enrichmentTopicService;
	
	@Autowired 
	JsonLdSerializer jsonLdSerializer;
	
	Logger logger = LogManager.getLogger(getClass());
		
	/**
	 * This method represents the /enrichment/topic/ end point,
	 * where a topic creation request will be processed.
	 * All requests on this end point are processed here.
	 * 
	 * @param wskey
	 * @return
	 * @throws Exception 
	 */
	@ApiOperation(value = "Create Topics", nickname = "postTopicCreation", notes = "This method stores the topics into the database\n")
	@RequestMapping(value = "/enrichment/topic/", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> postTopicCreation (@RequestParam(value = "wskey", required = true) String wskey,
			@RequestBody(required = true) TopicImpl [] topics) throws Exception
	{
		validateApiKey(wskey);

		for(TopicImpl topic : topics) {
			// check mandatory fields
			if (topic.getIdentifier()==null || topic.getIdentifier().isBlank())
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentModelFields.topicIdentifier, null);
			if (topic.getDescriptions()==null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentModelFields.topicDescriptions, null);
			if (topic.getTerms()==null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentModelFields.topicTerms, null);
			
			//TODO: add apiVersion to the generateETag method
			Date date = new Date();
			topic.setCreated(date);
			
			// use create topic service
			enrichmentTopicService.createTopic(topic);
		}
		
		String etag = generateETag(new Date(), WebFields.JSON_LD_REST);
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
	    headers.add(HttpHeaders.CONTENT_TYPE, HttpHeaders.CONTENT_TYPE_JSON_UTF8);
	    headers.add(HttpHeaders.ETAG, etag);
	    headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_POST);

		return new ResponseEntity<String>(jsonLdSerializer.serializeObject("{\"result\":\"Done.\"}"), headers, HttpStatus.OK);
		
	}
	
	/**
	 * This method represents the /enrichment/topic/<topicIdentifier> end point,
	 * where a topic creation request will be processed.
	 * All requests on this end point are processed here.
	 * 
	 * @param wskey, topicIdentifier
	 * @return 
	 * @throws Exception 
	 */
	@ApiOperation(value = "Update Topic", nickname = "postTopicUpdate", notes = "This method updates the topics in the database\n"
			+ "Non-updatable fields: identifier, model")
	@RequestMapping(value = "/enrichment/topic/update", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> postTopicUpdate (@RequestParam(value = "wskey", required = true) String wskey,
			@RequestBody(required = true) TopicImpl topic) throws Exception
	{
		validateApiKey(wskey);
		
		Date date = new Date();
		topic.setModified(date);
		Topic topicEntity = enrichmentTopicService.updateTopic(topic);

		if (topicEntity == null)
			throw new HttpException(null, "The required topic does not exist. Invalid topic identifier.", null, HttpStatus.BAD_REQUEST);
		else
		{
			String etag = generateETag(date, WebFields.JSON_LD_REST);
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
		    headers.add(HttpHeaders.CONTENT_TYPE, HttpHeaders.CONTENT_TYPE_JSON_UTF8);
		    headers.add(HttpHeaders.ETAG, etag);
		    headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_POST);
		    return new ResponseEntity<String>(jsonLdSerializer.serializeObject(topicEntity), headers, HttpStatus.OK);
		}
	}

	
	/**
	 * This method represents the /enrichment/topic/<topicIdentifier> end point,
	 * where a topic creation request will be processed.
	 * All requests on this end point are processed here.
	 * 
	 * @param wskey, topicIdentifier
	 * @return 
	 * @throws Exception 
	 */
	@ApiOperation(value = "Delete Topic", nickname = "postTopicDelete", notes = "This method deletes topics in the database\n"
			+ "Mandatory parameter: identifier")
	@RequestMapping(value = "/enrichment/topic/delete", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> postTopicDelete (@RequestParam(value = "wskey", required = true) String wskey, @RequestParam(value = "identifier", required = true) String identifier) throws Exception
	{
		validateApiKey(wskey);
	
		Topic topicEntity = enrichmentTopicService.deleteTopic(identifier);
		
		if (topicEntity == null)
			throw new HttpException(null, "The topic to be deleted does not exist. Invalid topic identifier.", null, HttpStatus.BAD_REQUEST);
		else
		{
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
		    headers.add(HttpHeaders.CONTENT_TYPE, HttpHeaders.CONTENT_TYPE_JSON_UTF8);
		    headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_POST);
		    return new ResponseEntity<String>(jsonLdSerializer.serializeObject(topicEntity), headers, HttpStatus.OK);	
		}
	}
	
    @ApiOperation(value = "Search topics for the given query. The parameter example values: query (query text) -> identifier:lda*; fq (filter query, can be a comma separated list) -> identifier:*TOPIC8,modelID:lda*; "
    		+ "fl (list of fields to include in the response, can be a comma separated list) -> identifier,modelID; facet (a comma separated list of facet.field values, i.e. the field for which the facets count will be calculated) -> "
    		+ "identifier, modelID; sort (a comma separated list of sortings) -> identifier asc, modelID desc; page (the page number); pageSize (the page size)", nickname = "searchTopics", response = java.lang.Void.class)
    @RequestMapping(value = "/enrichment/topic/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> searchTopics(
    		@RequestParam(value = "wskey", required = false) String wskey,
		    @RequestParam(value = "query") String queryString,
		    @RequestParam(value = "fq", required = false) String fq,
		    @RequestParam(value = "fl", required = false) String fl,
		    @RequestParam(value = "facet", required = false) String facet,
		    @RequestParam(value = "sort", required = false) String sort,
		    @RequestParam(value = "page", required = false, defaultValue = "0") int page,
		    @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) throws SolrServiceException, ParamValidationException {
	
		    if (StringUtils.isBlank(queryString)) {
		    	throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, "query", queryString);
		    }
		    
		    String topicsJson = enrichmentTopicService.searchTopics(queryString, fq, fl, facet, sort, page, pageSize);
		    // build response
		    MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
		    headers.add(HttpHeaders.CONTENT_TYPE, HttpHeaders.CONTENT_TYPE_JSON_UTF8);
		    headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_GET);
	
		    ResponseEntity<String> response = new ResponseEntity<String>(topicsJson, headers, HttpStatus.OK);
	
		    return response;  	
    }
	

}
