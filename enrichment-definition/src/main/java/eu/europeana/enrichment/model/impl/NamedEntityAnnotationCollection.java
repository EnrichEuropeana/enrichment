package eu.europeana.enrichment.model.impl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import static eu.europeana.enrichment.model.vocabulary.EntitySerializationConstants.ANNOTATION_CONTEXT;
import static eu.europeana.enrichment.model.vocabulary.EntitySerializationConstants.CONTEXT_FIELD;

@JsonPropertyOrder({ 
	CONTEXT_FIELD, 
	"id", 
	"type", 
	"creator",
	"total",
	"items"
})
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class NamedEntityAnnotationCollection {

	List<NamedEntityAnnotationImpl> items;
//	private String context = "http://www.w3.org/ns/anno.jsonld";
	private String idBase = "http://dsi-demo.ait.ac.at/enrichment-web/enrichment/annotation/";
	private String id;
	private String type = "AnnotationCollection";
	private String creator = "https://pro.europeana.eu/project/enrich-europeana";
	private String total; 

	public NamedEntityAnnotationCollection(List<NamedEntityAnnotationImpl> itemsParam, String storyId, String itemId) {
		
		items = itemsParam;
		total = String.valueOf(items.size());
		if(itemId.compareTo("all")==0)
		{
			id = idBase + storyId;
		}
		else
		{
			id = idBase + storyId + "/" + itemId;
		}
		
		
	}
	
	@JsonProperty("items")
	public List<NamedEntityAnnotationImpl> getItems() {
		return items;
	}
	
	public void setItems(List<NamedEntityAnnotationImpl> items) {
		this.items = items;
	}
	
//	@JsonProperty("@context")
//	public String getContext() {
//		return context;
//	}
//	
//	public void setContext(String context) {
//		this.context = context;
//	}
	
	@JsonProperty("id")
	public String getId() {
		return id;
	}
	
	public void setId(String storyId) {
		this.id = idBase + storyId;
	}
	
	@JsonProperty("type")
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	@JsonProperty("creator")
	public String getCreator() {
		return creator;
	}
	
	public void setCreator(String creator) {
		this.creator = creator;
	}

	@JsonProperty("total")
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
