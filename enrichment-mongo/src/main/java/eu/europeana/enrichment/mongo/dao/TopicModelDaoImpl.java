package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.eq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.exceptions.UnsupportedEntityTypeException;
import eu.europeana.enrichment.model.TopicModel;
import eu.europeana.enrichment.model.impl.TopicModelImpl;

@Repository(AppConfigConstants.BEAN_ENRICHMENT_TOPIC_MODEL_DAO)
public class TopicModelDaoImpl implements TopicModelDao {
	
	@Autowired
	private Datastore enrichmentDatastore; 

	@Override
	public TopicModel findTopicModel(String key) {
		return enrichmentDatastore.find(TopicModelImpl.class).filter(
                eq(EntityFields.TOPIC_MODEL_ID, key))
                .first();
	}
	
	@Override
	public void saveTopicModel(TopicModel tm) throws UnsupportedEntityTypeException {
		
		this.enrichmentDatastore.save(tm);
		
	}

	@Override
	public void deleteTopicModel(TopicModel topicModel) {
		this.enrichmentDatastore.delete(topicModel);
		
	}

	
	
	

}
