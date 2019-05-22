package eu.europeana.enrichment.ner.linking.model;

import java.util.List;
import java.util.TreeMap;

public class StanfordClassificationHeader {

	private TreeMap<String, List<StanfordNamedEntity>> stanfordResult;
	
	public TreeMap<String, List<StanfordNamedEntity>> getResult(){
		return stanfordResult;
	}
	

}
