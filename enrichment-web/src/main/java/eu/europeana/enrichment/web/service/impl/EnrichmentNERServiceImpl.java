package eu.europeana.enrichment.web.service.impl;

import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Resource;

import eu.europeana.enrichment.common.definitions.NamedEntity;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityService;
import eu.europeana.enrichment.ner.service.NERService;
import eu.europeana.enrichment.web.service.EnrichmentNERService;
import org.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;


public class EnrichmentNERServiceImpl implements EnrichmentNERService{

	@Resource(name = "stanfordNerModel3Service")
	NERService stanfordNerModel3Service;
	@Resource(name = "stanfordNerModel4Service")
	NERService stanfordNerModel4Service;
	@Resource(name = "stanfordNerModel7Service")
	NERService stanfordNerModel7Service;
	@Resource(name = "stanfordNerGermanModelService")
	NERService stanfordNerGermanModelService;
	private static final String stanfordNerModel3 = "Stanford_NER_model_3";
	private static final String stanfordNerModel4 = "Stanford_NER_model_4";
	private static final String stanfordNerModel7 = "Stanford_NER_model_7";
	private static final String stanfordNerModelGerman = "Stanford_NER_model_German";
	
	
	@Resource(name = "dbpediaSpotlightService")
	NERService dbpediaSpotlightService;
	private static final String dbpediaSpotlightName = "DBpedia_Spotlight";
	
	@Resource(name = "pythonService")
	NERService pythonService;
	private static final String spaCyName = "spaCy";
	private static final String nltkName = "nltk";
	private static final String flairName = "flair";
	
	@Resource(name = "persistentNamedEntityService")
	PersistentNamedEntityService persistentNamedEntityService;
	
	//@Cacheable("nerResults")
	@Override
	public String getEntities(String text, String tool) {
		TreeMap<String, TreeSet<String>> map;
		switch(tool){
			case stanfordNerModel3:
				map = stanfordNerModel3Service.identifyNER(text);
				break;
			case stanfordNerModel4:
				map = stanfordNerModel4Service.identifyNER(text);
				break;
			case stanfordNerModel7:
				map = stanfordNerModel7Service.identifyNER(text);
				break;
			case stanfordNerModelGerman:
				map = stanfordNerGermanModelService.identifyNER(text);
				break;
			case dbpediaSpotlightName:
				map = dbpediaSpotlightService.identifyNER(text);
				break;
			case spaCyName:
			case nltkName:
			case flairName:
				JSONObject jsonRequest = new JSONObject();
				jsonRequest.put("tool", tool);
				jsonRequest.put("text", text);
				map = pythonService.identifyNER(jsonRequest.toString());
				break;
			default:
				//TODO:Return tool is not supported
				map = new TreeMap<String, TreeSet<String>>();
				break;
		}
		
		TreeMap<String, List<NamedEntity>> entitiesWithPositions = stanfordNerModel3Service.getPositions(map, text);
		//stanfordNerModel3Service.addInformation(entitiesWithPositions);
		/*
		for (String key : entitiesWithPositions.keySet()) {
			for (NamedEntity entity : entitiesWithPositions.get(key)) {
				entity.setType(key);
				
				//Check if NamedEntity already exist
				NamedEntity dbEntity = persistentNamedEntityService.findNamedEntity(entity.getKey());
				if(dbEntity != null) {
					System.out.println("NamedEntity ("+ entity.getKey() +") already exist.");
				}
				else
					persistentNamedEntityService.saveNamedEntity(entity);
			}
		}
		*/
		return new JSONObject(map).toString();
	}

}
