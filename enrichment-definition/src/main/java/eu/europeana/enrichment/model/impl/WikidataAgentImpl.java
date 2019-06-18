package eu.europeana.enrichment.model.impl;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import eu.europeana.enrichment.commons.SolrUtils;
import eu.europeana.enrichment.model.WikidataAgent;
import eu.europeana.enrichment.model.vocabulary.WikidataEntitySolrDenormalizationFields;


//@JsonPropertyOrder({ "id", "type", "description", "depiction","country", "dateOfBirth","dateOfDeath","proffessionOrOccupation","prefLabel","altLabel","modificationDate","sameAs"})
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class WikidataAgentImpl extends WikidataEntityImpl implements WikidataAgent {

	private String country;
	private String[] dateOfBirth;
	private String[] dateOfDeath;
	private String[] professionOrOccupation;
	
	private String country_jsonProp = "claims.P27.mainsnak.datavalue.value.id";
	@Override
	@JsonIgnore
	public String getCountry_jsonProp() {
		return country_jsonProp;
	}

	private String dateOfBirth_jsonProp = "claims.P569.mainsnak.datavalue.value.time";
	@Override
	@JsonIgnore
	public String getDateOfBirth_jsonProp() {
		return dateOfBirth_jsonProp;
	}

	private String dateOfDeath_jsonProp = "claims.P570.mainsnak.datavalue.value.time";
	@Override
	@JsonIgnore
	public String getDateOfDeath_jsonProp() {
		return dateOfDeath_jsonProp;
	}

	private String professionOrOccupation_jsonProp = "claims.P106.mainsnak.datavalue.value.id";
	@Override
	@JsonIgnore
	public String getProfessionOrOccupation_jsonProp() {
		return professionOrOccupation_jsonProp;
	}


	@Override
	@JsonProperty("country")
	public String getCountry() {
		// TODO Auto-generated method stub
		return country;
	}

	@Override
	public void setCountry(String countryArg) {
		country = countryArg;
	}

	@Override
	@JsonProperty("dateOfBirth")
	public String[] getDateOfBirth() {
		return dateOfBirth;
	}

	@Override
	public void setDateOfBirth(String[] dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	@Override
	@JsonProperty("dateOfDeath")
	public String[] getDateOfDeath() {
		return dateOfDeath;
	}

	@Override
	public void setDateOfDeath(String[] dateOfDeath) {
		this.dateOfDeath = dateOfDeath;
	}

	@Override
	@JsonProperty("proffessionOrOccupation")
	public String[] getProfessionOrOccupation() {
		return professionOrOccupation;
	}

	@Override
	public void setProfessionOrOccupation(String[] professionOrOccupation) {
		this.professionOrOccupation = professionOrOccupation;
	}

}
