package eu.europeana.enrichment.model.impl;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import eu.europeana.enrichment.common.commons.SolrUtils;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.model.vocabulary.WikidataEntitySolrDenormalizationFields;

@JsonPropertyOrder({ "entityId","type","prefLabel","altLabel","description","depiction","modified","sameAs"})
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class WikidataEntityImpl implements WikidataEntity {
	
	protected String internalType;
	protected String entityId;
	protected Map<String, List<String>> prefLabel;
	protected Map<String, List<String>> altLabel;
	protected Map<String, List<String>> description;	
	protected String [] sameAs;
	protected String depiction;
	protected String modificationDate;
	
	protected String modificationDate_jsonProp = "modified";
	
	@Override
	@JsonIgnore
	public String getModificationDate_jsonProp() {
		return modificationDate_jsonProp;
	}

	@Override
	@JsonIgnore
	public String getPrefLabel_jsonProp() {
		return prefLabel_jsonProp;
	}

	@Override
	@JsonIgnore
	public String getAltLabel_jsonProp() {
		return altLabel_jsonProp;
	}

	@Override
	@JsonIgnore
	public String getDepiction_jsonProp() {
		return depiction_jsonProp;
	}

	@Override
	@JsonIgnore
	public String getDescription_jsonProp() {
		return description_jsonProp;
	}

	@Override
	@JsonIgnore
	public String getSameAs_jsonProp() {
		return sameAs_jsonProp;
	}

	protected String prefLabel_jsonProp = "labels.*.*";
	protected String altLabel_jsonProp = "aliases.*.*";
	protected String depiction_jsonProp = "claims.P18.mainsnak.datavalue.value";
	protected String description_jsonProp = "descriptions.*.*";
	protected String sameAs_jsonProp = "sitelinks.*.url";
	
	@Override
	@JsonIgnore
	public Map<String, List<String>> getPrefLabel() {
		return prefLabel;
	}
	
	@JsonProperty("prefLabel")
	public Map<String, String> getEntityPrefLabel() {
		Map<String, String> normalizedPrefLabel = SolrUtils.normalizeToStringMap(WikidataEntitySolrDenormalizationFields.PREF_LABEL_DENORMALIZED, this.getPrefLabel());
		return normalizedPrefLabel;
	}


	@Override
	public void setPrefLabel(Map<String, List<String>> prefLabel) {
		this.prefLabel = prefLabel;
	}

	@Override
	@JsonIgnore
	public Map<String, List<String>> getAltLabel() {
		return altLabel;
	}
	
	@JsonProperty("altLabel")
	public Map<String, List<String>> getEntityAltLabel() {
		Map<String, List<String>> normalizedAltLabel = SolrUtils.normalizeStringListMap(WikidataEntitySolrDenormalizationFields.ALT_LABEL_DENORMALIZED, this.getAltLabel());
		return normalizedAltLabel;

	}


	@Override
	public void setAltLabel(Map<String, List<String>> altLab) {
		this.altLabel = altLab;
	}

	@Override
	@JsonProperty("id")
	public String getEntityId() {
		return entityId;
	}

	@Override
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	@Override
	@JsonProperty("type")
	public String getInternalType() {
		return internalType;
	}

	@Override
	public void setInternalType(String internalType) {
		this.internalType = internalType;
	}

	@Override
	@JsonProperty("modified")
	public String getModificationDate() {
		
		return modificationDate;
	}

	@Override
	public void setModificationDate(String date) {
		modificationDate = date;
		
	}

	@Override
	@JsonProperty("depiction")
	public String getDepiction() {
		return depiction;
	}
	
	@Override
	public void setDepiction(String depiction) {
		this.depiction = depiction;
	}

	@Override
	@JsonIgnore
	public Map<String, List<String>> getDescription() {
		return description;
	}

	@JsonProperty("description")
	public Map<String, List<String>> getEntityDescription() {
		Map<String, List<String>> normalizedDescription = SolrUtils.normalizeStringListMap(WikidataEntitySolrDenormalizationFields.DC_DESCRIPTION_DENORMALIZED, this.getDescription());
		return normalizedDescription;

	}

	
	@Override
	public void setDescription(Map<String, List<String>> desc) {
	    	this.description = desc;
	}

	@Override
	@JsonProperty("sameAs")
	public String[] getSameAs() {
		return sameAs;
	}

	@Override
	public void setSameAs(String[] sameAs) {
		this.sameAs = sameAs;
	}

	/**
	 * This method returns the property specific for the given type of the wikidata entity
	 * that helps in recognizing it from other entities.
	 */
	@Override
	public String getIdentification_jsonProp() {
		// TODO Auto-generated method stub
		return null;
	}


	
}
