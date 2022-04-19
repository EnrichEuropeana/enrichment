package eu.europeana.enrichment.web.service;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.exceptions.UnsupportedEntityTypeException;
import eu.europeana.enrichment.model.Topic;

public interface EnrichmentTopicService {
	
	public Topic createTopic(Topic topic) throws HttpException, UnsupportedEntityTypeException;

	public Topic updateTopic(Topic topic);

	public Topic deleteTopic(String identifier);

}
