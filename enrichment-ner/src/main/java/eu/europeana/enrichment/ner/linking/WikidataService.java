package eu.europeana.enrichment.ner.linking;

import java.util.List;

public interface WikidataService {

	/*
	 * This method sends a Wikidata Geonames ID sparql search query
	 * and returns a list of Wikidata entity urls
	 * 
	 * @param geonamesId			(e.g. 2761333 should be Vienna)
	 * @return						a list of Wikidata entity urls
	 */
	public List<String> getWikidataId(String geonameId);
	
	/*
	 * This method send a Wikidata label sparql search query including
	 * language tag and returns a list of Wikidata entity urls
	 * 
	 * @param label					named entity label (e.g. Vienna, Max Mustermann, ..)
	 * @param language				language tag which excludes all other language
	 * 								labels for comparison
	 * @return						a list of Wikidata entity urls
	 */
	public List<String> getWikidataIdWithLabel(String label, String language);
	
}
