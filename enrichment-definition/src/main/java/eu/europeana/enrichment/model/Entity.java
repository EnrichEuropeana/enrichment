package eu.europeana.enrichment.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This interface is a base for the objects related to the Wikidata IDs, i.e. the ones fould with
 * the corresponding NER tool (those objects are: agent, place, and organization)
 * 
 * @author StevaneticS
 *
 */
public interface Entity {
	
	/**
	 * Retrieves the Preferable Label for the Entity (language,value)
	 * format
	 *  
	 * @return A Map<String,List<List<String>>> for the Preferable Labels (one per language)
	 */
	public Map<String, String> getPrefLabel();

	/**
	 * Set the prefLabel for the Entity
	 * 
	 * @param prefLabel
	 *            A Map<String,List<List<String>>> for the Preferable Labels (one per language)
	 */
	public void setPrefLabel(Map<String, String> prefLabel);

	/**
	 * Retrieves the Alternative Label for the Entity (language,value)
	 * format
	 * 
	 * @return A Map<String,List<List<String>>> for the Alternative Labels (one per language)
	 */
	public Map<String, List<String>> getAltLabel();

	/**
	 * Set the altLabel for the Entity
	 * 
	 * @param altLabel
	 *            A Map<String,List<List<String>>> for the Alternative Labels (one per language)
	 */
	public void setAltLabel(Map<String, List<String>> altLabel);

	/**
	 * Retrieves the ID of the Entity (e.g. http://www.wikidata.org/entity/Q762)
	 * 
	 * @return
	 */
	public String getEntityId();

	/**
	 * Sets the ID of the Entity
	 * 
	 * @param enitityId
	 */
	public void setEntityId(String enitityId);
	
	/**
	 * Retrieves the Internal Type of the Entity (e.g. agent, place, organization)
	 * 
	 * @return
	 */
	public String getInternalType();

	/**
	 * Sets the Internal Type of the Entity (e.g. agent, place, organization)
	 * 
	 * @param entityType
	 */
	public void setInternalType (String entityType);
	
	/**
	 * Get the Modification Date (e.g. Wikidata field "modified" from json: "2019-04-23T15:49:45Z")
	 * 
	 * @return
	 */
	public String getModificationDate();

	/**
	 * Sets the Modification Date (e.g. Wikidata field "modified" from json: "2019-04-23T15:49:45Z")
	 * 
	 * @param date
	 */
	public void setModificationDate(String date);

	/**
	 * Retrieves the Depiction for the Entity (e.g.  Wikidata field image: "https://commons.wikimedia.org/wiki/File:"+ the field (claims.P18.mainsnak.datavalue.value)) 
	 * 
	 * @return
	 */
	public String getDepiction();
	
	/**
	 * Sets the Depiction field (e.g.  Wikidata field image: "https://commons.wikimedia.org/wiki/File:"+ the field (claims.P18.mainsnak.datavalue.value)) 
	 * 
	 * @param depiction
	 */
	public void setDepiction(String depiction);

	/**
	 * Retrieves the Description for the Entity (language,value)
	 * format
	 *  
	 * @return A Map<String,List<List<String>>> for the Description (one per language)
	 */
	public Map<String, String> getDescription();

	/**
	 * Set the Description for the Entity
	 * 
	 * @param description
	 *            A Map<String,List<List<String>>> for the Description (one per language)
	 */
	public void setDescription(Map<String, String> description);

	/**
	 * Retrieves the Country for the Entity (e.g. Wikidata json field for the "agent" object: claims.P27.mainsnak.datavalue.value.id)
	 * 
	 * @return
	 */
	public String getCountry();
	 
	/**
	 * Sets the Country for the Entity
	 * @param country
	 * @return
	 */
	public void setCountry(String country);
		
	/**
	 * Retrieves the Wikidata links that represent the Entity in the format (site, url),
	 * e.g. site: "enwiki", url: "https://en.wikipedia.org/wiki/Leonardo_da_Vinci"
	 * 
	 * @return Map<String, String>
	 */
	public Map<String, String> getSameAs();

	
	/**
	 * Sets the Wikidata links that represent the Entity in the format (site, url),
	 * e.g. site: "enwiki", url: "https://en.wikipedia.org/wiki/Leonardo_da_Vinci"
	 * 
	 * @param wikidataURLs
	 */
	public void setSameAs(Map<String, String> wikidataURLs);


}
