package eu.europeana.enrichment.definitions.model.impl;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexes;

@Entity(value="PositionEntityImpl")
@Indexes(@Index(fields = { @Field("storyId"), @Field("itemId"), @Field("fieldUsedForNER"), @Field("namedEntityId") }, options = @IndexOptions(unique = true)))
public class PositionEntityImpl {

	private Map<Integer, String> offsetsTranslatedText;
	private List<Integer> offsetsOriginalText;
	private String storyId;
	private String itemId;
	private String fieldUsedForNER;
	private ObjectId namedEntityId;
	@Id
    private ObjectId _id;

	public ObjectId get_id() {
		return _id;
	}

	public PositionEntityImpl() {		
	}
	
	public String getFieldUsedForNER() {
		return fieldUsedForNER;
	}

	public void setFieldUsedForNER(String fieldUsedForNER) {
		this.fieldUsedForNER = fieldUsedForNER;
	}
	
	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getStoryId() {
		return storyId;
	}
	
	public void setStoryId(String storyItemId) {
		this.storyId = storyItemId;
	}	
	
	public Map<Integer, String> getOffsetsTranslatedText() {
		return offsetsTranslatedText;
	}
	
	public void setOffsetsTranslatedText(Map<Integer, String> offsetPositions) {
		this.offsetsTranslatedText = offsetPositions;
	}
	
	public List<Integer> getOffsetsOriginalText() {
		return offsetsOriginalText;
	}
	
	public void setOffsetsOriginalText(List<Integer> offsetPositions) {
		this.offsetsOriginalText=offsetPositions;
		
	}
	
	public void addOfssetsOriginalText(int offsetPosition) {
		offsetsOriginalText.add(offsetPosition);
		
	}
	
	public ObjectId getNamedEntityId() {
		return namedEntityId;
	}

	public void setNamedEntityId(ObjectId namedEntityId) {
		this.namedEntityId = namedEntityId;
	}
}
