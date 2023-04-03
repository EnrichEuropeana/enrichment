package eu.europeana.enrichment.mongo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.mongo.dao.ItemEntityDao;

@Service(EnrichmentConstants.BEAN_ENRICHMENT_PERSISTENT_ITEM_ENTITY_SERVICE)
public class PersistentItemEntityServiceImpl implements PersistentItemEntityService {

	//@Resource(name = "ItemEntityDao")
	@Autowired
	ItemEntityDao itemEntityDao;
	
	@Override
	public ItemEntityImpl findItemEntity(String storyId, String itemId) {
		return itemEntityDao.findItemEntityFromStory(storyId, itemId);
	}
	
	public ItemEntityImpl findItemEntity(String itemId) {
		return itemEntityDao.findItemEntity(itemId);
	}
	
	@Override
	public List<ItemEntityImpl> findItemEntities(String storyId, String itemId) {
		return itemEntityDao.findItemEntitiesFromStory(storyId, itemId);
	}

	@Override
	public List<ItemEntityImpl> findStoryItemEntitiesFromStory(String storyId) {
		// TODO Auto-generated method stub
		return itemEntityDao.findStoryItemEntitiesFromStory(storyId);
	}

	@Override
	public List<ItemEntityImpl> get_N_ItemEntities(int limit, int skip) {

		return itemEntityDao.find_N_ItemEntities(limit, skip);

	}

	@Override
	public void saveItemEntity(ItemEntityImpl entity) {
		itemEntityDao.saveItemEntity(entity);
	}

	@Override
	public void saveStoryItemEntities(List<ItemEntityImpl> entities) {
		for(ItemEntityImpl entity : entities) {
			saveItemEntity(entity);
		}
	}

	@Override
	public void deleteItemEntity(ItemEntityImpl entity) {
		itemEntityDao.deleteItemEntity(entity);
	}

	@Override
	public void deleteAllItemsOfStory(String storyId) {
		itemEntityDao.deleteAllItemsOfStory(storyId);
	}
	
	@Override
	public ItemEntityImpl findItemEntityFromStory(String storyId, String itemId) {
		
		return itemEntityDao.findItemEntityFromStory(storyId, itemId);
	}
	
	@Override
	public void updateNerToolsForItem(String itemId, String nerTool) {
		itemEntityDao.updateNerToolsForItem(itemId, nerTool);
	}

}
