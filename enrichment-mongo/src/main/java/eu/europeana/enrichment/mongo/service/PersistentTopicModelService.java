package eu.europeana.enrichment.mongo.service;

import eu.europeana.enrichment.exceptions.UnsupportedEntityTypeException;
import eu.europeana.enrichment.model.TopicModel;

public interface PersistentTopicModelService {
	
	public void saveTopicModel(TopicModel tm) throws UnsupportedEntityTypeException;

	public TopicModel findTopicModelByIdentifier(String identifier);

}
