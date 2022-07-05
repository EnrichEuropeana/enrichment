package eu.europeana.enrichment.model;

public interface WikidataAgent extends WikidataEntity {

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
	void setCountry(String country);

	String[] getDateOfBirth();

	void setDateOfBirth(String[] dateOfBirth);

	String[] getDateOfDeath();

	void setDateOfDeath(String[] dateOfDeath);

	String[] getProfessionOrOccupation();

	void setProfessionOrOccupation(String[] professionOrOccupation);

}
