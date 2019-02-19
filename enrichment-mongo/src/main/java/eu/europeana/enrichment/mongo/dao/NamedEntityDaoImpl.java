package eu.europeana.enrichment.mongo.dao;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.StoryItemEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.mongo.model.NamedEntityImpl;
import eu.europeana.enrichment.mongo.model.PositionEntityImpl;

public class NamedEntityDaoImpl implements NamedEntityDao {

	@Resource(name = "storyItemEntityDao")
	StoryItemEntityDao storyItemEntityDao;
	@Resource(name = "translationEntityDao")
	TranslationEntityDao translationEntityDao;
	private Datastore datastore; 
	
	public NamedEntityDaoImpl( Datastore datastore) {
		this.datastore = datastore;
	}
	
	private void addAdditonalInformation(NamedEntity dbEntity) {
		List<PositionEntity> positions = dbEntity.getPositionEntities();
		for(int index = positions.size()-1; index >= 0; index--) {
			PositionEntity dbPositionEntity = positions.get(index);
			String storyItemId = dbPositionEntity.getStoryItemId();
			String translationKey = dbPositionEntity.getTranslationKey();
			if(storyItemId != null && !storyItemId.isEmpty()) {
				StoryItemEntity dbStoryItemEntity = storyItemEntityDao.findStoryItemEntity(storyItemId);
				dbPositionEntity.setStoryItemEntity(dbStoryItemEntity);
			}
			if(translationKey != null && !translationKey.isEmpty()) {
				TranslationEntity dbTranslationEntity = translationEntityDao.findTranslationEntity(translationKey);
				dbPositionEntity.setTranslationEntity(dbTranslationEntity);
			}
		}
	}
	
	@Override
	public NamedEntity findNamedEntity(String key) {
		Query<NamedEntityImpl> persistentNamedEntities = datastore.createQuery(NamedEntityImpl.class);
		persistentNamedEntities.field("key").equal(key);
		List<NamedEntityImpl> result = persistentNamedEntities.asList();
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
	public List<NamedEntity> findNamedEntitiesWithAdditionalInformation(String itemId, boolean translation) {
		Query<NamedEntityImpl> persistentNamedEntities = datastore.createQuery(NamedEntityImpl.class);
		if(translation)
			persistentNamedEntities.disableValidation().field("positionEntities.translationKey").equal(itemId);
		else
			persistentNamedEntities.disableValidation().field("positionEntities.storyItemId").equal(itemId);
		List<NamedEntityImpl> result = persistentNamedEntities.asList();
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
		//TODO: update
		this.datastore.save(entity);
	}

	@Override
	public void deleteNamedEntity(NamedEntity entity) {
		deleteNamedEntityByKey(entity.getKey());
	}

	@Override
	public void deleteNamedEntityByKey(String key) {
		datastore.delete(datastore.find(NamedEntityImpl.class).filter("key", key));
	}

	

}
