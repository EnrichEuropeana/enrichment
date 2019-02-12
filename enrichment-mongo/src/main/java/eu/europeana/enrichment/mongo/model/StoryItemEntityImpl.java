package eu.europeana.enrichment.mongo.model;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.StoryItemEntity;
import eu.europeana.enrichment.model.TranslationEntity;

public class StoryItemEntityImpl implements StoryItemEntity{

	//id will be used for storing MongoDB _id
	@Id
    public String _id;
	public String storyItemId;
	public String language;
	public String type;
	public String text;
	public String hashKey;
	public String storyId;
	@Transient
	private StoryEntity storyEntity;
	
	public String getStoryId() {
		return storyId;
	}
	public void setStoryId(String storyId) {
		this.storyId = storyId;
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
		setStoryId(storyEntity.getStoryId());
	}

	@Override
	public String getStoryItemId() {
		return storyItemId;
	}

	@Override
	public void setStoryItemId(String storyItemId) {
		this.storyItemId = storyItemId;
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
	public String getText() {
		return text;
	}

	@Override
	public void setText(String text) {
		this.text = text;
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
	
}
