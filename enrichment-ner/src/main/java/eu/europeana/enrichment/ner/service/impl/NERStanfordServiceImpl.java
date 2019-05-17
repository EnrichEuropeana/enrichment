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
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.model.impl.PositionEntityImpl;
import eu.europeana.enrichment.ner.enumeration.NERClassification;
import eu.europeana.enrichment.ner.enumeration.NERStanfordClassification;
import eu.europeana.enrichment.ner.exception.NERAnnotateException;
import eu.europeana.enrichment.ner.service.NERService;
//import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
//import eu.europeana.enrichment.solr.service.SolrEntityPositionsService;


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
			classifier = CRFClassifier.getClassifierNoExceptions(model);
		}
	}
		
	@Override
	public TreeMap<String, List<NamedEntity>> identifyNER(String text) throws NERAnnotateException {
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
	private TreeMap<String, List<NamedEntity>> processClassifiedResult(List<List<CoreLabel>> classify) throws NERAnnotateException{
		TreeMap<String, List<NamedEntity>> map = new TreeMap<>();
		
		String previousWord = "";
		String previousCategory = "";
		NamedEntity previousNamedEntity;
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
					
					map.get(category).remove(map.get(category).size()-1);
				}
				
				previousWord = word;
				previousCategory = category;
				previousOffset = wordOffset;
				
				NamedEntity namedEntity = new NamedEntityImpl(word);
				namedEntity.setType(category);
				PositionEntity positionEntity = new PositionEntityImpl();
				// default: Offset position will be added to the translated 
				positionEntity.addOfssetsTranslatedText(wordOffset);
				namedEntity.addPositionEntity(positionEntity);
				previousNamedEntity = namedEntity;
				
				if (!"O".equals(category)) {
					
					List<NamedEntity> tmp;
					
					if (map.containsKey(category)) {
						// key is already their just insert in the list {word,position}
						tmp = map.get(category);
					} else {
						tmp = new ArrayList<>();
						map.put(category, tmp);
					}
					
					NamedEntity alreadyExistNamedEntity = null;
					for(int index = 0; index < tmp.size(); index++) {
						if(tmp.get(index).getKey().equals(namedEntity.getKey())) {
							alreadyExistNamedEntity = tmp.get(index);
							break;
						}
					}
					if(alreadyExistNamedEntity == null)
						tmp.add(namedEntity);
					else 
						alreadyExistNamedEntity.getPositionEntities().get(0).addOfssetsTranslatedText(wordOffset);
				}
			}
		}
		return map;
	}
	
}
