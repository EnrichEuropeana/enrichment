package eu.europeana.enrichment.solr.service;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.Topic;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.solr.exception.SolrServiceException;

public interface SolrBaseClientService {
	
	public static final String HANDLER_SELECT = "/select";
	public static final String HANDLER_SUGGEST = "/suggestEntity";

	/**
	 * Sends the query to the Solr server.
	 *  
	 * @param solrCollection
	 * @param query
	 * @return
	 * @throws SolrServiceException
	 */
	public QueryResponse query (String solrCollection, SolrQuery query) throws SolrServiceException ;
	
	/**
	 * This method stores an object to Solr.
	 * 
	 * @param solrObject
	 * @param doCommit
	 * @param solrCollection
	 * @throws SolrServiceException
	 */

	public void store(String solrCollection, Object solrObject, boolean doCommit) throws SolrServiceException ;

	/**
	 * This method searches for a term in Solr.
	 * @param term
	 * @param solrCollection
	 * @throws SolrServiceException 
	 */
	
	public void search (String solrCollection, String term) throws SolrServiceException ;
	
	/**
	 * This method deletes an object or more objects from Solr based on the fields in the query.
	 * @param query
	 * @param solrCollection
	 * @throws SolrServiceException 
	 */
	public void deleteByQuery(String solrCollection, String query) throws SolrServiceException;
	
	/**
	 * This method retrieves the list of positions of the given entity
	 * 
	 * @param entityName
	 * @param solrCollection
	 * @return
	 * @throws SolrServiceException 
	 */
	public List<Integer> searchByEntityName(String solrCollection, String entityName) throws SolrServiceException;
	
	/**
	 * This method deletes an object from Solr based on its unique key
	 * @param solrCollection
	 * @param id
	 * @throws SolrServiceException
	 */
	public void deleteById(String solrCollection, String id) throws SolrServiceException;

}
