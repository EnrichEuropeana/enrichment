package eu.europeana.enrichment.definitions.model.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexes;
import eu.europeana.enrichment.common.commons.HelperFunctions;

@Entity(value="TranslationEntityImpl")
@Indexes(@Index(fields = { @Field("storyId"), @Field("itemId"), @Field("type"), @Field("tool") }, options = @IndexOptions(unique = true)))
public class TranslationEntityImpl extends BaseEntityImpl {

	protected String key;
	private String language;
	private String translatedText;
	private String tool;
	private String eTranslationId;
	private String storyId;
	private String itemId;
	private String originLangGoogle;
	private String type;
	
	public TranslationEntityImpl (TranslationEntityImpl copy)
	{
		Date now = new Date();
		this.setCreated(now);
		this.setModified(now);		
		this.key = copy.getKey();
		this.language = copy.getLanguage();
		this.translatedText = copy.getTranslatedText();
		this.tool = copy.getTool();
		this.eTranslationId = copy.getETranslationId();
		this.storyId = copy.getStoryId();
		this.itemId = copy.getItemId();
		this.originLangGoogle = copy.getOriginLangGoogle();
		this.type = copy.getType();
	}
	
	public TranslationEntityImpl() {
		Date now = new Date();
		this.setCreated(now);
		this.setModified(now);
	}
	
	@Id
    private ObjectId _id;
	
	
	public ObjectId getId() {
		return _id;
	}

	
	public String getItemId() {
		return itemId;
	}

	
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	
	
	public String getETranslationId() {
		return eTranslationId;
	}
	
	
	public void setETranslationId(String eTranslationId) {
		this.eTranslationId = eTranslationId;
	}

	
	public String getKey() {
		return key;
	}

	
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

	
	public String getLanguage() {
		return language;
	}
	
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	
	public String getTranslatedText() {
		return translatedText;
	}

	
	public void setTranslatedText(String translatedText) {
		this.translatedText = translatedText;
	}

	
	public String getTool() {
		return tool;
	}

	
	public void setTool(String tool) {
		this.tool = tool;
	}
	
	
	public String getType() {
		return type;
	}
	
	
	public void setType(String type) {
		this.type = type;
	}
	
	
	public String getStoryId() {
		return storyId;
	}

	
	public void setStoryId(String storyId) {
		this.storyId = storyId;
	}
	
	public String getOriginLangGoogle() {
		return originLangGoogle;
	}

	public void setOriginLangGoogle(String originLangGoogle) {
		this.originLangGoogle = originLangGoogle;
	}
}
