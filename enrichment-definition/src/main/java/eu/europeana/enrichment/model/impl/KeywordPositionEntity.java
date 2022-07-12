package eu.europeana.enrichment.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import dev.morphia.annotations.Embedded;

@Embedded
public class KeywordPositionEntity {

	private List<Integer> offsetsTranslatedText;
	private String propertyId;
	private String propertyTypeId;
	private String value;
	private String translatedValue;
	private String detectedOriginalLanguage;
	private List<String> nerTools;
	
	// Overriding equals() to compare the 2 objects
    public boolean equals(Object pe){ 
  
        // If the object is compared with itself then return true   
        if (pe == this) { 
            return true; 
        } 
  
        if (!(pe instanceof KeywordPositionEntity)) { 
            return false; 
        } 
          
        //typecast so that we can compare data members  
        KeywordPositionEntity pe_new = (KeywordPositionEntity) pe; 
        
        Collections.sort(offsetsTranslatedText);
        Collections.sort(pe_new.getOffsetsTranslatedText());
        
        // Compare the data members and return accordingly  
        return Objects.equals(propertyId, pe_new.getPropertyId())
                && Objects.equals(propertyTypeId, pe_new.getPropertyTypeId())
                && Objects.equals(value, pe_new.getValue());
    } 
    
    public KeywordPositionEntity(List<Integer> offsetsTranslatedText, String propertyId, String propertyTypeId,
			String value, String translatedValue, String detectedOriginalLanguage, List<String> nerTools) {
		super();
		if(offsetsTranslatedText!=null) {
			this.offsetsTranslatedText = new ArrayList<Integer>(offsetsTranslatedText);
		}
		this.propertyId = propertyId;
		this.propertyTypeId = propertyTypeId;
		this.value = value;
		this.translatedValue = translatedValue;
		this.detectedOriginalLanguage = detectedOriginalLanguage;
		if(nerTools!=null) {
			this.nerTools = new ArrayList<String>(nerTools);
		}
	}

	public int hashCode() {
        int result = 17;
        result = 31 * result + propertyId.hashCode();
        result = 31 * result + propertyTypeId.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
    
	public List<Integer> getOffsetsTranslatedText() {
		return offsetsTranslatedText;
	}
	
	public void setOffsetsTranslatedText(List<Integer> offsetPositions) {
		this.offsetsTranslatedText = offsetPositions;
	}
	
	public List<String> getNerTools() {
		return nerTools;
	}

	public void setNerTools(List<String> nerTools) {
		this.nerTools = nerTools;
	}
	
	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	public String getPropertyTypeId() {
		return propertyTypeId;
	}

	public void setPropertyTypeId(String propertyTypeId) {
		this.propertyTypeId = propertyTypeId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTranslatedValue() {
		return translatedValue;
	}

	public void setTranslatedValue(String translatedValue) {
		this.translatedValue = translatedValue;
	}

	public String getDetectedOriginalLanguage() {
		return detectedOriginalLanguage;
	}

	public void setDetectedOriginalLanguage(String detectedOriginalLanguage) {
		this.detectedOriginalLanguage = detectedOriginalLanguage;
	}

}
