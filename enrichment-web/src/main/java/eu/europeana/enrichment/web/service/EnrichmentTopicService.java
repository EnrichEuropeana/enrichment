package eu.europeana.enrichment.web.service;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.exceptions.UnsupportedEntityTypeException;
import eu.europeana.enrichment.model.TopicEntity;
import eu.europeana.enrichment.web.model.EnrichmentTopicRequest;

public interface EnrichmentTopicService {
	
	public TopicEntity createTopic(EnrichmentTopicRequest topicRequest) throws HttpException, UnsupportedEntityTypeException;

	public TopicEntity updateTopic(EnrichmentTopicRequest request);

	public TopicEntity deleteTopic(String topicIdentifier);

}
