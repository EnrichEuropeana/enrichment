package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import eu.europeana.enrichment.model.Topic;

public interface TopicDao {
	
	public void save (Topic topicEntity);

	public Topic getByIdentifier(String identifier);

	public void delete(Topic dbtopicEntity);

	public List<Topic> getByModelIdentifier(String modelIdentifier);

	public List<Topic> getAll();

	Topic getByTopicId(long topicId);
	
	public long generateAutoIncrement(String internalType);

}
