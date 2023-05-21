package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import org.bson.types.ObjectId;

import eu.europeana.enrichment.definitions.model.impl.NamedEntityImpl;

/*
 * This interface defines database actions for named entities
 */
public interface NamedEntityDao {
	
	public List<NamedEntityImpl> findNamedEntities(String label, String type, String dbpediaId);
	public NamedEntityImpl findEqualNamedEntity(NamedEntityImpl ne);
	public List<NamedEntityImpl> findNamedEntitiesWithAdditionalInformation(String storyId,String itemId, String type, List<String> nerTools, boolean matchNerToolsExactly);
	public void saveNamedEntity(NamedEntityImpl entity);
	public void deletePositionEntitiesAndNamedEntities(String storyId,String itemId, String fieldUsedForNER);
	public List<NamedEntityImpl> get_N_NamedEntities(int limit, int skip);
	public long deleteAllNamedEntities();
	NamedEntityImpl findNamedEntity(ObjectId objId);
}
