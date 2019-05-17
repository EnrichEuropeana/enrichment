package eu.europeana.enrichment.mongo.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.impl.NamedEntityImpl;

public class DBNamedEntityImpl extends NamedEntityImpl{

	//id will be used for storing MongoDB _id
	@Id
    public String _id = new ObjectId().toString();

	public DBNamedEntityImpl() {
		super();
	}
	
	public DBNamedEntityImpl(NamedEntity entity) {
		super();
		setType(entity.getType());
		setKey(entity.getKey());
		setEuropeanaIds(entity.getEuropeanaIds());
		setWikidataIds(entity.getWikidataIds());
		setDbpediaWikidataIds(entity.getDbpediaWikidataIds());
		setPreferedWikidataIds(entity.getPreferedWikidataIds());
		setDBpediaIds(entity.getDBpediaIds());
		setPositionEntities(entity.getPositionEntities());
	}
	
	
	@Override
	public String getId() {
		return _id;
	}
	
}
