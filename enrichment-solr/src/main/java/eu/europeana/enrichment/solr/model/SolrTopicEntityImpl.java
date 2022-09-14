package eu.europeana.enrichment.solr.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.solr.client.solrj.beans.Field;

import eu.europeana.enrichment.common.commons.SolrUtils;
import eu.europeana.enrichment.model.Term;
import eu.europeana.enrichment.model.Topic;
import eu.europeana.enrichment.model.impl.TopicImpl;
import eu.europeana.enrichment.solr.model.vocabulary.EntitySolrFields;
import eu.europeana.enrichment.solr.model.vocabulary.TopicSolrFields;

public class SolrTopicEntityImpl extends TopicImpl implements Topic {
	
	@Field(TopicSolrFields.TERMS)
	private List<String> solrTerms;
	
	@Field(TopicSolrFields.KEYWORDS)
	private List<String> solrKeywords;
	
	public SolrTopicEntityImpl(Topic copy)
	{
		this.setIdentifier(copy.getIdentifier());
		this.setTopicID(copy.getTopicID());
		if(copy.getLabels()!=null) {
			this.setLabels(new ArrayList<>(copy.getLabels()));
		}
		if(copy.getTerms()!=null) {
			this.setTerms(copy.getTerms());
		}
		if(copy.getKeywords()!=null) {
			this.setKeywords(copy.getKeywords());
		}
		this.setModelId(copy.getModel().getIdentifier());
		this.setModel(copy.getModel());
		if(copy.getDescriptions()!=null) {
			this.setDescriptions(new HashMap<String, String>(copy.getDescriptions()));
		}
		this.setCreated(copy.getCreated());
		this.setModified(copy.getModified());
	}
	

	@Override
	@Field(TopicSolrFields.TOPIC_ID)
	public void setTopicID(String id) {
		super.setTopicID(id);
	}
	

	@Override
	@Field(TopicSolrFields.IDENTIFIER)
	public void setIdentifier(String identifier)
	{
		super.setIdentifier(identifier);
	}
	
	@Override
	@Field(TopicSolrFields.LABELS)
	public void setLabels(List<String> label) {
		super.setLabels(label);
	}
	
	@Override
	@Field(TopicSolrFields.DESCRIPTION_ALL)
	public void setDescriptions(Map<String,String> descr) {
		if (MapUtils.isNotEmpty(descr)) {
			super.setDescriptions(new HashMap<>(
				SolrUtils.normalizeStringMapByAddingPrefix(
						TopicSolrFields.DESCRIPTION + EntitySolrFields.DYNAMIC_FIELD_SEPARATOR,
						descr)));
	    }
	}
	
	@Override
	public void setTerms(List<Term> terms) {
		this.solrTerms = new ArrayList<String>();
		for (Term te: terms)
		{
			for (int i = 0; i<te.getScore();i++) {
				String solrTerm = te.getTerm();
				this.solrTerms.add(solrTerm);
				}
				
		}
	}

	@Override
	public void setKeywords(List<Term> keywords) {
		this.solrKeywords = new ArrayList<String>();
		for (Term te : keywords)
		{
			for (int i=0;i<te.getScore();i++) {
				String solrTerm = te.getTerm();
				this.solrKeywords.add(solrTerm);
			}
		}
	}

	@Override
	@Field(TopicSolrFields.MODEL_ID)
	public void setModelId(String modelId) {
		super.setModelId(modelId);
	}
	
}
