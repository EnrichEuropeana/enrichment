package eu.europeana.enrichment.web.controller;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import eu.europeana.api.commons.web.http.HttpHeaders;

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
import eu.europeana.enrichment.exceptions.UnsupportedEntityTypeException;
import eu.europeana.enrichment.model.TopicEntity;
import eu.europeana.enrichment.web.exception.ApplicationAuthenticationException;
import eu.europeana.enrichment.web.model.EnrichmentTopicRequest;
import eu.europeana.enrichment.web.service.EnrichmentTopicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.api.commons.web.controller.BaseRestController;

@RestController
@EnableCaching
//@SwaggerSelect
@Api(tags = "Topic service", description=" ")
public class TopicController extends BaseRest{
	
	@Autowired
	EnrichmentTopicService enrichmentTopicService;
	
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
	@ApiOperation(value = "Create Topic", nickname = "postTopicCreation", notes = "This method stores the topics into the database\n"
			+ "Mandatory fields: identifier, description and topicTerm")
	@RequestMapping(value = "/enrichment/topic/", method = {RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> postTopicCreation (@RequestParam(value = "wskey", required = true) String wskey,
			@RequestBody(required = false) String text) throws JsonMappingException, JsonProcessingException, HttpException, UnsupportedEntityTypeException
	{
		ResponseEntity<String> response = null;
		try {
			validateApiKey(wskey);
		} catch (ApplicationAuthenticationException e) {
			e.printStackTrace();
			response = new ResponseEntity<String>("",HttpStatus.UNAUTHORIZED);
		}
		
		// parse JSON 
		ObjectMapper objectMapper = new ObjectMapper();
		System.out.println(text);
		EnrichmentTopicRequest request = objectMapper.readValue(text, EnrichmentTopicRequest.class);
		
		
		
		// check mandatory fields
		if (!validString(request.topicIdentifier) || testNullOrEmpty(request.descriptions) || testNullOrEmpty(request.topicTerms) )
		{
			response = new ResponseEntity<String>("", HttpStatus.BAD_REQUEST);
			return response;
		}
			
		// use create topic service
		TopicEntity te = enrichmentTopicService.createTopic(request);
		if (te != null)
		{
			//TODO: add apiVersion to the generateETag method
			String etag = generateETag(te.getModifiedDate(), WebFields.JSON_LD_REST);
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
		    headers.add(HttpHeaders.CONTENT_TYPE, HttpHeaders.CONTENT_TYPE_JSON_UTF8);
		    headers.add(HttpHeaders.ETAG, etag);
		    headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_POST);
		    response = new ResponseEntity<String>(te.toJSON(), headers, HttpStatus.OK);
		}
		else
		{
			response = new ResponseEntity<String>("", HttpStatus.UNPROCESSABLE_ENTITY);
			return response;
		}

		return response;
		
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
	public ResponseEntity<String> postTopicUpdate (@RequestParam(value = "wskey", required = true) String wskey, @RequestParam(value = "topicIdentifier", required = true) String topicIdentifier,
			@RequestBody(required = false) String text) throws JsonMappingException, JsonProcessingException, HttpException, UnsupportedEntityTypeException
	{
		ResponseEntity<String> response = null;
		try {
			validateApiKey(wskey);
		} catch (ApplicationAuthenticationException e) {
			e.printStackTrace();
			response = new ResponseEntity<String>("",HttpStatus.UNAUTHORIZED);
		}
		
		// parse JSON 
		ObjectMapper objectMapper = new ObjectMapper();
		System.out.println(text);
		EnrichmentTopicRequest request = objectMapper.readValue(text, EnrichmentTopicRequest.class);
		request.setTopicIdentifier(topicIdentifier);
		
		TopicEntity topicEntity = enrichmentTopicService.updateTopic(request);
		if (topicEntity == null)
		{
			response = new ResponseEntity<String>("", HttpStatus.UNPROCESSABLE_ENTITY);
			return response;
		}
		else
		{
			String etag = generateETag(topicEntity.getModifiedDate(), WebFields.JSON_LD_REST);
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
		    headers.add(HttpHeaders.CONTENT_TYPE, HttpHeaders.CONTENT_TYPE_JSON_UTF8);
		    headers.add(HttpHeaders.ETAG, etag);
		    headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_POST);
		    response = new ResponseEntity<String>(topicEntity.toJSON(), headers, HttpStatus.OK);
		}
				
		return response;
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
	public ResponseEntity<String> postTopicDelete (@RequestParam(value = "wskey", required = true) String wskey, @RequestParam(value = "topicIdentifier", required = true) String topicIdentifier) throws JsonMappingException, JsonProcessingException, HttpException, UnsupportedEntityTypeException
	{
		ResponseEntity<String> response = null;
		try {
			validateApiKey(wskey);
		} catch (ApplicationAuthenticationException e) {
			e.printStackTrace();
			response = new ResponseEntity<String>("",HttpStatus.UNAUTHORIZED);
		}
		TopicEntity topicEntity = enrichmentTopicService.deleteTopic(topicIdentifier);
		
		if (topicEntity == null)
		{
			response = new ResponseEntity<String>("", HttpStatus.UNPROCESSABLE_ENTITY);
			return response;
		}
		else
		{
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(5);
		    headers.add(HttpHeaders.CONTENT_TYPE, HttpHeaders.CONTENT_TYPE_JSON_UTF8);
		    headers.add(HttpHeaders.ALLOW, HttpHeaders.ALLOW_POST);
		    response = new ResponseEntity<String>(topicEntity.toJSON(), headers, HttpStatus.OK);
		}
		
		return response;
		
	}
	private boolean testNullOrEmpty(Object object) {
		if (object == null)
			return true;
		if (object instanceof List)
		{
			if (((List)object).isEmpty())
				return true;
		}
		if (object instanceof Map)
		{
			if (((Map)object).isEmpty())
				return true;
		}
		return false;
	}
	
	

	private boolean validString(String string) {
		if (string == null)
			return false;
		if (string.isBlank() || string.isEmpty())
			return false;
		return true;
	}

}
