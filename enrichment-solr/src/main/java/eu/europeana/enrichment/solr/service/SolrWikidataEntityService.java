package eu.europeana.enrichment.solr.service;

import java.io.IOException;

import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;

public interface SolrWikidataEntityService {
	
	public String searchByWikidataURL_usingJackson (String wikidataURL, String type) throws SolrNamedEntityServiceException, IOException;
	public String searchByWikidataURL (String wikidataURL) throws SolrNamedEntityServiceException;

	/**
	 * Storing a single WikidataEntity to the Solr server 
	 * 
	 * @param solrCollection
	 * @param wikidataEntity
	 * @param doCommit
	 * @throws SolrNamedEntityServiceException
	 */
	public void store(String solrCollection, WikidataEntity wikidataEntity, boolean doCommit) throws SolrNamedEntityServiceException;

	/**
	 * This function aims at storing the Wikidata Entity obtained from its URL, i.e.
	 * first the json response obtained from wikidata is parsed and a result
	 * is saved into the Solr server
	 *
	 *  
	 * @param wikidataURL			(e.g. http://www.wikidata.org/entity/Q762)
	 * @param type					(e.g. agent, place, organization)
	 * @return int					(1-entity is found, 0-entity is not found)
	 * @throws SolrNamedEntityServiceException
	 */
	public int storeWikidataFromURL (String wikidataURL, String type) throws SolrNamedEntityServiceException, IOException;
	
	String searchNamedEntities_usingJackson(String wskey, String query, String type, String lang, String solrQuery, String solrSort,
			String pageSize, String page) throws SolrNamedEntityServiceException, IOException;
	WikidataEntity getWikidataEntity(String wikidataURL, String type) throws SolrNamedEntityServiceException;
	
	
	
	
}
