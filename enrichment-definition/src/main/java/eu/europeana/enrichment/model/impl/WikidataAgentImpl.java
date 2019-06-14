package eu.europeana.enrichment.model.impl;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import eu.europeana.enrichment.model.WikidataAgent;
import ioinformarics.oss.jackson.module.jsonld.annotation.JsonldProperty;


@JsonPropertyOrder({ "id", "type", "description", "depiction","country", "dateOfBirth","dateOfDeath","proffessionOrOccupation","prefLabel","altLabel","modificationDate","sameAs"})
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class WikidataAgentImpl extends WikidataEntityImpl implements WikidataAgent {

	private String country;
	private String[] dateOfBirth;
	private String[] dateOfDeath;
	private String[] professionOrOccupation;
	
	private String country_jsonProp = "claims.P27.mainsnak.datavalue.value.id";
	@Override
	public String getCountry_jsonProp() {
		return country_jsonProp;
	}

	private String dateOfBirth_jsonProp = "claims.P569.mainsnak.datavalue.value.time";
	@Override
	public String getDateOfBirth_jsonProp() {
		return dateOfBirth_jsonProp;
	}

	private String dateOfDeath_jsonProp = "claims.P570.mainsnak.datavalue.value.time";
	@Override
	public String getDateOfDeath_jsonProp() {
		return dateOfDeath_jsonProp;
	}

	private String professionOrOccupation_jsonProp = "claims.P106.mainsnak.datavalue.value.id";
	@Override
	public String getProfessionOrOccupation_jsonProp() {
		return professionOrOccupation_jsonProp;
	}


	@Override
	@JsonldProperty("country")
	public String getCountry() {
		// TODO Auto-generated method stub
		return country;
	}

	@Override
	public void setCountry(String countryArg) {
		country = countryArg;
	}

	@Override
	@JsonldProperty("dateOfBirth")
	public String[] getDateOfBirth() {
		return dateOfBirth;
	}

	@Override
	public void setDateOfBirth(String[] dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	@Override
	@JsonldProperty("dateOfDeath")
	public String[] getDateOfDeath() {
		return dateOfDeath;
	}

	@Override
	public void setDateOfDeath(String[] dateOfDeath) {
		this.dateOfDeath = dateOfDeath;
	}

	@Override
	@JsonldProperty("proffessionOrOccupation")
	public String[] getProfessionOrOccupation() {
		return professionOrOccupation;
	}

	@Override
	public void setProfessionOrOccupation(String[] professionOrOccupation) {
		this.professionOrOccupation = professionOrOccupation;
	}
		
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
	
	@Override
	@JsonldProperty("prefLabel")
	public Map<String, String> getPrefLabelStringMap() {
		return prefLabel;
	}

	@Override
	public void setPrefLabelStringMap(Map<String, String> prefLabel) {
		this.prefLabel = prefLabel;
	}

	@Override
	@JsonldProperty("altLabel")
	public Map<String, List<String>> getAltLabel() {
		return altLabel;
	}

	@Override
	public void setAltLabel(Map<String, List<String>> altLab) {
		this.altLabel = altLab;
	}

	@Override
	@JsonldProperty("id")
	public String getEntityId() {
		return entityId;
	}

	@Override
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	@Override
	@JsonldProperty("type")
	public String getInternalType() {
		return internalType;
	}

	@Override
	public void setInternalType(String internalType) {
		this.internalType = internalType;
	}

	@Override
	@JsonldProperty("modificationDate")
	public String getModificationDate() {
		
		return modificationDate;
	}

	@Override
	public void setModificationDate(String date) {
		modificationDate = date;
		
	}

	@Override
	@JsonldProperty("depiction")
	public String getDepiction() {
		return depiction;
	}
	
	@Override
	public void setDepiction(String depiction) {
		this.depiction = depiction;
	}

	@Override
	@JsonldProperty("description")
	public Map<String, String> getDescription() {
		return description;
	}

	@Override
	public void setDescription(Map<String, String> desc) {
	    	this.description = desc;
	}

	@Override
	@JsonldProperty("sameAs")
	public String[] getSameAs() {
		return sameAs;
	}

	@Override
	public void setSameAs(String[] sameAs) {
		this.sameAs = sameAs;
	}



}
