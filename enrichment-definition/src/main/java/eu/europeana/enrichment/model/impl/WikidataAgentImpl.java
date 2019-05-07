package eu.europeana.enrichment.model.impl;

import eu.europeana.enrichment.model.WikidataAgent;

public class WikidataAgentImpl extends WikidataEntityImpl implements WikidataAgent {

	private String country;
	private String dateOfBirth;
	private String dateOfDeath;
	private String professionOrOccupation;
	
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
	public String getDateOfBirth() {
		return dateOfBirth;
	}

	@Override
	public void setDateOfBirth(String setDateOfBirth) {
		dateOfBirth = setDateOfBirth;
		
	}

	@Override
	public String getDateOfDeath() {
		return dateOfDeath;
	}

	@Override
	public void setDateOfDeath(String setDateOfDeath) {
		dateOfDeath = setDateOfDeath;		
	}

	@Override
	public String getOccupation() {
		
		return professionOrOccupation;
	}

	@Override
	public void setOccupation(String setOccupation) {
		professionOrOccupation = setOccupation;
		
	}

}