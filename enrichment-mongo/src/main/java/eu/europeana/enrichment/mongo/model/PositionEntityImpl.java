package eu.europeana.enrichment.mongo.model;

import java.util.ArrayList;
import java.util.List;

import org.mongodb.morphia.annotations.NotSaved;
import org.mongodb.morphia.annotations.Transient;

import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.TranslationEntity;

public class PositionEntityImpl implements PositionEntity{

	public List<Integer> offsets;
	public String storyId;
	public String translationKey;
	@Transient
	@NotSaved
	StoryEntity storyEntity;
	@Transient
	@NotSaved
	TranslationEntity translationEntity;
	
	public PositionEntityImpl() {
		offsets = new ArrayList<>();
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
	public StoryEntity getStoryEntity() {
		return storyEntity;
	}

	@Override
	public void setStoryEntity(StoryEntity storyEntity) {
		this.storyEntity=storyEntity;
		if(storyEntity != null)
			setStoryId(storyEntity.getStoryId());
		else
			setStoryId(null);
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
