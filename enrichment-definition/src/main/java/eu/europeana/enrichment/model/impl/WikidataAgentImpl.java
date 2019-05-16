package eu.europeana.enrichment.model.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import eu.europeana.enrichment.model.WikidataAgent;

public class WikidataAgentImpl extends WikidataEntityImpl implements WikidataAgent {



	private String country;
	private String[] dateOfBirth;
	private String[] dateOfDeath;
	private Map<String, List<String>> professionOrOccupation;
	
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
	public String getCountry() {
		// TODO Auto-generated method stub
		return country;
	}

	@Override
	public void setCountry(String countryArg) {
		country = countryArg;
	}

	@Override
	public String[] getDateOfBirth() {
		return dateOfBirth;
	}

	@Override
	public void setDateOfBirth(String[] dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	@Override
	public String[] getDateOfDeath() {
		return dateOfDeath;
	}

	@Override
	public void setDateOfDeath(String[] dateOfDeath) {
		this.dateOfDeath = dateOfDeath;
	}

	@Override
	public Map<String, List<String>> getProfessionOrOccupation() {
		return professionOrOccupation;
	}

	@Override
	public void setProfessionOrOccupation(Map<String, List<String>> professionOrOccupation) {
		this.professionOrOccupation = professionOrOccupation;
	}


}
