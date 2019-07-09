package eu.europeana.enrichment.model.impl;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;

public class TranslationEntityImpl implements TranslationEntity{

	private String key;
	private String language;
	private String translatedText;
	private String tool;
	private String eTranslationId;
	private String storyId;
	private String itemId;
	
	@Override
	public String getItemId() {
		return itemId;
	}

	@Override
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	private String type;
	
	public String getETranslationId() {
		return eTranslationId;
	}
	
	public void setETranslationId(String eTranslationId) {
		this.eTranslationId = eTranslationId;
	}
	
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public void setKey(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		key = HelperFunctions.generateHashFromText(text);
		
//		MessageDigest digest = MessageDigest.getInstance("SHA-256");
//		String textWithoutWithespace = text.replaceAll("\\s+","");
//		byte[] hash = digest.digest(textWithoutWithespace.getBytes(StandardCharsets.UTF_8));
//		
//		// bytes to hex
//        StringBuilder sb = new StringBuilder();
//        for (byte b : hash) {
//            sb.append(String.format("%02x", b));
//        }
//        key = sb.toString();
		
		//key = new String(hash, "UTF-8");
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
	public String getType() {
		return type;
	}
	
	@Override
	public void setType(String type) {
		this.type = type;
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
	public StoryEntity getStoryEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setStoryEntity(StoryEntity ItemEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ItemEntity getItemEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setItemEntity(ItemEntity itemEntity) {
		// TODO Auto-generated method stub
		
	}
}
