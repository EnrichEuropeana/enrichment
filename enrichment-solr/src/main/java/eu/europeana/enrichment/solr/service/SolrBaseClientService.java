package eu.europeana.enrichment.solr.service;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.Topic;
import eu.europeana.enrichment.model.WikidataEntity;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;

public interface SolrBaseClientService {
	
	public static final String HANDLER_SELECT = "/select";
	public static final String HANDLER_SUGGEST = "/suggestEntity";

	/**
	 * Sends the query to the Solr server.
	 *  
	 * @param solrCollection
	 * @param query
	 * @return
	 * @throws SolrNamedEntityServiceException
	 */
	public QueryResponse query (String solrCollection, SolrQuery query) throws SolrNamedEntityServiceException ;
	
	/**
	 * This method stores an object to Solr.
	 * 
	 * @param solrObject
	 * @param doCommit
	 * @param solrCollection
	 * @throws SolrNamedEntityServiceException
	 */

	public void store(String solrCollection, Object solrObject, boolean doCommit) throws SolrNamedEntityServiceException ;

	/**
	 * This method searches for a term in Solr.
	 * @param term
	 * @param solrCollection
	 * @throws SolrNamedEntityServiceException 
	 */
	
	public void search (String solrCollection, String term) throws SolrNamedEntityServiceException ;
	
	/**
	 * This method deletes an object or more objects from Solr based on the fields in the query.
	 * @param query
	 * @param solrCollection
	 * @throws SolrNamedEntityServiceException 
	 */
	public void deleteByQuery(String solrCollection, String query) throws SolrNamedEntityServiceException;
	
	/**
	 * This method retrieves the list of positions of the given entity
	 * 
	 * @param entityName
	 * @param solrCollection
	 * @return
	 * @throws SolrNamedEntityServiceException 
	 */
	public List<Integer> searchByEntityName(String solrCollection, String entityName) throws SolrNamedEntityServiceException;
	
	/**
	 * This method deletes an object from Solr based on its unique key
	 * @param solrCollection
	 * @param id
	 * @throws SolrNamedEntityServiceException
	 */
	public void deleteById(String solrCollection, String id) throws SolrNamedEntityServiceException;

}
