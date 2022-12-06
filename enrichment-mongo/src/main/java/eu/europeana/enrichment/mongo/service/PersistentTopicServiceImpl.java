package eu.europeana.enrichment.mongo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.Topic;
import eu.europeana.enrichment.mongo.dao.TopicDao;

@Service(EnrichmentConstants.BEAN_ENRICHMENT_PERSISTENT_TOPIC_SERVICE)
public class PersistentTopicServiceImpl implements PersistentTopicService{
	
	@Autowired
	private TopicDao topicDao;

	@Override
	public void save(Topic topicEntity) {
		topicDao.save(topicEntity);
	}

	@Override
	public Topic getByIdentifier(String topicIdentifier) {
		//TODO: check if exists, otherwise throw exception
		return topicDao.getByIdentifier(topicIdentifier);
	}
	
	@Override
	public Topic getById(String topicId) {
		return topicDao.getByTopicId(topicId);
	}

	@Override
	public void update(Topic dbtopicEntity) {
		// check if exists, otherwise throw exception; update only non-null fields and then save
		topicDao.save(dbtopicEntity);
	}

	@Override
	public void delete(Topic dbtopicEntity) {
		// check if exists, otherwise throw exception; call delete in DAO
		topicDao.delete (dbtopicEntity);
	}

	@Override
	public List<Topic> getByModelIdentifier(String topicModel) {
		// find object in topicModelDao otherwise throw exception; then collect in a list all topics belonging to that model
		return topicDao.getByModelIdentifier(topicModel);
	}
	
	@Override
	public List<Topic> getAll() {
		return topicDao.getAll();
	}

}
