package eu.europeana.enrichment.web.service.impl;

import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.Resource;

import eu.europeana.enrichment.ner.service.NERService;
import eu.europeana.enrichment.ner.service.impl.NERSpacyServiceImpl;
import eu.europeana.enrichment.ner.service.impl.NERStanfordServiceImpl;
import eu.europeana.enrichment.web.service.EnrichmentNERService;
import org.json.JSONObject;

public class EnrichmentNERServiceImpl implements EnrichmentNERService{

	//@Resource
	NERService stanfordNerModel3Service;
	NERService stanfordNerModel4Service;
	NERService stanfordNerModel7Service;
	private static final String stanfordNerModel3 = "Stanford_NER_model_3";
	private static final String stanfordNerModel4 = "Stanford_NER_model_4";
	private static final String stanfordNerModel7 = "Stanford_NER_model_7";
	
	NERService spaCyService;
	private static final String spaCyName = "spaCy";
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		//Initialize default NER model
		stanfordNerModel3Service = new NERStanfordServiceImpl();
		stanfordNerModel3Service.init();
		stanfordNerModel4Service = new NERStanfordServiceImpl();
		stanfordNerModel4Service.init(NERStanfordServiceImpl.classifier_model_4);
		stanfordNerModel7Service = new NERStanfordServiceImpl();
		stanfordNerModel7Service.init(NERStanfordServiceImpl.classifier_model_7);
		
		spaCyService = new NERSpacyServiceImpl();
	}

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
			case spaCyName:
				map = spaCyService.identifyNER(text);
				break;
			default:
				//TODO:Return tool is not supported
				map = new TreeMap<String, TreeSet<String>>();
				break;
		}
		
		return new JSONObject(map).toString();
	}

}
