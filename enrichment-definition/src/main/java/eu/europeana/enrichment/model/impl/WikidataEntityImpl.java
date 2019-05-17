package eu.europeana.enrichment.model.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import eu.europeana.enrichment.model.WikidataEntity;

public class WikidataEntityImpl implements WikidataEntity {
	
	private String internalType;
	private String entityId;
	private Map<String, String> prefLabel;
	private Map<String, List<String>> altLabel;
	private Map<String, String> description;	
	private String [] sameAs;
	private String depiction;
	private String modificationDate;
	
	private String modificationDate_jsonProp = "modified";
	
	@Override
	public String getModificationDate_jsonProp() {
		return modificationDate_jsonProp;
	}

	@Override
	public String getPrefLabel_jsonProp() {
		return prefLabel_jsonProp;
	}

	@Override
	public String getAltLabel_jsonProp() {
		return altLabel_jsonProp;
	}

	@Override
	public String getDepiction_jsonProp() {
		return depiction_jsonProp;
	}

	@Override
	public String getDescription_jsonProp() {
		return description_jsonProp;
	}

	@Override
	public String getSameAs_jsonProp() {
		return sameAs_jsonProp;
	}

	private String prefLabel_jsonProp = "labels.*.*";
	private String altLabel_jsonProp = "aliases.*.*";
	private String depiction_jsonProp = "claims.P18.mainsnak.datavalue.value";
	private String description_jsonProp = "descriptions.*.*";
	private String sameAs_jsonProp = "sitelinks.*.url";
	
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
	public Map<String, String> getDescription() {
		return description;
	}

	@Override
	public void setDescription(Map<String, String> desc) {
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
