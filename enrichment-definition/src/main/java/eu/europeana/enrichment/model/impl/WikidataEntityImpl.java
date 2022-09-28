package eu.europeana.enrichment.model.impl;

import static eu.europeana.enrichment.model.vocabulary.EntitySerializationConstants.CONTEXT_FIELD;
import static eu.europeana.enrichment.model.vocabulary.EntitySerializationConstants.WIKIDATA_CONTEXT;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.model.vocabulary.WikidataEntitySolrDenormalizationFields;

@JsonPropertyOrder({ CONTEXT_FIELD,"id","type","prefLabel","altLabel","description","depiction","modified","sameAs"})
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

	@Override
	@JsonIgnore
	public Map<String, List<String>> getPrefLabel() {
		return prefLabel;
	}
	
	@JsonProperty("prefLabel")
	public Map<String, String> getEntityPrefLabel() {
		if(this.getPrefLabel()==null) return null;
		Map<String, String> normalizedPrefLabel = HelperFunctions.normalizeToStringMap(WikidataEntitySolrDenormalizationFields.PREF_LABEL_DENORMALIZED, this.getPrefLabel());
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
		if(this.getAltLabel()==null) return null;
		Map<String, List<String>> normalizedAltLabel = HelperFunctions.normalizeStringListMap(WikidataEntitySolrDenormalizationFields.ALT_LABEL_DENORMALIZED, this.getAltLabel());
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
		if(this.getDescription()==null) return null;
		Map<String, List<String>> normalizedDescription = HelperFunctions.normalizeStringListMap(WikidataEntitySolrDenormalizationFields.DC_DESCRIPTION_DENORMALIZED, this.getDescription());
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

	@JsonProperty(CONTEXT_FIELD)
	public String getContext() {
		return WIKIDATA_CONTEXT;
	}
	
}
