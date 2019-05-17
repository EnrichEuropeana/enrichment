package eu.europeana.enrichment.solr.commons;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.QueryResponse;

import org.json.simple.parser.ParseException;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class JavaJSONParser {

	private Gson gson; 
	
	public JavaJSONParser () {
		gson = new Gson(); 
	}
	
	@SuppressWarnings({ "unchecked", "serial" })
	public void getPositionsFromJSON(QueryResponse response, List<String> terms, List<Double> positions, List<List<Double>> offsets) throws ParseException {
				
		if(response==null) return; 
				
		String jsonResponse = gson.toJson(response.getResponse());

		
//		Map<String, Object> retMap = new Gson().fromJson(
//				jsonResponse, new TypeToken<HashMap<String, Object>>() {}.getType()
//			);

		Map<String, Object> retMap = gson.fromJson(
			jsonResponse, new TypeToken<HashMap<String, Object>>() {}.getType()
		);

		List<Object> topObjects = (List<Object>) retMap.get("nvPairs");
		//the part of the json that contains "highlighting" text
		Map<String, Object> highlighting = (Map<String, Object>) topObjects.get(5);
		List<Object> highlightingObjects = (List<Object>) highlighting.get("nvPairs");
		if(!(highlightingObjects.isEmpty() || highlightingObjects==null))
		{
			Map<String, Object> highlightingField = (Map<String, Object>) highlightingObjects.get(1);
			List<Object> highlightingFieldObjects = (List<Object>) highlightingField.get("nvPairs");
			
			Map<String, Object> highlightingFieldOffsets = (Map<String, Object>) highlightingFieldObjects.get(1);
			List<Object> highlightingFieldOffsetsObjects = (List<Object>) highlightingFieldOffsets.get("nvPairs");
			List<String> termsJson = (List<String>) highlightingFieldOffsetsObjects.get(1);
			List<Double> positionsJson = (List<Double>) highlightingFieldOffsetsObjects.get(3);
			List<List<Double>> offsetsJson = (List<List<Double>>) highlightingFieldOffsetsObjects.get(5);
		
			terms.addAll(termsJson);
			positions.addAll(positionsJson);
			offsets.addAll(offsetsJson);
		}
		
	}
	
	@SuppressWarnings({ "serial", "unchecked" })
	public List<Map<String, Object>> getStoriesAndItemsFromJSON (Reader reader) {
			
		Type collectionType = new TypeToken <List<Map<String, Object>>>() {}.getType();
		List<Map<String, Object>> results = (List<Map<String, Object>>) new Gson()
		               .fromJson( reader , collectionType);
		
		return results;	
		
	}
		 

}
