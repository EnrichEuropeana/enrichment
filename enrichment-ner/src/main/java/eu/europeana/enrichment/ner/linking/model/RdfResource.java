package eu.europeana.enrichment.ner.linking.model;

import javax.xml.bind.annotation.XmlAttribute;

//@JsonIgnoreProperties(ignoreUnknown=true)
public class RdfResource {

	//@XmlAttribute(name = XmlConstants.RESOURCE, namespace = XmlConstants.NAMESPACE_RDF)
	@XmlAttribute(name = XmlConstants.RESOURCE, namespace = XmlConstants.NAMESPACE_RDF)
	String resourceUrl;

	//@JacksonXmlProperty(isAttribute = true, namespace = "rdf", localName = XmlConstants.RESOURCE)
	public String getResourceUrl() {
		return resourceUrl;
	}
	
}
