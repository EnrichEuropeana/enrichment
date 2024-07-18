package eu.europeana.enrichment.definitions.model.impl;

import java.util.Objects;

import dev.morphia.annotations.Embedded;
import eu.europeana.enrichment.definitions.exceptions.UnsupportedRangeTermEntityException;

//@Entity(value="TermEntityImpl")
//@Embedded
@Embedded
public class TermImpl {
	
	// must be UTF-8 aware to allow special chars
	private String term;
	// between 1 and 250
	private Integer rank;
	// between 0 and 100
	private Double score;
	
	
	public TermImpl()
	{
		
	}
	
	public TermImpl(String term, Integer rank, Double score) {
//		if (!(1<=rank.intValue() && rank.intValue()<=250))
//			throw new UnsupportedRangeTermEntityException("Range not in [1,250]");
//		
//		if (!(0<=score.intValue() && score.intValue()<=100))
//			throw new UnsupportedRangeTermEntityException("Score not in [0,100]");
		
		this.rank = rank;
		this.score = score;
		this.term = term;
	}

	@Override
	public int hashCode() {
		return Objects.hash(rank, score, term);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TermImpl other = (TermImpl) obj;
		return Objects.equals(rank, other.rank) && Objects.equals(score, other.score)
				&& Objects.equals(term, other.term);
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;

	}

	public Double getScore() {
		return this.score;
	}

	public void setScore(Double score) throws UnsupportedRangeTermEntityException {
//		if (!(0<=score.intValue() && score.intValue()<=100))
//			throw new UnsupportedRangeTermEntityException("Score not in [0,100]");
		this.score = score;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) throws UnsupportedRangeTermEntityException {
//		if (!(1<=rank.intValue() && rank.intValue()<=250))
//			throw new UnsupportedRangeTermEntityException("Range not in [1,250]");
		this.rank = rank;
	}
}
