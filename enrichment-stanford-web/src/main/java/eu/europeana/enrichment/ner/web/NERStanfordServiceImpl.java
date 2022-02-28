package eu.europeana.enrichment.ner.web;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.json.JSONObject;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import eu.europeana.enrichment.model.impl.NamedEntityImpl;
import eu.europeana.enrichment.model.impl.PositionEntityImpl;
import eu.europeana.enrichment.model.vocabulary.NERConstants;
import eu.europeana.enrichment.ner.enumeration.NERClassification;
import eu.europeana.enrichment.ner.enumeration.NERStanfordClassification;

//@Service(AppConfigConstants.BEAN_ENRICHMENT_NER_STANFORD_SERVICE_ORIGIN)
public class NERStanfordServiceImpl{

	private CRFClassifier<CoreLabel> classifier;
	
	/*
	 * This class constructor loads a model for the Stanford named
	 * entity recognition and classification
	 */
	
	public NERStanfordServiceImpl(String model) {
	    if (model == null || model.isEmpty()) {
	    	System.err.println("NERStanfordServiceImp: No model for classifier defined");
	    } else {
	    	this.classifier = CRFClassifier.getClassifierNoExceptions(model);
	    } 
	}

	public String getEntities(String text) {
		List<List<CoreLabel>> classify = classifier.classify(text);
		return new JSONObject(processClassifiedResult(classify)).toString();
	}
	
	/*
	 * This methods combines words and creates a TreeMap based on the classification
	 * 
	 * @param classify 					contains all words including their classification
	 * @return 							a TreeMap with all relevant words
	 * @throws 							NERAnnotateException
	 */
	//TODO: check where exception could appear
	private TreeMap<String, List<NamedEntityImpl>> processClassifiedResult(List<List<CoreLabel>> classify){
		if(classify==null) return null;

		TreeMap<String, List<NamedEntityImpl>> map = new TreeMap<>();
		String previousWord = null;
		String previousCategory = null;
		int previousOffset=-1;
		boolean firstWordFromValidCategory = true;
		
		for (List<CoreLabel> coreLabels : classify) {
			for (CoreLabel coreLabel : coreLabels) {	
				
				int wordOffset=coreLabel.beginPosition();
				String word = coreLabel.word();
				String originalCategory = coreLabel.get(CoreAnnotations.AnswerAnnotation.class);
				
				// Check if previous word is from the same category
				String localCategory = null;
				if(NERStanfordClassification.isAgent(originalCategory))
					localCategory = NERClassification.AGENT.toString();
				else if(NERStanfordClassification.isPlace(originalCategory))
					localCategory = NERClassification.PLACE.toString();
				else if(NERStanfordClassification.isOrganization(originalCategory))
					localCategory = NERClassification.ORGANIZATION.toString();
				else if(NERStanfordClassification.isMisc(originalCategory))
					localCategory = NERClassification.MISC.toString();
				
				//keeping track of the entities with multiple names, like Michael Jeffrey Jordan, which are separatelly identified by the tool
				if (localCategory!=null && (firstWordFromValidCategory || localCategory.equals(previousCategory))) {
					if(firstWordFromValidCategory) {
						firstWordFromValidCategory = false;	
						previousWord = word;
						previousOffset = wordOffset;
						previousCategory = localCategory;
					}
					else {
						previousWord = previousWord + " " + word;
					}				
				}
				else if(previousWord!=null){
					updateNamedEntity(previousWord, previousOffset, previousCategory, map);
					//valid category but different from the previous one
					if (localCategory!=null) {
						previousWord = word;
						previousOffset = wordOffset;
						previousCategory = localCategory;
					}
					else {
						firstWordFromValidCategory=true;
						previousWord=null;
					}
				}
			}
		}
		//update the last found entity
		if(previousWord!=null) {
			updateNamedEntity(previousWord, previousOffset, previousCategory, map); 
		}
		
		return map;
	}
	
	private void updateNamedEntity (String word, int offset, String category, TreeMap<String, List<NamedEntityImpl>> map) {
		List<NamedEntityImpl> tmp;					
		if (map.containsKey(category)) {
			// key is already there just insert in the list {word,position}
			tmp = map.get(category);
		} else {
			tmp = new ArrayList<>();
			map.put(category, tmp);
		}
		
		NamedEntityImpl alreadyExistNamedEntity = null;
		for(int index = 0; index < tmp.size(); index++) {
			if(tmp.get(index).getLabel().equals(word)) {
				alreadyExistNamedEntity = tmp.get(index);
				break;
			}
		}
		if(alreadyExistNamedEntity == null) {
			NamedEntityImpl namedEntity = new NamedEntityImpl(word);
			namedEntity.setType(category);
			//setting the offsets(positions) of the found entity in the text
			if(offset!=-1) {
				PositionEntityImpl positionEntity = new PositionEntityImpl();
				List<Integer> offsetList = new ArrayList<Integer>();
				offsetList.add(offset);
				// default: Offset position will be added to the translated 
				positionEntity.setOffsetsTranslatedText(offsetList);
				List<String> nerTools = new ArrayList<String>();
				nerTools.add(NERConstants.stanfordNer);
				positionEntity.setNerTools(nerTools);
				List<PositionEntityImpl> positionEntities = new ArrayList<PositionEntityImpl>();
				positionEntities.add(positionEntity);
				namedEntity.setPositionEntities(positionEntities);
			}
			tmp.add(namedEntity);
		}
		else {
			if(alreadyExistNamedEntity.getPositionEntities()==null) {
				if(offset!=-1) {
					PositionEntityImpl positionEntity = new PositionEntityImpl();
					List<Integer> offsetList = new ArrayList<Integer>();
					offsetList.add(offset);
					// default: Offset position will be added to the translated 
					positionEntity.setOffsetsTranslatedText(offsetList);
					List<String> nerTools = new ArrayList<String>();
					nerTools.add(NERConstants.stanfordNer);
					positionEntity.setNerTools(nerTools);
					List<PositionEntityImpl> positionEntities = new ArrayList<PositionEntityImpl>();
					positionEntities.add(positionEntity);
					alreadyExistNamedEntity.setPositionEntities(positionEntities);
				}
			}
			else if(!alreadyExistNamedEntity.getPositionEntities().get(0).getOffsetsTranslatedText().contains(offset)) {
				alreadyExistNamedEntity.getPositionEntities().get(0).addOfssetsTranslatedText(offset);
			}
		}
	}
}
