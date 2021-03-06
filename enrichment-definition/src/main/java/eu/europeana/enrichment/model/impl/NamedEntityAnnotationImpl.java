package eu.europeana.enrichment.model.impl;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import eu.europeana.enrichment.model.NamedEntityAnnotation;

@JsonPropertyOrder({ "id", "type", "motivation","body","target"})
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class NamedEntityAnnotationImpl implements NamedEntityAnnotation {

	private final String idBase = "http://dsi-demo.ait.ac.at/enrichment-web/enrichment/annotation/";
	private final String targetBaseItems = "https://europeana.transcribathon.eu/documents/story/item/?";
	private final String targetBaseStories = "https://europeana.transcribathon.eu/documents/story/?";
	
	private String annoId;
	private String source;
	private String target;
	private String type;
	private String motivation;
	private String property;
	private String entityType;
	
	private Map<String,Object> body;

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
		body = new HashMap<String, Object>();
		wikidataId = "";
		storyId = "";
		itemId = "";
		property = "";
		entityType= "";
	}
	
	public NamedEntityAnnotationImpl (NamedEntityAnnotation entity) {
		this.source = entity.getWikidataId();
		if(entity.getItemId().compareToIgnoreCase("all")!=0)
		{
			this.target = targetBaseItems + "story="+entity.getStoryId()+"&item="+entity.getItemId();
		}
		else
		{
			this.target = targetBaseStories + "story="+entity.getStoryId();
		}
		this.annoId = entity.getAnnoId();
		this.type = "Annotation";
		this.motivation = "tagging";
		this.body = new HashMap<String, Object> ();
		this.body.put("id", entity.getWikidataId());
		this.body.put("type", entity.getEntityType());
		Map<String,String> bodyHiddenLabel = new HashMap<String, String>();
		@SuppressWarnings("unchecked")
		Map<String,String> bodyHiddenLableOld = (Map<String,String>)entity.getBody().get("hiddenLabel");
		bodyHiddenLabel.put("en",bodyHiddenLableOld.get("en"));
		this.body.put("hiddenLabel", bodyHiddenLabel);
		
		Map<String,String> bodyPrefLabel = new HashMap<String, String>();
		@SuppressWarnings("unchecked")
		Map<String,String> bodyPrefLableOld = (Map<String,String>)entity.getBody().get("prefLabel");
		bodyPrefLabel.put("en",bodyPrefLableOld.get("en"));
		this.body.put("prefLabel", bodyPrefLabel);
		
		this.wikidataId = entity.getWikidataId();
		this.storyId = entity.getStoryId();
		this.itemId = entity.getItemId();
		this.property = entity.getProperty();
		this.entityType = entity.getEntityType();

	}

	public NamedEntityAnnotationImpl (String storyId, String itemId, String wikidataId, String storyOrItemSource, String entityHiddenLabel, String entityPrefLabel, String prop, String entityTypeParam) {

		this.source = wikidataId;
		if(itemId.compareToIgnoreCase("all")!=0)
		{
			this.target = targetBaseItems + "story="+storyId+"&item="+itemId;	
		}
		else
		{
			this.target = targetBaseStories + "story="+storyId;
		}
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
		this.body = new HashMap<String, Object> ();
		this.body.put("id", wikidataId);
		if(entityTypeParam.compareToIgnoreCase("agent")==0)
		{
			this.entityType = "Person";
			this.body.put("type", "Person");
		}
		else 
		{
			this.entityType = "Place";
			this.body.put("type", "Place");
		}
		Map<String,String> bodyPrefLabel = new HashMap<String, String>();
		bodyPrefLabel.put("en", entityPrefLabel);
		this.body.put("prefLabel", bodyPrefLabel);
		
		Map<String,String> bodyHiddenLabel = new HashMap<String, String>();
		bodyHiddenLabel.put("en", entityHiddenLabel);
		this.body.put("hiddenLabel", bodyHiddenLabel);

		
		this.wikidataId = wikidataId;
		this.storyId = storyId;
		this.itemId = itemId;
		this.property = prop;
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
	public Map<String,Object> getBody() {
		
		return body;
	}

	@Override
	public void setBody(Map<String,Object> bodyParam) {
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
                && nea_new.getWikidataId().compareTo(wikidataId)==0
                && nea_new.getProperty().compareTo(property)==0;
    } 
    
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + storyId.hashCode();
        result = 31 * result + itemId.hashCode();
        result = 31 * result + wikidataId.hashCode();
        result = 31 * result + property.hashCode();
        return result;
    }


	@Override
	@JsonIgnore
	public String getProperty() {
		return property;
	}

	@Override
	public void setProperty(String prop) {
		this.property = prop;
	}
	
	@Override
	@JsonIgnore
	public String getEntityType() {
		return entityType;
	}

	@Override
	public void setEntityType(String type) {
		this.entityType = type;
	}



}
