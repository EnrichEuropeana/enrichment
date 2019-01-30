package eu.europeana.enrichment.ner.linking;

public interface EuropeanaEntityService {

	/*
	 * This method returns a list of Europeana IDs base on the named entity label,
	 * classification type and (if not entity was found) original language
	 * 
	 * Example http://entity-api-test.eanadev.org/entity/search?wskey=apidemo&query=label%3AGermany&lang=all&type=Place&sort=derived_score%2Bdesc&page=0&pageSize=10
	 * @param text					named entity label
	 * @param classificationType	named entity classification type (e.g. place, agent, ..)
	 * @param language				original languages (this will be used if no entity was found with "en"
	 * @return 						string of Europeana IDs (e.g. http://data.europeana.eu/place/base/73) 
	 */
	public String getEntitySuggestions(String text, String classificationType, String language);
	
	/*
	 * This method returns the Europeana entity preferred label of a Europeana ID url.
	 * 
	 * @param idUrl					Europeana ID url for specific entity
	 * @param language				original language will be used if no "" or "en" preferred label exist
	 * @return						preferred label or internal type (if no preferred label is available)
	 */
	public String retriveEntity(String idUrl, String language);
	
}
