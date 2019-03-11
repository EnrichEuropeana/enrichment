package eu.europeana.enrichment.solr.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class JavaJSONParser {

	@SuppressWarnings({ "unchecked", "serial" })
	public Map<String, List<Integer>> getPositionsFromJSON(QueryResponse response, String storyIdFieldName, String fieldName) throws ParseException {
		
		Gson gson = new Gson(); 
		String jsonResponse = gson.toJson(response.getResponse()); 
		
		Map<String, Object> retMap = new Gson().fromJson(
				jsonResponse, new TypeToken<HashMap<String, Object>>() {}.getType()
			);
		
		List<Object> topObjects = (List<Object>) retMap.get("nvPairs");
		Map<String, Object> stories= (Map<String, Object>) topObjects.get(5);
		List<Object> fields = (List<Object>) stories.get("nvPairs");
		Map<String, Object> fields2 = (Map<String, Object>) fields.get(1);
		List<Object> fields3 = (List<Object>) fields2.get("nvPairs");
		
		Map<String, Object> field4 = (Map<String, Object>) fields3.get(1);
		List<Object> field5 = (List<Object>) field4.get("nvPairs");
		List<String> terms = (List<String>) field5.get(1);
		List<Double> positions = (List<Double>) field5.get(3);
		List<Double> offsets = (List<Double>) field5.get(5);


		
		return null;
	}
}
