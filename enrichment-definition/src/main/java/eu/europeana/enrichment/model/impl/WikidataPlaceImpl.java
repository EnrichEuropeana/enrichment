package eu.europeana.enrichment.model.impl;

import static eu.europeana.enrichment.model.vocabulary.EntitySerializationConstants.CONTEXT_FIELD;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import eu.europeana.enrichment.model.WikidataPlace;

//@JsonPropertyOrder({ "id", "type", "description", "depiction","country", "logo","latitude","longitude","prefLabel","altLabel","modificationDate","sameAs"})
@JsonPropertyOrder({ 
	CONTEXT_FIELD,
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

	//this property is the latitude property
	private String identification_jsonProp_identifier = "P625";
	private String identification_jsonProp = "claims."+ identification_jsonProp_identifier+".mainsnak.datavalue.value.latitude";

	/**
	 * The identification property if the latitude from the coordinate location in this case
	 */
	@Override
	@JsonIgnore
	public String getIdentification_jsonProp() {
		return identification_jsonProp;
	}
	
	@Override
	@JsonIgnore
	public String getIdentification_jsonProp_identifier() {
		return identification_jsonProp_identifier;
	}
	
	private String country_jsonProp = "claims.P17.mainsnak.datavalue.value.id";
	
	@Override
	@JsonIgnore
	public String getCountry_jsonProp() {
		return country_jsonProp;
	}

	@Override
	@JsonIgnore
	public String getLogo_jsonProp() {
		return logo_jsonProp;
	}

	@Override
	@JsonIgnore
	public String getLatitude_jsonProp() {
		return latitude_jsonProp;
	}

	@Override
	@JsonIgnore
	public String getLongitude_jsonProp() {
		return longitude_jsonProp;
	}

	private String logo_jsonProp = "claims.P154.mainsnak.datavalue.value";
	private String latitude_jsonProp = "claims.P625.mainsnak.datavalue.value.latitude";
	private String longitude_jsonProp = "claims.P625.mainsnak.datavalue.value.longitude";

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
