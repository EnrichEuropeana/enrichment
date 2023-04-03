package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import eu.europeana.enrichment.model.impl.ItemEntityImpl;

/*
 * This interface defines database actions for story items
 */
public interface ItemEntityDao {

	public ItemEntityImpl findItemEntityFromStory (String storyId, String itemId);
	public List<ItemEntityImpl> findItemEntitiesFromStory(String storyId, String itemId);
	public List<ItemEntityImpl> find_N_ItemEntities(int limit, int skip);
	public List<ItemEntityImpl> findStoryItemEntitiesFromStory(String storyId);
	//public List<NamedEntity> getAllStoryItemEntities();
	public void saveItemEntity(ItemEntityImpl entity);
	public void deleteItemEntity(ItemEntityImpl entity);
	public long deleteAllItemsOfStory(String storyId);
	public void updateNerToolsForItem(String itemId, String nerTool);
	public List<String> getNerToolsForItem(String itemId);
//	public int getNumerAnalysedNamedEntities(String field);
//	public void setNumerAnalysedNamedEntities(String field, int num);
	public ItemEntityImpl findItemEntity(String itemId);
}
