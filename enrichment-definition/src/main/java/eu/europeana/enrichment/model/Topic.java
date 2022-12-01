package eu.europeana.enrichment.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import eu.europeana.enrichment.model.impl.TopicImpl;

@JsonDeserialize(as = TopicImpl.class)
public interface Topic {
	
	public String getTopicID();
	public void setTopicID(String id);
	
	public String getIdentifier();
	public void setIdentifier(String identifier);
	
	public List<String> getLabels();
	public void setLabels(List<String> label);
	
	public Map<String,String> getDescriptions();
	public void setDescriptions(Map<String,String> descr);
	
	public List<Term> getTerms();
	public void setTerms(List<Term> terms);
	
	// keywords from transcribathon that are related to this topic
	public List<Term> getKeywords();
	public void setKeywords(List<Term> keywords);
	
	public TopicModel getModel();
	public void setModel (TopicModel model);
	
	public Date getCreated();
	public Date getModified();
	
	public void setCreated(Date date);
	public void setModified(Date date);
	//public ObjectId getObjectId();
	//public String toJSON() throws JsonProcessingException;
	String getModelId();
	void setModelId(String modelId);
	
}
