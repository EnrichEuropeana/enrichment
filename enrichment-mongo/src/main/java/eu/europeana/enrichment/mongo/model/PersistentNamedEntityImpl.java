package eu.europeana.enrichment.mongo.model;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

import eu.europeana.enrichment.common.model.NamedEntityImpl;
import eu.europeana.enrichment.mongo.model.internal.PersistentNamedEntity;

@Entity("namedentity")
@Indexes(@Index(value = PersistentNamedEntity.FIELD_IDENTIFIER, unique = true))
public class PersistentNamedEntityImpl extends NamedEntityImpl implements PersistentNamedEntity {

	@Id
	private ObjectId id;
	
	public ObjectId getObjectId() {
		return id;
	}

	public void setObjectId(ObjectId id) {
		this.id = id;
	}
		
	@Override
	public String toString() {
		return "PersistentNamedEntity [Id:" + getObjectId() + "]";
	}


}
