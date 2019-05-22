package eu.europeana.enrichment.ner.linking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DBpediaResponseHeader {

	@JacksonXmlProperty(localName = "Description")
	DBpediaResponse result;

	@JsonIgnore
	public DBpediaResponse getResult() {
		return result;
	}
	
}
