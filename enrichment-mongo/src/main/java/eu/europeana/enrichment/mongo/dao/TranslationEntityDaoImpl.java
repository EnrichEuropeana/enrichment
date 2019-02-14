package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import javax.annotation.Resource;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import eu.europeana.enrichment.model.StoryItemEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.mongo.model.TranslationEntityImpl;

public class TranslationEntityDaoImpl implements TranslationEntityDao {

	@Resource(name = "storyItemEntityDao")
	StoryItemEntityDao storyItemEntityDao;
	
	private Datastore datastore; 
	
	public TranslationEntityDaoImpl(Datastore datastore) {
		this.datastore = datastore;
	}
	
	@Override
	public TranslationEntity findTranslationEntity(String key) {
		Query<TranslationEntityImpl> persistentNamedEntities = datastore.createQuery(TranslationEntityImpl.class);
		persistentNamedEntities.field("key").equal(key);
		List<TranslationEntityImpl> result = persistentNamedEntities.asList();
		if(result.size() == 0)
			return null;
		else {
			TranslationEntityImpl dbEntity = result.get(0);
			StoryItemEntity dbStoryItemEntity = storyItemEntityDao.findStoryItemEntity(dbEntity.getStoryItemId());
			dbEntity.setStoryItemEntity(dbStoryItemEntity);
			return dbEntity;
		}
	}
	@Override
	public TranslationEntity findTranslationEntityWithStoryInformation(String storyItemId, String tool, String language) {
		Query<TranslationEntityImpl> persistentNamedEntities = datastore.createQuery(TranslationEntityImpl.class);
		persistentNamedEntities.and(
				persistentNamedEntities.criteria("storyItemId").equal(storyItemId),
				persistentNamedEntities.criteria("tool").equal(tool),
				persistentNamedEntities.criteria("language").equal(language)
				);
		List<TranslationEntityImpl> result = persistentNamedEntities.asList();
		if(result.size() == 0)
			return null;
		else
			return result.get(0);
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
		datastore.delete(datastore.find(TranslationEntityImpl.class).filter("key", key));
	}

}
