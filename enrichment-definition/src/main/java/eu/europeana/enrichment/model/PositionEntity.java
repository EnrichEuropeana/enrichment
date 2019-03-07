package eu.europeana.enrichment.model;

import java.util.List;

public interface PositionEntity {

	/*
	 * Transcribathon and Europeana information
	 */
	StoryEntity getStoryEntity();
	void setStoryEntity(StoryEntity ItemEntity);
	public String getStoryId();
	public void setStoryId(String storyId);
	
	/*
	 * 
	 */
	TranslationEntity getTranslationEntity();
	void setTranslationEntity(TranslationEntity translationEntity);
	public String getTranslationKey();
	public void setTranslationKey(String translationKey);
	
	/*
	 * offset positions
	 */
	List<Integer> getOffsetPositions();
	void setOffsetPositions(List<Integer> offsetPositions);
	void addOfssetPosition(int offsetPosition);
}
