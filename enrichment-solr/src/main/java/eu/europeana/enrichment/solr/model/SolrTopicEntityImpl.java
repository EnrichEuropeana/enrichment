package eu.europeana.enrichment.solr.model;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.beans.Field;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import eu.europeana.enrichment.model.Term;
import eu.europeana.enrichment.model.Topic;
import eu.europeana.enrichment.model.impl.TopicImpl;
import eu.europeana.enrichment.solr.model.vocabulary.TopicEntitySolrFields;

public class SolrTopicEntityImpl extends TopicImpl implements Topic {
	
	@Field(TopicEntitySolrFields.TERMS)
	private String solrTerms;
	
	@Field(TopicEntitySolrFields.KEYWORDS)
	private String solrKeywords;
	
	public SolrTopicEntityImpl(Topic copy)
	{
		this.setIdentifier(copy.getIdentifier());
		this.setTopicID(copy.getTopicID());
		this.setLabel(copy.getLabel());
		this.setTopicTerms(copy.getTopicTerms());
		this.setTopicKeywords(copy.getTopicKeywords());
		this.setModelId(copy.getModelId());
		this.setTopicModel(copy.getTopicModel());
		this.setDescription(copy.getDescription());
		this.setCreatedDate(copy.getCreatedDate());
		this.setModifiedDate(copy.getModifiedDate());
	}
	

	@Override
	@Field(TopicEntitySolrFields.TOPIC_ID)
	public void setTopicID(String id) {
		super.setTopicID(id);
	}
	

	@Override
	@Field(TopicEntitySolrFields.IDENTIFIER)
	public void setIdentifier(String identifier)
	{
		super.setIdentifier(identifier);
	}
	
	@Override
	@Field(TopicEntitySolrFields.LABELS)
	public void setLabel(List<String> label) {
		super.setLabel(label);
	}
	
	@Override
	@Field(TopicEntitySolrFields.DESCRIPTION)
	public void setDescription(Map<String,String> descr) {
		super.setDescription(descr);
	}
	
	@Override
	//@Field(TopicEntitySolrFields.TERMS)
	public void setTopicTerms(List<Term> terms) {
		try {
			this.solrTerms = toJSON(terms);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//for (TermEntity te: terms)
		//{
		//	String solrTerm = te.getTerm()+"^"+te.getScore();
		//	this.solrTerms.add(solrTerm);
		//}
	}
	
	private String toJSON(List<Term> terms) throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(terms);
	}


	@Override
	//@Field(TopicEntitySolrFields.KEYWORDS)
	public void setTopicKeywords(List<Term> keywords) {
		try {
			this.solrKeywords = toJSON(keywords);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//for (TermEntity te : keywords)
		//{
			//String solrTerm = te.getTerm()+"^"+te.getScore();
			//this.solrKeywords.add(solrTerm);
		//}
	}

	@Override
	@Field(TopicEntitySolrFields.MODEL_ID)
	public void setModelId(String modelId) {
		super.setModelId(modelId);
	}
	
}
