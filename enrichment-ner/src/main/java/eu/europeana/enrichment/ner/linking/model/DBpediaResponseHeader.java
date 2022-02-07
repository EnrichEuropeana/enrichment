package eu.europeana.enrichment.ner.linking.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//@JsonIgnoreProperties(ignoreUnknown=true)
@XmlRootElement(namespace = XmlConstants.NAMESPACE_RDF, name = XmlConstants.RDF)
@XmlAccessorType(XmlAccessType.FIELD)
public class DBpediaResponseHeader {

//	@JacksonXmlProperty(localName = "Description")
//	DBpediaResponse result;

	@XmlElement(namespace = XmlConstants.NAMESPACE_RDF, name = XmlConstants.XML_DESCRIPTION)
	private DBpediaResponse result;

	//@JacksonXmlProperty(namespace = "rdf", localName = XmlConstants.XML_SAME_DESCRIPTION)
	public DBpediaResponse getResult() {
		return result;
	}
	
}
