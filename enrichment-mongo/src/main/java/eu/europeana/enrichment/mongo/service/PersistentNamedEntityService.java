package eu.europeana.enrichment.mongo.service;

import java.util.List;

import eu.europeana.enrichment.common.definitions.NamedEntity;
import eu.europeana.enrichment.mongo.dao.NamedEntityDao;

public interface PersistentNamedEntityService {

	
	public NamedEntity findNamedEntity(String key);
	public List<NamedEntity> getAllNamedEntities();
	public void saveNamedEntity(NamedEntity entity);
	public void saveNamedEntities(List<NamedEntity> entities);
	public void deleteNamedEntity(NamedEntity entity);

}
