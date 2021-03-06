package eu.europeana.enrichment.mongo.dao;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import eu.europeana.enrichment.model.ItemEntity;

/*
 * This interface defines database actions for story items
 */
public interface ItemEntityDao {

	public ItemEntity findItemEntity(String key);
	public ItemEntity findItemEntityFromStory (String storyId, String itemId);
	public List<ItemEntity> findAllItemEntities ();
	public List<ItemEntity> findStoryItemEntitiesFromStory(String storyId);
	//public List<NamedEntity> getAllStoryItemEntities();
	public void saveItemEntity(ItemEntity entity) throws NoSuchAlgorithmException, UnsupportedEncodingException;
	public void deleteItemEntity(ItemEntity entity);
	public void deleteItemEntityByStoryItemId(String key);
	public void updateNerToolsForItem(String itemId, String nerTool);
	public List<String> getNerToolsForItem(String itemId);
//	public int getNumerAnalysedNamedEntities(String field);
//	public void setNumerAnalysedNamedEntities(String field, int num);
}
