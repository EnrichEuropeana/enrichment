package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import dev.morphia.query.internal.MorphiaCursor;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.Topic;
import eu.europeana.enrichment.model.impl.TopicImpl;

@Repository(EnrichmentConstants.BEAN_ENRICHMENT_TOPIC_ENTITY_DAO)
public class TopicDaoImpl implements TopicDao{

	@Autowired
	private Datastore enrichmentDatastore;
	
	@Override
	public void save(Topic topicEntity) {
		this.enrichmentDatastore.save(topicEntity);
	}

	@Override
	public Topic getByIdentifier(String identifier) {
		TopicImpl dbEntity = enrichmentDatastore.find(TopicImpl.class).filter(
                eq(EntityFields.TOPIC_ENTITY_IDENTIFIER, identifier)
                )
                .first();		
		return dbEntity;
	}	

	@Override
	public void delete(Topic dbtopicEntity) {
		enrichmentDatastore.delete(dbtopicEntity);
	}

	@Override
	public List<Topic> getByModelIdentifier(String modelIdentifier) {
		MorphiaCursor<TopicImpl> iter = enrichmentDatastore.find(TopicImpl.class).filter(eq(EntityFields.MODEL_ID,modelIdentifier)).iterator();
		List<Topic> list = new ArrayList<Topic>();
		while (iter.hasNext())
		{
			list.add(iter.next());
		}
		
		return list ;
	}
	
}
