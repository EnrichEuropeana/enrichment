package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import eu.europeana.enrichment.model.impl.NamedEntityImpl;

/*
 * This interface defines database actions for named entities
 */
public interface NamedEntityDao {
	
	public NamedEntityImpl findNamedEntity(String label);
	public NamedEntityImpl findNamedEntity(String label, String type);
	public List<NamedEntityImpl> findNamedEntitiesWithAdditionalInformation(String storyId,String itemId, String type);
	public List<NamedEntityImpl> findNamedEntitiesWithAdditionalInformation(String storyId, String itemId, String type, List<String> nerTools);

	//public List<NamedEntityImpl> getAllNamedEntities();
	public void saveNamedEntity(NamedEntityImpl entity);
	public void deletePositionEntitiesFromNamedEntity(String storyId,String itemId, String fieldUsedForNER);
	public long deleteNamedEntityByKey(String key);
	public List<NamedEntityImpl> findAllNamedEntities();
	public long deleteAllNamedEntities();
	
}
