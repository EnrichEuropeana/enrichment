package eu.europeana.enrichment.ner.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import eu.europeana.enrichment.ner.exception.NERAnnotateException;
import eu.europeana.enrichment.model.NamedEntity;
import eu.europeana.enrichment.model.PositionEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.TranslationEntity;
import eu.europeana.enrichment.mongo.model.NamedEntityImpl;
import eu.europeana.enrichment.mongo.model.PositionEntityImpl;

public interface NERService {

	/**
	 * This method identifies named entities based on the translated text.
	 * 
	 * @param text					translated text in English
	 * @return 						a TreeMap based on the classification type
	 * 								including all named entities findings
	 * @throws NERAnnotateException	
	 */
	public TreeMap<String, List<List<String>>> identifyNER(String text) throws NERAnnotateException;
	
	/*
	 * This methods is the default implementation for getting the positions of the NER entities
	 * on the original text
	 * 
	 * @param 						findings are the NER findings which are 
	 * 								generated throw the identifyNER function
	 * @param 						originalText is the transcribed text where 
	 * 								the NER needs to be located
	 * @return 						all findings including their original 
	 * 								position at the transcribed text
	 */
	default void getPositions(NamedEntity namedEntity, StoryEntity storyEntity, TranslationEntity translationEntity){
		//TODO: report named entities which we could not find in the original text
		
		String text;
		if(storyEntity != null)
			text = storyEntity.getStoryTranscription();
		else if(translationEntity != null)
			text = translationEntity.getTranslatedText();
		else {
			//TODO: proper exception handling
			return;
		}

		PositionEntity posEntity;
		if(storyEntity != null) {
			List<PositionEntity> positions = namedEntity.getPositionEntities().stream().filter(x -> x.getStoryId()
					.equals(storyEntity.getStoryId())).collect(Collectors.toList());
			if(positions.size() == 0) {
				posEntity = new PositionEntityImpl();
				posEntity.setStoryEntity(storyEntity);
				positions.add(posEntity);
			}
			else {
				posEntity = positions.get(0);
			}
		}
		else {
			List<PositionEntity> positions = namedEntity.getPositionEntities().stream().filter(x -> x.getTranslationKey()
					.equals(translationEntity.getKey())).collect(Collectors.toList());
			if(positions.size() == 0) {
				posEntity = new PositionEntityImpl();
				posEntity.setTranslationEntity(translationEntity);
				positions.add(posEntity);
			}
			else {
				posEntity = positions.get(0);
			}
		}
		String entityKey = namedEntity.getKey();
		int index = text.indexOf(entityKey);
		while(index >= 0) {
			posEntity.addOfssetPosition(index);
			index = text.indexOf(entityKey, index+entityKey.length());
		}

	}
}
