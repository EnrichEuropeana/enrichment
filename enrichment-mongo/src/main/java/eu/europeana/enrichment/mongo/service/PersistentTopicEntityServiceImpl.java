package eu.europeana.enrichment.mongo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.model.TopicEntity;
import eu.europeana.enrichment.mongo.dao.TopicEntityDao;

@Service(AppConfigConstants.BEAN_ENRICHMENT_PERSISTENT_TOPIC_ENTITY_SERVICE)
public class PersistentTopicEntityServiceImpl implements PersistentTopicEntityService{
	
	
	@Autowired
	private TopicEntityDao topicEntityDao;

	@Override
	public void saveTopicEntity(TopicEntity topicEntity) {
		topicEntityDao.saveTopicEntity(topicEntity);
	}

	@Override
	public TopicEntity findTopicEntityByIdentifier(String topicIdentifier) {
		return topicEntityDao.findTopicEntityByIdentifier(topicIdentifier);
	}

}
