package eu.europeana.enrichment.web.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.solr.common.SolrDocumentList;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.definitions.exceptions.UnsupportedEntityTypeException;
import eu.europeana.enrichment.definitions.model.impl.TopicImpl;
import eu.europeana.enrichment.solr.exception.SolrServiceException;
import eu.europeana.enrichment.web.model.topic.search.BaseTopicResultPage;

public interface EnrichmentTopicService {
	
	public TopicImpl createTopic(TopicImpl topic) throws HttpException, UnsupportedEntityTypeException;

	public TopicImpl updateTopic(long id, TopicImpl topic);
	
	public List<TopicImpl> detectTopics (String text, int topics) throws URISyntaxException, UnsupportedEncodingException, ClientProtocolException, IOException, HttpException;

	public TopicImpl deleteTopic(long topicId);
	
	public SolrDocumentList searchTopics(String query, String fq, String fl, String facets, String sort, int page, int pageSize) throws SolrServiceException;

	BaseTopicResultPage<?> buildResultsPage(Map<String, String[]> requestParams, SolrDocumentList solrResults)
			throws Exception;
	
	public void updateTopicForSerialization (TopicImpl topic);
}
