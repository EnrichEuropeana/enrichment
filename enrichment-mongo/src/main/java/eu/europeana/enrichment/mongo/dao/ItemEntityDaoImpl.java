package eu.europeana.enrichment.mongo.dao;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.mongo.model.DBItemEntityImpl;

public class ItemEntityDaoImpl implements ItemEntityDao{

	@Resource(name= "storyEntityDao")
	StoryEntityDao storyEntityDao;
	
	private Datastore datastore;
	
	public ItemEntityDaoImpl(Datastore datastore) {
		this.datastore = datastore;
	}
	
	private void addAdditionalInformation(DBItemEntityImpl dbEntity) {
		StoryEntity dbStoryEntity = storyEntityDao.findStoryEntity(dbEntity.getStoryId());
		dbEntity.setStoryEntity(dbStoryEntity);
	}
	
	@Override
	public ItemEntity findItemEntity(String key) {
		Query<DBItemEntityImpl> persistentStoryItemEntities = datastore.createQuery(DBItemEntityImpl.class);
		persistentStoryItemEntities.field("itemId").equal(key);
		List<DBItemEntityImpl> result = persistentStoryItemEntities.asList();
		if(result.size() == 0)
			return null;
		else {
			DBItemEntityImpl dbEntity = result.get(0);
			addAdditionalInformation(dbEntity);
			return dbEntity;
		}
	}
	
	@Override
	public List<ItemEntity> findStoryItemEntitiesFromStory(String storyId){
		Query<DBItemEntityImpl> persistentStoryItemEntities = datastore.createQuery(DBItemEntityImpl.class);
		persistentStoryItemEntities.field("storyId").equal(storyId);
		List<DBItemEntityImpl> result = persistentStoryItemEntities.asList();
		List<ItemEntity> tmpResult = new ArrayList<>();
		for(int index = result.size()-1; index >= 0; index--) {
			DBItemEntityImpl dbEntity = result.get(index);
			addAdditionalInformation(dbEntity);
			tmpResult.add(dbEntity);
		}
		return tmpResult;
	}

	@Override
	public void saveItemEntity(ItemEntity entity) {
		ItemEntity dbItemEntity = findItemEntity(entity.getItemId());
		if(dbItemEntity!=null)
		{
			dbItemEntity.setLanguage(entity.getLanguage());
			dbItemEntity.setTitle(entity.getTitle());
			dbItemEntity.setTranscription(entity.getTranscription());
			dbItemEntity.setType(entity.getType());
			dbItemEntity.setStoryId(entity.getStoryId());
			this.datastore.save(dbItemEntity);
		}
		else
		{
			this.datastore.save(entity);
		}
	}

	@Override
	public void deleteItemEntity(ItemEntity entity) {
		deleteItemEntityByStoryItemId(entity.getItemId());
	}

	@Override
	public void deleteItemEntityByStoryItemId(String key) {
		datastore.delete(datastore.find(DBItemEntityImpl.class).filter("itemId", key));
	}

}
