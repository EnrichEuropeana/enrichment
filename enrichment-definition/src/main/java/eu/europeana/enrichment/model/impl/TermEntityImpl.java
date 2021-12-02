package eu.europeana.enrichment.model.impl;

import eu.europeana.enrichment.exceptions.UnsupportedRangeTermEntityException;
import eu.europeana.enrichment.model.TermEntity;

public class TermEntityImpl implements TermEntity {
	
	// must be UTF-8 aware to allow special chars
	private String term;
	// between 1 and 250
	private Integer rank;
	// between 0 and 100
	private Integer score;
	
	public TermEntityImpl(String term, Integer rank, Integer score) throws UnsupportedRangeTermEntityException {
		if (!(1<=rank.intValue() && rank.intValue()<=250))
			throw new UnsupportedRangeTermEntityException("Range not in [1,250]");
		
		if (!(0<=score.intValue() && score.intValue()<=100))
			throw new UnsupportedRangeTermEntityException("Score not in [0,100]");
		
		this.rank = rank;
		this.score = score;
		this.term = term;
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
	public Integer getScore() {
		return this.score;
	}

	@Override
	public void setScore(Integer score) throws UnsupportedRangeTermEntityException {
		if (!(0<=score.intValue() && score.intValue()<=100))
			throw new UnsupportedRangeTermEntityException("Score not in [0,100]");
		this.score = score;
	}

	@Override
	public Integer getRank() {
		return rank;
	}

	@Override
	public void setRank(Integer rank) throws UnsupportedRangeTermEntityException {
		if (!(1<=rank.intValue() && rank.intValue()<=250))
			throw new UnsupportedRangeTermEntityException("Range not in [1,250]");
	}

}
