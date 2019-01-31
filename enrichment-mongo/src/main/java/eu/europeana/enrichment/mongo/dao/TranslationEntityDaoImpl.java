package eu.europeana.enrichment.mongo.dao;

import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

import eu.europeana.enrichment.common.definitions.TranslationEntity;
import eu.europeana.enrichment.common.model.TranslationEntityImpl;

public class TranslationEntityDaoImpl implements TranslationEntityDao {

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
		else
			return result.get(0);
	}

	@Override
	public void saveTranslationEntity(TranslationEntity entity) {
		this.datastore.save(entity);
	}

	@Override
	public void deleteTranslationEntity(TranslationEntity entity) {
		deleteByKey(entity.getKey());
	}

	@Override
	public void deleteByKey(String key) {
		datastore.delete(datastore.find(TranslationEntityImpl.class).filter("key", key));
	}

}
