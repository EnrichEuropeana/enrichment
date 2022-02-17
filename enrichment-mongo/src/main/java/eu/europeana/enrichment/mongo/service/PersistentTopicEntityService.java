package eu.europeana.enrichment.mongo.service;

import eu.europeana.enrichment.model.TopicEntity;

public interface PersistentTopicEntityService {
	
	public void saveTopicEntity(TopicEntity topicEntity);

	public TopicEntity findTopicEntityByIdentifier(String topicIdentifier);

}
