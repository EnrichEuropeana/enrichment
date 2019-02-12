package eu.europeana.enrichment.mongo.model;

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.annotations.NotSaved;
import org.mongodb.morphia.annotations.Transient;

import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.StoryItemEntity;
import eu.europeana.enrichment.model.TranslationEntity;

public class PositionEntityImpl implements PositionEntity{

	public List<Integer> offsets;
	public String storyItemId;
	public String translationKey;
	@Transient
	@NotSaved
	StoryItemEntity storyItemEntity;
	@Transient
	@NotSaved
	TranslationEntity translationEntity;
	
	public PositionEntityImpl() {
		offsets = new ArrayList<>();
	}
	
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
		if(storyItemEntity != null)
			setStoryItemId(storyItemEntity.getStoryItemId());
		else
			setStoryItemId(null);
	}

	@Override
	public TranslationEntity getTranslationEntity() {
		return translationEntity;
	}

	@Override
	public void setTranslationEntity(TranslationEntity translationEntity) {
		this.translationEntity = translationEntity;
		if(translationEntity != null)
			setTranslationKey(translationEntity.getKey());
		else
			setTranslationKey(null);
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
