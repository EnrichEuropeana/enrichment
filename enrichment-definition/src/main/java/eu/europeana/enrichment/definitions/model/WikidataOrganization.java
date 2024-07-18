package eu.europeana.enrichment.definitions.model;

public interface WikidataOrganization extends WikidataEntity {

	/**
	 * The wikidata json field claims.P17.mainsnak.datavalue.value.id
	 * @return
	 */
	String getCountry();
	 
	/**
	 * Sets the Country for Agent
	 * 
	 * @param country
	 * @return
	 */
	void setCountry(String country);

	/**
	 * The wikidata json field claims.P856.mainsnak.datavalue.value
	 * @return
	 */
	String[] getOfficialWebsite();
	void setOfficialWebsite(String[] officialWebsiteParam);

	/**
	 * The wikidata json field claims.P214.mainsnak.datavalue.value
	 * @return
	 */
	String getVIAF_ID();
	void setVIAF_ID(String vIAF_ID);
		
	/**
	 * The wikidata json field claims.P213.mainsnak.datavalue.value
	 * @return
	 */
	public String getISNI();
	public void setISNI(String iSNI);
	
	/**
	 * The wikidata json field claims.P154.mainsnak.datavalue.value
	 * @return
	 */
	public String getLogo();
	public void setLogo(String logoParam);
	
	/**
	 * The wikidata json field claims.P571.mainsnak.datavalue.value.time
	 * @return
	 */
	public String getInception();
	public void setInception(String inceptionParam);

	/**
	 * The wikidata json field claims.P159.qualifiers.P276.datavalue.value.id
	 * @return
	 */
	public String getHeadquartersLoc();
	public void setHeadquartersLoc(String headquartersLocParam);

	/**
	 * The wikidata json field claims.P159.qualifiers.P281.datavalue.value
	 * @return
	 */
	public String getHeadquartersPostalCode();
	public void setHeadquartersPostalCode(String headquartersPostalCodeParam);

	/**
	 * The wikidata json field claims.P159.qualifiers.P6375.datavalue.value.text
	 * @return
	 */
	public String getHeadquartersStreetAddress();
	public void setHeadquartersStreetAddress(String headquartersStreetAddressParam);

	/**
	 * The wikidata json field claims.P159.qualifiers.P625.datavalue.value.latitude
	 * @return
	 */
	public Float getHeadquartersLatitude();
	public void setHeadquartersLatitude(Float headquartersLatitudeParam);

	/**
	 * The wikidata json field claims.P159.qualifiers.P625.datavalue.value.longitude
	 * @return
	 */
	public Float getHeadquartersLongitude();
	public void setHeadquartersLongitude(Float headquartersLongitudeParam);

	/**
	 * The wikidata json field claims.P452.mainsnak.datavalue.value.id
	 * @return
	 */
	public String[] getIndustry();
	public void setIndustry(String[] industryParam);

	/**
	 * The wikidata json field claims.P1329.mainsnak.datavalue.value
	 * @return
	 */
	public String[] getPhone();
	public void setPhone(String[] phoneParam);
	
}
