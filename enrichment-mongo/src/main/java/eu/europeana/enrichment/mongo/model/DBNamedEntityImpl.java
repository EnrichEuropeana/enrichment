package eu.europeana.enrichment.mongo.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import eu.europeana.enrichment.model.impl.NamedEntityImpl;

public class DBNamedEntityImpl extends NamedEntityImpl{

	//id will be used for storing MongoDB _id
	@Id
    public String _id = new ObjectId().toString();

	@Override
	public String getId() {
		return _id;
	}
	
}
