package eu.europeana.enrichment.solr.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocumentList;
import org.json.simple.parser.ParseException;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.ner.service.NERService;
import eu.europeana.enrichment.solr.commons.JavaJSONParser;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.solr.model.SolrItemEntityImpl;
import eu.europeana.enrichment.solr.model.SolrStoryEntityImpl;
import eu.europeana.enrichment.solr.model.vocabulary.StoryEntitySolrFields;
import eu.europeana.enrichment.solr.service.SolrEntityPositionsService;


public class SolrEntityPositionsServiceImpl implements SolrEntityPositionsService{

	@Resource
	SolrClient solrServer;
	
	@Resource(name = "javaJSONParser")
	JavaJSONParser javaJSONParser;

	
	
	private final Logger log = LogManager.getLogger(getClass());

	public void setSolrServer(SolrClient solrServer) {
		this.solrServer = solrServer;
	}

	@Override
	public List<Integer> searchByEntityName(String entityName) throws SolrNamedEntityServiceException {


		return null;
	}
	

	@Override
	public boolean store(StoryEntity storyEntity) throws SolrNamedEntityServiceException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void store(List<? extends StoryEntity> storyEntities) throws SolrNamedEntityServiceException {
		
		for(StoryEntity ent : storyEntities) {
			store(ent, false);
		}
		
		try {
			solrServer.commit();
		} catch (SolrServerException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected Solr server exception occured when storing a list of StoryEntity.", ex);			
		} catch (IOException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected IO exception occured when storing a list of StoryEntity", ex);
		}
		
	}

	@Override
	public void store(StoryEntity storyEntity, boolean doCommit) throws SolrNamedEntityServiceException {
		try {
			
			log.debug("store: " + storyEntity.toString());
			
			SolrStoryEntityImpl solrStoryEntity = null;
			if(storyEntity instanceof SolrStoryEntityImpl) {
				solrStoryEntity=(SolrStoryEntityImpl) storyEntity;
			}
			else {
				solrStoryEntity=new SolrStoryEntityImpl(storyEntity);
			}
			
			UpdateResponse rsp = solrServer.addBean(solrStoryEntity);
			log.info("store response: " + rsp.toString());
			if(doCommit)
				solrServer.commit();
		} catch (SolrServerException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected Solr server exception occured when storing StoryEntity with storyId: " + storyEntity.getStoryId(),
					ex);
		} catch (IOException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected IO exception occured when storing StoryEntity with storyId: " + storyEntity.getStoryId(), ex);
		}
		
	}

	
	@Override
	public void search (String term) throws SolrNamedEntityServiceException {

		log.info("search StoryEntity by term: " + term);

		/**
		 * Construct a SolrQuery
		 */
		SolrQuery query = new SolrQuery(term);
		log.info("query: " + query.toString());

		/**
		 * Query the server
		 */
		try {
			QueryResponse rsp = solrServer.query(query);
			log.info("query response: " + rsp.toString());
			
		} catch (IOException | SolrServerException e) {
			throw new SolrNamedEntityServiceException("Unexpected exception occured when searching StoryEntity in Solr for the term: " + term,
					e);
		}

	}

	@Override
	public void update(StoryEntity stryEntity) throws SolrNamedEntityServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteByQuery(String query) throws SolrNamedEntityServiceException {
		try {
			log.info("Solr deleteByQuery call: " + query);
			UpdateResponse rsp = solrServer.deleteByQuery(query);
			log.info("Solr deleteByQuery response: " + rsp.toString());
			solrServer.commit();
		} catch (IOException | SolrServerException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected solr server or IO exception occured when deleting StoryEntity with query: " + query, ex);
		}		
	}

	@Override
	public double findTermPositionsInStory(String storyId, String term, int startAfterOffset, int numberWordsInTerm) throws SolrNamedEntityServiceException {
	
		SolrQuery query = new SolrQuery();
	
		query.setRequestHandler("/select");		
		query.set("hl.fl",StoryEntitySolrFields.TEXT);
		query.set("hl","on");		
		query.set("indent","on");
//		query.set("hl.usePhraseHighlighter", true);
//		query.set("hl.highlightMultiTerm", true);
//		query.set("hl.qparser","complexphrase");
		query.set("q", StoryEntitySolrFields.TEXT+":"+ "\"" + term + "\""+" AND "+StoryEntitySolrFields.STORY_ID+":"+storyId);	
		query.set("wt","json");	
		
		QueryResponse response=null;
		
		try {
			response = solrServer.query(query);
		} catch (IOException | SolrServerException e) {
			throw new SolrNamedEntityServiceException("Unexpected exception occured when executing Solr query.", e);
		}
		
		
		List<String> terms = new ArrayList<String>();
		List<Double> positions = new ArrayList<Double>();
		List<List<Double>> offsets = new ArrayList<List<Double>>();
		
		try {
			javaJSONParser.getPositionsFromJSON(response, terms, positions, offsets);
		} catch (ParseException e) {
			throw new SolrNamedEntityServiceException("Exception occured when parsing JSON response from Solr. Searched for the term: " + term,e);
		}
	
		//finding the exact offset of the term from the list of all offsets
		double exactOffset = findNextOffset(offsets, startAfterOffset, numberWordsInTerm);
		return exactOffset;
	
		
	}

	/**
	 * This method performs a binary search in the array of offsets to find the first one greater than the given number
	 * 
	 * @param offsets
	 * @param target
	 * @return
	 */
	
	private double findNextOffset (List<List<Double>> offsets, int lastOffset, int numberWordsInTerm) 
    { 
		/*
		 * taking just the first number in the list because the "arr" list returns offsets 
		 * for each word in the term: e.g. for the term "dumitru nistor" it will return [[0,7],[8,14]]
		 */

		int skip = numberWordsInTerm;
		int size = offsets.size();
		// Limit to carefully avoid IndexOutOfBoundsException
		int limit = size / skip + Math.min(size % skip, 1);

		List<Double> adaptedOffsets = Stream.iterate(offsets, l -> l.subList(skip, l.size()))
		    .limit(limit)
		    .map(l -> l.get(0).get(0))
		    .collect(Collectors.toList());

		//sorting in ascending order (1,5,7,13,...)
		Collections.sort(adaptedOffsets);
		
        int start = 0, end = adaptedOffsets.size() - 1; 
  
        int ans = -1; 
        while (start <= end) { 
            int mid = (start + end) / 2; 
  
            // Move to right side if target is greater. 
            if (adaptedOffsets.get(mid) <= lastOffset) { 
                start = mid + 1; 
            } 
  
            // Move left side. 
            else { 
                ans = mid; 
                end = mid - 1; 
            } 
        } 
        return adaptedOffsets.get(ans); 
    } 

}
