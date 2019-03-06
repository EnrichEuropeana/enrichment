package eu.europeana.enrichment.model;

import java.util.List;

public interface PositionEntity {

	/*
	 * Transcribathon and Europeana information
	 */
	ItemEntity getItemEntity();
	void setItemEntity(ItemEntity ItemEntity);
	public String getStoryItemId();
	public void setStoryItemId(String storyItemId);
	
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
