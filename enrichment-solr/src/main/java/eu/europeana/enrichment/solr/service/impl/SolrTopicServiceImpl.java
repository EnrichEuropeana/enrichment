package eu.europeana.enrichment.solr.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.impl.BaseHttpSolrClient.RemoteSolrException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.model.impl.TopicImpl;
import eu.europeana.enrichment.solr.commons.SolrUtils;
import eu.europeana.enrichment.solr.exception.SolrServiceException;
import eu.europeana.enrichment.solr.model.SolrTopicEntityImpl;
import eu.europeana.enrichment.solr.model.vocabulary.TopicSolrFields;

@Service(EnrichmentConstants.BEAN_ENRICHMENT_SOLR_TOPIC_SERVICE)
public class SolrTopicServiceImpl extends SolrBaseClientServiceImpl {
	
	private final Logger logger = LogManager.getLogger(getClass());
	
	public List<TopicImpl> searchTopics (String query, String fq, String fl, String facets, String sort, int page, int pageSize) throws SolrServiceException {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(query);
		if (fq != null) {
		    solrQuery.addFilterQuery(HelperFunctions.toArray(fq,","));
		}
		if (fl != null) {
		    solrQuery.setFields(HelperFunctions.toArray(fl,","));
		}
		if (facets != null) {
		    solrQuery.setFacet(true);
		    solrQuery.addFacetField(HelperFunctions.toArray(facets,","));
		    solrQuery.setFacetLimit(SolrUtils.FACET_LIMIT);
		}
		if (sort != null) {
		    SolrUtils.buildSortQuery(solrQuery, HelperFunctions.toArray(sort,","));
		}
		solrQuery.setRows(pageSize);
		solrQuery.setStart(page * pageSize);
		
		QueryResponse rsp =null;
		try {
			logger.debug("Solr topic search query: " + query);
		    rsp = solrServer.query(TopicSolrFields.SOLR_CORE, solrQuery);
		} catch (RemoteSolrException e) {
			throw handleRemoteSolrException(solrQuery, e);
		} catch (IOException | SolrServerException | RuntimeException e) {
			throw new SolrServiceException("Exception during sending the query: " + solrQuery.toString() + ", to the Solr server", e);
		}
		
		SolrDocumentList docs = rsp.getResults();
		if(docs.size()==0) {
			return null;
		}
		DocumentObjectBinder binder = new DocumentObjectBinder();
		List<TopicImpl> solrTopics = new ArrayList<TopicImpl>();
		Iterator<SolrDocument> iteratorSolrDocs = docs.iterator();
		while (iteratorSolrDocs.hasNext()) {
			SolrDocument doc = iteratorSolrDocs.next();
			SolrTopicEntityImpl solrTopic = (SolrTopicEntityImpl) binder.getBean(SolrTopicEntityImpl.class, doc);
			solrTopics.add(solrTopic);
		}
		return solrTopics;		
	}

    private SolrServiceException handleRemoteSolrException(SolrQuery searchQuery, RemoteSolrException e) {
	String remoteMessage = e.getMessage();
	String UNDEFINED_FIELD = "undefined field";
	SolrServiceException ex;
	if (remoteMessage.contains(UNDEFINED_FIELD)) {
	    // invalid search field
	    int startPos = remoteMessage.indexOf(UNDEFINED_FIELD) + UNDEFINED_FIELD.length();
	    String fieldName = remoteMessage.substring(startPos);
	    ex = new SolrServiceException("Exception during the solr search, undefined field: " + fieldName, e);
	} else {
	    int separatorPos = remoteMessage.lastIndexOf(':');
	    if (separatorPos > 0) {
		// remove server url from remote message
		remoteMessage = remoteMessage.substring(separatorPos + 1);
	    }
	    ex = new SolrServiceException("Exception during the solr search. Remote message: " + remoteMessage, e);
	}
	return ex;
    }
		
}
