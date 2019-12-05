package eu.europeana.enrichment.model.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import eu.europeana.enrichment.model.NamedEntityAnnotation;

@JsonPropertyOrder({ "id", "type", "motivation","body","target"})
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class NamedEntityAnnotationImpl implements NamedEntityAnnotation {

	private final String idBase = "http://dsi-demo.ait.ac.at/enrichment-web/enrichment/annotation/";
	private String annoId;
	private String source;
	private String target;
	private String type;
	private String motivation;
	private String body;
	private String wikidataId;
	private String storyId;
	private String itemId;

	
	@Override
	@JsonIgnore
	public String getWikidataId() {
		return wikidataId;
	}

	
	@Override
	@JsonIgnore
	public String getStoryId() {
		return storyId;
	}
	
	@Override
	@JsonIgnore
	public String getItemId() {
		return itemId;
	}
	
	public NamedEntityAnnotationImpl () {
		init();
	}
	
	void init() {
		source = "";
		target = "";
		annoId = "";
		type = "";
		motivation = "";
		body = "";
		wikidataId = "";
		storyId = "";
		itemId = "";		
	}
	
	public NamedEntityAnnotationImpl (NamedEntityAnnotation entity) {
		this.source = entity.getWikidataId();
		this.target = entity.getTarget();
		this.annoId = entity.getAnnoId();
		this.type = "Annotation";
		this.motivation = "tagging";
		this.body = this.source;
		this.wikidataId = entity.getWikidataId();
		this.storyId = entity.getStoryId();
		this.itemId = entity.getItemId();

	}

	public NamedEntityAnnotationImpl (String storyId, String itemId, String wikidataId, String storyOrItemSource) {

		this.source = wikidataId;
		this.target = storyOrItemSource;	
		if(itemId.compareTo("all")==0)
		{
			this.annoId = idBase + storyId + "/" + wikidataId.substring(wikidataId.lastIndexOf("/")+1);
		}
		else
		{
			this.annoId = idBase + storyId + "/" + itemId + "/" + wikidataId.substring(wikidataId.lastIndexOf("/")+1);
		}
		this.type = "Annotation";
		this.motivation = "tagging";
		this.body = this.source;
		this.wikidataId = wikidataId;
		this.storyId = storyId;
		this.itemId = itemId;
	}

	
	@Override
	@JsonIgnore
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	@JsonIgnore
	public String getSource() {
		return source;
	}

	@Override
	public void setSource(String sourceParam) {
		source = sourceParam;	
	}

	@Override
	@JsonProperty("target")
	public String getTarget() {
		return target;
	}

	@Override
	public void setTarget(String targetParam) {
		target = targetParam;
		
	}

	@Override
	@JsonProperty("id")
	public String getAnnoId() {
		
		return annoId;
	}

	@Override
	public void setAnnoId(String idParam) {
		annoId = idParam; 
		
	}

	@Override
	@JsonProperty("type")
	public String getType() {
		
		return type;
	}

	@Override
	public void setType(String typeParam) {
		type = typeParam;
		
	}

	@Override
	@JsonProperty("motivation")
	public String getMotivation() {
		return motivation;
	}

	@Override
	public void setMotivation(String motivationParam) {
		motivation = motivationParam;
		
	}

	@Override
	@JsonProperty("body")
	public String getBody() {
		
		return body;
	}

	@Override
	public void setBody(String bodyParam) {
		body = bodyParam;
		
	}
	
	// Overriding equals() to compare two NamedEntityAnnotation objects 
    @Override
    public boolean equals(Object nea) {
  
        // If the object is compared with itself then return true   
        if (nea == this) { 
            return true; 
        } 
  
        if (!(nea instanceof NamedEntityAnnotation)) { 
            return false; 
        } 
          
        // typecast nea to NamedEntityAnnotation so that we can compare the fields
        NamedEntityAnnotation nea_new = (NamedEntityAnnotation) nea; 
       
        // Compare the data members and return accordingly  
        return nea_new.getStoryId().compareTo(storyId)==0
                && nea_new.getItemId().compareTo(itemId)==0
                && nea_new.getWikidataId().compareTo(wikidataId)==0;
    } 
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + storyId.hashCode();
        result = 31 * result + itemId.hashCode();
        result = 31 * result + wikidataId.hashCode();
        return result;
    }


}
