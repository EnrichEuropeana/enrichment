package eu.europeana.enrichment.definitions.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import dev.morphia.annotations.Embedded;
import eu.europeana.enrichment.definitions.exceptions.UnsupportedRangeTermEntityException;
import eu.europeana.enrichment.definitions.model.impl.TermImpl;

@JsonDeserialize(as = TermImpl.class)
@Embedded
public interface Term {
	
	public String getTerm();
	public void setTerm(String term);
	
	public Double getScore();
	public void setScore(Double score) throws UnsupportedRangeTermEntityException;
	
	public Integer getRank();
	public void setRank(Integer rank) throws UnsupportedRangeTermEntityException;
	
	

}
