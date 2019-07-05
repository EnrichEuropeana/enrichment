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

	ItemEntity getItemEntity();
	void setItemEntity(ItemEntity itemEntity);
	
	public String getItemId();
	public void setItemId(String itemId);

	public String getFieldUsedForNER();
	public void setFieldUsedForNER(String fieldUsedForNER);

	
	/*
	 * 
	 */
	TranslationEntity getTranslationEntity();
	void setTranslationEntity(TranslationEntity translationEntity);
	public String getTranslationKey();
	public void setTranslationKey(String translationKey);
	
	/*
	 * offset positions (the positions of the named entities) in the translated text
	 */
	List<Integer> getOffsetsTranslatedText();
	void setOffsetsTranslatedText(List<Integer> offsetPositions);
	void addOfssetsTranslatedText(int offsetPosition);
	
	/*
	 * offset positions (the positions of the named entities) in the original text
	 */
	List<Integer> getOffsetsOriginalText();
	void setOffsetsOriginalText(List<Integer> offsetPositions);
	void addOfssetsOriginalText(int offsetPosition);
	
	boolean equals(PositionEntity pe);
	int hashCode();
	
	List<String> getNERTools();
	void addNERTool(String tool);
	void setNERTools(List<String> tools);

}
