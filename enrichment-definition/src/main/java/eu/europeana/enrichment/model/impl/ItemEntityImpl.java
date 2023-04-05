package eu.europeana.enrichment.model.impl;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexes;
import dev.morphia.annotations.Transient;

@Entity(value = "ItemEntityImpl")
@Indexes(@Index(fields = { @Field("itemId"), @Field("storyId")}, options = @IndexOptions(unique = true)))
public class ItemEntityImpl {

	// id will be used for storing MongoDB _id
	@Id
	private ObjectId _id;

	@Transient
	private StoryEntityImpl storyEntity;
	private String itemId;
	private List<String> transcriptionLanguages;
	private String type;
	private String transcriptionText;
	private String hashKey;
	private String storyId;
	private String title;
	private String source;
	private List<String> keywords;

	public ItemEntityImpl(ItemEntityImpl item) {
		this.itemId = item.getItemId();
		this.type = item.getType();
		this.transcriptionText = item.getTranscriptionText();
		this.hashKey = item.getKey();
		this.storyId = item.getStoryId();
		this.title = item.getTitle();
		this.source = item.getSource();
		if (item.getKeywords() != null)	this.keywords = new ArrayList<>(item.getKeywords());
		if (item.getTranscriptionLanguages()!=null) this.transcriptionLanguages = new ArrayList<>(item.getTranscriptionLanguages());
	}
	
	public ItemEntityImpl() {

	}

	public String getStoryId() {
		return storyId;
	}

	public void setStoryId(String storyId) {
		this.storyId = storyId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String storyItemId) {
		this.itemId = storyItemId;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String textType) {
		this.type = textType;
	}

	public String getTranscriptionText() {
		return transcriptionText;
	}

	public void setTranscriptionText(String transcriptionText) {
		this.transcriptionText = transcriptionText;
	}

	public String getKey() {
		return hashKey;
	}

	public void setKey(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		String textWithoutWithespace = text.replaceAll("\\s+", "");
		byte[] hash = digest.digest(textWithoutWithespace.getBytes(StandardCharsets.UTF_8));
		hashKey = new String(hash, "UTF-8");
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String itemTitle) {
		this.title = itemTitle;
	}

	public String getSource() {

		return source;
	}

	public void setSource(String sourceParam) {
		this.source = sourceParam;

	}

	public ObjectId getId() {
		return _id;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public List<String> getTranscriptionLanguages() {
		return transcriptionLanguages;
	}

	public void setTranscriptionLanguages(List<String> transcriptionLanguages) {
		this.transcriptionLanguages = transcriptionLanguages;
	}

	public void copyFromItem(ItemEntityImpl item) {
		this.setItemId(item.getItemId());
		if(item.getKeywords()!=null) this.setKeywords(new ArrayList<>(item.getKeywords()));
		this.setSource(item.getSource());
		this.setStoryId(item.getStoryId());
		this.setTitle(item.getTitle());
		if(item.getTranscriptionLanguages()!=null) this.setTranscriptionLanguages(new ArrayList<>(item.getTranscriptionLanguages()));
		this.setTranscriptionText(item.getTranscriptionText());
		this.setType(item.getType());
	}
	
	@Override
    public boolean equals(Object item){ 
        if (item == this) { 
            return true; 
        } 
        if (!(item instanceof ItemEntityImpl)) { 
            return false; 
        }          
        ItemEntityImpl item_new = (ItemEntityImpl) item;
        
        if(transcriptionLanguages!=null) {
        	Collections.sort(transcriptionLanguages);
        }
        if(item_new.getTranscriptionLanguages()!=null) {
        	Collections.sort(item_new.getTranscriptionLanguages());
        }
        
        // Compare the data members and return accordingly  
        return Objects.equals(item_new.getItemId(), itemId)
        		&& Objects.equals(item_new.getSource(), source)
        		&& Objects.equals(item_new.getTitle(), title)
        		&& Objects.equals(item_new.getTranscriptionLanguages(), transcriptionLanguages)
        		&& Objects.equals(item_new.getTranscriptionText(), transcriptionText);
    }     
    
	@Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + itemId.hashCode();
        if(source!=null) {
        	result = 31 * result + source.hashCode();
        }
        if(title!=null) {
        	result = 31 * result + title.hashCode();
        }
        if(transcriptionLanguages!=null) {
        	result = 31 * result + transcriptionLanguages.hashCode();
        }
        if(transcriptionText!=null) {
        	result = 31 * result + transcriptionText.hashCode();
        }
        return result;
    }
	
}
