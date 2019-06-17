package eu.europeana.enrichment.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This interface is a base for the objects related to the Wikidata IDs, i.e. the ones found with
 * the corresponding NER tool (those objects are: agent, place, and organization)
 * 
 * @author StevaneticS
 *
 */
public interface WikidataEntity {
	
	/**
	 * Retrieves Preferable Label for the Entity (language,value)
	 * format
	 *  
	 * @return A Map<String, String> for the Preferable Labels 
	 */
	public Map<String, String> getPrefLabelStringMap();

	/**
	 * Set Preferable Label for Entity
	 * 
	 * @param prefLabel
	 *            A Map<String, String> for Preferable Label 
	 */
	void setPrefLabelStringMap (Map<String, String> prefLabel);

	/**
	 * Retrieves Alternative Label for Entity (language,value)
	 * format
	 * 
	 * @return A Map<String, List<String>> for Alternative Label 
	 */
	public Map<String, List<String>> getAltLabel();

	/**
	 * Set Alternative Label for Entity
	 * 
	 * @param altLabel
	 *            A List<List<String>> for Alternative Label 
	 */
	public void setAltLabel(Map<String, List<String>> altLabel);

	/**
	 * Retrieves the ID of Entity (e.g. http://www.wikidata.org/entity/Q762)
	 * 
	 * @return
	 */
	public String getEntityId();

	/**
	 * Sets the ID of Entity
	 * 
	 * @param enitityId
	 */
	public void setEntityId(String enitityId);
	
	/**
	 * Retrieves Internal Type of Entity (e.g. agent, place, organization)
	 * 
	 * @return
	 */
	public String getInternalType();

	/**
	 * Sets Internal Type of Entity (e.g. agent, place, organization)
	 * 
	 * @param entityType
	 */
	public void setInternalType (String entityType);
	
	/**
	 * Get Modification Date (e.g. Wikidata field "modified" from json: "2019-04-23T15:49:45Z")
	 * 
	 * @return
	 */
	public String getModificationDate();

	/**
	 * Sets Modification Date (e.g. Wikidata field "modified" from json: "2019-04-23T15:49:45Z")
	 * 
	 * @param date
	 */
	public void setModificationDate(String date);

	/**
	 * Retrieves Depiction for Entity (e.g.  Wikidata field image: "https://commons.wikimedia.org/wiki/File:"+ the field (claims.P18.mainsnak.datavalue.value)) 
	 * 
	 * @return
	 */
	public String getDepiction();
	
	/**
	 * Sets Depiction field (e.g.  Wikidata field image: "https://commons.wikimedia.org/wiki/File:"+ the field (claims.P18.mainsnak.datavalue.value)) 
	 * 
	 * @param depiction
	 */
	public void setDepiction(String depiction);

	/**
	 * Retrieves Description for Entity (language,value)
	 * format
	 *  
	 * @return A Map<String, String> for the Description 
	 */
	public Map<String, List<String>> getDescription();

	/**
	 * Set Description for Entity
	 * 
	 * @param description
	 *            A Map<String, String> for Description 
	 */
	public void setDescription(Map<String, List<String>> description);
		
	/**
	 * Retrieves the Wikidata links that represent the Entity in the format (site, url),
	 * e.g. site: "enwiki", url: "https://en.wikipedia.org/wiki/Leonardo_da_Vinci"
	 * 
	 * @return List<List<String>>
	 */
	public String [] getSameAs();

	
	/**
	 * Sets the Wikidata links that represent the Entity in the format (site, url),
	 * e.g. site: "enwiki", url: "https://en.wikipedia.org/wiki/Leonardo_da_Vinci"
	 * 
	 * @param wikidataURLs
	 */
	public void setSameAs(String [] wikidataURLs);
	
	/**
	 * This method returns the json field from the wikidata json response which stores Modification Date for Entity
	 * This and all other get methods that return json fields work with the following syntax for specifying the 
	 * json field to be taken: e.g. "claims.P18.mainsnak.datavalue.value" (the dot "." defines a json element within 
	 * another json element). Also "*" as a sign can be provided to specify all elements within a given json element, 
	 * e.g. "aliases.*.*" meaning all elements within an "aliases" element, and all of their elements  
	 * 
	 * @return
	 */

	String getModificationDate_jsonProp();

	String getPrefLabel_jsonProp();

	String getAltLabel_jsonProp();

	String getDepiction_jsonProp();

	String getDescription_jsonProp();

	String getSameAs_jsonProp();

	


}
