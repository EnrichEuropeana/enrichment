package eu.europeana.enrichment.model.impl;

import eu.europeana.enrichment.model.StoryEntity;

public class StoryEntityImpl implements StoryEntity {

	//public String language;
	private String storyId;
	private String title;
	private String source;
	private String description;
	private String summary;
	private String language;
	private String transcriptionText;
	
	
	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
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
