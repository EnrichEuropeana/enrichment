package eu.europeana.enrichment.mongo.service;

import java.util.List;

import eu.europeana.enrichment.model.TopicEntity;
import eu.europeana.enrichment.model.TopicModel;

public interface PersistentTopicEntityService {
	
	public void saveTopicEntity(TopicEntity topicEntity);

	public TopicEntity findTopicEntityByIdentifier(String topicIdentifier);

	public void updateTopicEntity(TopicEntity dbtopicEntity);

	public void deleteTopicEntity(TopicEntity dbtopiEntity);

	public List<TopicEntity> findTopicEntitiesByTopicModel(String topicModel);

}
