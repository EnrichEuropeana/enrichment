package eu.europeana.enrichment.model.impl;

import static eu.europeana.enrichment.model.vocabulary.EntitySerializationConstants.ANNOTATION_CONTEXT;
import static eu.europeana.enrichment.model.vocabulary.EntitySerializationConstants.CONTEXT_FIELD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexes;
import dev.morphia.annotations.Property;
import eu.europeana.enrichment.model.NamedEntityAnnotation;
import eu.europeana.enrichment.model.vocabulary.EntityFields;

@Entity(value="NamedEntityAnnotationImpl")
@JsonPropertyOrder({ 
	CONTEXT_FIELD,
	EntityFields.ID, 
	EntityFields.TYPE, 
	EntityFields.MOTIVATION,
	EntityFields.BODY,
	EntityFields.TARGET
})
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@Indexes(@Index(fields = { @Field("storyId"), @Field("itemId"), @Field("wikidataId") }, options = @IndexOptions(unique = true)))
public class NamedEntityAnnotationImpl implements NamedEntityAnnotation {

	@JsonIgnore
	private static String idBaseUrl;
	@JsonIgnore
	private static String targetItemsBaseUrl;
	
	private String annoId;
	private SpecificResource target;
	private String type;
	private String motivation;
		
	@Property(EntityFields.PROPERTY)
	private String property;
	
	private String entityType;	
	private Map<String,Object> body;
	
	@Property(EntityFields.WIKIDATA_ID)
	private String wikidataId;
	
	@Property(EntityFields.STORY_ID)
	private String storyId;
	
	@Property(EntityFields.ITEM_ID)
	private String itemId;
	
	@Property(EntityFields.PROCESSING)
	private Processing processing;

	//id will be used for storing MongoDB _id
	@Id
	@JsonIgnore
    private ObjectId _id;

	@JsonIgnore
	public ObjectId getId() {
		return _id;
	}
	
	@JsonIgnore
	public String getWikidataId() {
		return wikidataId;
	}

	@JsonIgnore
	public String getStoryId() {
		return storyId;
	}
	
	@JsonIgnore
	public String getItemId() {
		return itemId;
	}
	
	public NamedEntityAnnotationImpl () {
	}
	
	public NamedEntityAnnotationImpl (String idBaseUrlPar, String targetItemsBaseUrlPar, String storyId, String itemId, String wikidataId, String entityHiddenLabel, String entityPrefLabel, String prop, String entityTypeParam,
			double score, List<String> nerTools) {

		idBaseUrl=idBaseUrlPar;
		targetItemsBaseUrl=targetItemsBaseUrlPar;
		target = new SpecificResource();
		if(itemId!=null)
		{
			target.setId("story="+storyId+"&item="+itemId);	
			target.setSource(target.getId() + "#" + prop);
			this.itemId = itemId;
			this.annoId = storyId + "/" + itemId + "/" + wikidataId.substring(wikidataId.lastIndexOf("/")+1);
		}
		else
		{
			target.setId("story="+storyId);
			target.setSource(target.getId() + "#" + prop);
			this.annoId = storyId + "/" + wikidataId.substring(wikidataId.lastIndexOf("/")+1);
		}
		this.type = "Annotation";
		this.motivation = "tagging";
		this.body = new HashMap<String, Object> ();
		this.body.put("id", wikidataId);
		if(entityTypeParam.equalsIgnoreCase("agent"))
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
		this.property = prop;
		
		Processing processing = new Processing();
		processing.setScore(score);
		processing.setFoundByNerTools(new ArrayList<String>(nerTools));
		this.processing=processing;
	}

	@JsonProperty(EntityFields.TARGET)
	public SpecificResource getTarget() {
		return target;
	}
	
	public void setTarget(SpecificResource targetParam) {
		target = targetParam;
	}

	@JsonProperty(EntityFields.ID)
	public String getAnnoId() {		
		return annoId;
	}
	
	public void setAnnoId(String idParam) {
		annoId = idParam; 
		
	}

	@JsonProperty(EntityFields.TYPE)
	public String getType() {	
		return type;
	}

	public void setType(String typeParam) {
		type = typeParam;		
	}

	@JsonProperty(EntityFields.MOTIVATION)
	public String getMotivation() {
		return motivation;
	}

	public void setMotivation(String motivationParam) {
		motivation = motivationParam;
	}

	@JsonProperty(EntityFields.BODY)
	public Map<String,Object> getBody() {	
		return body;
	}

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
        return this.hashCode()==nea_new.hashCode();
//        		nea_new.getStoryId().compareTo(storyId)==0
//                && nea_new.getItemId().compareTo(itemId)==0
//                && nea_new.getWikidataId().compareTo(wikidataId)==0
//                && nea_new.getProperty().compareTo(property)==0;
    } 
    
    @Override
    public int hashCode() {
        int result = 17;
        if(storyId!=null) result = 31 * result + storyId.hashCode();
        if(itemId!=null) result = 31 * result + itemId.hashCode();
        if(wikidataId!=null) result = 31 * result + wikidataId.hashCode();
        if(property!=null) result = 31 * result + property.hashCode();
        return result;
    }


	@JsonIgnore
	public String getProperty() {
		return property;
	}

	public void setProperty(String prop) {
		this.property = prop;
	}

	@JsonIgnore
	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String type) {
		this.entityType = type;
	}

	@JsonProperty(CONTEXT_FIELD)
	public String getContext() {
		return ANNOTATION_CONTEXT;
	}
	
	@JsonProperty(EntityFields.PROCESSING)
	public Processing getProcessing() {
		return processing;
	}

	public void setProcessing(Processing processing) {
		this.processing = processing;
	}
}
