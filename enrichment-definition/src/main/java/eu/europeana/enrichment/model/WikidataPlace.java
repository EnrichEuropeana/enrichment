package eu.europeana.enrichment.model;

public interface WikidataPlace extends WikidataEntity {

	/**
	 * Retrieves Country for Place (i.e. json field: claims.P17.mainsnak.datavalue.value.id)
	 * 
	 * @return
	 */
	public String getCountry();
	 
	/**
	 * Sets Country for Place
	 * 
	 * @param country
	 * @return
	 */
	public void setCountry(String country);

	/**
	 * Retrieves Logo for Place (i.e. json field: claims.P154.mainsnak.datavalue.value)
	 * 
	 * @return
	 */
	public String getLogo();
	 
	/**
	 * Sets Logo for Place
	 * 
	 * @param setLogo
	 * @return
	 */
	public void setLogo(String setLogo);

	Float getLatitude();

	void setLatitude(Float setLatitude);

	Float getLongitude();

	void setLongitude(Float setLongitude);


}
