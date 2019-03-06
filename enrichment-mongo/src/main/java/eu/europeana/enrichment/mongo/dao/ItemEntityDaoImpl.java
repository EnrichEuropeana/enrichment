package eu.europeana.enrichment.mongo.dao;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.mongo.model.ItemEntityImpl;

public class ItemEntityDaoImpl implements ItemEntityDao{

	@Resource(name= "storyEntityDao")
	StoryEntityDao storyEntityDao;
	
	private Datastore datastore;
	
	public ItemEntityDaoImpl(Datastore datastore) {
		this.datastore = datastore;
	}
	
	private void addAdditionalInformation(ItemEntityImpl dbEntity) {
		StoryEntity dbStoryEntity = storyEntityDao.findStoryEntity(dbEntity.getStoryId());
		dbEntity.setStoryEntity(dbStoryEntity);
	}
	
	@Override
	public ItemEntity findItemEntity(String key) {
		Query<ItemEntityImpl> persistentStoryItemEntities = datastore.createQuery(ItemEntityImpl.class);
		persistentStoryItemEntities.field("storyItemId").equal(key);
		List<ItemEntityImpl> result = persistentStoryItemEntities.asList();
		if(result.size() == 0)
			return null;
		else {
			ItemEntityImpl dbEntity = result.get(0);
			addAdditionalInformation(dbEntity);
			return dbEntity;
		}
	}
	
	@Override
	public List<ItemEntity> findStoryItemEntitiesFromStory(String storyId){
		Query<ItemEntityImpl> persistentStoryItemEntities = datastore.createQuery(ItemEntityImpl.class);
		persistentStoryItemEntities.field("storyId").equal(storyId);
		List<ItemEntityImpl> result = persistentStoryItemEntities.asList();
		List<ItemEntity> tmpResult = new ArrayList<>();
		for(int index = result.size()-1; index >= 0; index--) {
			ItemEntityImpl dbEntity = result.get(index);
			addAdditionalInformation(dbEntity);
			tmpResult.add(dbEntity);
		}
		return tmpResult;
	}

	@Override
	public void saveItemEntity(ItemEntity entity) {
		this.datastore.save(entity);
	}

	@Override
	public void deleteItemEntity(ItemEntity entity) {
		deleteItemEntityByStoryItemId(entity.getStoryItemId());
	}

	@Override
	public void deleteItemEntityByStoryItemId(String key) {
		datastore.delete(datastore.find(ItemEntityImpl.class).filter("storyItemId", key));
	}

}
