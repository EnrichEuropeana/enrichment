package eu.europeana.enrichment.solr.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public boolean store(ItemEntity ItemEntity) throws SolrNamedEntityServiceException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void store(List<? extends ItemEntity> storyItemEntities) throws SolrNamedEntityServiceException {
		
		for(ItemEntity ent : storyItemEntities) {
			store(ent, false);
		}
		
		try {
			solrServer.commit();
		} catch (SolrServerException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected Solr server exception occured when storing a list of ItemEntity.", ex);			
		} catch (IOException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected IO exception occured when storing a list of storyItemEntit", ex);
		}
		
	}

	@Override
	public void store(ItemEntity ItemEntity, boolean doCommit) throws SolrNamedEntityServiceException {
		try {
			
			log.debug("store: " + ItemEntity.toString());
			
			SolrItemEntityImpl solrStoryItem = null;
			if(ItemEntity instanceof SolrItemEntityImpl) {
				solrStoryItem=(SolrItemEntityImpl) ItemEntity;
			}
			else {
				solrStoryItem=new SolrItemEntityImpl(ItemEntity);
			}
			
			UpdateResponse rsp = solrServer.addBean(solrStoryItem);
			log.info("store response: " + rsp.toString());
			if(doCommit)
				solrServer.commit();
		} catch (SolrServerException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected Solr server exception occured when storing ItemEntity with itemId: " + ItemEntity.getItemId(),
					ex);
		} catch (IOException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected IO exception occured when storing ItemEntity with itemId: " + ItemEntity.getItemId(), ex);
		}
		
	}

	@Override
	public void store(StoryEntity storyEntity, boolean doCommit) throws SolrNamedEntityServiceException {
		try {
			
			log.debug("store: " + storyEntity.toString());
			
			SolrStoryEntityImpl solrStory = null;
			if(solrStory instanceof SolrStoryEntityImpl) {
				solrStory=(SolrStoryEntityImpl) solrStory;
			}
			else {
				solrStory=new SolrStoryEntityImpl(storyEntity);
			}
			
			UpdateResponse rsp = solrServer.addBean(solrStory);
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

		log.info("search Annotation by term: " + term);

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
			throw new SolrNamedEntityServiceException("Unexpected exception occured when searching annotations for: " + term,
					e);
		}

	}

	@Override
	public void update(ItemEntity ItemEntity) throws SolrNamedEntityServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(String storyItemID) throws SolrNamedEntityServiceException {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Integer> findTermPositionsInStory(String storyId, String term, int startAfterOffset) throws SolrNamedEntityServiceException {
	
		SolrQuery query = new SolrQuery();
	
		query.setRequestHandler("/select");
		
		query.set("hl.fl","sie_text");
		query.set("hl","on");		
		query.set("indent","on");		

		query.set("q", "sie_text:"+ "\"" + term + "\"");
	
		query.set("wt","json");	
		
		QueryResponse response=null;
		
		try {
			response = solrServer.query(query);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Map<String, List<Integer>> results;
		try {
			results=javaJSONParser.getPositionsFromJSON(response, "sie_story_id", "sie_text");
		} catch (ParseException e) {
			throw new SolrNamedEntityServiceException("Exception occured when parsing JSON response from Solr. Searched for the term: " + term,e);
		}
	
		return null;
	
		
	}


}
