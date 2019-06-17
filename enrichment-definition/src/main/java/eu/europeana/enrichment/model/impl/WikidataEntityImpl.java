package eu.europeana.enrichment.model.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import eu.europeana.enrichment.model.WikidataEntity;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class WikidataEntityImpl implements WikidataEntity {
	
	protected String internalType;
	protected String entityId;
	protected Map<String, String> prefLabel;
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
	public Map<String, String> getPrefLabelStringMap() {
		return prefLabel;
	}

	@Override
	public void setPrefLabelStringMap(Map<String, String> prefLabel) {
		this.prefLabel = prefLabel;
	}

	@Override
	public Map<String, List<String>> getAltLabel() {
		return altLabel;
	}

	@Override
	public void setAltLabel(Map<String, List<String>> altLab) {
		this.altLabel = altLab;
	}

	@Override
	public String getEntityId() {
		return entityId;
	}

	@Override
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	@Override
	public String getInternalType() {
		return internalType;
	}

	@Override
	public void setInternalType(String internalType) {
		this.internalType = internalType;
	}

	@Override
	public String getModificationDate() {
		
		return modificationDate;
	}

	@Override
	public void setModificationDate(String date) {
		modificationDate = date;
		
	}

	@Override
	public String getDepiction() {
		return depiction;
	}
	
	@Override
	public void setDepiction(String depiction) {
		this.depiction = depiction;
	}

	@Override
	public Map<String, List<String>> getDescription() {
		return description;
	}

	@Override
	public void setDescription(Map<String, List<String>> desc) {
	    	this.description = desc;
	}

	@Override
	public String[] getSameAs() {
		return sameAs;
	}

	@Override
	public void setSameAs(String[] sameAs) {
		this.sameAs = sameAs;
	}

	
}
