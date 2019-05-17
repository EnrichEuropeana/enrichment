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
	public float getLatitude();
	 
	/**
	 * Sets Latitude for Place
	 * 
	 * @param setLatitude
	 * @return
	 */
	public void setLatitude(float setLatitude);
	
	/**
	 * Retrieves Longitude for Place (i.e. json field: claims.P625.mainsnak.datavalue.value.longitude)
	 * 
	 * @return
	 */
	public float getLongitude();
	 
	/**
	 * Sets Longitude for Place
	 * 
	 * @param setLongitude
	 * @return
	 */
	public void setLongitude(float setLongitude);

	String getCountry_jsonProp();

	String getLogo_jsonProp();

	String getLatitude_jsonProp();

	String getLongitude_jsonProp();

}
