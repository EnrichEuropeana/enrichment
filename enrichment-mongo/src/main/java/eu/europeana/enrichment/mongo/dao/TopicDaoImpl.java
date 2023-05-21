package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import dev.morphia.query.experimental.updates.UpdateOperators;
import dev.morphia.query.internal.MorphiaCursor;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.definitions.model.Topic;
import eu.europeana.enrichment.definitions.model.impl.SequenceGenerator;
import eu.europeana.enrichment.definitions.model.impl.TopicImpl;
import eu.europeana.enrichment.mongo.utils.MorphiaUtils;

@Repository(EnrichmentConstants.BEAN_ENRICHMENT_TOPIC_ENTITY_DAO)
public class TopicDaoImpl implements TopicDao{

	@Autowired
	@Qualifier(EnrichmentConstants.BEAN_ENRICHMENT_DATASTORE)
	private Datastore enrichmentDatastore;
	
	@Override
	public void save(Topic topicEntity) {
		this.enrichmentDatastore.save(topicEntity);
	}

	@Override
	public Topic getByTopicId(long topicId) {
		TopicImpl dbEntity = enrichmentDatastore.find(TopicImpl.class).filter(
                eq(EnrichmentConstants.TOPIC_ID, topicId)
                )
                .first();		
		return dbEntity;
	}

	@Override
	public Topic getByIdentifier(String identifier) {
		TopicImpl dbEntity = enrichmentDatastore.find(TopicImpl.class).filter(
                eq(EnrichmentConstants.TOPIC_IDENTIFIER, identifier)
                )
                .first();		
		return dbEntity;
	}
	
	@Override
	public List<Topic> getAll() {
		MorphiaCursor<TopicImpl> iter = enrichmentDatastore.find(TopicImpl.class).iterator();
		List<Topic> result = new ArrayList<>();
		while (iter.hasNext())
		{
			result.add(iter.next());
		}
		return result;
	}


	@Override
	public void delete(Topic dbtopicEntity) {
		enrichmentDatastore.delete(dbtopicEntity);
	}

	@Override
	public List<Topic> getByModelIdentifier(String modelIdentifier) {
		MorphiaCursor<TopicImpl> iter = enrichmentDatastore.find(TopicImpl.class)
				.filter(eq(EnrichmentConstants.TOPIC_MODEL + "." + EnrichmentConstants.TOPIC_MODEL_IDENTIFIER, modelIdentifier))
				.iterator();
		List<Topic> list = new ArrayList<Topic>();
		while (iter.hasNext())
		{
			list.add(iter.next());
		}		
		return list ;
	}

	/**
	   * Generates an autoincrement value for entities, based on the Entity type
	   *
	   * @param internalType internal type for Entity
	   * @return autoincrement value
	*/
	public long generateAutoIncrement(String internalType) {
	    /*
	     * Get the given key from the auto increment entity and try to increment it.
	     * Synchronization occurs on the DB-level, so we don't need to synchronize this code block.
	     */
	    SequenceGenerator autoIncrement =
	    		enrichmentDatastore
	            .find(SequenceGenerator.class)
	            .filter(eq("_id", internalType))
	            .modify(UpdateOperators.inc("value"))
	            .execute(MorphiaUtils.MAJORITY_WRITE_MODIFY_OPTS);
	
	    /*
	     * If none is found, we need to create one for the given key. This shouldn't happen in
	     * production as the db is pre-populated with entities
	     */
	    if (autoIncrement == null) {
	      autoIncrement = new SequenceGenerator(internalType, 1L);
	      enrichmentDatastore.save(autoIncrement);
	    }
	    return autoIncrement.getValue();
	}	
	
}
