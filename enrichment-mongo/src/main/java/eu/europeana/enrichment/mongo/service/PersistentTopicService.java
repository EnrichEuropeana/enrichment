package eu.europeana.enrichment.mongo.service;

import java.util.List;

import eu.europeana.enrichment.definitions.model.impl.TopicImpl;

public interface PersistentTopicService {
	
	public void save(TopicImpl topicEntity);

	public TopicImpl getByIdentifier(String topicIdentifier);

	public void update(TopicImpl dbtopicEntity);

	public void delete(TopicImpl dbtopiEntity);

	public List<TopicImpl> getByModelIdentifier(String modelIdentif);

	public List<TopicImpl> getAll();

	TopicImpl getById(long topicId);
	
	public long generateAutoIncrement(String entityType);

}
