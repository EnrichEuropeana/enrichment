package eu.europeana.enrichment.web.model;

public class EnrichmentNERRequest {

	public String text;
	public String tool;
	
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
	
}
