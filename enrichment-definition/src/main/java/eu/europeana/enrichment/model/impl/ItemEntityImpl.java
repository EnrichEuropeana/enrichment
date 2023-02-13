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
import dev.morphia.annotations.Id;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.Transient;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;

@Entity(value = "ItemEntityImpl")
public class ItemEntityImpl implements ItemEntity {

	// id will be used for storing MongoDB _id
	@Id
	private ObjectId _id;

	@Transient
	private StoryEntity storyEntity;

	@Indexed(options = @IndexOptions(unique = true))
	private String itemId;
	
	private List<String> transcriptionLanguages;
	private String type;
	private String transcriptionText;
	private String hashKey;
	private String storyId;
	private String title;
	private String source;
	private List<String> keywords;

	public ItemEntityImpl(ItemEntity item) {
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
		String textWithoutWithespace = text.replaceAll("\\s+", "");
		byte[] hash = digest.digest(textWithoutWithespace.getBytes(StandardCharsets.UTF_8));
		hashKey = new String(hash, "UTF-8");
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void setTitle(String itemTitle) {
		this.title = itemTitle;
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
	public ObjectId getId() {
		return _id;
	}

	@Override
	public StoryEntity getStoryEntity() {
		return storyEntity;
	}

	@Override
	public void setStoryEntity(StoryEntity storyEntity) {
		this.storyEntity = storyEntity;
		if (storyEntity != null)
			setStoryId(storyEntity.getStoryId());
		else
			setStoryId(null);
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

	@Override
	public void copyFromItem(ItemEntity item) {
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
