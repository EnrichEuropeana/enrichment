package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import eu.europeana.enrichment.common.definitions.NamedEntity;
import eu.europeana.enrichment.common.model.NamedEntityImpl;

public class NamedEntityDaoImpl implements NamedEntityDao {

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
		//TODO: update
		this.datastore.save(entity);
	}

	@Override
	public void deleteNamedEntity(NamedEntity entity) {
		deleteByKey(entity.getKey());
	}

	@Override
	public void deleteByKey(String key) {
		datastore.delete(datastore.find(NamedEntityImpl.class).filter("key", key));
	}

	

}
