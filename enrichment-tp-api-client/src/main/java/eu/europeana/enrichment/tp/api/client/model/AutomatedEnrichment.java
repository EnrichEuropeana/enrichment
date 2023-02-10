package eu.europeana.enrichment.tp.api.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class AutomatedEnrichment {
	public Integer AutomatedEnrichmentId;
	public String Name;
	public String Type;
	public String ExternalId;
	public String WikiData;
	public Integer ItemId;
	
	public void setAutomatedEnrichmentId(Integer automatedEnrichmentId) {
		AutomatedEnrichmentId = automatedEnrichmentId;
	}
	public void setName(String name) {
		Name = name;
	}
	public void setType(String type) {
		Type = type;
	}
	public void setExternalId(String id) {
		ExternalId = id;
	}
	public void setWikiData(String wikiData) {
		WikiData = wikiData;
	}
	public void setItemId(Integer itemId) {
		ItemId = itemId;
	}
	
	
}
