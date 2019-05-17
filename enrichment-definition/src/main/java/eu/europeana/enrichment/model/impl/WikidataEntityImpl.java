package eu.europeana.enrichment.model.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import eu.europeana.enrichment.model.WikidataEntity;

public class WikidataEntityImpl implements WikidataEntity {
	
	private String internalType;
	private String entityId;
	private List<List<String>> prefLabel;
	private List<List<String>> altLabel;
	private List<List<String>> description;	
	private List<List<String>> sameAs;
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
	public List<List<String>> getPrefLabel() {
		return prefLabel;
	}

	@Override
	public void setPrefLabel(List<List<String>> prefLabelArg) {
		prefLabel = prefLabelArg;
	}

	@Override
	public List<List<String>> getAltLabel() {
		
		return altLabel;
	}

	@Override
	public void setAltLabel(List<List<String>> altLabelArg) {
		
		altLabel = altLabelArg;
		
	}

	@Override
	public String getEntityId() {
		
		return entityId;
	}

	@Override
	public void setEntityId(String enitityIdArg) {
		entityId = enitityIdArg;		
	}

	@Override
	public String getInternalType() {
		
		return internalType;
	}

	@Override
	public void setInternalType(String entityType) {
		internalType = entityType;
		
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
	public void setDepiction(String depictionArg) {
		depiction = depictionArg;
		
	}

	@Override
	public List<List<String>> getDescription() {
		
		return description;
	}

	@Override
	public void setDescription(List<List<String>> descriptionArg) {
		description = descriptionArg;
		
	}

	@Override
	public List<List<String>> getSameAs() {
		
		return sameAs;
	}

	@Override
	public void setSameAs(List<List<String>> wikidataURLs) {
		sameAs = wikidataURLs;		
	}

	
}
