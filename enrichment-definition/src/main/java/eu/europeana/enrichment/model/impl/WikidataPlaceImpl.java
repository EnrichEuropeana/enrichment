package eu.europeana.enrichment.model.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.WikidataPlace;

//@JsonPropertyOrder({ "id", "type", "description", "depiction","country", "logo","latitude","longitude","prefLabel","altLabel","modificationDate","sameAs"})
@JsonPropertyOrder({ 
	EnrichmentConstants.CONTEXT_FIELD,
	"id",
	"type",
	"prefLabel",
	"altLabel",
	"country",
	"logo",
	"latitude",
	"longitude",
	"description",
	"depiction",
	"modified",
	"sameAs"
})
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class WikidataPlaceImpl extends WikidataEntityImpl implements WikidataPlace {

	private String country;
	private String logo;
	private Float latitude;
	private Float longitude;

	@Override
	@JsonProperty("country")
	public String getCountry() {
		
		return country;
	}

	@Override
	public void setCountry(String setCountry) {
		country = setCountry;
		
	}

	@Override
	@JsonProperty("logo")
	public String getLogo() {
		return logo;
	}

	@Override
	public void setLogo(String setLogo) {
		logo = setLogo;
		
	}

	@Override
	@JsonProperty("latitude")
	public Float getLatitude() {
		return latitude;
	}

	@Override
	public void setLatitude(Float setLatitude) {
		latitude = setLatitude;
	}

	@Override
	@JsonProperty("longitude")
	public Float getLongitude() {
		return longitude;
	}

	@Override
	public void setLongitude(Float setLongitude) {
		longitude = setLongitude;
		
	}
	
}
