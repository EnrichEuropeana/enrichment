package eu.europeana.enrichment.definitions.model;

import java.util.List;

public interface AnnotationCollection {

	List<NamedEntityAnnotation> getItems ();
	
	void setItems (List<NamedEntityAnnotation> items);
}
