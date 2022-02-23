package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.eq;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.exceptions.UnsupportedEntityTypeException;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TopicEntity;
import eu.europeana.enrichment.model.TopicModel;
import eu.europeana.enrichment.model.impl.StoryEntityImpl;
import eu.europeana.enrichment.model.impl.TopicEntityImpl;
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
		TopicModel dbTopicModel = findTopicModel(tm.getIdentifier());
		if (dbTopicModel != null)
		{
			dbTopicModel.setURL(tm.getURL());
			dbTopicModel.setAlgorithm(tm.getAlgorithm());
			dbTopicModel.setDescription(tm.getDescription());
			this.enrichmentDatastore.save(dbTopicModel);
		}
		else {
			TopicModel tmp = null;
			if(tm instanceof TopicModelImpl)
				tmp = (TopicModel) tm;
			else {
				tmp = new TopicModelImpl(tm);
			}
			if(tmp != null)
				this.enrichmentDatastore.save(tmp);
		}
		
	}

	@Override
	public void deleteTopicModel(TopicModel topicModel) {
		this.enrichmentDatastore.delete(topicModel);
		
	}

	
	
	

}
