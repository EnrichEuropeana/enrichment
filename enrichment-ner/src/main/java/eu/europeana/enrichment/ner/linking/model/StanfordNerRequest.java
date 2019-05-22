package eu.europeana.enrichment.ner.linking.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StanfordNerRequest {

	private String text;
	
	public StanfordNerRequest(String text) {
		this.text = text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	@JsonProperty(value = "text")
	public String getText() {
		return text;
	}
}
