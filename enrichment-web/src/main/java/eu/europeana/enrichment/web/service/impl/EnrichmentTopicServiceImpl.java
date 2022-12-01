package eu.europeana.enrichment.web.service.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import eu.europeana.api.commons.definitions.vocabulary.CommonApiConstants;
import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.common.serializer.JsonLdSerializer;
import eu.europeana.enrichment.exceptions.UnsupportedEntityTypeException;
import eu.europeana.enrichment.model.Topic;
import eu.europeana.enrichment.model.vocabulary.LdProfile;
import eu.europeana.enrichment.mongo.service.PersistentTopicService;
import eu.europeana.enrichment.solr.exception.SolrServiceException;
import eu.europeana.enrichment.solr.model.SolrTopicEntityImpl;
import eu.europeana.enrichment.solr.model.vocabulary.TopicSolrFields;
import eu.europeana.enrichment.solr.service.impl.SolrTopicServiceImpl;
import eu.europeana.enrichment.web.common.config.I18nConstants;
import eu.europeana.enrichment.web.exception.ParamValidationException;
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
	JsonLdSerializer jsonLdSerializer;
	
	@Autowired
	@Qualifier(EnrichmentConstants.BEAN_ENRICHMENT_CONFIGURATION)
	EnrichmentConfiguration config;

	@Override
	public Topic createTopic(Topic topic) throws HttpException, UnsupportedEntityTypeException {
		Topic dbtopicEntity = persistentTopicService.getByIdentifier(topic.getIdentifier());
		if (dbtopicEntity != null)
			return dbtopicEntity;
				
		if (topic.getCreated() == null)
			topic.setCreated(new Date());

		persistentTopicService.save(topic);
		
		try {
			solrTopicService.store(TopicSolrFields.SOLR_CORE, new SolrTopicEntityImpl(topic), true);
		} catch (SolrServiceException e) {
			logger.log(Level.ERROR, "Exception is thrown during saving of the topic to Solr.", e);
		}
		return topic;
	}

	@Override
	public Topic updateTopic(Topic topic) {
		Topic dbtopicEntity = persistentTopicService.getByIdentifier(topic.getIdentifier());
		if (dbtopicEntity != null)
		{
			if (topic.getTerms() != null)
				dbtopicEntity.setTerms(topic.getTerms());
			if (topic.getKeywords() != null)
				dbtopicEntity.setKeywords(topic.getKeywords());
			if (topic.getDescriptions() != null)
				dbtopicEntity.setDescriptions(topic.getDescriptions());
			if (topic.getTopicID() != null)
				dbtopicEntity.setTopicID(topic.getTopicID());
			if (topic.getLabels() != null)
				dbtopicEntity.setLabels(topic.getLabels());
			
			dbtopicEntity.setModified(new Date());
			persistentTopicService.save(dbtopicEntity);
			try {
				solrTopicService.store(TopicSolrFields.SOLR_CORE, new SolrTopicEntityImpl(dbtopicEntity), true);
			} catch (SolrServiceException e) {
				logger.log(Level.ERROR, "Exception is thrown during saving of the topic to Solr.", e);
			}
			return dbtopicEntity;
		}
		return null;
	}

	@Override
	public Topic deleteTopic(String topicIdentifier) {
		Topic dbtopicEntity = persistentTopicService.getByIdentifier(topicIdentifier);
		if (dbtopicEntity == null)
			return null;
		
		persistentTopicService.delete(dbtopicEntity);
		
		try {
			solrTopicService.deleteById(TopicSolrFields.SOLR_CORE, dbtopicEntity.getIdentifier());
		} catch (SolrServiceException e) {
			logger.log(Level.ERROR, "Exception is thrown during the deletion of the topic from Solr.", e);
		}
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
	    if(totalInCollection-pageSize*currentPageNum<0) {
			throw new ParamValidationException(I18nConstants.INVALID_PARAM_VALUE, CommonApiConstants.QUERY_PARAM_PAGE, String.valueOf(pageSize));
	    }    
	    
	    //create items based on profile
	    LdProfile profile = LdProfile.getByStringValue(requestParams.get(CommonApiConstants.QUERY_PARAM_PROFILE)[0]);
	    if(LdProfile.MINIMAL.equals(profile)) {
	    	//only serialize the topic ids 
	    	resPage =  new TopicIdsResultPage();
	    	List<String> solrTopicsIds = new ArrayList<>();
	    	if(solrResults.size()>0) {
				DocumentObjectBinder binder = new DocumentObjectBinder();
			    Iterator<SolrDocument> iteratorSolrDocs = solrResults.iterator();
			    while (iteratorSolrDocs.hasNext()) {
			    	SolrDocument doc = iteratorSolrDocs.next();
			    	SolrTopicEntityImpl solrTopic = (SolrTopicEntityImpl) binder.getBean(SolrTopicEntityImpl.class, doc);
			    	solrTopicsIds.add(config.getEnrichApiEndpoint() + "/topic/" + solrTopic.getTopicID());
			    }
	    	}
		    ((TopicIdsResultPage)resPage).setItems(solrTopicsIds);
	    }
	    else if(LdProfile.STANDARD.equals(profile)) {
	    	//serialize the whole topic
	    	resPage =  new TopicResultPage();
	    	List<Topic> solrTopics = new ArrayList<>();
	    	if(solrResults.size()>0) {
				DocumentObjectBinder binder = new DocumentObjectBinder();
			    Iterator<SolrDocument> iteratorSolrDocs = solrResults.iterator();
			    while (iteratorSolrDocs.hasNext()) {
			    	SolrDocument doc = iteratorSolrDocs.next();
			    	SolrTopicEntityImpl solrTopic = (SolrTopicEntityImpl) binder.getBean(SolrTopicEntityImpl.class, doc);
			    	solrTopic.setTopicID(config.getEnrichApiEndpoint() + "/topic/" + solrTopic.getTopicID());
			    	solrTopics.add(solrTopic);
			    }		
	    	}
	    	((TopicResultPage)resPage).setItems(solrTopics);
	    }
	    
	    //create a collection overview
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
	    String firstPage = wholePageUrlWithoutPageAndPageSize + "&" + CommonApiConstants.QUERY_PARAM_PAGE + "=0"
	    		+ "&" + CommonApiConstants.QUERY_PARAM_PAGE_SIZE + "=" + pageSize;
	    String lastPage = wholePageUrlWithoutPageAndPageSize + "&" + CommonApiConstants.QUERY_PARAM_PAGE + "=" + lastPageNum
	    		+ "&" + CommonApiConstants.QUERY_PARAM_PAGE_SIZE + "=" + pageSize;
	    CollectionOverview resultList = new CollectionOverview(collOverviewUrl, totalInCollection, firstPage, lastPage);
	    resPage.setPartOf(resultList);
	    
	    //setting the total found items
	    resPage.setTotalInPage(solrResults.size());
	
	    //adding pagination
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
	    
	    return resPage;
  }
	
}
