package eu.europeana.enrichment.web.model;

import java.util.List;

/*
 * This class represents the Rest Post body structure for
 * end point /enrichment/entities. The request body will
 * be parsed into this class.
 */
public class EnrichmentNERRequest {

	public String text;
	public String tool;
	public List<String> linking;
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getTool() {
		return tool;
	}
	public void setTool(String tool) {
		this.tool = tool;
	}
	public List<String> getLinking(){
		return linking;
	}
	public void setLinking(List<String> linking) {
		this.linking = linking;
	}
}
