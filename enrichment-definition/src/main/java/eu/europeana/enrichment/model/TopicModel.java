package eu.europeana.enrichment.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import dev.morphia.annotations.Embedded;
import eu.europeana.enrichment.exceptions.UnsupportedEntityTypeException;
import eu.europeana.enrichment.model.impl.TopicModelImpl;

@JsonDeserialize(as = TopicModelImpl.class)
@Embedded
public interface TopicModel {
	
	public String getURL();
	public void setURL(String url);
	
	public String getIdentifier();
	public void setIdentifier(String id);
	
	public String getDescription();
	public void setDescription(String descr);
	
	public String getAlgorithm();
	public void setAlgorithm(String alg) throws UnsupportedEntityTypeException;
	
}