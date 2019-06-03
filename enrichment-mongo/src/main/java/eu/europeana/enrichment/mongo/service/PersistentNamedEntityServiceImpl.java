package eu.europeana.enrichment.mongo.service;

import java.util.List;

import javax.annotation.Resource;

import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.mongo.dao.NamedEntityDaoImpl;
import eu.europeana.enrichment.mongo.model.DBNamedEntityImpl;


public class PersistentNamedEntityServiceImpl implements PersistentNamedEntityService {

	@Resource(name = "namedEntityDao")
	NamedEntityDaoImpl namedEntityDao;
	
	@Override
	public NamedEntity findNamedEntity(String key) {
		return namedEntityDao.findNamedEntity(key);
	}
	@Override
	public List<NamedEntity> findNamedEntitiesWithAdditionalInformation(String storyId, String type, boolean translation) {
		return namedEntityDao.findNamedEntitiesWithAdditionalInformation(storyId, type, translation);
	}

	@Override
	public List<NamedEntity> getAllNamedEntities() {
		return namedEntityDao.findAllNamedEntities();
	}

	@Override
	public void saveNamedEntity(NamedEntity entity) {
		namedEntityDao.saveNamedEntity(entity);
	}

	@Override
	public void saveNamedEntities(List<NamedEntity> entities) {
		for (NamedEntity namedEntity : entities) {
			saveNamedEntity(namedEntity);
		}
	}
	
	@Override
	public void deleteNamedEntity(NamedEntity entity) {
		namedEntityDao.deleteNamedEntity(entity);
	}
	
	@Override
	public void deleteAllNamedEntities() {
		namedEntityDao.deleteAllNamedEntities();
		
	}

}
