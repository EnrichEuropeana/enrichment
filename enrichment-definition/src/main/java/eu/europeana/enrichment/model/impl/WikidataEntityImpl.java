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
	private String country;
	private Map<String, String> sameAs;
	private String depiction;
	private String modificationDate;


	@Override
	public Map<String, String> getPrefLabel() {
		return prefLabel;
	}

	@Override
	public void setPrefLabel(Map<String, String> prefLabelArg) {
		prefLabel = prefLabelArg;
	}

	@Override
	public Map<String, List<String>> getAltLabel() {
		
		return altLabel;
	}

	@Override
	public void setAltLabel(Map<String, List<String>> altLabelArg) {
		
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
	public Map<String, String> getDescription() {
		
		return description;
	}

	@Override
	public void setDescription(Map<String, String> descriptionArg) {
		description = descriptionArg;
		
	}

	@Override
	public String getCountry() {
		// TODO Auto-generated method stub
		return country;
	}

	@Override
	public void setCountry(String countryArg) {
		country = countryArg;
	}

	@Override
	public Map<String, String> getSameAs() {
		
		return sameAs;
	}

	@Override
	public void setSameAs(Map<String, String> wikidataURLs) {
		sameAs = wikidataURLs;		
	}

	
}
