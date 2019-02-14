package eu.europeana.enrichment.mongo.model;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.NotSaved;
import org.mongodb.morphia.annotations.Transient;
import org.springframework.data.annotation.Id;


import eu.europeana.enrichment.model.StoryItemEntity;
import eu.europeana.enrichment.model.TranslationEntity;

public class TranslationEntityImpl implements TranslationEntity{

	@Id
    public String _id = new ObjectId().toString();
	public String key;
	public String language;
	public String translatedText;
	public String tool;
	public String eTranslationId;
	public String storyItemId;
	@Transient
	@NotSaved
	private StoryItemEntity storyItemEntity;
	
	public String getETranslationId() {
		return eTranslationId;
	}
	
	public void setETranslationId(String eTranslationId) {
		this.eTranslationId = eTranslationId;
	}
	
	public String getId() {
		return _id;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public void setKey(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		String textWithoutWithespace = text.replaceAll("\\s+","");
		byte[] hash = digest.digest(textWithoutWithespace.getBytes(StandardCharsets.UTF_8));
		key = new String(hash, "UTF-8");
	}

	@Override
	public String getLanguage() {
		return language;
	}
	
	@Override
	public void setLanguage(String language) {
		this.language = language;
	}
	
	@Override
	public String getTranslatedText() {
		return translatedText;
	}

	@Override
	public void setTranslatedText(String translatedText) {
		this.translatedText = translatedText;
	}

	@Override
	public String getTool() {
		return tool;
	}

	@Override
	public void setTool(String tool) {
		this.tool = tool;
	}

	@Override
	public StoryItemEntity getStoryItemEntity() {
		return this.storyItemEntity;
	}

	@Override
	public void setStoryItemEntity(StoryItemEntity storyItemEntity) {
		this.storyItemEntity = storyItemEntity;
		if(storyItemEntity != null)
			setStoryItemId(storyItemEntity.getStoryItemId());
		else
			setStoryItemId(null);
	}
	
	public String getStoryItemId() {
		return storyItemId;
	}

	public void setStoryItemId(String storyItemId) {
		this.storyItemId = storyItemId;
	}
}
