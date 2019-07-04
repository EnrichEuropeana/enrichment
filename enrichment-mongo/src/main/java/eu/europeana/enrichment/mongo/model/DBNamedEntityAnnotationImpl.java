package eu.europeana.enrichment.mongo.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import eu.europeana.enrichment.model.NamedEntityAnnotation;
import eu.europeana.enrichment.model.impl.NamedEntityAnnotationImpl;

public class DBNamedEntityAnnotationImpl extends NamedEntityAnnotationImpl {

	//id will be used for storing MongoDB _id
	@Id
    public String _id = new ObjectId().toString();
	
	public DBNamedEntityAnnotationImpl() {
		super();
	}

	public DBNamedEntityAnnotationImpl(String storyId, String itemId, String wikidataId, String storyOrItemSource) {
		super(storyId, itemId, wikidataId, storyOrItemSource);
	}

	public DBNamedEntityAnnotationImpl(NamedEntityAnnotation entity) {
		super(entity);

	}
	
	@Override
	public String getId() {
		return _id;
	}
}
