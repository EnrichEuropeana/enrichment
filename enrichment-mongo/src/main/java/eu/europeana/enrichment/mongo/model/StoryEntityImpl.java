package eu.europeana.enrichment.mongo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import eu.europeana.enrichment.model.StoryEntity;

public class StoryEntityImpl implements StoryEntity {

	//id will be used for storing MongoDB _id
	@Id
    public String _id;
	//public String language;
	public String storyId;
	
	@Override
	public String getId() {
		return _id;
	}

	@Override
	public String getStoryId() {
		return storyId;
	}

	@Override
	public void setStoryId(String storyId) {
		this.storyId = storyId;
	}

}
