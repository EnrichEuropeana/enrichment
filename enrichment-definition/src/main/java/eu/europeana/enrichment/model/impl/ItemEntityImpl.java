package eu.europeana.enrichment.model.impl;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Transient;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;

@Entity(value = "ItemEntityImpl")
public class ItemEntityImpl implements ItemEntity {

	// id will be used for storing MongoDB _id
	@Id
	public String _id = new ObjectId().toString();

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
	public void copyFromItem(ItemEntity item) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		this.setItemId(item.getItemId());
		this.setKey(item.getKey());
		if(item.getKeywords()!=null) this.setKeywords(new ArrayList<>(item.getKeywords()));
		this.setSource(item.getSource());
		this.setStoryId(item.getStoryId());
		this.setTitle(item.getTitle());
		if(item.getTranscriptionLanguages()!=null) this.setTranscriptionLanguages(new ArrayList<>(item.getTranscriptionLanguages()));
		this.setTranscriptionText(item.getTranscriptionText());
		this.setType(item.getType());
	}
}
