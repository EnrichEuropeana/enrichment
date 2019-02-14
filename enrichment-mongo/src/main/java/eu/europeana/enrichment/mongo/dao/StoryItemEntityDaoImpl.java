package eu.europeana.enrichment.mongo.dao;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.StoryItemEntity;
import eu.europeana.enrichment.mongo.model.StoryItemEntityImpl;

public class StoryItemEntityDaoImpl implements StoryItemEntityDao{

	@Resource(name= "storyEntityDao")
	StoryEntityDao storyEntityDao;
	
	private Datastore datastore;
	
	public StoryItemEntityDaoImpl(Datastore datastore) {
		this.datastore = datastore;
	}
	
	private void addAdditionalInformation(StoryItemEntityImpl dbEntity) {
		StoryEntity dbStoryEntity = storyEntityDao.findStoryEntity(dbEntity.getStoryId());
		dbEntity.setStoryEntity(dbStoryEntity);
	}
	
	@Override
	public StoryItemEntity findStoryItemEntity(String key) {
		Query<StoryItemEntityImpl> persistentStoryItemEntities = datastore.createQuery(StoryItemEntityImpl.class);
		persistentStoryItemEntities.field("storyItemId").equal(key);
		List<StoryItemEntityImpl> result = persistentStoryItemEntities.asList();
		if(result.size() == 0)
			return null;
		else {
			StoryItemEntityImpl dbEntity = result.get(0);
			addAdditionalInformation(dbEntity);
			return dbEntity;
		}
	}
	
	@Override
	public List<StoryItemEntity> findStoryItemEntitiesFromStory(String storyId){
		Query<StoryItemEntityImpl> persistentStoryItemEntities = datastore.createQuery(StoryItemEntityImpl.class);
		persistentStoryItemEntities.field("storyId").equal(storyId);
		List<StoryItemEntityImpl> result = persistentStoryItemEntities.asList();
		List<StoryItemEntity> tmpResult = new ArrayList<>();
		for(int index = result.size()-1; index >= 0; index--) {
			StoryItemEntityImpl dbEntity = result.get(index);
			addAdditionalInformation(dbEntity);
			tmpResult.add(dbEntity);
		}
		return tmpResult;
	}

	@Override
	public void saveStoryItemEntity(StoryItemEntity entity) {
		this.datastore.save(entity);
	}

	@Override
	public void deleteStoryItemEntity(StoryItemEntity entity) {
		deleteStoryItemEntityByStoryItemId(entity.getStoryItemId());
	}

	@Override
	public void deleteStoryItemEntityByStoryItemId(String key) {
		datastore.delete(datastore.find(StoryItemEntityImpl.class).filter("storyItemId", key));
	}

}
