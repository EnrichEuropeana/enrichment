package eu.europeana.enrichment.mongo.model;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.mongodb.morphia.annotations.NotSaved;
import org.mongodb.morphia.annotations.Transient;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.TranslationEntity;

public class ItemEntityImpl implements ItemEntity{

	//id will be used for storing MongoDB _id
	@Id
    public String _id = new ObjectId().toString();
	public String storyItemId;
	public String language;
	public String type;
	public String transcriptionText;
	public String hashKey;
	public String storyId;
	public String storyItemTitle;
	@Transient
	@NotSaved
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
		if(storyEntity != null)
			setStoryId(storyEntity.getStoryId());
		else
			setStoryId(null);
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
	public String getTranscription() {
		return transcriptionText;
	}

	@Override
	public void setTranscription(String transcriptionText) {
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
		return storyItemTitle;
	}
	@Override
	public void setTitle(String storyItemTitle) {
		this.storyItemTitle=storyItemTitle;		
	}
	
}
