package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import eu.europeana.enrichment.model.TopicEntity;
import eu.europeana.enrichment.model.TopicModel;

public interface TopicEntityDao {
	
	public void save (TopicEntity topicEntity);

	public TopicEntity findById(String topicID);

	public void update(TopicEntity dbtopicEntity);

	public void delete(TopicEntity dbtopicEntity);

	public List<TopicEntity> findByModelId(String topicModelId);

}
