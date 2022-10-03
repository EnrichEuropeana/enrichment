package eu.europeana.enrichment.model.impl;

import static eu.europeana.enrichment.model.vocabulary.EntitySerializationConstants.CONTEXT_FIELD;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import eu.europeana.enrichment.model.WikidataAgent;

@JsonPropertyOrder({ 
	CONTEXT_FIELD,
	"id",
	"type",
	"prefLabel",
	"altLabel",
	"country",
	"dateOfBirth",
	"dateOfDeath",
	"professionOrOccupation",
	"description",
	"depiction",
	"modified",
	"sameAs"
})
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class WikidataAgentImpl extends WikidataEntityImpl implements WikidataAgent {

	private String country;
	private String[] dateOfBirth;
	private String[] dateOfDeath;
	private String[] professionOrOccupation;

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
