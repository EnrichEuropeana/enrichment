package eu.europeana.enrichment.model.impl;

import eu.europeana.enrichment.model.WikidataPlace;

public class WikidataPlaceImpl extends WikidataEntityImpl implements WikidataPlace {

	private String country;
	private String logo;
	private String latitude;
	private String longitude;
	
	private String country_jsonProp = "claims.P17.mainsnak.datavalue.value.id";
	
	@Override
	public String getCountry_jsonProp() {
		return country_jsonProp;
	}

	@Override
	public String getLogo_jsonProp() {
		return logo_jsonProp;
	}

	@Override
	public String getLatitude_jsonProp() {
		return latitude_jsonProp;
	}

	@Override
	public String getLongitude_jsonProp() {
		return longitude_jsonProp;
	}

	private String logo_jsonProp = "claims.P154.mainsnak.datavalue.value";
	private String latitude_jsonProp = "claims.P625.mainsnak.datavalue.value.latitude";
	private String longitude_jsonProp = "claims.P625.mainsnak.datavalue.value.longitude";


	@Override
	public String getCountry() {
		
		return country;
	}

	@Override
	public void setCountry(String setCountry) {
		country = setCountry;
		
	}

	@Override
	public String getLogo() {
		return logo;
	}

	@Override
	public void setLogo(String setLogo) {
		logo = setLogo;
		
	}

	@Override
	public String getLatitude() {
		return latitude;
	}

	@Override
	public void setLatitude(String setLatitude) {
		latitude = setLatitude;
	}

	@Override
	public String getLongitude() {
		return longitude;
	}

	@Override
	public void setLongitude(String setLongitude) {
		longitude = setLongitude;
		
	}

}
