package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import eu.europeana.enrichment.model.impl.NamedEntityImpl;

/*
 * This interface defines database actions for named entities
 */
public interface NamedEntityDao {
	
	public NamedEntityImpl findNamedEntityByLabel(String label);
	public NamedEntityImpl findExistingNamedEntity(NamedEntityImpl ne);
	public List<NamedEntityImpl> findAllNamedEntitiesByLabelAndType(String label, String type);
	public List<NamedEntityImpl> findNamedEntitiesWithAdditionalInformation(String storyId,String itemId, String type, List<String> nerTools, boolean matchNerToolsExactly);

	//public List<NamedEntityImpl> getAllNamedEntities();
	public void saveNamedEntity(NamedEntityImpl entity);
	public void deletePositionEntitiesAndNamedEntity(String storyId,String itemId, String fieldUsedForNER);
	public List<NamedEntityImpl> findAllNamedEntities();
	public long deleteAllNamedEntities();
}
