package eu.europeana.enrichment.web.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Resource;

import eu.europeana.enrichment.common.definitions.NamedEntity;
import eu.europeana.enrichment.common.model.NamedEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentNamedEntityService;
import eu.europeana.enrichment.ner.service.NERService;
import eu.europeana.enrichment.ner.service.impl.NERDBpediaSpotlightServiceImpl;
import eu.europeana.enrichment.ner.service.impl.NERPythonServiceImpl;
import eu.europeana.enrichment.ner.service.impl.NERStanfordServiceImpl;
import eu.europeana.enrichment.web.service.EnrichmentNERService;
import org.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;


public class EnrichmentNERServiceImpl implements EnrichmentNERService{

	//@Resource
	NERService stanfordNerModel3Service;
	NERService stanfordNerModel4Service;
	NERService stanfordNerModel7Service;
	private static final String stanfordNerModel3 = "Stanford_NER_model_3";
	private static final String stanfordNerModel4 = "Stanford_NER_model_4";
	private static final String stanfordNerModel7 = "Stanford_NER_model_7";
	
	NERService dbpediaSpotlightService;
	private static final String dbpediaSpotlightName = "DBpedia_Spotlight";
	
	NERService pythonService;
	private static final String spaCyName = "spaCy";
	private static final String nltkName = "nltk";
	
	@Resource(name = "persistentNamedEntityService")
	PersistentNamedEntityService persistentNamedEntityService;
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		//saveService = new PersistentNamedEntityServiceImpl();
		//Initialize default NER model
		stanfordNerModel3Service = new NERStanfordServiceImpl();
		stanfordNerModel3Service.init();
		stanfordNerModel4Service = new NERStanfordServiceImpl();
		stanfordNerModel4Service.init(NERStanfordServiceImpl.classifier_model_4);
		stanfordNerModel7Service = new NERStanfordServiceImpl();
		stanfordNerModel7Service.init(NERStanfordServiceImpl.classifier_model_7);
		
		dbpediaSpotlightService = new NERDBpediaSpotlightServiceImpl();
		
		pythonService = new NERPythonServiceImpl();
	}

	@Cacheable("nerResults")
	@Override
	public String annotateText(String text, String tool) {
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
			case dbpediaSpotlightName:
				map = dbpediaSpotlightService.identifyNER(text);
				break;
			case spaCyName:
			case nltkName:
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
		
		for (String key : entitiesWithPositions.keySet()) {
			for (NamedEntity entity : entitiesWithPositions.get(key)) {
				entity.setClassificationType(key);
				
				//Check if NamedEntity already exist
				NamedEntity dbEntity = persistentNamedEntityService.findNamedEntity(entity.getKey());
				if(dbEntity != null) {
					System.out.println("NamedEntity ("+ entity.getKey() +") already exist.");
				}
				else
					persistentNamedEntityService.saveNamedEntity(entity);
			}
		}
		
		return new JSONObject(map).toString();
	}

}
