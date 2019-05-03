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
public interface WikidataEntity {
	
	/**
	 * Retrieves Preferable Label for the Entity (language,value)
	 * format
	 *  
	 * @return A List<List<String>> for the Preferable Labels 
	 */
	public List<List<String>> getPrefLabel();

	/**
	 * Set Preferable Label for Entity
	 * 
	 * @param prefLabel
	 *            A List<List<String>> for Preferable Label 
	 */
	public void setPrefLabel(List<List<String>> prefLabel);

	/**
	 * Retrieves Alternative Label for Entity (language,value)
	 * format
	 * 
	 * @return A List<List<String>> for Alternative Label 
	 */
	public List<List<String>> getAltLabel();

	/**
	 * Set Alternative Label for Entity
	 * 
	 * @param altLabel
	 *            A List<List<String>> for Alternative Label 
	 */
	public void setAltLabel(List<List<String>> altLabel);

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
	 * @return A List<List<String>> for the Description 
	 */
	public List<List<String>> getDescription();

	/**
	 * Set Description for Entity
	 * 
	 * @param description
	 *            A List<List<String>> for Description 
	 */
	public void setDescription(List<List<String>> description);
		
	/**
	 * Retrieves the Wikidata links that represent the Entity in the format (site, url),
	 * e.g. site: "enwiki", url: "https://en.wikipedia.org/wiki/Leonardo_da_Vinci"
	 * 
	 * @return List<List<String>>
	 */
	public List<List<String>> getSameAs();

	
	/**
	 * Sets the Wikidata links that represent the Entity in the format (site, url),
	 * e.g. site: "enwiki", url: "https://en.wikipedia.org/wiki/Leonardo_da_Vinci"
	 * 
	 * @param wikidataURLs
	 */
	public void setSameAs(List<List<String>> wikidataURLs);

	String getModificationDate_jsonProp();

	String getPrefLabel_jsonProp();

	String getAltLabel_jsonProp();

	String getDepiction_jsonProp();

	String getDescription_jsonProp();

	String getSameAs_jsonProp();


}
