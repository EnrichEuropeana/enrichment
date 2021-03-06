package eu.europeana.enrichment.model.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import eu.europeana.enrichment.model.WikidataEntity;

@JsonPropertyOrder({ "@context", "id", "type", "total","partOf", "items"})
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
	
	@JsonProperty("items")
	public List<WikidataEntity> getItems() {
		return items;
	}
	
	public void setItems(List<WikidataEntity> items) {
		this.items = items;
	}
	
	@JsonProperty("partOf")
	public Map<String,String> getPartOf() {
		return partOf;
	}
	
	public void setPartOf(Map<String,String> partOf) {
		this.partOf = partOf;
	}
	
	
	@JsonProperty("id")
	public String getId() {
		return id;
	}
	
	public void setId(String url) {
		this.id = url;
	}
	
	@JsonProperty("type")
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("total")
	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}
	
	
}
