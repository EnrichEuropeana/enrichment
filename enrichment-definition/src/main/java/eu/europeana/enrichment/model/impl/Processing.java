package eu.europeana.enrichment.model.impl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import eu.europeana.enrichment.model.vocabulary.EnrichmentFields;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@Embedded
public class Processing {

	@Property(EnrichmentFields.FOUND_BY_NER_TOOLS)
	private List<String> foundByNerTools;
	
	private String matching;
	private double score;

	public List<String> getFoundByNerTools() {
		return foundByNerTools;
	}
	public void setFoundByNerTools(List<String> foundByNerTools) {
		this.foundByNerTools = foundByNerTools;
	}
	public String getMatching() {
		return matching;
	}
	public void setMatching(String matching) {
		this.matching = matching;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}	
	
}
