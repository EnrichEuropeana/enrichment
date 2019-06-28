package eu.europeana.enrichment.mongo.model;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.NotSaved;
import org.mongodb.morphia.annotations.Transient;
import org.springframework.data.annotation.Id;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.model.impl.TranslationEntityImpl;

public class DBTranslationEntityImpl extends TranslationEntityImpl{

	@Id
    private String _id = new ObjectId().toString();
	@Transient
	@NotSaved
	private StoryEntity storyEntity;
	
	@Transient
	@NotSaved
	private ItemEntity itemEntity;


	public DBTranslationEntityImpl() {
		super();
	}
	
	public DBTranslationEntityImpl(TranslationEntity entity) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		super();
		setKey(entity.getKey());
		setLanguage(entity.getLanguage());
		setTranslatedText(entity.getTranslatedText());
		setTool(entity.getTool());
		setStoryId(entity.getStoryId());
		setItemId(entity.getItemId());
	}
	
	@Override
	public String getId() {
		return _id;
	}

	@Override
	public StoryEntity getStoryEntity() {
		return this.storyEntity;
	}

	@Override
	public void setStoryEntity(StoryEntity storyEntity) {
		this.storyEntity = storyEntity;
		if(storyEntity != null)
			setStoryId(storyEntity.getStoryId());
		else
			setStoryId(null);
	}
	
	@Override
	public ItemEntity getItemEntity() {
		return this.itemEntity;
	}

	@Override
	public void setItemEntity(ItemEntity itemEntity) {
		this.itemEntity = itemEntity;
		if(itemEntity != null)
			setItemId(itemEntity.getItemId());
		else
			setItemId(null);
	}
	
}
