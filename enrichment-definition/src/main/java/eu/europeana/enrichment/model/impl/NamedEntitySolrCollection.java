package eu.europeana.enrichment.model.impl;

import static eu.europeana.enrichment.model.vocabulary.EntitySerializationConstants.CONTEXT_FIELD;
import static eu.europeana.enrichment.model.vocabulary.EntitySerializationConstants.WIKIDATA_CONTEXT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.model.vocabulary.EnrichmentFields;

@JsonPropertyOrder({ 
	CONTEXT_FIELD, 
	EnrichmentFields.ID, 
	EnrichmentFields.TYPE, 
	EnrichmentFields.TOTAL,
	EnrichmentFields.PART_OF, 
	EnrichmentFields.ITEMS
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
	
	@JsonProperty(EnrichmentFields.ITEMS)
	public List<WikidataEntity> getItems() {
		return items;
	}
	
	public void setItems(List<WikidataEntity> items) {
		this.items = items;
	}
	
	@JsonProperty(EnrichmentFields.PART_OF)
	public Map<String,String> getPartOf() {
		return partOf;
	}
	
	public void setPartOf(Map<String,String> partOf) {
		this.partOf = partOf;
	}
	
	
	@JsonProperty(EnrichmentFields.ID)
	public String getId() {
		return id;
	}
	
	public void setId(String url) {
		this.id = url;
	}
	
	@JsonProperty(EnrichmentFields.TYPE)
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
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
		return WIKIDATA_CONTEXT;
	}
	
}
