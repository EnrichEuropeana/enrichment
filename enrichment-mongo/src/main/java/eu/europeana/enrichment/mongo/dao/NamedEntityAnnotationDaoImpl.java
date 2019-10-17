package eu.europeana.enrichment.mongo.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import eu.europeana.enrichment.model.NamedEntityAnnotation;
import eu.europeana.enrichment.mongo.model.DBNamedEntityAnnotationImpl;
import eu.europeana.enrichment.mongo.model.DBNamedEntityImpl;

public class NamedEntityAnnotationDaoImpl implements NamedEntityAnnotationDao {

	private Datastore datastore; 
	
	Logger logger = LogManager.getLogger(getClass());
	
	public NamedEntityAnnotationDaoImpl( Datastore datastore) {
		this.datastore = datastore;
	}

	@Override
	public NamedEntityAnnotation findNamedEntityAnnotation(String id) {
		Query<DBNamedEntityAnnotationImpl> persistentNamedEntityAnnotations = datastore.createQuery(DBNamedEntityAnnotationImpl.class);
		persistentNamedEntityAnnotations.field("id").equal(id);
		List<DBNamedEntityAnnotationImpl> result = persistentNamedEntityAnnotations.asList();
		if(result.size() == 0)
			return null;
		else
		{
			NamedEntityAnnotation dbEntity = result.get(0);
			return dbEntity;
		}

	}

	@Override
	public List<NamedEntityAnnotation> findNamedEntityAnnotationWithStoryAndItemId(String storyId, String itemId) {
		
		Query<DBNamedEntityAnnotationImpl> persistentNamedEntityAnnotations = datastore.createQuery(DBNamedEntityAnnotationImpl.class);
		
		persistentNamedEntityAnnotations.disableValidation().and(
				persistentNamedEntityAnnotations.criteria("storyId").equal(storyId),
				persistentNamedEntityAnnotations.criteria("itemId").equal(itemId)
			);

		List<DBNamedEntityAnnotationImpl> result = persistentNamedEntityAnnotations.asList();
		if(result.size() == 0)
			return null;
		else
		{

			List<NamedEntityAnnotation> tmpResult = new ArrayList<>();
			for(int index = result.size()-1; index >= 0; index--) {
				NamedEntityAnnotation dbEntity = result.get(index);
				tmpResult.add(dbEntity);
			}
			return tmpResult;
		}


	}

	@Override
	public NamedEntityAnnotation findNamedEntityAnnotationWithStoryIdItemIdAndWikidataId(String storyId, String itemId, String wikidataId) 
	{
		Query<DBNamedEntityAnnotationImpl> persistentNamedEntityAnnotations = datastore.createQuery(DBNamedEntityAnnotationImpl.class);

		persistentNamedEntityAnnotations.disableValidation().and(
			persistentNamedEntityAnnotations.criteria("storyId").equal(storyId),
			persistentNamedEntityAnnotations.criteria("itemId").equal(itemId),
			persistentNamedEntityAnnotations.criteria("wikidataId").equal(wikidataId)
		);

		List<DBNamedEntityAnnotationImpl> result = persistentNamedEntityAnnotations.asList();
		if(result.size() == 0)
			return null;
		else
		{
			NamedEntityAnnotation dbEntity = result.get(0);
			return dbEntity;
		}
	}
	
	@Override
	public void saveNamedEntityAnnotation(NamedEntityAnnotation entity) {
		DBNamedEntityAnnotationImpl tmp = null;
		if(entity instanceof DBNamedEntityAnnotationImpl)
			tmp = (DBNamedEntityAnnotationImpl) entity;
		else {
			tmp = new DBNamedEntityAnnotationImpl(entity);
		}
		if(tmp != null)
			this.datastore.save(tmp);
		
	}

	@Override
	public void deleteNamedEntityAnnotation(NamedEntityAnnotation entity) {
		deleteNamedEntityAnnotationById(entity.getAnnoId());
	}

	@Override
	public void deleteNamedEntityAnnotationById(String id) {
		datastore.delete(datastore.find(DBNamedEntityAnnotationImpl.class).filter("id", id));		
	}
	
	@Override
	public void deleteNamedEntityAnnotation(String storyId, String itemId) {
		Query<DBNamedEntityAnnotationImpl> persistentNamedEntitiesAnnotationQuery = datastore.createQuery(DBNamedEntityAnnotationImpl.class);
		persistentNamedEntitiesAnnotationQuery.disableValidation();
		persistentNamedEntitiesAnnotationQuery.filter("storyId", storyId);
		persistentNamedEntitiesAnnotationQuery.filter("itemId", itemId);
		datastore.delete(persistentNamedEntitiesAnnotationQuery);		
	}


	@Override
	public void deleteAllNamedEntityAnnotation() {
		// TODO Auto-generated method stub
		
	}



}
