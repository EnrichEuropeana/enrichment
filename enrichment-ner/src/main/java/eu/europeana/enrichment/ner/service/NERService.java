package eu.europeana.enrichment.ner.service;

import java.util.TreeMap;
import java.util.TreeSet;

import eu.europeana.enrichment.ner.exception.NERAnnotateException;

public interface NERService {

	public static final String HANDLER_ANNOTATE = "/annotate";
	
	
	/**
	 * This method identifies named entities.
	 * 
	 * @param text
	 * @return a TreeMap with all findings
	 * @throws NERAnnotateException	
	 */
	public TreeMap<String, TreeSet<String>> identifyNER(String text) throws NERAnnotateException;
	
	
}
