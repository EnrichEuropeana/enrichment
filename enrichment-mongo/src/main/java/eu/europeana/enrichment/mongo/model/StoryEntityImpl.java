package eu.europeana.enrichment.mongo.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import eu.europeana.enrichment.model.StoryEntity;

public class StoryEntityImpl implements StoryEntity {

	//id will be used for storing MongoDB _id
	@Id
    public String _id = new ObjectId().toString();
	//public String language;
	public String storyId;
	public String storyTitle;
	public String storySource;
	public String storyDescription;
	public String storySummary;
	public String storyLanguage;
	public String storyTranscription;
	
	
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

	@Override
	public String getStoryTitle() {
		return storyTitle;
	}

	@Override
	public void setStoryTitle(String storyTitle) {
		this.storyTitle=storyTitle;
		
	}

	@Override
	public String getStorySource() {
		return storySource;
	}

	@Override
	public void setStorySource(String storySource) {
		this.storySource=storySource;		
	}

	@Override
	public String getStoryDescription() {
		return storyDescription;
	}

	@Override
	public void setStoryDescription(String storyDescription) {
		this.storyDescription=storyDescription;
	}

	@Override
	public String getStorySummary() {
		return storySummary;
	}

	@Override
	public void setStorySummary(String storySummary) {
		this.storySummary=storySummary;
		
	}

	@Override
	public String getStoryLanguage() {
		return storyLanguage;
	}

	@Override
	public void setStoryLanguage(String storyLanguage) {
		this.storyLanguage=storyLanguage;
	}

	@Override
	public String getStoryTranscription() {
		return storyTranscription;
	}

	@Override
	public void setStoryTranscription(String storyTranscription) {
		this.storyTranscription=storyTranscription;
	}

}
