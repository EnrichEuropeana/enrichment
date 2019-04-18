package eu.europeana.enrichment.ner.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Resource;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import eu.europeana.enrichment.ner.enumeration.NERClassification;
import eu.europeana.enrichment.ner.enumeration.NERStanfordClassification;
import eu.europeana.enrichment.ner.exception.NERAnnotateException;
import eu.europeana.enrichment.ner.service.NERService;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.solr.service.SolrEntityPositionsService;
import eu.fbk.dh.tint.runner.TintPipeline;
import eu.fbk.dh.tint.runner.TintRunner;


public class NERStanfordServiceItalianImpl implements NERService{

	private CRFClassifier<CoreLabel> classifier;
	private TintPipeline pipeline; 
	/*
	 * This class constructor loads a model for the Stanford named
	 * entity recognition and classification
	 */
	public NERStanfordServiceItalianImpl() {

		// Initialize the Tint pipeline
		pipeline = new TintPipeline();

		// Load the default properties
		// see https://github.com/dhfbk/tint/blob/master/tint-runner/src/main/resources/default-config.properties
		try {
			pipeline.loadDefaultProperties();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		// Add a custom property
		// pipeline.setProperty("my_property", "my_value");
		//pipeline.setProperty("ner.applyFineGrained", "0");
		pipeline.setProperty("annotators", "ner");

		// Load the models
		pipeline.load();
	}
		
	@Override
	public TreeMap<String, List<List<String>>> identifyNER(String text) throws NERAnnotateException {
		
		TreeMap<String, List<List<String>>> map = new TreeMap<String, List<List<String>>>();

		// Use for example a text in a String
		text = "I topi non avevano nipoti. Il mio nome è Nicolo Rossi.";

		// Get the original Annotation (Stanford CoreNLP)
		Annotation stanfordAnnotation = pipeline.runRaw(text);

		// **or**

		// Get the JSON
		// (optionally getting the original Stanford CoreNLP Annotation as return value)
		InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
		try {
			Annotation annotation = pipeline.run(stream, System.out, TintRunner.OutputFormat.JSON);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
			
			
		int i=9;
		
		return map;
	}
	
	/*
	 * This methods combines words and creates a TreeMap based on the classification
	 * 
	 * @param classify 					contains all words including their classification
	 * @return 							a TreeMap with all relevant words
	 * @throws 							NERAnnotateException
	 */
	//TODO: check where exception could appear
	private TreeMap<String, List<List<String>>> processClassifiedResult(List<List<CoreLabel>> classify) throws NERAnnotateException{
		TreeMap<String, List<List<String>>> map = new TreeMap<String, List<List<String>>>();
		
		String previousWord = "";
		String previousCategory = "";
		int previousOffset=-1;
		
		for (List<CoreLabel> coreLabels : classify) {
			for (CoreLabel coreLabel : coreLabels) {	
				
				int wordOffset=coreLabel.beginPosition();
				
				String word = coreLabel.word();
				String category = coreLabel.get(CoreAnnotations.AnswerAnnotation.class);
				// Check if previous word is from the same category
				String originalCategory = category;
				if(NERStanfordClassification.isAgent(category))
					category = NERClassification.AGENT.toString();
				else if(NERStanfordClassification.isPlace(category))
					category = NERClassification.PLACE.toString();
				else if(NERStanfordClassification.isOrganization(category))
					category = NERClassification.ORGANIZATION.toString();
				else if(NERStanfordClassification.isMisc(category))
					category = NERClassification.MISC.toString();
				
				if (category.equals(previousCategory) && (NERStanfordClassification.isAgent(originalCategory) || 
						NERStanfordClassification.isPlace(originalCategory) ||
						NERStanfordClassification.isOrganization(originalCategory) ||
						NERStanfordClassification.isMisc(originalCategory))) {
					word = previousWord + " " + word;
					wordOffset=previousOffset;
					
					List<String> wordWithPositionPrevious = new ArrayList<String>();
					wordWithPositionPrevious.add(previousWord);
					wordWithPositionPrevious.add(String.valueOf(previousOffset));
					map.get(category).remove(wordWithPositionPrevious);
				}
				
				previousWord = word;
				previousCategory = category;
				previousOffset = wordOffset;
				
				if (!"O".equals(category)) {
					
					
					List<String> wordWithPosition = new ArrayList<String>();
					wordWithPosition.add(word);
					wordWithPosition.add(String.valueOf(wordOffset));
							
					
					if (map.containsKey(category)) {
						// key is already their just insert in the list {word,position}
						map.get(category).add(wordWithPosition);
					} else {
						List<List<String>> temp = new ArrayList<List<String>>();					
						temp.add(wordWithPosition);
						map.put(category, temp);
					}
					//System.out.println(word + ":" + category);
				}
			}
		}
		return map;
	}
	
}
