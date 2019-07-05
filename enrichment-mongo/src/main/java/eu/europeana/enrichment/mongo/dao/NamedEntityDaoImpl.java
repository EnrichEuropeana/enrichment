package eu.europeana.enrichment.mongo.dao;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.mongo.model.DBNamedEntityImpl;

public class NamedEntityDaoImpl implements NamedEntityDao {

	@Resource(name = "storyEntityDao")
	StoryEntityDao storyEntityDao;
	@Resource(name = "ItemEntityDao")
	ItemEntityDao ItemEntityDao;

	@Resource(name = "translationEntityDao")
	TranslationEntityDao translationEntityDao;
	private Datastore datastore; 
	
	Logger logger = LogManager.getLogger(getClass());
	
	public NamedEntityDaoImpl( Datastore datastore) {
		this.datastore = datastore;
	}
	
	private void addAdditonalInformation(NamedEntity dbEntity) {
		List<PositionEntity> positions = dbEntity.getPositionEntities();
		for(int index = positions.size()-1; index >= 0; index--) {
			PositionEntity dbPositionEntity = positions.get(index);
			String storyId = dbPositionEntity.getStoryId();
			String itemId = dbPositionEntity.getItemId();
			String translationKey = dbPositionEntity.getTranslationKey();
			if(storyId != null && !storyId.isEmpty()) {
				StoryEntity dbStoryEntity = storyEntityDao.findStoryEntity(storyId);
				dbPositionEntity.setStoryEntity(dbStoryEntity);
			}
			if(itemId != null && !itemId.isEmpty()) {
				ItemEntity dbItemEntity = ItemEntityDao.findItemEntityFromStory(itemId, storyId);
				dbPositionEntity.setItemEntity(dbItemEntity);
			}
			if(translationKey != null && !translationKey.isEmpty()) {
				TranslationEntity dbTranslationEntity = translationEntityDao.findTranslationEntity(translationKey);
				dbPositionEntity.setTranslationEntity(dbTranslationEntity);
			}
		}
	}
	
	@Override
	public NamedEntity findNamedEntity(String key) {
		Query<DBNamedEntityImpl> persistentNamedEntities = datastore.createQuery(DBNamedEntityImpl.class);
		persistentNamedEntities.field("key").equal(key);
		List<DBNamedEntityImpl> result = persistentNamedEntities.asList();
		if(result.size() == 0)
			return null;
		else
		{
			NamedEntity dbEntity = result.get(0);
			addAdditonalInformation(dbEntity);
			return dbEntity;
		}
	}
	
	@Override
	public List<NamedEntity> findAllNamedEntities() {
		Query<DBNamedEntityImpl> persistentNamedEntities = datastore.createQuery(DBNamedEntityImpl.class);		
		List<DBNamedEntityImpl> result = persistentNamedEntities.asList();
		if(result.size() == 0)
			return null;
		else
		{
			List<NamedEntity> tmpResult = new ArrayList<>();
			for(int index = result.size()-1; index >= 0; index--) {
				NamedEntity dbEntity = result.get(index);
				addAdditonalInformation(dbEntity);
				tmpResult.add(dbEntity);
				logger.info("NamedEntity found is: " + String.valueOf(index));
			}
			return tmpResult;
		}
	}
	

	@Override
	public List<NamedEntity> findNamedEntitiesWithAdditionalInformation(String storyId, String itemId, String type, boolean translation) {
		Query<DBNamedEntityImpl> persistentNamedEntities = datastore.createQuery(DBNamedEntityImpl.class);
		if(translation) {
			persistentNamedEntities.disableValidation().and(
					persistentNamedEntities.criteria("positionEntities.translationKey").equal(storyId),
					persistentNamedEntities.criteria("positionEntities.fieldUsedForNER").equal(type)
					);
		} else {
			persistentNamedEntities.disableValidation().and(
					persistentNamedEntities.criteria("positionEntities.storyId").equal(storyId),
					persistentNamedEntities.criteria("positionEntities.itemId").equal(itemId),
					persistentNamedEntities.criteria("positionEntities.fieldUsedForNER").equal(type)
					);
		}

		List<DBNamedEntityImpl> result = persistentNamedEntities.asList();
		List<NamedEntity> tmpResult = new ArrayList<>();
		for(int index = result.size()-1; index >= 0; index--) {
			NamedEntity dbEntity = result.get(index);
			addAdditonalInformation(dbEntity);
			tmpResult.add(dbEntity);
		}
		return tmpResult;
	}
	


	/*@Override
	public List<NamedEntity> getAllNamedEntities() {
		//return this.mongoOps.findAll(NamedEntity.class, NAMEDENTITY_COLLECTION);
		return null;
	}*/

	@Override
	public void saveNamedEntity(NamedEntity entity) {
		DBNamedEntityImpl tmp = null;
		if(entity instanceof DBNamedEntityImpl)
			tmp = (DBNamedEntityImpl) entity;
		else {
			tmp = new DBNamedEntityImpl(entity);
		}
		if(tmp != null)
			this.datastore.save(tmp);
	}

	@Override
	public void deleteNamedEntity(NamedEntity entity) {
		deleteNamedEntityByKey(entity.getKey());
	}

	@Override
	public void deleteNamedEntityByKey(String key) {
		datastore.delete(datastore.find(DBNamedEntityImpl.class).filter("key", key));
	}

	@Override
	public void deleteAllNamedEntities() {
		datastore.delete(datastore.find(DBNamedEntityImpl.class));		
	}


	

}
