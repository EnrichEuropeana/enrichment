package eu.europeana.enrichment.model;

public interface WikidataAgent extends WikidataEntity{

	/**
	 * Retrieves the Country for Agent (i.e. json field claims.P27.mainsnak.datavalue.value.id)
	 * 
	 * @return
	 */
	public String getCountry();
	 
	/**
	 * Sets the Country for Agent
	 * 
	 * @param country
	 * @return
	 */
	public void setCountry(String country);
	
	/**
	 * Retrieves the date of birth for Entity (i.e. json field claims.P569.mainsnak.datavalue.value.time)
	 * 
	 * @return
	 */
	public String getDateOfBirth();
	 
	/**
	 * Sets the date of birth for Agent
	 * 
	 * @param setDateOfBirth
	 * @return
	 */
	public void setDateOfBirth(String setDateOfBirth);
	/**
	 * Retrieves the date of death for Entity (i.e. json field claims.P570.mainsnak.datavalue.value.time)
	 * 
	 * @return
	 */
	public String getDateOfDeath();
	 
	/**
	 * Sets the date of death for Agent
	 * 
	 * @param setDateOfDeath
	 * @return
	 */
	public void setDateOfDeath(String setDateOfDeath);
	/**
	 * Retrieves the occupation for Entity (i.e. json field "http://www.wikidata.org/entity/" + claims.P106.mainsnak.datavalue.value.id)
	 * 
	 * @return
	 */
	public String getOccupation();
	 
	/**
	 * Sets the occupation for Agent
	 * 
	 * @param setOccupation
	 * @return
	 */
	public void setOccupation(String setOccupation);

	String getCountry_jsonProp();

	String getDateOfBirth_jsonProp();

	String getDateOfDeath_jsonProp();

	String getProfessionOrOccupation_jsonProp();


}
