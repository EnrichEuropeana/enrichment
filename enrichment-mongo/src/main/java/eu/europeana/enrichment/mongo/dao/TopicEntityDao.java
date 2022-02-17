package eu.europeana.enrichment.mongo.dao;

import eu.europeana.enrichment.model.TopicEntity;

public interface TopicEntityDao {
	
	public void saveTopicEntity (TopicEntity topicEntity);

	public TopicEntity findTopicEntityByIdentifier(String topicID);

}
