package eu.europeana.enrichment.model;

public interface WikidataPlace extends WikidataEntity{

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
	
	/**
	 * Retrieves Latitude for Place (i.e. json field: claims.P625.mainsnak.datavalue.value.latitude)
	 * 
	 * @return
	 */
	public String getLatitude();
	 
	/**
	 * Sets Latitude for Place
	 * 
	 * @param setLatitude
	 * @return
	 */
	public void setLatitude(String setLatitude);
	
	/**
	 * Retrieves Longitude for Place (i.e. json field: claims.P625.mainsnak.datavalue.value.longitude)
	 * 
	 * @return
	 */
	public String getLongitude();
	 
	/**
	 * Sets Longitude for Place
	 * 
	 * @param setLongitude
	 * @return
	 */
	public void setLongitude(String setLongitude);

	String getCountry_jsonProp();

	String getLogo_jsonProp();

	String getLatitude_jsonProp();

	String getLongitude_jsonProp();

}
