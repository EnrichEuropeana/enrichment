package eu.europeana.enrichment.mongo.dao;

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.mongo.model.DBStoryEntityImpl;

public class StoryEntityDaoImpl implements StoryEntityDao{

	private Datastore datastore; 
	
	public StoryEntityDaoImpl(Datastore datastore) {
		this.datastore = datastore;
	}
	
	@Override
	public StoryEntity findStoryEntity(String key) {
		Query<DBStoryEntityImpl> persistentStoryEntities = datastore.createQuery(DBStoryEntityImpl.class);
		persistentStoryEntities.field("storyId").equal(key);
		List<DBStoryEntityImpl> result = persistentStoryEntities.asList();
		if(result.size() == 0)
			return null;
		else {
			return result.get(0);
		}
	}

	@Override
	public void saveStoryEntity(StoryEntity entity) {
		StoryEntity dbStoryEntity = findStoryEntity(entity.getStoryId());
		if(dbStoryEntity!=null)
		{
			dbStoryEntity.setStoryDescription(entity.getStoryDescription());
			dbStoryEntity.setStoryLanguage(entity.getStoryLanguage());
			dbStoryEntity.setStorySource(entity.getStorySource());
			dbStoryEntity.setStorySummary(entity.getStorySummary());
			dbStoryEntity.setStoryTitle(entity.getStoryTitle());
			dbStoryEntity.setStoryTranscription(entity.getStoryTranscription());
			this.datastore.save(dbStoryEntity);
		}
		else
		{
			this.datastore.save(entity);
		}
	}

	@Override
	public void deleteStoryEntity(StoryEntity entity) {
		deleteStoryEntityByStoryId(entity.getStoryId());
	}

	@Override
	public void deleteStoryEntityByStoryId(String key) {
		datastore.delete(datastore.find(DBStoryEntityImpl.class).filter("storyId", key));
	}

	@Override
	public List<StoryEntity> findAllStoryEntities() {
		Query<DBStoryEntityImpl> persistentStoryEntities = datastore.createQuery(DBStoryEntityImpl.class);		
		List<DBStoryEntityImpl> result = persistentStoryEntities.asList();
		if(result.size() == 0)
			return null;
		else
		{
			List<StoryEntity> tmpResult = new ArrayList<>();
			for(int index = result.size()-1; index >= 0; index--) {
				StoryEntity dbEntity = result.get(index);
				tmpResult.add(dbEntity);
			}
			return tmpResult;
		}
	}

}
