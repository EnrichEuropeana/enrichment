package eu.europeana.enrichment.model.impl;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.Indexes;
import dev.morphia.annotations.Transient;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import dev.morphia.annotations.*;

@Entity(value="ItemEntityImpl")
public class ItemEntityImpl implements ItemEntity{

	//id will be used for storing MongoDB _id
	@Id
    public String _id = new ObjectId().toString();
	
	@Transient
	private StoryEntity storyEntity;

	private String itemId;
	private String language;
	private String type;
	private String transcriptionText;
	private String description;
	private String hashKey;
	private String storyId;
	private String title;
	private String source;
	
	public ItemEntityImpl(ItemEntity item) {
		this.itemId = item.getItemId();
		this.language = item.getLanguage();
		this.type = item.getType();
		this.transcriptionText = item.getTranscriptionText();
		this.description = item.getDescription();
		this.hashKey = item.getKey();
		this.storyId = item.getStoryId();
		this.title = item.getTitle();
		this.source = item.getSource();
	}

	public ItemEntityImpl() {
		
	}

	@Override
	public String getStoryId() {
		return storyId;
	}
	
	@Override
	public void setStoryId(String storyId) {
		this.storyId = storyId;
	}

	@Override
	public String getItemId() {
		return itemId;
	}

	@Override
	public void setItemId(String storyItemId) {
		this.itemId = storyItemId;
	}

	@Override
	public String getLanguage() {
		return this.language;
	}

	@Override
	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public String getType() {
		return this.type;
	}

	@Override
	public void setType(String textType) {
		this.type = textType;
	}

	@Override
	public String getTranscriptionText() {
		return transcriptionText;
	}

	@Override
	public void setTranscriptionText(String transcriptionText) {
		this.transcriptionText = transcriptionText;
	}
	@Override
	public String getKey() {
		return hashKey;
	}
	@Override
	public void setKey(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		String textWithoutWithespace = text.replaceAll("\\s+","");
		byte[] hash = digest.digest(textWithoutWithespace.getBytes(StandardCharsets.UTF_8));
		hashKey = new String(hash, "UTF-8");
	}
	@Override
	public String getTitle() {
		return title;
	}
	@Override
	public void setTitle(String itemTitle) {
		this.title=itemTitle;		
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String descriptionText) {
		this.description = descriptionText;	
	}

	@Override
	public String getSource() {
		
		return source;
	}

	@Override
	public void setSource(String sourceParam) {
		this.source = sourceParam;
		
	}
	
	@Override
	public String getId() {
		return _id;
	}
	
	@Override
	public StoryEntity getStoryEntity() {
		return storyEntity;
	}

	@Override
	public void setStoryEntity(StoryEntity storyEntity) {
		this.storyEntity = storyEntity;
		if(storyEntity != null)
			setStoryId(storyEntity.getStoryId());
		else
			setStoryId(null);
	}

	
}
