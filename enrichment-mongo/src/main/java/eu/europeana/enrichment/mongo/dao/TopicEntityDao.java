package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import eu.europeana.enrichment.model.TopicEntity;
import eu.europeana.enrichment.model.TopicModel;

public interface TopicEntityDao {
	
	public void saveTopicEntity (TopicEntity topicEntity);

	public TopicEntity findTopicEntityByIdentifier(String topicID);

	public void updateTopicEntity(TopicEntity dbtopicEntity);

	public void deleteTopicEntity(TopicEntity dbtopicEntity);

	public List<TopicEntity> findTopicEntitiesByTopicModel(String topicModelId);

}
