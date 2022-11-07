package eu.europeana.enrichment.solr.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.model.impl.TopicImpl;
import eu.europeana.enrichment.solr.exception.SolrServiceException;
import eu.europeana.enrichment.solr.model.SolrTopicEntityImpl;
import eu.europeana.enrichment.solr.service.SolrBaseClientService;

@Service(EnrichmentConstants.BEAN_ENRICHMENT_SOLR_BASE_CLIENT_SERVICE)
public class SolrBaseClientServiceImpl implements SolrBaseClientService {

	@Autowired
	protected SolrClient solrServer;
	
	private final Logger log = LogManager.getLogger(getClass());

	//private Jyandex clientJyandex;

	@Override
	public List<Integer> searchByEntityName(String solrCollection, String entityName) throws SolrServiceException {
		return null;
	}	

	@Override
	public void store(String solrCollection, Object solrObject, boolean doCommit) throws SolrServiceException {
		try {
			log.debug("store: " + solrObject.toString());
			UpdateResponse rsp = solrServer.addBean(solrCollection, solrObject);
			log.debug("store response: " + rsp.toString());
			if(doCommit)
				solrServer.commit(solrCollection);
		} catch (SolrServerException ex) {
			throw new SolrServiceException(
					"Unexpected Solr server exception occured when storing to the collection: " + solrCollection, ex);
		} catch (IOException ex) {
			throw new SolrServiceException(
					"Unexpected IO exception occured when storing Solr object into collection: " + solrCollection, ex);
		}
	}
	
	@Override
	public void search (String solrCollection, String term) throws SolrServiceException {

		log.debug("search StoryEntity by term: " + term);

		/**
		 * Construct a SolrQuery
		 */
		SolrQuery query = new SolrQuery(term);
		log.debug("query: " + query.toString());

		/**
		 * Query the server
		 */
		QueryResponse rsp=null;
		try {
			rsp = solrServer.query(solrCollection, query);
			log.debug("query response: " + rsp.toString());
			
		} catch (IOException | SolrServerException e) {
			throw new SolrServiceException("Unexpected exception occured when searching StoryEntity in Solr for the term: " + term,
					e);
		}
		
	}

	@Override
	public void deleteByQuery(String solrCollection, String query) throws SolrServiceException {
		try {
			solrServer.deleteByQuery(solrCollection, query);
			solrServer.commit(solrCollection);
		} catch (IOException | SolrServerException ex) {
			throw new SolrServiceException(
				"Unexpected Solr server or IO exception occured during deleteByQuery for the collection: " + solrCollection, ex);
		}		
	}

	@Override
	public QueryResponse query(String solrCollection, SolrQuery query) throws SolrServiceException {
		
		/**
		 * Query the server
		 */
		QueryResponse rsp = null;
		try {
			rsp = solrServer.query(solrCollection, query);
			log.debug("query response: " + rsp.toString());
			
		} catch (IOException | SolrServerException e) {
			throw new SolrServiceException("Unexpected exception occured when sending a query to the Solr server: " + query.toString(),
					e);
		}
		return rsp;
		
	}

	@Override
	public void deleteById(String solrCollection, String id) throws SolrServiceException {
		try {
			solrServer.deleteById(solrCollection, id);
			solrServer.commit(solrCollection);
		} catch (SolrServerException | IOException e) {
			throw new SolrServiceException(
				"Unexpected exception occured during the deleteById from the Solr collection: " + solrCollection, e);
		}		
	}

}
