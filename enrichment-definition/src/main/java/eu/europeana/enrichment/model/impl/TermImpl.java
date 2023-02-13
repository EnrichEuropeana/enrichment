package eu.europeana.enrichment.model.impl;

import java.util.Objects;

import eu.europeana.enrichment.exceptions.UnsupportedRangeTermEntityException;
import eu.europeana.enrichment.model.Term;

//@Entity(value="TermEntityImpl")
//@Embedded
public class TermImpl implements Term {
	
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

	@Override
	public String getTerm() {
		return term;
	}

	@Override
	public void setTerm(String term) {
		this.term = term;

	}

	@Override
	public Double getScore() {
		return this.score;
	}

	@Override
	public void setScore(Double score) throws UnsupportedRangeTermEntityException {
//		if (!(0<=score.intValue() && score.intValue()<=100))
//			throw new UnsupportedRangeTermEntityException("Score not in [0,100]");
		this.score = score;
	}

	@Override
	public Integer getRank() {
		return rank;
	}

	@Override
	public void setRank(Integer rank) throws UnsupportedRangeTermEntityException {
//		if (!(1<=rank.intValue() && rank.intValue()<=250))
//			throw new UnsupportedRangeTermEntityException("Range not in [1,250]");
		this.rank = rank;
	}

}
