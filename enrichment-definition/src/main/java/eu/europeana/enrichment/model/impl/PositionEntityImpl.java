package eu.europeana.enrichment.model.impl;

import java.util.ArrayList;
import java.util.List;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.TranslationEntity;

public class PositionEntityImpl implements PositionEntity{

	private List<Integer> offsetsTranslatedText;
	private List<Integer> offsetsOriginalText;
	private String storyId;
	private String itemId;
	private List<String> nerTools;

	private String fieldUsedForNER;
	
	public String getFieldUsedForNER() {
		return fieldUsedForNER;
	}

	public void setFieldUsedForNER(String fieldUsedForNER) {
		this.fieldUsedForNER = fieldUsedForNER;
	}

	private String translationKey;
	
	public PositionEntityImpl() {
		offsetsTranslatedText = new ArrayList<Integer>();
		offsetsOriginalText = new ArrayList<Integer>();
		nerTools = new ArrayList<String>();
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
	
	public String getTranslationKey() {
		return translationKey;
	}
	
	public void setTranslationKey(String translationKey) {
		this.translationKey = translationKey;
	}
	
	@Override
	public List<Integer> getOffsetsTranslatedText() {
		return offsetsTranslatedText;
	}

	@Override
	public void setOffsetsTranslatedText(List<Integer> offsetPositions) {
		this.offsetsTranslatedText = offsetPositions;
	}

	@Override
	public void addOfssetsTranslatedText(int offsetPosition) {
		offsetsTranslatedText.add(offsetPosition);
	}

	@Override
	public List<Integer> getOffsetsOriginalText() {
		return offsetsOriginalText;
	}

	@Override
	public void setOffsetsOriginalText(List<Integer> offsetPositions) {
		this.offsetsOriginalText=offsetPositions;
		
	}

	@Override
	public void addOfssetsOriginalText(int offsetPosition) {
		offsetsOriginalText.add(offsetPosition);
		
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
	public TranslationEntity getTranslationEntity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTranslationEntity(TranslationEntity translationEntity) {
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
	
	// Overriding equals() to compare two PositionEntity objects 
    @Override
    public boolean equals(PositionEntity pe) { 
  
        // If the object is compared with itself then return true   
        if (pe == this) { 
            return true; 
        } 
  
        /* Check if object is an instance of PositionEntity or not 
          "null instanceof [type]" also returns false */
        if (!(pe instanceof PositionEntity)) { 
            return false; 
        } 
          
        // typecast pe to PositionEntity so that we can compare data members  
        PositionEntity pe_new = (PositionEntity) pe; 
          
        // Compare the data members and return accordingly  
        return pe_new.getStoryId().compareTo(storyId)==0
                && pe_new.getItemId().compareTo(itemId)==0
                && pe_new.getFieldUsedForNER().compareTo(fieldUsedForNER)==0;
    } 
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + storyId.hashCode();
        result = 31 * result + itemId.hashCode();
        result = 31 * result + fieldUsedForNER.hashCode();
        return result;
    }
    
	@Override
	public List<String> getNERTools() {
		
		return nerTools;
	}

	@Override
	public void addNERTool(String tool) {
		nerTools.add(tool);
	}

	@Override
	public void setNERTools(List<String> tools) {
		nerTools.addAll(tools);
	}

}
