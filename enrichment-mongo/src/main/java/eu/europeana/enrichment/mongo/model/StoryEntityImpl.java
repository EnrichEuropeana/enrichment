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
	public String title;
	public String source;
	public String description;
	public String summary;
	public String language;
	public String transcriptionText;
	
	
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
		return title;
	}

	@Override
	public void setStoryTitle(String storyTitle) {
		this.title=storyTitle;
		
	}

	@Override
	public String getStorySource() {
		return source;
	}

	@Override
	public void setStorySource(String storySource) {
		this.source=storySource;		
	}

	@Override
	public String getStoryDescription() {
		return description;
	}

	@Override
	public void setStoryDescription(String storyDescription) {
		this.description=storyDescription;
	}

	@Override
	public String getStorySummary() {
		return summary;
	}

	@Override
	public void setStorySummary(String storySummary) {
		this.summary=storySummary;
		
	}

	@Override
	public String getStoryLanguage() {
		return language;
	}

	@Override
	public void setStoryLanguage(String storyLanguage) {
		this.language=storyLanguage;
	}

	@Override
	public String getStoryTranscription() {
		return transcriptionText;
	}

	@Override
	public void setStoryTranscription(String storyTranscription) {
		this.transcriptionText=storyTranscription;
	}

}
