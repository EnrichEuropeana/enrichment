package eu.europeana.enrichment.mongo.service;

import java.util.List;

import javax.annotation.Resource;

import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.mongo.dao.NamedEntityDaoImpl;


public class PersistentNamedEntityServiceImpl implements PersistentNamedEntityService {

	@Resource(name = "namedEntityDao")
	NamedEntityDaoImpl namedEntityDao;
	
	@Override
	public NamedEntity findNamedEntity(String key) {
		return namedEntityDao.findNamedEntity(key);
	}
	@Override
	public List<NamedEntity> findNamedEntitiesWithAdditionalInformation(String itemId, boolean translation) {
		return namedEntityDao.findNamedEntitiesWithAdditionalInformation(itemId, translation);
	}

	@Override
	public List<NamedEntity> getAllNamedEntities() {
		// TODO Auto-generated method stub
		return null;
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

}
