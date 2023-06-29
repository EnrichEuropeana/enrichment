package eu.europeana.enrichment.mongo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.definitions.model.impl.TopicImpl;
import eu.europeana.enrichment.mongo.dao.TopicDao;

@Service(EnrichmentConstants.BEAN_ENRICHMENT_PERSISTENT_TOPIC_SERVICE)
public class PersistentTopicServiceImpl implements PersistentTopicService{
	
	@Autowired
	private TopicDao topicDao;

	@Override
	public void save(TopicImpl topicEntity) {
		topicDao.save(topicEntity);
	}

	@Override
	public TopicImpl getByIdentifier(String topicIdentifier) {
		//TODO: check if exists, otherwise throw exception
		return topicDao.getByIdentifier(topicIdentifier);
	}
	
	@Override
	public TopicImpl getById(long topicId) {
		return topicDao.getByTopicId(topicId);
	}

	@Override
	public void update(TopicImpl dbtopicEntity) {
		// check if exists, otherwise throw exception; update only non-null fields and then save
		topicDao.save(dbtopicEntity);
	}

	@Override
	public void delete(TopicImpl dbtopicEntity) {
		// check if exists, otherwise throw exception; call delete in DAO
		topicDao.delete (dbtopicEntity);
	}

	@Override
	public List<TopicImpl> getByModelIdentifier(String modelIdentif) {
		// find object in topicModelDao otherwise throw exception; then collect in a list all topics belonging to that model
		return topicDao.getByModelIdentifier(modelIdentif);
	}
	
	@Override
	public List<TopicImpl> getAll() {
		return topicDao.getAll();
	}
	
	public long generateAutoIncrement(String entityType) {
		return topicDao.generateAutoIncrement(entityType);
	}	

}
