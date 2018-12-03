package eu.europeana.enrichment.mongo.model;

import java.io.Serializable;
import java.util.List;

import org.mongodb.morphia.Datastore;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import eu.europeana.api.commons.nosql.dao.impl.NosqlDaoImpl;


public class DaoNamedEntityImpl implements DaoNamedEntity {

	private static final String NAMEDENTITY_COLLECTION = "NamedEntity";
	private Datastore datastore; 
	
	public DaoNamedEntityImpl( Datastore datastore) {
		this.datastore = datastore;
	}
	
	@Override
	public PersistentNamedEntity findNamedEntity(String key) {
		Query query = new Query(Criteria.where("_id").is(key));
		PersistentNamedEntity entity = datastore.get(PersistentNamedEntity.class, key);
		return entity;
	}

	/*@Override
	public List<NamedEntity> getAllNamedEntities() {
		//return this.mongoOps.findAll(NamedEntity.class, NAMEDENTITY_COLLECTION);
		return null;
	}*/

	@Override
	public void saveNamedEntity(PersistentNamedEntity entity) {
		//TODO: normalize key because of special characters 
		this.datastore.save(entity);
	}

	@Override
	public void saveNamedEntities(List<PersistentNamedEntity> entities) {
		for (PersistentNamedEntity namedEntity : entities) {
			saveNamedEntity(namedEntity);
		}
	}

	@Override
	public void deleteNamedEntity(PersistentNamedEntity entity) {
		deleteByKey(entity.id);
	}

	@Override
	public void deleteByKey(String key) {
		Query query = new Query(Criteria.where("_id").is(key));
		this.datastore.delete(query);
	}

	

}
