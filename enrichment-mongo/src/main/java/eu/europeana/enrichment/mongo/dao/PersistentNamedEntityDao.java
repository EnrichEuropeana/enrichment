package eu.europeana.enrichment.mongo.dao;

import java.io.Serializable;

import eu.europeana.api.commons.nosql.dao.NosqlDao;
import eu.europeana.enrichment.mongo.model.internal.PersistentNamedEntity;

public interface PersistentNamedEntityDao<E extends PersistentNamedEntity, T extends Serializable> extends NosqlDao<E, T> {

	//long generateNextUserSetId(String provider);
	
}
