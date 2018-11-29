package eu.europeana.enrichment.mongo.service.impl;

import eu.europeana.api.commons.nosql.service.impl.AbstractNoSqlServiceImpl;
import eu.europeana.enrichment.common.definitions.NamedEntity;
import eu.europeana.enrichment.mongo.model.internal.PersistentNamedEntity;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityService;

public class PersistentNamedEntityServiceImpl extends AbstractNoSqlServiceImpl<PersistentNamedEntity, String> implements PersistentNamedEntityService {

	@Override
	public NamedEntity store(NamedEntity object) {
		PersistentNamedEntity persistentObject = null;
		if(object instanceof PersistentNamedEntity)
			persistentObject = (PersistentNamedEntity) object;
		else
			return null;
		return this.store(persistentObject);
	}

	@Override
	public PersistentNamedEntity update(PersistentNamedEntity namedEntity) {
		//getDao().update(query, ops)
		return store(namedEntity);
	}

}
