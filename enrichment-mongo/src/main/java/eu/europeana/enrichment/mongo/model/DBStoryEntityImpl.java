package eu.europeana.enrichment.mongo.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import eu.europeana.enrichment.model.impl.StoryEntityImpl;

public class DBStoryEntityImpl extends StoryEntityImpl {

	//id will be used for storing MongoDB _id
	@Id
    private String _id = new ObjectId().toString();
	
	
	@Override
	public String getId() {
		return _id;
	}


}
