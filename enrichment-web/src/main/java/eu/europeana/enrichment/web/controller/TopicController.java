package eu.europeana.enrichment.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.europeana.api.commons.definitions.vocabulary.CommonApiConstants;
import eu.europeana.api.commons.web.definitions.WebFields;
import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.api.commons.web.http.HttpHeaders;
import eu.europeana.api.commons.web.model.vocabulary.Operations;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.common.serializer.JsonLdSerializer;
import eu.europeana.enrichment.model.Topic;
import eu.europeana.enrichment.model.impl.TopicImpl;
import eu.europeana.enrichment.model.vocabulary.LdProfile;
import eu.europeana.enrichment.web.common.config.I18nConstants;
import eu.europeana.enrichment.web.exception.ParamValidationException;
import eu.europeana.enrichment.web.model.topic.search.BaseTopicResultPage;
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
	 * @return
	 * @throws Exception 
	 */
	@ApiOperation(value = "Create Topics", nickname = "postTopicCreation", notes = "This method stores the topics into the database\n")
	@RequestMapping(value = "/enrichment/topic/", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> postTopicCreation (@RequestBody(required = true) TopicImpl [] topics,
			HttpServletRequest request) throws Exception
	{
		verifyWriteAccess(Operations.CREATE, request);

		List<Topic> result = new ArrayList<>();
		for(TopicImpl topic : topics) {
			// check mandatory fields
			if (topic.getIdentifier()==null || topic.getIdentifier().isBlank())
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentConstants.TOPIC_IDENTIFIER, null);
			if (topic.getDescriptions()==null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentConstants.TOPIC_DESCRIPTIONS, null);
			if (topic.getTerms()==null)
				throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, EnrichmentConstants.TOPIC_TERMS, null);
			
			//TODO: add apiVersion to the generateETag method
			Date date = new Date();
			topic.setCreated(date);
			
			Topic createdTopic = enrichmentTopicService.createTopic(topic);
			
			enrichmentTopicService.updateTopicForSerialization(createdTopic);
			
			result.add(createdTopic);
		}
		
		String etag = generateETag(new Date(), WebFields.JSON_LD_REST);
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
	    headers.add(HttpHeaders.CONTENT_TYPE, HttpHeaders.CONTENT_TYPE_JSON_UTF8);
	    headers.add(HttpHeaders.ETAG, etag);
	    headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_POST);

		return new ResponseEntity<String>(jsonLdSerializer.serializeObject(result), headers, HttpStatus.OK);
		
	}
	
	/**
  	 * This method represents the /enrichment/topic/<topicId> end point,
	 * where a topic creation request will be processed.
	 * All requests on this end point are processed here.

	 * @param topicId
	 * @param topic
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "Update Topic", nickname = "updateTopic", notes = "This method updates a topic in the database\n"
			+ "Non-updatable fields: id, identifier, model")
	@RequestMapping(value = "/enrichment/topic/{topicId}", method = {RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> updateTopic (
			@PathVariable("topicId") long topicId,
			@RequestBody(required = true) TopicImpl topic, 
			HttpServletRequest request) throws Exception
	{
		verifyWriteAccess(Operations.UPDATE, request);
		
		Date date = new Date();
		topic.setModified(date);
		Topic topicEntity = enrichmentTopicService.updateTopic(topicId, topic);

		if (topicEntity == null)
			throw new HttpException(null, "The required topic does not exist. Invalid topic id.", null, HttpStatus.NOT_FOUND);
		else
		{
			String etag = generateETag(date, WebFields.JSON_LD_REST);
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
		    headers.add(HttpHeaders.CONTENT_TYPE, HttpHeaders.CONTENT_TYPE_JSON_UTF8);
		    headers.add(HttpHeaders.ETAG, etag);
		    headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_GPuD);
		    enrichmentTopicService.updateTopicForSerialization(topicEntity);
		    return new ResponseEntity<String>(jsonLdSerializer.serializeObject(topicEntity), headers, HttpStatus.OK);
		}
	}

	/**
	 * This method represents the /enrichment/topic/detect end point,
	 * for detecting the topics for the given text based on the already created machine learning model.
	 * @param model
	 * @param topics
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "Detect Topics", nickname = "detectTopics", notes = "This method detects the topics for the given text. \n")
	@RequestMapping(value = "/enrichment/topic/detect", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> detectTopics (
			@RequestBody(required = true) String text, 
			@RequestParam(required = true, value = "model") String model,
			@RequestParam(required = true, value = "topics") int topics,
			@RequestParam(value = CommonApiConstants.PARAM_WSKEY) String wskey,
			HttpServletRequest request) throws Exception
	{
		verifyReadAccess(request);
		
		Date date = new Date();
		List<Topic> result = enrichmentTopicService.detectTopics(text, topics);

		String etag = generateETag(date, WebFields.JSON_LD_REST);
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
	    headers.add(HttpHeaders.CONTENT_TYPE, HttpHeaders.CONTENT_TYPE_JSON_UTF8);
	    headers.add(HttpHeaders.ETAG, etag);
	    headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_POST);
    	return new ResponseEntity<String>(jsonLdSerializer.serializeObject(result), headers, HttpStatus.OK);
	}

	
	/**
	 * This method represents the /enrichment/topic/<topicId> end point,
	 * where a topic deletion request will be processed.
	 * All requests on this end point are processed here.
	 * 
	 * @param topicId
	 * @return 
	 * @throws Exception 
	 */
	@ApiOperation(value = "Delete Topic", nickname = "deleteTopic", notes = "This method deletes a topic from the database.")
	@DeleteMapping(value = "/enrichment/topic/{topicId}")
	public ResponseEntity<String> topicDelete (
			@PathVariable(value = "topicId") long topicId,
			HttpServletRequest request) throws Exception
	{
		verifyWriteAccess(Operations.DELETE, request);
	
		Topic topicEntity = enrichmentTopicService.deleteTopic(topicId);
		
		if (topicEntity == null)
			throw new HttpException(null, "The topic to be deleted does not exist.", null, HttpStatus.UNPROCESSABLE_ENTITY);
		else
		{
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
		    headers.add(HttpHeaders.CONTENT_TYPE, HttpHeaders.CONTENT_TYPE_JSON_UTF8);
		    headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_GPuD);
		    return new ResponseEntity<String>(headers, HttpStatus.NO_CONTENT);	
		}
	}
	
    @ApiOperation(value = "Search topics for the given query. The parameter example values: query (query text) -> identifier:lda*; qf (query filter, can be a comma separated list) -> identifier:*TOPIC8,modelID:lda*; "
    		+ "profile (a profile used for the serialization, can be minimal or standard) -> minimal; "
    		+ "sort (a comma separated list of sortings) -> identifier asc, modelID desc; page (the page number); pageSize (the page size)", nickname = "searchTopics", response = java.lang.Void.class)
    @RequestMapping(value = "/enrichment/topic/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> searchTopics(
		    @RequestParam(value = CommonApiConstants.QUERY_PARAM_QUERY) String query,
		    @RequestParam(value = CommonApiConstants.QUERY_PARAM_QF, required = false) String qf,
		    @RequestParam(value = CommonApiConstants.QUERY_PARAM_PROFILE, required = false, defaultValue = "standard") String profile,
		    @RequestParam(value = CommonApiConstants.QUERY_PARAM_SORT, required = false) String sort,
		    @RequestParam(value = CommonApiConstants.QUERY_PARAM_PAGE, required = false, defaultValue = "0") int page,
		    @RequestParam(value = CommonApiConstants.QUERY_PARAM_PAGE_SIZE, required = false, defaultValue = "10") int pageSize,
		    @RequestParam(value = CommonApiConstants.PARAM_WSKEY) String wskey,
		    HttpServletRequest request) throws Exception {
    	
    	verifyReadAccess(request);
    	
    	Date date = new Date();
    		
	    if (StringUtils.isBlank(query)) {
	    	throw new ParamValidationException(I18nConstants.EMPTY_PARAM_MANDATORY, "query", query);
	    }
	    
	    String fl=null;
	    if(LdProfile.MINIMAL.getStringValue().equals(profile)) {
	    	fl=EnrichmentConstants.TOPIC_ID;
	    }
	    SolrDocumentList solrTopics = enrichmentTopicService.searchTopics(query, qf, fl, null, sort, page, pageSize);
	    // build response
	    @SuppressWarnings("rawtypes")
		BaseTopicResultPage resultsPage = enrichmentTopicService.buildResultsPage(request.getParameterMap(), solrTopics);

	    MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
	    headers.add(HttpHeaders.CONTENT_TYPE, HttpHeaders.CONTENT_TYPE_JSON_UTF8);
	    headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_GET);
	    String etag = generateETag(date, WebFields.JSON_LD_REST);
	    headers.add(HttpHeaders.ETAG, etag);

	    ResponseEntity<String> response = new ResponseEntity<String>(jsonLdSerializer.serializeObject(resultsPage), headers, HttpStatus.OK);

	    return response;  	
    }
	
}
