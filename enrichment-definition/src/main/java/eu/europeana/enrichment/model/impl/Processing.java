package eu.europeana.enrichment.model.impl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Property;
import eu.europeana.enrichment.model.vocabulary.EntityFields;

@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
@Embedded
public class Processing {

	@Property(EntityFields.FOUND_BY_NER_TOOLS)
	private List<String> foundByNerTools;
	
	private String matching;
	private float score;

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
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}	
	
}
