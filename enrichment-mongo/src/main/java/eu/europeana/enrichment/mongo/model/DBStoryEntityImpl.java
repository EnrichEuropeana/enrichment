package eu.europeana.enrichment.mongo.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.impl.StoryEntityImpl;

public class DBStoryEntityImpl extends StoryEntityImpl {

	//id will be used for storing MongoDB _id
	@Id
    private String _id = new ObjectId().toString();
	
	public DBStoryEntityImpl() {
		super();
	}
	
	public DBStoryEntityImpl(StoryEntity entity) {
		setStoryId(entity.getStoryId());
		setTitle(entity.getTitle());
		setSource(entity.getSource());
		setDescription(entity.getDescription());
		setSummary(entity.getSummary());
		setLanguage(entity.getLanguage());
		setTranscriptionText(entity.getTranscriptionText());
	}
	
	@Override
	public String getId() {
		return _id;
	}


}
