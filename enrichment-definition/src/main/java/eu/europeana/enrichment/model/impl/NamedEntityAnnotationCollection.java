package eu.europeana.enrichment.model.impl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.NamedEntityAnnotation;

/**
 * This class is used for the annotations serialization.
 * @author StevaneticS
 *
 */
@JsonPropertyOrder({ 
	EnrichmentConstants.CONTEXT_FIELD, 
	EnrichmentConstants.ID, 
	EnrichmentConstants.TYPE, 
	EnrichmentConstants.CREATOR,
	EnrichmentConstants.TOTAL,
	EnrichmentConstants.ITEMS
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
	
	@JsonProperty(EnrichmentConstants.ITEMS)
	public List<NamedEntityAnnotation> getItems() {
		return items;
	}
	
	public void setItems(List<NamedEntityAnnotation> items) {
		this.items = items;
	}
	
	public String getId() {
		return id;
	}
	
	@JsonProperty(EnrichmentConstants.ID)
	public String getIdSerialization() {
		return id;
	}
	
	@JsonProperty(EnrichmentConstants.TYPE)
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	@JsonProperty(EnrichmentConstants.CREATOR)
	public String getCreator() {
		return creator;
	}

	@JsonProperty(EnrichmentConstants.TOTAL)
	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}
	
	@JsonProperty(EnrichmentConstants.CONTEXT_FIELD)
	public String getContext() {
		return EnrichmentConstants.ANNOTATION_CONTEXT;
	}	
}
