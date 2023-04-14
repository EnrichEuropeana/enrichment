package eu.europeana.enrichment.mongo.service;

import java.util.List;

import eu.europeana.enrichment.model.Topic;

public interface PersistentTopicService {
	
	public void save(Topic topicEntity);

	public Topic getByIdentifier(String topicIdentifier);

	public void update(Topic dbtopicEntity);

	public void delete(Topic dbtopiEntity);

	public List<Topic> getByModelIdentifier(String modelIdentif);

	public List<Topic> getAll();

	Topic getById(long topicId);
	
	public long generateAutoIncrement(String entityType);

}
