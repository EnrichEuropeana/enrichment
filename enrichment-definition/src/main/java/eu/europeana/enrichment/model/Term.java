package eu.europeana.enrichment.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import dev.morphia.annotations.Embedded;
import eu.europeana.enrichment.exceptions.UnsupportedRangeTermEntityException;
import eu.europeana.enrichment.model.impl.TermImpl;

@JsonDeserialize(as = TermImpl.class)
@Embedded
public interface Term {
	
	public String getTerm();
	public void setTerm(String term);
	
	public Integer getScore();
	public void setScore(Integer score) throws UnsupportedRangeTermEntityException;
	
	public Integer getRank();
	public void setRank(Integer rank) throws UnsupportedRangeTermEntityException;
	
	

}
