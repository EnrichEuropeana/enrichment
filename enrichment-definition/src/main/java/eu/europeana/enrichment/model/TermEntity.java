package eu.europeana.enrichment.model;

import dev.morphia.annotations.Embedded;
import eu.europeana.enrichment.exceptions.UnsupportedRangeTermEntityException;

@Embedded
public interface TermEntity {
	

	
	public String getTerm();
	public void setTerm(String term);
	
	public Integer getScore();
	public void setScore(Integer score) throws UnsupportedRangeTermEntityException;
	
	public Integer getRank();
	public void setRank(Integer rank) throws UnsupportedRangeTermEntityException;
	
	

}
