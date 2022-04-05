package eu.europeana.enrichment.solr.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.beans.Field;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import eu.europeana.enrichment.model.Term;
import eu.europeana.enrichment.model.Topic;
import eu.europeana.enrichment.model.impl.TopicImpl;
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
		this.setLabels(copy.getLabels());
		this.setTerms(copy.getTerms());
		this.setKeywords(copy.getKeywords());
		this.setModelId(copy.getModel().getIdentifier());
		this.setModel(copy.getModel());
		this.setDescriptions(copy.getDescriptions());
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
	@Field(TopicSolrFields.DESCRIPTION)
	public void setDescriptions(Map<String,String> descr) {
		super.setDescriptions(descr);
	}
	
	@Override
	//@Field(TopicEntitySolrFields.TERMS)
	public void setTerms(List<Term> terms) {
		//try {
		//	this.solrTerms = toJSON(terms);
		//} catch (IOException e) {
		//	e.printStackTrace();
		//}
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
	//@Field(TopicEntitySolrFields.KEYWORDS)
	public void setKeywords(List<Term> keywords) {
		//try {
		//	this.solrKeywords = toJSON(keywords);
		//} catch (IOException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
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
