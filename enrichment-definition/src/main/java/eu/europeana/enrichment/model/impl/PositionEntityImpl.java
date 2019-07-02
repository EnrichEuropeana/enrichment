package eu.europeana.enrichment.model.impl;

import java.util.ArrayList;
import java.util.List;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;

public class PositionEntityImpl implements PositionEntity{

	private List<Integer> offsetsTranslatedText;
	private List<Integer> offsetsOriginalText;
	private String storyId;
	private String itemId;

	private String storyFieldUsedForNER;
	
	public String getStoryFieldUsedForNER() {
		return storyFieldUsedForNER;
	}

	public void setStoryFieldUsedForNER(String storyFieldUsedForNER) {
		this.storyFieldUsedForNER = storyFieldUsedForNER;
	}

	private String translationKey;
	
	public PositionEntityImpl() {
		offsetsTranslatedText = new ArrayList<Integer>();
		offsetsOriginalText = new ArrayList<Integer>();
	}
	
	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getStoryId() {
		return storyId;
	}
	
	public void setStoryId(String storyItemId) {
		this.storyId = storyItemId;
	}
	
	public String getTranslationKey() {
		return translationKey;
	}
	
	public void setTranslationKey(String translationKey) {
		this.translationKey = translationKey;
	}
	
	@Override
	public List<Integer> getOffsetsTranslatedText() {
		return offsetsTranslatedText;
	}

	@Override
	public void setOffsetsTranslatedText(List<Integer> offsetPositions) {
		this.offsetsTranslatedText = offsetPositions;
	}

	@Override
	public void addOfssetsTranslatedText(int offsetPosition) {
		offsetsTranslatedText.add(offsetPosition);
	}

	@Override
	public List<Integer> getOffsetsOriginalText() {
		return offsetsOriginalText;
	}

	@Override
	public void setOffsetsOriginalText(List<Integer> offsetPositions) {
		this.offsetsOriginalText=offsetPositions;
		
	}

	@Override
	public void addOfssetsOriginalText(int offsetPosition) {
		offsetsOriginalText.add(offsetPosition);
		
	}

	@Override
	public StoryEntity getStoryEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStoryEntity(StoryEntity ItemEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TranslationEntity getTranslationEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTranslationEntity(TranslationEntity translationEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ItemEntity getItemEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setItemEntity(ItemEntity itemEntity) {
		// TODO Auto-generated method stub
		
	}

}
