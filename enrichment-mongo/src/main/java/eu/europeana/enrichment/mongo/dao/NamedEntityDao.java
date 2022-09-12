package eu.europeana.enrichment.mongo.dao;

import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;

import eu.europeana.enrichment.model.impl.NamedEntityImpl;

/*
 * This interface defines database actions for named entities
 */
public interface NamedEntityDao {
	
	public NamedEntityImpl findNamedEntityByLabel(String label);
	public NamedEntityImpl findNamedEntityByLabelAndType(String label, String type);
	public List<NamedEntityImpl> findNamedEntitiesWithAdditionalInformation(String storyId,String itemId, String type);

	//public List<NamedEntityImpl> getAllNamedEntities();
	public void saveNamedEntity(NamedEntityImpl entity);
	public void deletePositionEntitiesAndNamedEntity(String storyId,String itemId, String fieldUsedForNER);
	public List<NamedEntityImpl> findAllNamedEntities();
	public long deleteAllNamedEntities();
}
