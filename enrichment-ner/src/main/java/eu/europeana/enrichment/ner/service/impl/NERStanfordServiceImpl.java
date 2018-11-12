package eu.europeana.enrichment.ner.service.impl;

import java.io.File;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import eu.europeana.enrichment.ner.enumeration.NERClassification;
import eu.europeana.enrichment.ner.exception.NERAnnotateException;
import eu.europeana.enrichment.ner.service.NERService;


public class NERStanfordServiceImpl implements NERService{

	/*
	 * All types of classifier (Person, Location, Organization, ... )
	 */
	public static final String classifier_model_3 = "src/main/resources/classifiers/stanford/english.all.3class.distsim.crf.ser.gz";
	public static final String classifier_model_4 = "src/main/resources/classifiers/stanford/english.conll.4class.distsim.crf.ser.gz";
	public static final String classifier_model_7 = "src/main/resources/classifiers/stanford/english.muc.7class.distsim.crf.ser.gz";
	
	private CRFClassifier<CoreLabel> classifier;
	
	public NERStanfordServiceImpl() {
		File file = new File(classifier_model_3);
		classifier = CRFClassifier.getClassifierNoExceptions(file.getAbsolutePath());
	}
	
	public NERStanfordServiceImpl(String model) {
		File file = new File(model);
		classifier = CRFClassifier.getClassifierNoExceptions(file.getAbsolutePath());
	}
	
	@Override
	public TreeMap<String, TreeSet<String>> identifyNER(String text) throws NERAnnotateException {
		List<List<CoreLabel>> classify = classifier.classify(text);
		return processClassifiedResult(classify);
	}
	
	/*
	 * This methods combines words and creates a TreeMap based on the classification
	 * 
	 * @param classify contains all words including their classification
	 * @return a TreeMap with all relevant words
	 * @throws NERAnnotateException
	 */
	//TODO: check where exception could appear
	private TreeMap<String, TreeSet<String>> processClassifiedResult(List<List<CoreLabel>> classify) throws NERAnnotateException{
		TreeMap<String, TreeSet<String>> map = new TreeMap<String, TreeSet<String>>();
		
		String previousWord = "";
		String previousCategory = "";
		for (List<CoreLabel> coreLabels : classify) {
			for (CoreLabel coreLabel : coreLabels) {
				String word = coreLabel.word();
				String category = coreLabel.get(CoreAnnotations.AnswerAnnotation.class);
				// Check if previous word is from the same category
				if (category.equals(previousCategory) && (category.equals(NERClassification.PERSON.toString()) || 
						category.equals(NERClassification.LOCATION.toString()) || 
						category.equals(NERClassification.ORGANIZATION.toString()) || 
						category.equals(NERClassification.MISC.toString()))) {
					word = previousWord + " " + word;
					map.get(category).remove(previousWord);
				}
				
				previousWord = word;
				previousCategory = category;
				
				if (!"O".equals(category)) {
					if (map.containsKey(category)) {
						// key is already their just insert in arraylist
						map.get(category).add(word);
					} else {
						TreeSet<String> temp = new TreeSet<String>();
						temp.add(word);
						map.put(category, temp);
					}
					//System.out.println(word + ":" + category);
				}
			}
		}
		return map;
	}
	
}
