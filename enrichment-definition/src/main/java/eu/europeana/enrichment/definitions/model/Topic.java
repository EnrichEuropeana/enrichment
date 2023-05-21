package eu.europeana.enrichment.definitions.model;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import eu.europeana.enrichment.definitions.model.impl.TopicImpl;

@JsonDeserialize(as = TopicImpl.class)
public interface Topic {
	
	public long getId();
	public void setId(long id);
	
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
	
	String getModelId();
	void setModelId(String modelId);
	
	public Float getScore();
	public void setScore(Float score);
	
	public String getUrlId();
	public void setUrlId(String urlId);	
}
