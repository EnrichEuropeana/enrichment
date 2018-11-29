package eu.europeana.enrichment.ner.service;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import eu.europeana.enrichment.ner.exception.NERAnnotateException;
import eu.europeana.enrichment.common.definitions.NamedEntity;
import eu.europeana.enrichment.common.model.NamedEntityImpl;

public interface NERService {

	public void init();
	public void init(String model);
	
	/**
	 * This method identifies named entities.
	 * 
	 * @param text
	 * @return a TreeMap with all findings
	 * @throws NERAnnotateException	
	 */
	public TreeMap<String, TreeSet<String>> identifyNER(String text) throws NERAnnotateException;
	
	/*
	 * This methods is the default implementation for getting the positions of the NER entities
	 * on the original text
	 * 
	 * @param findings are the NER findings which are generated throw the identifyNER function
	 * @param originalText is the transcribed text where the NER needs to be located
	 * @return all findings including their original position at the transcribed text
	 */
	default TreeMap<String, ArrayList<NamedEntity>> getPositions(TreeMap<String, TreeSet<String>> findings, String originalText){
		TreeMap<String, ArrayList<NamedEntity>> entitiesWithPositions = new TreeMap<String, ArrayList<NamedEntity>>();
		
		//TODO: report named entities which we could not find in the original text
		TreeMap<String, TreeSet<String>> notFoundEntities = new TreeMap<String, TreeSet<String>>();
		
		for (Map.Entry<String, TreeSet<String>> classificiationDict : findings.entrySet()) {
			String classification = classificiationDict.getKey();
			TreeSet<String> entities = classificiationDict.getValue();
			
			ArrayList<NamedEntity> newEntities = new ArrayList<NamedEntity>();
			TreeSet<String> notFound = new TreeSet<String>();
			
			for(String entityKey : entities) {
				NamedEntity nerEntity = new NamedEntityImpl(entityKey);
				int index = originalText.indexOf(entityKey);
				while(index >= 0) {
					nerEntity.addPosition(index);
					index = originalText.indexOf(entityKey, index+entityKey.length());
				}
				if(nerEntity.getPositions().size() > 0)
					newEntities.add(nerEntity);
				else
					notFound.add(entityKey);
			}
			entitiesWithPositions.put(classification, newEntities);
			notFoundEntities.put(classification, notFound);
		}
		
		
		return entitiesWithPositions;
	}
}
