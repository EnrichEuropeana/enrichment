package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import eu.europeana.enrichment.definitions.model.impl.ItemEntityImpl;

/*
 * This interface defines database actions for story items
 */
public interface ItemEntityDao {

	public List<ItemEntityImpl> find_N_ItemEntities(int limit, int skip);
	public ItemEntityImpl findItemEntity(String storyId, String itemId);
	public List<ItemEntityImpl> findAllItemsOfStory(String storyId);
	public ItemEntityImpl saveItemEntity(ItemEntityImpl entity);
	public void deleteItemEntity(ItemEntityImpl entity);
	public long deleteAllItemsOfStory(String storyId);
	public void updateNerToolsForItem(String itemId, String nerTool);
	public List<String> getNerToolsForItem(String itemId);
}
