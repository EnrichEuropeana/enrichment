package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.mongo.model.StoryEntityImpl;

public class StoryEntityDaoImpl implements StoryEntityDao{

	private Datastore datastore; 
	
	public StoryEntityDaoImpl(Datastore datastore) {
		this.datastore = datastore;
	}
	
	@Override
	public StoryEntity findStoryEntity(String key) {
		Query<StoryEntityImpl> persistentStoryEntities = datastore.createQuery(StoryEntityImpl.class);
		persistentStoryEntities.field("storyId").equal(key);
		List<StoryEntityImpl> result = persistentStoryEntities.asList();
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
		datastore.delete(datastore.find(StoryEntityImpl.class).filter("storyId", key));
	}

}
