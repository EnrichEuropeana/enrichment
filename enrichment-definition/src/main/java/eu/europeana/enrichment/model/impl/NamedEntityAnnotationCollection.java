package eu.europeana.enrichment.model.impl;

import static eu.europeana.enrichment.model.vocabulary.EntitySerializationConstants.ANNOTATION_CONTEXT;
import static eu.europeana.enrichment.model.vocabulary.EntitySerializationConstants.CONTEXT_FIELD;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import eu.europeana.enrichment.model.NamedEntityAnnotation;
import eu.europeana.enrichment.model.vocabulary.EnrichmentFields;

/**
 * This class is used for the annotations serialization.
 * @author StevaneticS
 *
 */
@JsonPropertyOrder({ 
	CONTEXT_FIELD, 
	EnrichmentFields.ID, 
	EnrichmentFields.TYPE, 
	EnrichmentFields.CREATOR,
	EnrichmentFields.TOTAL,
	EnrichmentFields.ITEMS
})
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class NamedEntityAnnotationCollection {

	private static String creator;

	List<NamedEntityAnnotation> items;
//	private String context = "http://www.w3.org/ns/anno.jsonld";
	private String id;
	private String type = "AnnotationCollection";
	private String total; 

	public NamedEntityAnnotationCollection(String idBaseUrlPar, String storyBaseUrl, String itemBaseUrl, String creatorPar, List<NamedEntityAnnotation> itemsParam, String storyId, String itemId) {
		
		creator=creatorPar;
		
		for(NamedEntityAnnotation item : itemsParam) {
			item.setAnnoId(idBaseUrlPar + item.getAnnoId());
			if(itemId!=null) {
				item.getTarget().setId(itemBaseUrl + item.getTarget().getId());
				item.getTarget().setSource(itemBaseUrl + item.getTarget().getSource());
			}
			else {
				item.getTarget().setId(storyBaseUrl + item.getTarget().getId());
				item.getTarget().setSource(storyBaseUrl + item.getTarget().getSource());				
			}
		}
		items = itemsParam;
		
		total = String.valueOf(items.size());
		if(itemId==null)
		{
			id = idBaseUrlPar + storyId;
		}
		else
		{
			id = idBaseUrlPar + storyId + "/" + itemId;
		}
		
	}
	
	@JsonProperty(EnrichmentFields.ITEMS)
	public List<NamedEntityAnnotation> getItems() {
		return items;
	}
	
	public void setItems(List<NamedEntityAnnotation> items) {
		this.items = items;
	}
	
	public String getId() {
		return id;
	}
	
	@JsonProperty(EnrichmentFields.ID)
	public String getIdSerialization() {
		return id;
	}
	
	@JsonProperty(EnrichmentFields.TYPE)
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	@JsonProperty(EnrichmentFields.CREATOR)
	public String getCreator() {
		return creator;
	}

	@JsonProperty(EnrichmentFields.TOTAL)
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
