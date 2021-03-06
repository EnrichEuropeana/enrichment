package eu.europeana.enrichment.mongo.dao;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

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
	public List<TranslationEntity> findAllTranslationEntities() {
		Query<DBTranslationEntityImpl> persistentTranslationEntities = datastore.createQuery(DBTranslationEntityImpl.class);		
		List<DBTranslationEntityImpl> result = persistentTranslationEntities.asList();
		if(result.size() == 0)
			return null;
		else
		{
			List<TranslationEntity> tmpResult = new ArrayList<>();
			for(int index = result.size()-1; index >= 0; index--) {
				TranslationEntity dbEntity = result.get(index);
				tmpResult.add(dbEntity);
			}
			return tmpResult;
		}
	}

	@Override
	public TranslationEntity findTranslationEntityWithAllAditionalInformation(String storyId, String itemId, String tool, String language, String type, String key) {
		Query<DBTranslationEntityImpl> persistentNamedEntities = datastore.createQuery(DBTranslationEntityImpl.class);
		persistentNamedEntities.and(
				persistentNamedEntities.criteria("storyId").equal(storyId),
				persistentNamedEntities.criteria("itemId").equal(itemId),
				persistentNamedEntities.criteria("tool").equal(tool),
				persistentNamedEntities.criteria("language").equal(language),
				persistentNamedEntities.criteria("type").equal(type),
				persistentNamedEntities.criteria("key").equal(key)
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
	public TranslationEntity findTranslationEntityWithAditionalInformation(String storyId, String itemId,
			String tool, String language, String type) {
		Query<DBTranslationEntityImpl> persistentNamedEntities = datastore.createQuery(DBTranslationEntityImpl.class);
		persistentNamedEntities.and(
				persistentNamedEntities.criteria("storyId").equal(storyId),
				persistentNamedEntities.criteria("itemId").equal(itemId),
				persistentNamedEntities.criteria("tool").equal(tool),
				persistentNamedEntities.criteria("language").equal(language),
				persistentNamedEntities.criteria("type").equal(type)
				);
		List<DBTranslationEntityImpl> result = persistentNamedEntities.asList();
		if(result.size() == 0)
			return null;
		else{
			DBTranslationEntityImpl dbEntity = result.get(0);
			//addItemEntity(dbEntity);
			return dbEntity;
		}
	}

	@Override
	public void saveTranslationEntity(TranslationEntity entity) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		TranslationEntity dbTranslationEntity = findTranslationEntity(entity.getKey());
		if(dbTranslationEntity!=null)
		{
			dbTranslationEntity.setType(entity.getType());
			dbTranslationEntity.setTranslatedText(entity.getTranslatedText());
			dbTranslationEntity.setTool(entity.getTool());
			dbTranslationEntity.setStoryId(entity.getStoryId());
			dbTranslationEntity.setLanguage(entity.getLanguage());
			dbTranslationEntity.setKey(entity.getKey());
			dbTranslationEntity.setItemId(entity.getItemId());
			this.datastore.save(dbTranslationEntity);
		}
		else
		{
			DBTranslationEntityImpl tmp = null;
			if(entity instanceof DBTranslationEntityImpl)
				tmp = (DBTranslationEntityImpl) entity;
			else {
				try {
					tmp = new DBTranslationEntityImpl(entity);
				} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
					
					throw e;
				}
			}
			if(tmp != null)
				this.datastore.save(tmp);
		}
	}

	@Override
	public void deleteTranslationEntity(TranslationEntity entity) {
		deleteTranslationEntityByKey(entity.getKey());
	}

	@Override
	public void deleteTranslationEntityByKey(String key) {
		datastore.delete(datastore.find(DBTranslationEntityImpl.class).filter("key", key));
	}
	
	@Override
	public void deleteTranslationEntity(String storyId, String itemId, String type) {
		Query<DBTranslationEntityImpl> persistentTranslationEntitiesQuery = datastore.createQuery(DBTranslationEntityImpl.class);
		persistentTranslationEntitiesQuery.disableValidation();
		persistentTranslationEntitiesQuery.filter("storyId", storyId);
		persistentTranslationEntitiesQuery.filter("itemId", itemId);
		persistentTranslationEntitiesQuery.filter("type", type);
		datastore.delete(persistentTranslationEntitiesQuery);
		
	}


	

}
