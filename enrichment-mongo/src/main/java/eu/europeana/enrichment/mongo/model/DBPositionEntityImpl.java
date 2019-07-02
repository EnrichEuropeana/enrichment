package eu.europeana.enrichment.mongo.model;

import org.mongodb.morphia.annotations.NotSaved;
import org.mongodb.morphia.annotations.Transient;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.model.impl.PositionEntityImpl;

public class DBPositionEntityImpl extends PositionEntityImpl{

	@Transient
	@NotSaved
	private StoryEntity storyEntity;
	@Transient
	@NotSaved
	private ItemEntity itemEntity;

	@Transient
	@NotSaved
	private TranslationEntity translationEntity;
	
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
	public ItemEntity getItemEntity() {
		return itemEntity;
	}

	@Override
	public void setItemEntity(ItemEntity itemEntity) {
		this.itemEntity=itemEntity;
		if(itemEntity != null)
			setItemId(itemEntity.getItemId());
		else
			setItemId(null);
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
	
}
