package eu.europeana.enrichment.model;

public interface StoryEntity {

	/*
	 * Database ID
	 */
	String getId();
	
	/*
	 * Transcribathon and Europeana StoryId
	 */
	String getStoryId();
	void setStoryId(String storyId);
	
	
}
