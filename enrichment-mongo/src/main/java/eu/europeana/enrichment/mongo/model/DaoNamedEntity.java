package eu.europeana.enrichment.mongo.model;

import java.util.ArrayList;
import java.util.List;


public interface DaoNamedEntity {
	
	public PersistentNamedEntity findNamedEntity(String key);
	//public List<NamedEntity> getAllNamedEntities();
	public void saveNamedEntity(PersistentNamedEntity entity);
	public void saveNamedEntities(List<PersistentNamedEntity> entities);
	public void deleteNamedEntity(PersistentNamedEntity entity);
	public void deleteByKey(String key);

}
