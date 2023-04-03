package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import org.bson.types.ObjectId;

import eu.europeana.enrichment.model.impl.NamedEntityImpl;

/*
 * This interface defines database actions for named entities
 */
public interface NamedEntityDao {
	
	public NamedEntityImpl findNamedEntityByLabel(String label);
	public NamedEntityImpl findNamedEntity(String label, String type, String dbpediaId);
	public NamedEntityImpl findNamedEntityByNerTool(NamedEntityImpl ne);
	public List<NamedEntityImpl> findAllNamedEntitiesByLabelAndType(String label, String type);
	public List<NamedEntityImpl> findNamedEntitiesWithAdditionalInformation(String storyId,String itemId, String type, List<String> nerTools, boolean matchNerToolsExactly);

	//public List<NamedEntityImpl> getAllNamedEntities();
	public void saveNamedEntity(NamedEntityImpl entity);
	public void deletePositionEntitiesAndNamedEntities(String storyId,String itemId, String fieldUsedForNER);
	public List<NamedEntityImpl> get_N_NamedEntities(int limit, int skip);
	public long deleteAllNamedEntities();
	NamedEntityImpl findNamedEntity(ObjectId objId);
}
