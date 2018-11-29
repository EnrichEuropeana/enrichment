package eu.europeana.enrichment.mongo.service;

import eu.europeana.api.commons.nosql.service.AbstractNoSqlService;
import eu.europeana.enrichment.common.definitions.NamedEntity;
import eu.europeana.enrichment.mongo.model.internal.PersistentNamedEntity;

public interface PersistentNamedEntityService extends AbstractNoSqlService<PersistentNamedEntity, String>{

	public NamedEntity store(NamedEntity object);
	
	public PersistentNamedEntity findByID(String identifier);
	
	public PersistentNamedEntity update(PersistentNamedEntity namedEntity);
	
	public void remove(String identifier);
	
}
