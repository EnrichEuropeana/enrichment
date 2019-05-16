package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import javax.annotation.Resource;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.mongo.model.DBTranslationEntityImpl;

public class TranslationEntityDaoImpl implements TranslationEntityDao {

	@Resource(name = "storyEntityDao")
	StoryEntityDao storyEntityDao;
	
	private Datastore datastore; 
	
	public TranslationEntityDaoImpl(Datastore datastore) {
		this.datastore = datastore;
	}
	
	private void addItemEntity(DBTranslationEntityImpl dbEntity) {
		StoryEntity dbItemEntity = storyEntityDao.findStoryEntity(dbEntity.getStoryId());
		dbEntity.setStoryEntity(dbItemEntity);
	}
	
	@Override
	public TranslationEntity findTranslationEntity(String key) {
		Query<DBTranslationEntityImpl> persistentNamedEntities = datastore.createQuery(DBTranslationEntityImpl.class);
		persistentNamedEntities.field("key").equal(key);
		List<DBTranslationEntityImpl> result = persistentNamedEntities.asList();
		if(result.size() == 0)
			return null;
		else {
			DBTranslationEntityImpl dbEntity = result.get(0);
			addItemEntity(dbEntity);
			return dbEntity;
		}
	}
	@Override
	public TranslationEntity findTranslationEntityWithStoryInformation(String storyId, String tool, String language) {
		Query<DBTranslationEntityImpl> persistentNamedEntities = datastore.createQuery(DBTranslationEntityImpl.class);
		persistentNamedEntities.and(
				persistentNamedEntities.criteria("storyId").equal(storyId),
				persistentNamedEntities.criteria("tool").equal(tool),
				persistentNamedEntities.criteria("language").equal(language)
				);
		List<DBTranslationEntityImpl> result = persistentNamedEntities.asList();
		if(result.size() == 0)
			return null;
		else{
			DBTranslationEntityImpl dbEntity = result.get(0);
			addItemEntity(dbEntity);
			return dbEntity;
		}
	}

	@Override
	public void saveTranslationEntity(TranslationEntity entity) {
		this.datastore.save(entity);
	}

	@Override
	public void deleteTranslationEntity(TranslationEntity entity) {
		deleteTranslationEntityByKey(entity.getKey());
	}

	@Override
	public void deleteTranslationEntityByKey(String key) {
		datastore.delete(datastore.find(DBTranslationEntityImpl.class).filter("key", key));
	}

}
