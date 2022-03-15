package eu.europeana.enrichment.mongo.service;

import java.util.List;

import eu.europeana.enrichment.model.TopicEntity;
import eu.europeana.enrichment.model.TopicModel;

public interface PersistentTopicEntityService {
	
	public void save(TopicEntity topicEntity);

	public TopicEntity findById(String topicIdentifier);

	public void update(TopicEntity dbtopicEntity);

	public void delete(TopicEntity dbtopiEntity);

	public List<TopicEntity> findByModelId(String topicModel);

}
