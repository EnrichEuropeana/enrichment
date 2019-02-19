package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import eu.europeana.enrichment.model.NamedEntity;

/*
 * This interface defines database actions for named entities
 */
public interface NamedEntityDao {
	
	public NamedEntity findNamedEntity(String key);
	public List<NamedEntity> findNamedEntitiesWithAdditionalInformation(String itemId, boolean translation);
	//public List<NamedEntity> getAllNamedEntities();
	public void saveNamedEntity(NamedEntity entity);
	public void deleteNamedEntity(NamedEntity entity);
	public void deleteNamedEntityByKey(String key);
	public List<NamedEntity> findAllNamedEntities();

}
