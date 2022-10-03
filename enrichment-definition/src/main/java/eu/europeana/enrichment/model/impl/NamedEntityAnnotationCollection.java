package eu.europeana.enrichment.model.impl;

import static eu.europeana.enrichment.model.vocabulary.EntitySerializationConstants.ANNOTATION_CONTEXT;
import static eu.europeana.enrichment.model.vocabulary.EntitySerializationConstants.CONTEXT_FIELD;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import eu.europeana.enrichment.model.NamedEntityAnnotation;
import eu.europeana.enrichment.model.vocabulary.EntityFields;

@JsonPropertyOrder({ 
	CONTEXT_FIELD, 
	EntityFields.ID, 
	EntityFields.TYPE, 
	EntityFields.CREATOR,
	EntityFields.TOTAL,
	EntityFields.ITEMS
})
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class NamedEntityAnnotationCollection {

	private static String idBaseUrl;
	private static String creator;

	List<NamedEntityAnnotation> items;
//	private String context = "http://www.w3.org/ns/anno.jsonld";
	private String id;
	private String type = "AnnotationCollection";
	private String total; 

	public NamedEntityAnnotationCollection(String idBaseUrlPar, String creatorPar, List<NamedEntityAnnotation> itemsParam, String storyId, String itemId) {
		
		idBaseUrl=idBaseUrlPar;
		creator=creatorPar;
		items = itemsParam;
		total = String.valueOf(items.size());
		if(itemId==null)
		{
			id = storyId;
		}
		else
		{
			id = storyId + "/" + itemId;
		}
		
		
	}
	
	@JsonProperty(EntityFields.ITEMS)
	public List<NamedEntityAnnotation> getItems() {
		return items;
	}
	
	public void setItems(List<NamedEntityAnnotation> items) {
		this.items = items;
	}
	
	public String getId() {
		return id;
	}
	
	@JsonProperty(EntityFields.ID)
	public String getIdSerialization() {
		return idBaseUrl + id;
	}
	
	@JsonProperty(EntityFields.TYPE)
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	@JsonProperty(EntityFields.CREATOR)
	public String getCreator() {
		return creator;
	}

	@JsonProperty(EntityFields.TOTAL)
	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}
	
	@JsonProperty(CONTEXT_FIELD)
	public String getContext() {
		return ANNOTATION_CONTEXT;
	}	
}
