package eu.europeana.enrichment.model.impl;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;

public class ItemEntityImpl implements ItemEntity{

	private String itemId;
	private String language;
	private String type;
	private String transcriptionText;
	private String description;
	private String hashKey;
	private String storyId;
	private String title;
	private String source;
	
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
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StoryEntity getStoryEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStoryEntity(StoryEntity storyEntity) {
		// TODO Auto-generated method stub
		
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


	
}
