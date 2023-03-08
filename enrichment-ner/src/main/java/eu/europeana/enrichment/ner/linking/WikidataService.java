package eu.europeana.enrichment.ner.linking;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.solr.exception.SolrServiceException;

public interface WikidataService {

	/**
	 * This method sends a REST request to Wikidata to get
	 * the json for the given Wikidata ID. 
	 * 
	 * @param WikidataID			(e.g. http://www.wikidata.org/entity/Q762)
	 * @return
	 */
	public String getWikidataJSONFromRemote(String WikidataID);
	
	/**
	 * This method returns a value of the specific JSON field 
	 * from the Wikidata JSON file. The format for the field is
	 * given in the point separated way, e.g. claims.P569.mainsnak.datavalue.value.time
	 * which means that each name after the dot is a json element within a
	 * a given json element before the dot. In special case, if the name between 2 dots is "*",
	 * this means taking all elements of the given json element (e.g. aliases.*.language)
	 * 
	 * @param WikidataJSON			(e.g. entities: {
													Q762: {
													pageid: 1069,
													ns: 0,
													title: "Q762",
													lastrevid: 922891204,
													modified: "2019-04-23T15:49:45Z",
													type: "item",
													id: "Q762",
													labels: {},
													descriptions: {},
													aliases: {},
													claims: {},
													sitelinks: {}
													}
													})		
	 * @param field					(e.g. claims.P569.mainsnak.datavalue.value.time)
	 * @return
	 */
	public List<List<String>> getJSONFieldFromWikidataJSON (String WikidataJSON, String field);
	
	/*
	 * This method sends a Wikidata Geonames ID sparql search query
	 * and returns a list of Wikidata entity urls
	 * 
	 * @param geonamesId			(e.g. 2761333 should be Vienna)
	 * @return						a list of Wikidata entity urls
	 */
	public List<String> getWikidataId(String geonameId);
	
	/*
	 * This method sends a Wikidata label sparql search query including
	 * language tag and returns a list of Wikidata entity urls
	 * 
	 * @param label					named entity label (e.g. Vienna, Max Mustermann, ..)
	 * @param language				language tag which excludes all other language
	 * 								labels for comparison
	 * @return						a list of Wikidata entity urls
	 */
	public List<String> getWikidataIdWithLabel(String label, String language);
	
	/*
	 * This method sends a Wikidata sparql search query for label or altLabel including
	 * language tag and returns a list of Wikidata entity urls
	 * 
	 * @param label					named entity label (e.g. Vienna, Max Mustermann, ..)
	 * @param language				language tag which excludes all other language
	 * 								labels for comparison
	 * @return						a list of Wikidata entity urls
	 */
	public List<String> getWikidataIdWithLabelAltLabel(String label, String language);
	
	/*
	 * This method sends a Wikidata place label sparql search query including
	 * language tag and returns a list of Wikidata entity urls
	 * 
	 * @param label					named entity label (e.g. Vienna, Adriatic Sea, ..)
	 * @param language				language tag which excludes all other language
	 * 								labels for comparison
	 * @return						a list of Wikidata places entity urls
	 */
	public List<String> getWikidataPlaceIdWithLabel(String label, String language);
	
	/*
	 * This method sends a Wikidata place label and altlabel sparql search query including
	 * language tags (en and original) and returns a list of Wikidata entity urls
	 * 
	 * @param label					named entity label (e.g. Vienna, Adriatic Sea, ..)
	 * @param language				language tag which excludes all other language
	 * 								labels for comparison
	 * @return						a list of Wikidata places entity urls
	 */
	public List<String> getWikidataPlaceIdWithLabelAltLabel(String label, String language);
	
	/*
	 * This method sends a Wikidata agent label sparql search query including
	 * language tag and returns a list of Wikidata entity urls
	 * 
	 * @param label					named entity label (e.g. Vienna, Adriatic Sea, ..)
	 * @param language				language tag which excludes all other language
	 * 								labels for comparison
	 * @return						a list of Wikidata agents entity urls
	 */
	public List<String> getWikidataAgentIdWithLabel(String label, String language);
	
	public List<String> getWikidataAgentIdWithLabelAltLabel(String label, String language);
		
	public WikidataEntity getWikidataEntityAndSaveToLocalCache(String wikidataURL, String type, boolean matchType) throws IOException;

	public WikidataEntity getWikidataEntity(String wikidataURL, String WikidataJSON, String type);
	
	public String getDescriptionEnFromWikidataJson(String wikidataJson);

	boolean validWikidataPage(String wikidataJSONResponse);

	List<String> getWikidataIdWithWikidataSearch(String label);
	
	String computePreferedWikidataId(NamedEntityImpl namedEntity, boolean matchType) throws IOException, SolrServiceException;
	
	CompletableFuture<String> saveWikidataJSONFromRemoteParallel(String wikidataId) throws IOException;
	
	public boolean matchInstanceOfProperty(String wikidataJSONResponse, String type);
	
	public Set<String> readWikidataIdsFromQueryServiceOutput(String path) throws IOException;
}
