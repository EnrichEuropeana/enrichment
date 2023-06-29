package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import eu.europeana.enrichment.definitions.model.impl.TopicImpl;

public interface TopicDao {
	
	public void save (TopicImpl topicEntity);

	public TopicImpl getByIdentifier(String identifier);

	public void delete(TopicImpl dbtopicEntity);

	public List<TopicImpl> getByModelIdentifier(String modelIdentifier);

	public List<TopicImpl> getAll();

	TopicImpl getByTopicId(long topicId);
	
	public long generateAutoIncrement(String internalType);

}
