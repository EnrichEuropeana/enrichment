package eu.europeana.enrichment.ner.web.controller;

/*
 * This class represents the Rest Post body structure for
 * end point /enrichment/entities. The request body will
 * be parsed into this class.
 */
public class StanfordRequest {

	private String text;
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
