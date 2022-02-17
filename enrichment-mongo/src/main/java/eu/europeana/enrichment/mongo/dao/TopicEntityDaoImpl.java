package eu.europeana.enrichment.mongo.dao;

import static dev.morphia.query.experimental.filters.Filters.eq;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import dev.morphia.Datastore;
import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.model.TopicEntity;
import eu.europeana.enrichment.model.impl.TopicEntityImpl;

@Repository(AppConfigConstants.BEAN_ENRICHMENT_TOPIC_ENTITY_DAO)
public class TopicEntityDaoImpl implements TopicEntityDao{

	

	@Autowired
	private Datastore enrichmentDatastore;
	
	@Override
	public void saveTopicEntity(TopicEntity topicEntity) {
		TopicEntity dbTopicEntity = findTopicEntityByIdentifier(topicEntity.getIdentifier());
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
	public TopicEntity findTopicEntityByIdentifier(String topicID) {
		TopicEntityImpl dbEntity = enrichmentDatastore.find(TopicEntityImpl.class).filter(
                eq(EntityFields.TOPIC_ENTITY_IDENTIFIER, topicID)
                )
                .first();		
		return dbEntity;
	}



}
