package eu.europeana.enrichment.mongo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.model.TopicEntity;
import eu.europeana.enrichment.model.TopicModel;
import eu.europeana.enrichment.model.impl.TopicEntityImpl;
import eu.europeana.enrichment.mongo.dao.TopicEntityDao;

@Service(AppConfigConstants.BEAN_ENRICHMENT_PERSISTENT_TOPIC_ENTITY_SERVICE)
public class PersistentTopicEntityServiceImpl implements PersistentTopicEntityService{
	
	
	@Autowired
	private TopicEntityDao topicEntityDao;

	@Override
	public void save(TopicEntity topicEntity) {
		topicEntityDao.save(topicEntity);
	}

	@Override
	public TopicEntity findById(String topicIdentifier) {
		return topicEntityDao.findById(topicIdentifier);
	}

	@Override
	public void update(TopicEntity dbtopicEntity) {
		topicEntityDao.update(dbtopicEntity);
		
	}

	@Override
	public void delete(TopicEntity dbtopicEntity) {
		topicEntityDao.delete (dbtopicEntity);
		
	}

	@Override
	public List<TopicEntity> findByModelId(String topicModel) {
		return topicEntityDao.findByModelId(topicModel);
	}

}
