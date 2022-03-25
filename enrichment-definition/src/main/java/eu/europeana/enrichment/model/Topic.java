package eu.europeana.enrichment.model;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import eu.europeana.enrichment.model.impl.TopicImpl;

import java.util.Date;

@JsonDeserialize(as = TopicImpl.class)
public interface Topic {
	
	public String getTopicID();
	public void setTopicID(String id);
	
	public String getIdentifier();
	public void setIdentifier(String identifier);
	
	public List<String> getLabel();
	public void setLabel(List<String> label);
	
	public Map<String,String> getDescription();
	public void setDescription(Map<String,String> descr);
	
	public List<Term> getTopicTerms();
	public void setTopicTerms(List<Term> terms);
	
	// keywords from transcribathon that are related to this topic
	public List<Term> getTopicKeywords();
	public void setTopicKeywords(List<Term> keywords);
	
	public TopicModel getTopicModel();
	public void setTopicModel (TopicModel model);
	
	public Date getCreatedDate();
	public Date getModifiedDate();
	
	public void setCreatedDate(Date date);
	public void setModifiedDate(Date date);
	public ObjectId getObjectId();
	//public String toJSON() throws JsonProcessingException;
	
	public String getModelId();
	public void setModelId(String modelId);
}
