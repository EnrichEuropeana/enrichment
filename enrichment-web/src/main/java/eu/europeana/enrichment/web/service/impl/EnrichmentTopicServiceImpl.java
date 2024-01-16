package eu.europeana.enrichment.web.service.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import eu.europeana.api.commons.definitions.vocabulary.CommonApiConstants;
import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.definitions.exceptions.UnsupportedEntityTypeException;
import eu.europeana.enrichment.definitions.model.impl.TopicImpl;
import eu.europeana.enrichment.definitions.model.vocabulary.EntityTypes;
import eu.europeana.enrichment.definitions.model.vocabulary.LdProfile;
import eu.europeana.enrichment.mongo.service.PersistentTopicService;
import eu.europeana.enrichment.solr.exception.SolrServiceException;
import eu.europeana.enrichment.solr.model.SolrTopicEntityImpl;
import eu.europeana.enrichment.solr.service.impl.SolrTopicServiceImpl;
import eu.europeana.enrichment.web.model.topic.search.BaseTopicResultPage;
import eu.europeana.enrichment.web.model.topic.search.CollectionOverview;
import eu.europeana.enrichment.web.model.topic.search.TopicIdsResultPage;
import eu.europeana.enrichment.web.model.topic.search.TopicResultPage;
import eu.europeana.enrichment.web.service.EnrichmentTopicService;

@Service(EnrichmentConstants.BEAN_ENRICHMENT_TOPIC_SERVICE)
public class EnrichmentTopicServiceImpl implements EnrichmentTopicService{
	
	Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	PersistentTopicService persistentTopicService;
	
	@Autowired
	@Qualifier(EnrichmentConstants.BEAN_ENRICHMENT_SOLR_TOPIC_SERVICE)
	SolrTopicServiceImpl solrTopicService;
		
	@Autowired
	@Qualifier(EnrichmentConstants.BEAN_ENRICHMENT_CONFIGURATION)
	EnrichmentConfiguration config;

	@Override
	public TopicImpl createTopic(TopicImpl topic) throws HttpException, UnsupportedEntityTypeException {
		TopicImpl dbtopicEntity = persistentTopicService.getByIdentifier(topic.getIdentifier());
		if (dbtopicEntity != null)
			return dbtopicEntity;
				
		if (topic.getCreated() == null)
			topic.setCreated(new Date());
		
		// here we need to create topic ID
		long id = persistentTopicService.generateAutoIncrement(EntityTypes.Topic.getEntityType());
		topic.setId(id);		

		TopicImpl savedTopic = persistentTopicService.save(topic);

		try {
			solrTopicService.store(EnrichmentConstants.TOPIC_SOLR_CORE, new SolrTopicEntityImpl(topic), true);
		} catch (SolrServiceException e) {
			logger.log(Level.ERROR, "Exception is thrown during saving of the topic to Solr.", e);
		}

		return savedTopic;
	}

	@Override
	public TopicImpl updateTopic(long id, TopicImpl topic) {
		TopicImpl dbtopicEntity = persistentTopicService.getById(id);
		if (dbtopicEntity != null)
		{
			if (topic.getTerms() != null)
				dbtopicEntity.setTerms(topic.getTerms());
			if (topic.getKeywords() != null)
				dbtopicEntity.setKeywords(topic.getKeywords());
			if (topic.getDescriptions() != null)
				dbtopicEntity.setDescriptions(topic.getDescriptions());
			if (topic.getLabels() != null)
				dbtopicEntity.setLabels(topic.getLabels());
			
			dbtopicEntity.setModified(new Date());
			TopicImpl dbtopicEntityUpdated = persistentTopicService.save(dbtopicEntity);
			try {
				solrTopicService.store(EnrichmentConstants.TOPIC_SOLR_CORE, new SolrTopicEntityImpl(dbtopicEntity), true);
			} catch (SolrServiceException e) {
				logger.log(Level.ERROR, "Exception is thrown during saving of the topic to Solr.", e);
			}
			return dbtopicEntityUpdated;
		}
		else {
			return null;
		}
	}
	
	public List<TopicImpl> detectTopics (String text, int topics) throws URISyntaxException, ClientProtocolException, IOException, HttpException {
		
	    CloseableHttpClient client = HttpClients.createDefault();
	    
	    //check if the language of the text is only English
	    HttpPost httpPost = new HttpPost(config.getSparkLanguageDetectionUrl());
		StringEntity httpEntity = new StringEntity(text);
	    httpPost.setEntity(httpEntity);	    
	    CloseableHttpResponse response = client.execute(httpPost);
	    String responseString = EntityUtils.toString(response.getEntity());
	    JSONObject responseJson = new JSONObject(responseString);
	    boolean textOnlyEn = true;
	    Iterator<String> responseLanguagesIter = responseJson.keys();
	    while(responseLanguagesIter.hasNext()) {
	    	if(!responseLanguagesIter.next().equals("en")) {
	    		textOnlyEn=false;
	    		break;
	    	}
	    }
	    if(!textOnlyEn) {
	    	throw new HttpException(null, "The text of the topic should be in English.", null, HttpStatus.BAD_REQUEST);
	    }
	    
	    //topic detection request
	    List<NameValuePair> postParameters = new ArrayList<>();
	    postParameters.add(new BasicNameValuePair("num_topics", String.valueOf(topics)));
	    //Build the server URI together with the parameters and the body
	    URIBuilder uriBuilder = new URIBuilder(config.getSparkTopicDetectionUrl());
	    uriBuilder.addParameters(postParameters);
	    httpPost = new HttpPost(uriBuilder.build());
		httpEntity = new StringEntity(text);
	    httpPost.setEntity(httpEntity);	    
	    response = client.execute(httpPost);

	    responseString = EntityUtils.toString(response.getEntity());
	    responseJson = new JSONObject(responseString);
	    Iterator<String> responseTopicIdIter = responseJson.keys();
	    Map<String,Float> unsortedResponseMap = new HashMap<>();
	    while(responseTopicIdIter.hasNext()) {
	    	String topicId = responseTopicIdIter.next();
	    	unsortedResponseMap.put(topicId, responseJson.getFloat(topicId));
	    }	    
	    LinkedHashMap<String, Float> sortedResponseMap = new LinkedHashMap<>();
	    unsortedResponseMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
	        .forEachOrdered(x -> sortedResponseMap.put(x.getKey(), x.getValue()));

	    List<TopicImpl> result = new ArrayList<>();
	    Set<String> topicIdsKeys = sortedResponseMap.keySet();
	    for(String topicId : topicIdsKeys) {
			TopicImpl dbtopicEntity = persistentTopicService.getById(Integer.valueOf(topicId)+1);
			if (dbtopicEntity != null) {
				updateTopicForSerialization(dbtopicEntity);
				dbtopicEntity.setScore(sortedResponseMap.get(topicId));
				result.add(dbtopicEntity);
			}			 
	    }

	    client.close();
	    return result;

	}

	@Override
	public TopicImpl deleteTopic(long topicId) {
		TopicImpl dbtopicEntity = persistentTopicService.getById(topicId);
		if (dbtopicEntity == null)
			return null;
				
		try {
			solrTopicService.deleteById(EnrichmentConstants.TOPIC_SOLR_CORE, String.valueOf(dbtopicEntity.getId()));
		} catch (SolrServiceException e) {
			logger.log(Level.ERROR, "Exception is thrown during the deletion of the topic from Solr.", e);
		}
		
		persistentTopicService.delete(dbtopicEntity);

		return dbtopicEntity;
	}

	public SolrDocumentList searchTopics(String query, String fq, String fl, String facets, String sort, int page, int pageSize) throws SolrServiceException {
		SolrDocumentList topics = solrTopicService.searchTopics(query, fq, fl, facets, sort, page, pageSize);
		return topics;
	}

	public BaseTopicResultPage<?> buildResultsPage(Map<String,String[]> requestParams, SolrDocumentList solrResults) throws Exception {
	    BaseTopicResultPage<?> resPage = null;
	    int pageSize = Integer.valueOf(requestParams.get(CommonApiConstants.QUERY_PARAM_PAGE_SIZE)[0]);
	    int currentPageNum = Integer.valueOf(requestParams.get(CommonApiConstants.QUERY_PARAM_PAGE)[0]);
	    long totalInCollection = solrResults.getNumFound();
	    
	    //validate the page number
	    if(totalInCollection>0 && totalInCollection-pageSize*currentPageNum<=0) {
	    	throw new HttpException(null, "Invalid combination of the " + CommonApiConstants.QUERY_PARAM_PAGE + " and " +
	    			CommonApiConstants.QUERY_PARAM_PAGE_SIZE + " parameters (out of range).", null, HttpStatus.BAD_REQUEST);
	    }    
	    
	    //set the result page items based on profile
	    LdProfile profile = LdProfile.getByStringValue(requestParams.get(CommonApiConstants.QUERY_PARAM_PROFILE)[0]);
	    if(LdProfile.MINIMAL.equals(profile)) {
	    	//only serialize the topic ids 
	    	resPage =  new TopicIdsResultPage();
	    }
	    else if(LdProfile.STANDARD.equals(profile)) {
	    	//serialize the whole topic
	    	resPage =  new TopicResultPage();
	    }	    
	    setResultPageItems(profile, solrResults, resPage);
	    
	    //set the collection overview
	    String collOverviewUrl = config.getEnrichApiEndpoint() + "/topic/search?";
	    String wholePageUrlWithoutPageAndPageSize = config.getEnrichApiEndpoint() + "/topic/search?";
	    int counter=1;
	    for(Map.Entry<String,String[]> paramEntry : requestParams.entrySet()) {
	    	if(!(paramEntry.getKey().equals(CommonApiConstants.QUERY_PARAM_PAGE) || paramEntry.getKey().equals(CommonApiConstants.QUERY_PARAM_PAGE_SIZE) 
	    			|| paramEntry.getKey().equals(CommonApiConstants.QUERY_PARAM_FACET))) {
	    		collOverviewUrl += paramEntry.getKey() + "=" + URLEncoder.encode(paramEntry.getValue()[0], StandardCharsets.UTF_8.toString());
	    		if(counter<requestParams.size()) {
	    			collOverviewUrl+= "&";
	    		}
	    	}    	
	    	if(!(paramEntry.getKey().equals(CommonApiConstants.QUERY_PARAM_PAGE) || paramEntry.getKey().equals(CommonApiConstants.QUERY_PARAM_PAGE_SIZE))) {
	    		wholePageUrlWithoutPageAndPageSize += paramEntry.getKey() + "=" + URLEncoder.encode(paramEntry.getValue()[0], StandardCharsets.UTF_8.toString());
	    		if(counter<requestParams.size()) {
	    			wholePageUrlWithoutPageAndPageSize+= "&";
	    		}
	    	}
	    	counter++;
	    }
	    long lastPageNum = totalInCollection/pageSize + (totalInCollection%pageSize>0 ? 1 : 0) - 1;
	    if(lastPageNum<0) {
	    	lastPageNum=0;
	    }
	    String firstPage = wholePageUrlWithoutPageAndPageSize + "&" + CommonApiConstants.QUERY_PARAM_PAGE + "=0"
	    		+ "&" + CommonApiConstants.QUERY_PARAM_PAGE_SIZE + "=" + pageSize;
	    String lastPage = wholePageUrlWithoutPageAndPageSize + "&" + CommonApiConstants.QUERY_PARAM_PAGE + "=" + lastPageNum
	    		+ "&" + CommonApiConstants.QUERY_PARAM_PAGE_SIZE + "=" + pageSize;
	    CollectionOverview resultList = new CollectionOverview(collOverviewUrl, totalInCollection, firstPage, lastPage);
	    resPage.setPartOf(resultList);
	    	    
	    //set the total found items
	    resPage.setTotalInPage(solrResults.size());
	
	    //set the pagination
	    setResultPagePagination(currentPageNum, firstPage, lastPage, wholePageUrlWithoutPageAndPageSize, lastPageNum, pageSize, resPage);
	    
	    return resPage;
	}
	
	private void setResultPageItems (LdProfile profile, SolrDocumentList solrResults, BaseTopicResultPage<?> resPage) {
	    if(LdProfile.MINIMAL.equals(profile)) {
	    	List<String> solrTopicsIds = new ArrayList<>();
	    	if(solrResults.size()>0) {
				DocumentObjectBinder binder = new DocumentObjectBinder();
			    Iterator<SolrDocument> iteratorSolrDocs = solrResults.iterator();
			    while (iteratorSolrDocs.hasNext()) {
			    	SolrDocument doc = iteratorSolrDocs.next();
			    	SolrTopicEntityImpl solrTopic = (SolrTopicEntityImpl) binder.getBean(SolrTopicEntityImpl.class, doc);
			    	solrTopicsIds.add(config.getEnrichApiEndpoint() + "/topic/" + solrTopic.getId());
			    }
	    	}
		    ((TopicIdsResultPage)resPage).setItems(solrTopicsIds);
	    }
	    else if(LdProfile.STANDARD.equals(profile)) {
	    	List<TopicImpl> dbTopics = new ArrayList<>();
	    	if(solrResults.size()>0) {
				DocumentObjectBinder binder = new DocumentObjectBinder();
			    Iterator<SolrDocument> iteratorSolrDocs = solrResults.iterator();
			    while (iteratorSolrDocs.hasNext()) {
			    	SolrDocument doc = iteratorSolrDocs.next();
			    	SolrTopicEntityImpl solrTopic = (SolrTopicEntityImpl) binder.getBean(SolrTopicEntityImpl.class, doc);
					TopicImpl dbtopicEntity = persistentTopicService.getById(solrTopic.getId());
					if (dbtopicEntity != null) {
						updateTopicForSerialization(dbtopicEntity);
				    	dbTopics.add(dbtopicEntity);
					}			    	
			    }		
	    	}
	    	((TopicResultPage)resPage).setItems(dbTopics);
	    }
	}
	
	private void setResultPagePagination(int currentPageNum, String firstPage, String lastPage, 
			String wholePageUrlWithoutPageAndPageSize, long lastPageNum, int pageSize, BaseTopicResultPage<?> resPage) {
	    String prevPage = null;
	    String nextPage = null;
	    if(currentPageNum==0) {
	    	prevPage=firstPage;
	    }
	    else {
	    	prevPage = wholePageUrlWithoutPageAndPageSize + "&" + CommonApiConstants.QUERY_PARAM_PAGE + "=" + (currentPageNum-1)
	        		+ "&" + CommonApiConstants.QUERY_PARAM_PAGE_SIZE + "=" + pageSize;
	    }
	    resPage.setPrevPageUri(prevPage);
	    if(currentPageNum==lastPageNum) {
	    	nextPage = lastPage;
	    }
	    else {
	    	nextPage = wholePageUrlWithoutPageAndPageSize + "&" + CommonApiConstants.QUERY_PARAM_PAGE + "=" + (currentPageNum+1)
	        		+ "&" + CommonApiConstants.QUERY_PARAM_PAGE_SIZE + "=" + pageSize;    	
	    }
	    resPage.setNextPageUri(nextPage);		
	}
	
	public void updateTopicForSerialization (TopicImpl topic) {
		topic.setUrlId(config.getEnrichApiEndpoint() + "/topic/" + topic.getId());
		topic.getModel().setId(config.getEnrichApiEndpoint() + "/model/" + topic.getModel().getIdentifier());
	}
	
}
