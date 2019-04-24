package eu.europeana.enrichment.ner.service.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Resource;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import eu.europeana.enrichment.ner.enumeration.NERClassification;
import eu.europeana.enrichment.ner.enumeration.NERStanfordClassification;
import eu.europeana.enrichment.ner.exception.NERAnnotateException;
import eu.europeana.enrichment.ner.service.NERService;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.solr.service.SolrEntityPositionsService;


public class NERStanfordServiceImpl implements NERService{

	private CRFClassifier<CoreLabel> classifier;
	
	/*
	 * This class constructor loads a model for the Stanford named
	 * entity recognition and classification
	 */
	public NERStanfordServiceImpl(String model) {
		if(model == null || model.isEmpty()) {
			System.err.println("NERStanfordServiceImp: No model for classifier defined");
		}
		else {
			//URL url = NERStanfordServiceImpl.class.getClassLoader().getResource(model);
			//classifier = CRFClassifier.getClassifierNoExceptions(url.getPath());
			classifier = CRFClassifier.getClassifierNoExceptions(model);
		}
	}
		
	@Override
	public TreeMap<String, List<List<String>>> identifyNER(String text) throws NERAnnotateException {
		List<List<CoreLabel>> classify = classifier.classify(text);
		return processClassifiedResult(classify);
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
