package eu.europeana.enrichment.solr.service;

import java.io.IOException;

import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.solr.exception.SolrServiceException;

public interface SolrWikidataEntityService {
	
	public String searchByWikidataURL_usingJackson (String wikidataURL, String type) throws SolrServiceException, IOException;
	public String searchByWikidataURL (String wikidataURL) throws SolrServiceException;
	public boolean existWikidataURL(String wikidataURL) throws SolrServiceException;

	/**
	 * This function aims at storing the Wikidata Entity obtained from its URL, i.e.
	 * first the json response obtained from wikidata is parsed and a result
	 * is saved into the Solr server
	 *
	 *  
	 * @param wikidataURL			(e.g. http://www.wikidata.org/entity/Q762)
	 * @param type					(e.g. agent, place, organization)
	 * @return int					(1-entity is found, 0-entity is not found)
	 * @throws SolrServiceException
	 */
	public int storeWikidataFromURL (String wikidataURL, String type) throws SolrServiceException, IOException;
	
	String searchNamedEntities_usingJackson(String query, String type, String lang, String solrQuery, String solrSort,
			String pageSize, String page) throws SolrServiceException, IOException;
	WikidataEntity getWikidataEntity(String wikidataURL, String type) throws SolrServiceException;
		
}
