package eu.europeana.enrichment.mongo.dao.impl;

import java.io.Serializable;

import javax.annotation.Resource;

import org.mongodb.morphia.Datastore;

import eu.europeana.api.commons.nosql.dao.impl.NosqlDaoImpl;
import eu.europeana.enrichment.mongo.config.MongoConfiguration;
import eu.europeana.enrichment.mongo.dao.PersistentNamedEntityDao;
import eu.europeana.enrichment.mongo.model.internal.PersistentNamedEntity;

public class PersistentNamedEntityDaoImpl <E extends PersistentNamedEntity, T extends Serializable> 
	extends NosqlDaoImpl<E, T> implements PersistentNamedEntityDao<E, T>{

	@Resource
	private MongoConfiguration configuration;
	
	public MongoConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(MongoConfiguration configuration) {
		this.configuration = configuration;
	}
	public PersistentNamedEntityDaoImpl(Datastore datastore, Class<E> clazz) {
		super(datastore, clazz);
		// TODO Auto-generated constructor stub
	}

	/*
	@SuppressWarnings("deprecation")
	public long generateNextUserSetId(String provider) {

		GeneratedUserSetIdImpl nextUserSetId = null;

		synchronized ((Object) provider) {

			Query<GeneratedUserSetIdImpl> q = getDatastore().createQuery(GeneratedUserSetIdImpl.class);
			q.filter("_id", provider);
			
			UpdateOperations<GeneratedUserSetIdImpl> uOps = getDatastore()
					.createUpdateOperations(GeneratedUserSetIdImpl.class)
					.inc(GeneratedUserSetIdImpl.SEQUENCE_COLUMN_NAME);
			// search UserSetId and get incremented UserSet number 
			nextUserSetId = getDatastore().findAndModify(q, uOps);
			
			if (nextUserSetId == null) {
				nextUserSetId = new GeneratedUserSetIdImpl( 
						provider, ""+1L);
				ds.save(nextUserSetId);
			}
		}

		return nextUserSetId.getUserSetId();
	}
	*/
	
}
