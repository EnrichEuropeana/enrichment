package eu.europeana.enrichment.definitions.model.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.definitions.model.WikidataEntity;

@JsonPropertyOrder({ 
	EnrichmentConstants.CONTEXT_FIELD, 
	EnrichmentConstants.ID, 
	EnrichmentConstants.TYPE, 
	EnrichmentConstants.TOTAL,
	EnrichmentConstants.PART_OF, 
	EnrichmentConstants.ITEMS
})
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class NamedEntitySolrCollection {

	List<WikidataEntity> items;
	private String id;
	private String type = "ResultPage";
	private Map<String,String> partOf; 
	private String total; 

	public NamedEntitySolrCollection(List<WikidataEntity> itemsParam, String url, String urlWithoutPageInfo, int totalPage, int totalAll) {
		
		partOf = new HashMap<String, String>();
		partOf.put("id", urlWithoutPageInfo);
		partOf.put("type", "ResultList");
		partOf.put("total", String.valueOf(totalAll));
		items = itemsParam;
		id = url;
		total = String.valueOf(totalPage);			
	}
	
	@JsonProperty(EnrichmentConstants.ITEMS)
	public List<WikidataEntity> getItems() {
		return items;
	}
	
	public void setItems(List<WikidataEntity> items) {
		this.items = items;
	}
	
	@JsonProperty(EnrichmentConstants.PART_OF)
	public Map<String,String> getPartOf() {
		return partOf;
	}
	
	public void setPartOf(Map<String,String> partOf) {
		this.partOf = partOf;
	}
	
	
	@JsonProperty(EnrichmentConstants.ID)
	public String getId() {
		return id;
	}
	
	public void setId(String url) {
		this.id = url;
	}
	
	@JsonProperty(EnrichmentConstants.TYPE)
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
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
		return EnrichmentConstants.WIKIDATA_CONTEXT;
	}
	
}
