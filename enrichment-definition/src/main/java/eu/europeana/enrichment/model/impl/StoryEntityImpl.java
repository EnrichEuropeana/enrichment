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
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String storyTitle) {
		this.title=storyTitle;
		
	}

	@Override
	public String getSource() {
		return source;
	}

	@Override
	public void setSource(String storySource) {
		this.source=storySource;		
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description=description;
	}

	@Override
	public String getSummary() {
		return summary;
	}

	@Override
	public void setSummary(String storySummary) {
		this.summary=storySummary;
		
	}

	@Override
	public String getLanguage() {
		return language;
	}

	@Override
	public void setLanguage(String storyLanguage) {
		this.language=storyLanguage;
	}

	@Override
	public String getTranscription() {
		return transcriptionText;
	}

	@Override
	public void setTranscription(String storyTranscription) {
		this.transcriptionText=storyTranscription;
	}

}
