package eu.europeana.enrichment.model.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexes;
import dev.morphia.annotations.Transient;
import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;

@Entity(value="TranslationEntityImpl")
public class TranslationEntityImpl implements TranslationEntity{

	private String key;
	private String language;
	private String translatedText;
	private String tool;
	private String eTranslationId;
	private String storyId;
	private String itemId;
	
	public TranslationEntityImpl (TranslationEntity copy)
	{
		this.key = copy.getKey();
		this.language = copy.getLanguage();
		this.translatedText = copy.getTranslatedText();
		this.tool = copy.getTool();
		this.eTranslationId = copy.getETranslationId();
		this.storyId = copy.getStoryId();
		this.itemId = copy.getItemId();
	}
	
	public TranslationEntityImpl() {
		
	}
	
	@Id
    private String _id = new ObjectId().toString();
	
	@Transient
	private StoryEntity storyEntity;
	
	@Transient
	private ItemEntity itemEntity;
	
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
	
	@Override
	public String getItemId() {
		return itemId;
	}

	@Override
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	private String type;
	
	@Override
	public String getETranslationId() {
		return eTranslationId;
	}
	
	@Override
	public void setETranslationId(String eTranslationId) {
		this.eTranslationId = eTranslationId;
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
}
