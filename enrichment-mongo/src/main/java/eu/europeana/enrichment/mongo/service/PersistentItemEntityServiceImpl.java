package eu.europeana.enrichment.mongo.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.mongo.dao.ItemEntityDao;

@Service(AppConfigConstants.BEAN_ENRICHMENT_PERSISTENT_ITEM_ENTITY_SERVICE)
public class PersistentItemEntityServiceImpl implements PersistentItemEntityService {

	//@Resource(name = "ItemEntityDao")
	@Autowired
	ItemEntityDao itemEntityDao;
	
	@Override
	public ItemEntity findItemEntity(String storyId, String itemId) {
		return itemEntityDao.findItemEntityFromStory(storyId, itemId);
	}
	
	@Override
	public List<ItemEntityImpl> findItemEntities(String storyId, String itemId) {
		return itemEntityDao.findItemEntitiesFromStory(storyId, itemId);
	}

	@Override
	public List<ItemEntity> findStoryItemEntitiesFromStory(String storyId) {
		// TODO Auto-generated method stub
		return itemEntityDao.findStoryItemEntitiesFromStory(storyId);
	}

	@Override
	public List<ItemEntity> getAllItemEntities() {

		return itemEntityDao.findAllItemEntities();

	}

	@Override
	public void saveItemEntity(ItemEntity entity) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		itemEntityDao.saveItemEntity(entity);
	}

	@Override
	public void saveStoryItemEntities(List<ItemEntity> entities) throws Exception {
		for(ItemEntity entity : entities) {
			saveItemEntity(entity);
		}
	}

	@Override
	public void deleteItemEntity(ItemEntity entity) {
		itemEntityDao.deleteItemEntity(entity);
	}

	@Override
	public ItemEntity findItemEntityFromStory(String storyId, String itemId) {
		
		return itemEntityDao.findItemEntityFromStory(storyId, itemId);
	}
	
	@Override
	public void updateNerToolsForItem(String itemId, String nerTool) {
		itemEntityDao.updateNerToolsForItem(itemId, nerTool);
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
