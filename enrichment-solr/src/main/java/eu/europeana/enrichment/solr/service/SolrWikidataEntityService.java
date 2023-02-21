package eu.europeana.enrichment.solr.service;

import java.io.IOException;

import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.solr.exception.SolrServiceException;

public interface SolrWikidataEntityService {
	
	public String searchByWikidataURL_usingJackson (String wikidataURL, String type) throws SolrServiceException, IOException;
	public String searchByWikidataURL (String wikidataURL) throws SolrServiceException;
	public boolean existWikidataURL(String wikidataURL) throws SolrServiceException;
	public int storeWikidataEntity (WikidataEntity entity, String type) throws SolrServiceException, IOException;
	
	String searchNamedEntities_usingJackson(String query, String type, String lang, String solrQuery, String solrSort,
			String pageSize, String page) throws SolrServiceException, IOException;
	WikidataEntity getWikidataEntity(String wikidataURL, String type) throws SolrServiceException;
		
}
