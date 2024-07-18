package eu.europeana.enrichment.definitions.model.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.definitions.model.WikidataOrganization;

/**
 * The properties are created based on the "properties for this type" field of the 
 * wikidata organization entity https://www.wikidata.org/wiki/Q43229.
 */
@JsonPropertyOrder({ 
	EnrichmentConstants.CONTEXT_FIELD,
	"id",
	"type",
	"prefLabel",
	"altLabel",
	"country",
	"description",
	"depiction",
	"officialWebsite",
	"VIAF_ID",
	"ISNI",
	"logo,",
	"inception",
	"headquartersLoc",
	"headquartersPostalCode",
	"headquartersStreetAddress",
	"headquartersLatitude",
	"headquartersLongitude",
	"industry",
	"phone",
	"modified",
	"sameAs"
})
@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class WikidataOrganizationImpl extends WikidataEntityImpl implements WikidataOrganization {

	private String country;
	private String[] officialWebsite;
	private String VIAF_ID;
	private String ISNI;
	private String logo;
	private String inception;
	private String headquartersLoc;
	private String headquartersPostalCode;
	private String headquartersStreetAddress;
	private Float headquartersLatitude;
	private Float headquartersLongitude;
	private String[] industry;
	private String[] phone;

	@Override
	@JsonProperty("country")
	public String getCountry() {
		return country;
	}

	@Override
	public void setCountry(String countryArg) {
		country = countryArg;
	}

	@Override
	@JsonProperty("officialWebsite")
	public String[] getOfficialWebsite() {
		return officialWebsite;
	}

	@Override
	public void setOfficialWebsite(String[] officialWebsiteParam) {
		officialWebsite = officialWebsiteParam;
	}

	@Override
	@JsonProperty("VIAF_ID")
	public String getVIAF_ID() {
		return VIAF_ID;
	}
	
	@Override
	public void setVIAF_ID(String vIAF_ID) {
		VIAF_ID = vIAF_ID;
	}

	@Override
	@JsonProperty("ISNI")
	public String getISNI() {
		return ISNI;
	}

	@Override
	public void setISNI(String iSNI) {
		ISNI = iSNI;
	}

	@Override
	@JsonProperty("logo")
	public String getLogo() {
		return logo;
	}

	@Override
	public void setLogo(String logoParam) {
		logo = logoParam;
	}

	@Override
	@JsonProperty("inception")
	public String getInception() {
		return inception;
	}

	@Override
	public void setInception(String inceptionParam) {
		inception = inceptionParam;
	}

	@Override
	@JsonProperty("headquartersLoc")
	public String getHeadquartersLoc() {
		return headquartersLoc;
	}
	
	@Override
	public void setHeadquartersLoc(String headquartersLocParam) {
		headquartersLoc=headquartersLocParam;
	}

	@Override
	@JsonProperty("headquartersPostalCode")	
	public String getHeadquartersPostalCode() {
		return headquartersPostalCode;
	}
	@Override
	public void setHeadquartersPostalCode(String headquartersPostalCodeParam) {
		headquartersPostalCode=headquartersPostalCodeParam;
	}

	@Override
	@JsonProperty("headquartersStreetAddress")	
	public String getHeadquartersStreetAddress() {
		return headquartersStreetAddress;
	}
	@Override
	public void setHeadquartersStreetAddress(String headquartersStreetAddressParam) {
		headquartersStreetAddress=headquartersStreetAddressParam;
	}

	@Override
	@JsonProperty("headquartersLatitude")	
	public Float getHeadquartersLatitude() {
		return headquartersLatitude;
	}
	@Override
	public void setHeadquartersLatitude(Float headquartersLatitudeParam) {
		headquartersLatitude=headquartersLatitudeParam;
	}

	@Override
	@JsonProperty("headquartersLongitude")	
	public Float getHeadquartersLongitude() {
		return headquartersLongitude;
	}
	@Override
	public void setHeadquartersLongitude(Float headquartersLongitudeParam) {
		headquartersLongitude=headquartersLongitudeParam;
	}

	@Override
	@JsonProperty("industry")		
	public String[] getIndustry() {
		return industry;
	}
	@Override
	public void setIndustry(String[] industryParam) {
		industry=industryParam;
	}

	@Override
	@JsonProperty("phone")		
	public String[] getPhone() {
		return phone;
	}
	@Override
	public void setPhone(String[] phoneParam) {
		phone=phoneParam;
	}

}
