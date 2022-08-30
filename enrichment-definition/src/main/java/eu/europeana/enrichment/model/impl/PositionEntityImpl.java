package eu.europeana.enrichment.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexes;
import dev.morphia.annotations.Transient;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;

@Entity(value="PositionEntityImpl")
@Indexes(@Index(fields = { @Field("storyId"), @Field("itemId")}, options = @IndexOptions(unique = true)))
public class PositionEntityImpl {

	private List<Integer> offsetsTranslatedText;
	private List<Integer> offsetsOriginalText;
	private String storyId;
	private String itemId;
	private List<String> nerTools;
	private String fieldUsedForNER;
	private String namedEntityType;
	private String namedEntityLabel;
	
	public PositionEntityImpl() {		
	}
	
	public PositionEntityImpl(PositionEntityImpl copy) {
		if(offsetsTranslatedText!=null) {
			this.offsetsTranslatedText = new ArrayList<Integer>(copy.getOffsetsTranslatedText());
		}
		if(offsetsOriginalText!=null) {
			this.offsetsOriginalText = new ArrayList<Integer>(copy.getOffsetsOriginalText());
		}
		this.storyId=copy.getStoryId();
		this.itemId=copy.getItemId();
		if(nerTools!=null) {
			this.nerTools = new ArrayList<String>(copy.getNerTools());
		}
		this.fieldUsedForNER = copy.getFieldUsedForNER();
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
	
	public List<Integer> getOffsetsTranslatedText() {
		return offsetsTranslatedText;
	}
	
	public void setOffsetsTranslatedText(List<Integer> offsetPositions) {
		this.offsetsTranslatedText = offsetPositions;
	}
	
	public void addOfssetsTranslatedText(int offsetPosition) {
		offsetsTranslatedText.add(offsetPosition);
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

	// Overriding equals() to compare two PositionEntity objects     
    public boolean equals(Object pe){ 
  
        // If the object is compared with itself then return true   
        if (pe == this) { 
            return true; 
        } 
  
        /* Check if object is an instance of PositionEntity or not 
          "null instanceof [type]" also returns false */
        if (!(pe instanceof PositionEntityImpl)) { 
            return false; 
        } 
          
        // typecast pe to PositionEntity so that we can compare data members  
        PositionEntityImpl pe_new = (PositionEntityImpl) pe; 
        
        Collections.sort(offsetsTranslatedText);
        Collections.sort(pe_new.getOffsetsTranslatedText());
        
        // Compare the data members and return accordingly  
        return pe_new.getStoryId().compareTo(storyId)==0
                && pe_new.getItemId().compareTo(itemId)==0
                && pe_new.getOffsetsTranslatedText().equals(offsetsTranslatedText) //compare the 2 lists including the order of elements, that is why we first sorted them
                && pe_new.getFieldUsedForNER().compareTo(fieldUsedForNER)==0;
    }     
    
    public int hashCode() {
        int result = 17;
        result = 31 * result + storyId.hashCode();
        result = 31 * result + itemId.hashCode();
        result = 31 * result + fieldUsedForNER.hashCode();
        return result;
    }
    
	public List<String> getNerTools() {
		return nerTools;
	}

	public void setNerTools(List<String> nerTools) {
		this.nerTools = nerTools;
	}
}
