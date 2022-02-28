package eu.europeana.enrichment.ner.linking.model;

import java.util.List;
import java.util.TreeMap;

import eu.europeana.enrichment.model.impl.NamedEntityImpl;

public class StanfordClassificationHeader {

	private TreeMap<String, List<NamedEntityImpl>> stanfordResult;
	
	public TreeMap<String, List<NamedEntityImpl>> getResult(){
		return stanfordResult;
	}
	

}
