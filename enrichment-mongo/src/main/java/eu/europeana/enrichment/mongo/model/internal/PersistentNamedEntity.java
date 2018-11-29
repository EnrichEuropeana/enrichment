package eu.europeana.enrichment.mongo.model.internal;

import org.bson.types.ObjectId;

import eu.europeana.api.commons.nosql.entity.NoSqlEntity;
import eu.europeana.enrichment.common.definitions.NamedEntity;

public interface PersistentNamedEntity extends NamedEntity, NoSqlEntity {

	public final static String FIELD_IDENTIFIER = "identifier";
	public static final String FIELD_TITLE = "title";
	public static final String FIELD_DESCRIPTION = "description";
	public static final String FIELD_SET_TYPE = "setType";
	
	public abstract ObjectId getObjectId();
	
}
