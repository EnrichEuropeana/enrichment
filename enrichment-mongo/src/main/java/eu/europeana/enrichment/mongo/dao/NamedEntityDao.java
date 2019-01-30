package eu.europeana.enrichment.mongo.dao;

import eu.europeana.enrichment.common.definitions.NamedEntity;

/*
 * This interface defines database actions for named entities
 */
public interface NamedEntityDao {
	
	public NamedEntity findNamedEntity(String key);
	//public List<NamedEntity> getAllNamedEntities();
	public void saveNamedEntity(NamedEntity entity);
	public void deleteNamedEntity(NamedEntity entity);
	public void deleteByKey(String key);

}
