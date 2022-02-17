package eu.europeana.enrichment.mongo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.exceptions.UnsupportedEntityTypeException;
import eu.europeana.enrichment.model.TopicModel;
import eu.europeana.enrichment.mongo.dao.TopicModelDao;

@Service(AppConfigConstants.BEAN_ENRICHMENT_PERSISTENT_TOPIC_MODEL_SERVICE)
public class PersistentTopicModelServiceImpl implements PersistentTopicModelService{

	@Autowired
	private TopicModelDao topicModelDao;
	
	@Override
	public void saveTopicModel(TopicModel tm) throws UnsupportedEntityTypeException {
		
			topicModelDao.saveTopicModel(tm);
	}

	@Override
	public TopicModel findTopicModelByIdentifier(String identifier) {
		return topicModelDao.findTopicModel(identifier);
	}
	
	

}
