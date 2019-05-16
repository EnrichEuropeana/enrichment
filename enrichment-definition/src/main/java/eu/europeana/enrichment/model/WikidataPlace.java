package eu.europeana.enrichment.model;

import java.util.Map;

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


	String getCountry_jsonProp();

	String getLogo_jsonProp();

	String getLatitude_jsonProp();

	String getLongitude_jsonProp();


	Float getLatitude();

	void setLatitude(Float setLatitude);

	Float getLongitude();

	void setLongitude(Float setLongitude);


}
