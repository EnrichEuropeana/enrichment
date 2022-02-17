package eu.europeana.enrichment.mongo.dao;

import eu.europeana.enrichment.exceptions.UnsupportedEntityTypeException;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TopicModel;

public interface TopicModelDao {
	
	public void saveTopicModel (TopicModel tm) throws UnsupportedEntityTypeException;

	public TopicModel findTopicModel(String key);

}
