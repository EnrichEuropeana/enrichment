package eu.europeana.enrichment.mongo.dao;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Criteria;
import org.mongodb.morphia.query.CriteriaContainer;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateResults;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.mongo.model.DBNamedEntityImpl;
import eu.europeana.enrichment.mongo.model.DBPositionEntityImpl;

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
	public NamedEntity findNamedEntity(String label) {
		Query<DBNamedEntityImpl> persistentNamedEntities = datastore.createQuery(DBNamedEntityImpl.class);
		persistentNamedEntities.field("label").equal(label);
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
		
		Query<DBPositionEntityImpl> elemMatchQueryPositionEntity = datastore.createQuery(DBPositionEntityImpl.class);
		
		
		
		if(translation) {
//			persistentNamedEntities.disableValidation().and(
//					persistentNamedEntities.criteria("positionEntities.translationKey").equal(storyId),
//					persistentNamedEntities.criteria("positionEntities.fieldUsedForNER").equal(type)
//					);
			

			elemMatchQueryPositionEntity.field("translationKey").equal(storyId);
			elemMatchQueryPositionEntity.field("fieldUsedForNER").equal(type);

			
			
		} else {
//			persistentNamedEntities.disableValidation().and(
//					persistentNamedEntities.criteria("positionEntities.storyId").equal(storyId),
//					persistentNamedEntities.criteria("positionEntities.itemId").equal(itemId),
//					persistentNamedEntities.criteria("positionEntities.fieldUsedForNER").equal(type)
//					);
			
			elemMatchQueryPositionEntity.field("storyId").equal(storyId);
			elemMatchQueryPositionEntity.field("itemId").equal(itemId);
			elemMatchQueryPositionEntity.field("fieldUsedForNER").equal(type);

		}
		
		persistentNamedEntities.field("positionEntities").elemMatch(elemMatchQueryPositionEntity);

		List<DBNamedEntityImpl> result = persistentNamedEntities.asList();
		List<NamedEntity> tmpResult = new ArrayList<>();
		for(int index = result.size()-1; index >= 0; index--) {
			NamedEntity dbEntity = result.get(index);
			//commented out addAdditonalInformation() function from performance reasons becuase it slows down the db operations in case of many NamedEntities
			//addAdditonalInformation(dbEntity);
			tmpResult.add(dbEntity);
		}
		return tmpResult;
	}

	@Override
	public List<NamedEntity> findNamedEntitiesWithAdditionalInformation(String storyId, String itemId, String type, List<String> nerTools) {
		Query<DBNamedEntityImpl> persistentNamedEntities = datastore.createQuery(DBNamedEntityImpl.class);
		
		Query<DBPositionEntityImpl> elemMatchQueryPositionEntity = datastore.createQuery(DBPositionEntityImpl.class);
		elemMatchQueryPositionEntity.field("storyId").equal(storyId);
		elemMatchQueryPositionEntity.field("itemId").equal(itemId);
		elemMatchQueryPositionEntity.field("fieldUsedForNER").equal(type);
		List<Criteria> criteriaList = new ArrayList<Criteria>();
		for(int i=0;i<nerTools.size();i++)
		{
			criteriaList.add(elemMatchQueryPositionEntity.criteria("nerTools").hasThisOne(nerTools.get(i)));
		}		
		elemMatchQueryPositionEntity.disableValidation().or(criteriaList.toArray(new CriteriaContainer[criteriaList.size()]));
		
		persistentNamedEntities.field("positionEntities").elemMatch(elemMatchQueryPositionEntity);
		
//		persistentNamedEntities.disableValidation().and(
//				persistentNamedEntities.criteria("positionEntities.storyId").equal(storyId),
//				persistentNamedEntities.criteria("positionEntities.itemId").equal(itemId),
//				persistentNamedEntities.criteria("positionEntities.fieldUsedForNER").equal(type)
//				);
		
		//adding the criteria for the nerTools
//		List<Criteria> criteriaList = new ArrayList<Criteria>();
//		for(int i=0;i<nerTools.size();i++)
//		{
//			criteriaList.add(persistentNamedEntities.criteria("positionEntities.nerTools").hasThisOne(nerTools.get(i)));
//		}
//		
//		persistentNamedEntities.disableValidation().or(criteriaList.toArray(new CriteriaContainer[criteriaList.size()]));
		

		List<DBNamedEntityImpl> result = persistentNamedEntities.asList();
		List<NamedEntity> tmpResult = new ArrayList<>();
		for(int index = result.size()-1; index >= 0; index--) {
			NamedEntity dbEntity = result.get(index);
			//commented out addAdditonalInformation() function from performance reasons becuase it slows down the db operations in case of many NamedEntities
			//addAdditonalInformation(dbEntity);
			tmpResult.add(dbEntity);
		}
		return tmpResult;
	}

	
	@Override
	public List<NamedEntity> findNamedEntitiesWithAdditionalInformation(String storyId, String itemId, boolean translation) {
		Query<DBNamedEntityImpl> persistentNamedEntities = datastore.createQuery(DBNamedEntityImpl.class);
		
		Query<DBPositionEntityImpl> elemMatchQueryPositionEntity = datastore.createQuery(DBPositionEntityImpl.class);

		if(translation) {
//			persistentNamedEntities.disableValidation();
//			persistentNamedEntities.criteria("positionEntities.translationKey").equal(storyId);
			elemMatchQueryPositionEntity.field("translationKey").equal(storyId);
					
		} else {
//			persistentNamedEntities.disableValidation().and(
//					persistentNamedEntities.criteria("positionEntities.storyId").equal(storyId),
//					persistentNamedEntities.criteria("positionEntities.itemId").equal(itemId)
//					);
			elemMatchQueryPositionEntity.field("storyId").equal(storyId);
			elemMatchQueryPositionEntity.field("itemId").equal(itemId);

		}
		
		persistentNamedEntities.field("positionEntities").elemMatch(elemMatchQueryPositionEntity);

		List<DBNamedEntityImpl> result = persistentNamedEntities.asList();
		List<NamedEntity> tmpResult = new ArrayList<>();
		for(int index = result.size()-1; index >= 0; index--) {
			NamedEntity dbEntity = result.get(index);
			//commented out addAdditonalInformation() function from performance reasons becuase it slows down the db operations in case of many NamedEntities
			//addAdditonalInformation(dbEntity);
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
		NamedEntity dbNamedEntity = findNamedEntity(entity.getLabel());
		if(dbNamedEntity!=null)
		{
			dbNamedEntity.setDBpediaIds(entity.getDBpediaIds());
			dbNamedEntity.setDbpediaWikidataIds(entity.getDbpediaWikidataIds());
			dbNamedEntity.setEuropeanaIds(entity.getEuropeanaIds());
			dbNamedEntity.setLabel(entity.getLabel());
			dbNamedEntity.setPositionEntities(entity.getPositionEntities());
			dbNamedEntity.setPreferredWikidataIds(entity.getPreferredWikidataIds());
			dbNamedEntity.setType(entity.getType());
			dbNamedEntity.setWikidataIds(entity.getWikidataIds());
			this.datastore.save(dbNamedEntity);
		}
		else
		{	
			DBNamedEntityImpl tmp = null;
			if(entity instanceof DBNamedEntityImpl)
				tmp = (DBNamedEntityImpl) entity;
			else {
				tmp = new DBNamedEntityImpl(entity);
			}
			if(tmp != null)
				this.datastore.save(tmp);
		}
	}

	@Override
	public void deleteNamedEntity(NamedEntity entity) {
		deleteNamedEntityByKey(entity.getLabel());
	}

	@Override
	public void deleteNamedEntityByKey(String label) {
		datastore.delete(datastore.find(DBNamedEntityImpl.class).filter("label", label));
	}

	@Override
	public void deleteAllNamedEntities() {
		datastore.delete(datastore.find(DBNamedEntityImpl.class));		
	}

	@Override
	public void deletePositionEntitiesFromNamedEntity(String storyId, String itemId, String fieldUsedForNER) {
		

		
		Query<DBNamedEntityImpl> persistentNamedEntitiesQuery = datastore.createQuery(DBNamedEntityImpl.class);
		
		Query<DBPositionEntityImpl> elemMatchQueryPositionEntity = datastore.createQuery(DBPositionEntityImpl.class);
		elemMatchQueryPositionEntity.field("storyId").equal(storyId);
		elemMatchQueryPositionEntity.field("itemId").equal(itemId);
		elemMatchQueryPositionEntity.field("fieldUsedForNER").equal(fieldUsedForNER);
		
		persistentNamedEntitiesQuery.field("positionEntities").elemMatch(elemMatchQueryPositionEntity);
		
		//fetching the PositionEntity-ies to be deleted from the NamedEntity-ies
		List<DBNamedEntityImpl> result = persistentNamedEntitiesQuery.asList();
		for(int index = result.size()-1; index >= 0; index--) {
			List<PositionEntity> positionEntityList = result.get(index).getPositionEntities();
			int posIndex = 0;
			while(posIndex<positionEntityList.size())
			{
				if(positionEntityList.get(posIndex).getStoryId().equals(storyId) && positionEntityList.get(posIndex).getItemId().equals(itemId)
						&& positionEntityList.get(posIndex).getFieldUsedForNER().equals(fieldUsedForNER)) 
				{
					positionEntityList.remove(posIndex);
				}
				else
				{
					posIndex++;
				}
			}
		}

		for(int i=0;i<result.size();i++)
		{
			saveNamedEntity(result.get(i));
		}
		
		
//		persistentPositionEntitiesQuery.disableValidation();
//		persistentPositionEntitiesQuery.filter("positionEntities.storyId", storyId);
//		persistentPositionEntitiesQuery.filter("positionEntities.itemId", itemId);
//		persistentPositionEntitiesQuery.filter("positionEntities.fieldUsedForNER", fieldUsedForNER);
		
//		final UpdateResults results = datastore.update(datastore.createQuery(DBNamedEntityImpl.class),
//				datastore.createUpdateOperations(DBNamedEntityImpl.class).removeAll("positionEntities", positionEntitiesDelete));
//		logger.info("Number of updated NamedEntity-s is: " + results.getUpdatedCount());
		
//		persistentNamedEntitiesQuery.disableValidation();
//		persistentNamedEntitiesQuery.filter("positionEntities.storyId", storyId);
//		persistentNamedEntitiesQuery.filter("positionEntities.itemId", itemId);
//		persistentNamedEntitiesQuery.filter("positionEntities.fieldUsedForNER", fieldUsedForNER);
		
	}


	

}
