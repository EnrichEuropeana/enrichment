package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.eq;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import dev.morphia.query.internal.MorphiaCursor;
import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.model.TopicEntity;
import eu.europeana.enrichment.model.TopicModel;
import eu.europeana.enrichment.model.impl.TopicEntityImpl;
import eu.europeana.enrichment.model.impl.TopicModelImpl;

@Repository(AppConfigConstants.BEAN_ENRICHMENT_TOPIC_ENTITY_DAO)
public class TopicEntityDaoImpl implements TopicEntityDao{

	

	@Autowired
	private Datastore enrichmentDatastore;
	
	@Override
	public void save(TopicEntity topicEntity) {
		TopicEntity dbTopicEntity = findById(topicEntity.getIdentifier());
		if(dbTopicEntity!=null)
		{
			dbTopicEntity.setIdentifier(topicEntity.getIdentifier());
			dbTopicEntity.setTopicID(topicEntity.getTopicID());
			dbTopicEntity.setLabel(topicEntity.getLabel());
			dbTopicEntity.setTopicKeywords(topicEntity.getTopicKeywords());
			dbTopicEntity.setTopicTerms(topicEntity.getTopicTerms());
			dbTopicEntity.setDescription(topicEntity.getDescription());
			dbTopicEntity.setTopicModel(topicEntity.getTopicModel());
			dbTopicEntity.setCreatedDate(topicEntity.getCreatedDate());
			if (topicEntity.getModifiedDate() == null)
				dbTopicEntity.setModifiedDate(new Date());
			else
				dbTopicEntity.setModifiedDate(topicEntity.getModifiedDate());
			this.enrichmentDatastore.save(dbTopicEntity);
			
		}
		else
		{
			TopicEntityImpl tmp = null;
			if(topicEntity instanceof TopicEntityImpl)
				tmp = (TopicEntityImpl) topicEntity;
			else {
				tmp = new TopicEntityImpl(topicEntity);				
			}
			if(tmp != null)
				this.enrichmentDatastore.save(tmp);
			
		}
	}


	@Override
	public TopicEntity findById(String topicID) {
		TopicEntityImpl dbEntity = enrichmentDatastore.find(TopicEntityImpl.class).filter(
                eq(EntityFields.TOPIC_ENTITY_IDENTIFIER, topicID)
                )
                .first();		
		return dbEntity;
	}


	@Override
	public void update(TopicEntity dbtopicEntity) {
		enrichmentDatastore.save(dbtopicEntity);
	}


	@Override
	public void delete(TopicEntity dbtopicEntity) {
		enrichmentDatastore.delete(dbtopicEntity);
	}


	@Override
	public List<TopicEntity> findByModelId(String topicModel) {
		MorphiaCursor<TopicEntityImpl> iter = enrichmentDatastore.find(TopicEntityImpl.class).filter(eq(EntityFields.MODEL_ID,topicModel)).iterator();
		List<TopicEntity> list = new ArrayList<TopicEntity>();
		while (iter.hasNext())
		{
			list.add(iter.next());
		}
		
		return list ;
	}



}
