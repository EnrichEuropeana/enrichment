package eu.europeana.enrichment.mongo.service;

import java.util.List;

import eu.europeana.enrichment.common.definitions.NamedEntity;
import eu.europeana.enrichment.mongo.model.DaoNamedEntity;

public interface PersistentNamedEntityService {

	
	public DaoNamedEntity findNamedEntity(String key);
	public List<DaoNamedEntity> getAllNamedEntities();
	public void saveNamedEntity(NamedEntity entity);
	public void saveNamedEntities(List<NamedEntity> entities);

}
