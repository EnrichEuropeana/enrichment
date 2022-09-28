package eu.europeana.enrichment.web.service;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.exceptions.UnsupportedEntityTypeException;
import eu.europeana.enrichment.model.Topic;
import eu.europeana.enrichment.solr.exception.SolrServiceException;

public interface EnrichmentTopicService {
	
	public Topic createTopic(Topic topic) throws HttpException, UnsupportedEntityTypeException;

	public Topic updateTopic(Topic topic);

	public Topic deleteTopic(String identifier);
	
	public String searchTopics(String query, String fq, String fl, String facets, String sort, int page, int pageSize) throws SolrServiceException;

}
