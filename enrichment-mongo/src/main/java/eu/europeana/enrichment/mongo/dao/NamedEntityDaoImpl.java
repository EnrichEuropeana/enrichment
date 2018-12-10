package eu.europeana.enrichment.mongo.dao;

import java.io.Serializable;
import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import eu.europeana.api.commons.nosql.dao.impl.NosqlDaoImpl;
import eu.europeana.enrichment.common.definitions.NamedEntity;
import eu.europeana.enrichment.common.model.NamedEntityImpl;

public class NamedEntityDaoImpl implements NamedEntityDao {

	private static final String NAMEDENTITY_COLLECTION = "NamedEntity";
	private Datastore datastore; 
	
	public NamedEntityDaoImpl( Datastore datastore) {
		this.datastore = datastore;
	}
	
	@Override
	public NamedEntity findNamedEntity(String key) {
		Query<NamedEntityImpl> persistentNamedEntities = datastore.createQuery(NamedEntityImpl.class);
		persistentNamedEntities.field("key").equal(key);
		List<NamedEntityImpl> result = persistentNamedEntities.asList();
		if(result.size() == 0)
			return null;
		else
			return result.get(0);
	}

	/*@Override
	public List<NamedEntity> getAllNamedEntities() {
		//return this.mongoOps.findAll(NamedEntity.class, NAMEDENTITY_COLLECTION);
		return null;
	}*/

	@Override
	public void saveNamedEntity(NamedEntity entity) {
		this.datastore.save(entity);
	}

	@Override
	public void deleteNamedEntity(NamedEntity entity) {
		deleteByKey(entity.getId());
	}

	@Override
	public void deleteByKey(String key) {
		//Query query = new Query(Criteria.where("key").is(key));
		//this.datastore.delete(query);
	}

	

}
