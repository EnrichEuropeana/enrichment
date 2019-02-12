package eu.europeana.enrichment.mongo.model;

import java.util.List;

import org.springframework.data.annotation.Transient;

import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.StoryItemEntity;
import eu.europeana.enrichment.model.TranslationEntity;

public class PositionEntityImpl implements PositionEntity{

	public List<Integer> offsets;
	public String storyItemId;
	public String translationKey;
	@Transient
	StoryItemEntity storyItemEntity;
	@Transient
	TranslationEntity translationEntity;
	
	public String getStoryItemId() {
		return storyItemId;
	}
	
	public void setStoryItemId(String storyItemId) {
		this.storyItemId = storyItemId;
	}
	
	public String getTranslationKey() {
		return translationKey;
	}
	
	public void setTranslationKey(String translationKey) {
		this.translationKey = translationKey;
	}
	
	@Override
	public StoryItemEntity getStoryItemEntity() {
		return storyItemEntity;
	}

	@Override
	public void setStoryItemEntity(StoryItemEntity storyItemEntity) {
		this.storyItemEntity = storyItemEntity;
		setStoryItemId(storyItemEntity.getStoryItemId());
	}

	@Override
	public TranslationEntity getTranslationEntity() {
		return translationEntity;
	}

	@Override
	public void setTranslationEntity(TranslationEntity translationEntity) {
		this.translationEntity = translationEntity;
		setTranslationKey(translationEntity.getKey());
	}
	
	@Override
	public List<Integer> getOffsetPositions() {
		return offsets;
	}

	@Override
	public void setOffsetPositions(List<Integer> offsetPositions) {
		this.offsets = offsetPositions;
	}

	@Override
	public void addOfssetPosition(int offsetPosition) {
		offsets.add(offsetPosition);
	}

}
