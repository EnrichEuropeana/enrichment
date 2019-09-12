package eu.europeana.enrichment.mongo.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.annotation.Resource;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.mongo.dao.ItemEntityDao;

public class PersistentItemEntityServiceImpl implements PersistentItemEntityService {

	@Resource(name = "ItemEntityDao")
	ItemEntityDao ItemEntityDao;
	
	@Override
	public ItemEntity findItemEntity(String storyItemId) {
		return ItemEntityDao.findItemEntity(storyItemId);
	}

	@Override
	public List<ItemEntity> findStoryItemEntitiesFromStory(String storyId) {
		// TODO Auto-generated method stub
		return ItemEntityDao.findStoryItemEntitiesFromStory(storyId);
	}

	@Override
	public List<ItemEntity> getAllStoryItemEntities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveItemEntity(ItemEntity entity) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		ItemEntityDao.saveItemEntity(entity);
	}

	@Override
	public void saveStoryItemEntities(List<ItemEntity> entities) throws Exception {
		for(ItemEntity entity : entities) {
			saveItemEntity(entity);
		}
	}

	@Override
	public void deleteItemEntity(ItemEntity entity) {
		ItemEntityDao.deleteItemEntity(entity);
	}

	@Override
	public ItemEntity findItemEntityFromStory(String storyId, String itemId) {
		
		return ItemEntityDao.findItemEntityFromStory(storyId, itemId);
	}
	
	@Override
	public void updateNerToolsForItem(String itemId, String nerTool) {
		ItemEntityDao.updateNerToolsForItem(itemId, nerTool);
	}

//	@Override
//	public List<String> getNerToolsForItem(String itemId) {
//		return ItemEntityDao.getNerToolsForItem(itemId);
//	}
//	
//	@Override
//	public int getNumerAnalysedNamedEntities(String field) {
//		return ItemEntityDao.getNumerAnalysedNamedEntities(field);
//	}



}
