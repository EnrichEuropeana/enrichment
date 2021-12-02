package eu.europeana.enrichment.model;

import java.util.List;
import java.util.Map;
import java.util.Date;

public interface TopicEntity {
	
	public String getID();
	public void setID(String id);
	
	public String getIdentifier();
	public void setIdentifier(String identifier);
	
	public List<String> getLabel();
	public void setLabel(List<String> label);
	
	public Map<String,String> getDescription();
	public void setDescription(Map<String,String> descr);
	
	public List<TermEntity> getTopicTerms();
	public void setTopicTerms(List<TermEntity> terms);
	
	// keywords from transcribathon that are related to this topic
	public List<TermEntity> getTopicKeywords();
	public void setTopicKeywords(List<TermEntity> keywords);
	
	public TopicModel getTopicModel();
	public void setTopicModel (TopicModel model);
	
	public Date getCreatedDate();
	public Date getModifiedDate();
}
