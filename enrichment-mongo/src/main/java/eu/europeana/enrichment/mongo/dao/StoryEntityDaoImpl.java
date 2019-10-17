package eu.europeana.enrichment.mongo.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.mongo.model.DBStoryEntityImpl;

public class StoryEntityDaoImpl implements StoryEntityDao{

	private Datastore datastore; 
	
	private static Map<String, List<String>> nerToolsForStory;
	
	
	public StoryEntityDaoImpl(Datastore datastore) {
		this.datastore = datastore;
		nerToolsForStory = new HashMap<String, List<String>>();
		
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
			dbStoryEntity.setDescription(entity.getDescription());
			dbStoryEntity.setLanguage(entity.getLanguage());
			dbStoryEntity.setSource(entity.getSource());
			dbStoryEntity.setSummary(entity.getSummary());
			dbStoryEntity.setTitle(entity.getTitle());
			dbStoryEntity.setTranscriptionText(entity.getTranscriptionText());
			this.datastore.save(dbStoryEntity);
		}
		else
		{
			DBStoryEntityImpl tmp = null;
			if(entity instanceof DBStoryEntityImpl)
				tmp = (DBStoryEntityImpl) entity;
			else {
				tmp = new DBStoryEntityImpl(entity);
			}
			if(tmp != null)
				this.datastore.save(tmp);
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

	@Override
	public void updateNerToolsForStory(String storyId, String nerTool) {

		if(nerToolsForStory.get(storyId)==null)
		{
			List<String> toolsForNer = new ArrayList<String>();
			toolsForNer.add(nerTool);
			nerToolsForStory.put(storyId, toolsForNer);
		}
		else if(!nerToolsForStory.get(storyId).contains(nerTool))
		{
			nerToolsForStory.get(storyId).add(nerTool);
		}

	}

	@Override
	public List<String> getNerToolsForStory(String storyId) {
		return nerToolsForStory.get(storyId);
	}
	
//	@Override
//	public int getNumerAnalysedNamedEntities(String field) {
//		return numberAnalysedNamedEntities.get(field).intValue();
//	}
//
//	@Override
//	public void setNumerAnalysedNamedEntities(String field, int num) {
//		numberAnalysedNamedEntities.put(field, Integer.valueOf(num));
//	}

}
