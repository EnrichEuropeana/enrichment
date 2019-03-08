package eu.europeana.enrichment.solr.commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JavaJSONParser {

	@SuppressWarnings("unchecked")
	public Map<String, List<Integer>> getPositionsFromJSON(String jsonContent, String storyIdFieldName, String fieldName) throws ParseException {
		
		/*
		 * key - storyId/itemId, value - list of positions
		 */
		Map<String, List<Integer>> results = new HashMap<String, List<Integer>>();
		
		/*
		 * Parsing the JSON data from the string format
		 */
		JSONParser parse = new JSONParser();
		
		/*
		 * Type caste the parsed json data in json object
		 */
		JSONObject jobj = (JSONObject)parse.parse(jsonContent);
		
		JSONObject jsonResponse = (JSONObject) jobj.get("response");

		JSONObject jsontHighlihgting = (JSONObject) jobj.get("highlighting");

		/*
		 * Store the JSON object in JSON array as objects
		 */
		JSONArray jsonResponseDocs = (JSONArray) jsonResponse.get("docs");
		
		for(int j=0;j<jsonResponseDocs.size();j++)
		{
			JSONObject jsonDoc = (JSONObject) jsonResponseDocs.get(j);
			String storyId = (String) jsonDoc.get(storyIdFieldName);
			
			JSONObject jsonHighlightingItem = (JSONObject) jsontHighlihgting.get(storyId);
						
			List<Integer> jsonHighlightingPositions = (List<Integer>) jsonHighlightingItem.get("positions");
			List<Integer> listOfOffsets = new ArrayList<Integer>();
			if(jsonHighlightingPositions.size()>1)
			{
				List<List<Integer>> jsonHighlightingOffsets = (List<List<Integer>>) jsonHighlightingItem.get("offsets");
				for (List<Integer> offsetItem  : jsonHighlightingOffsets)
				{
					listOfOffsets.add(offsetItem.get(0));
				}
			}
			else
			{
				List<Integer> jsonHighlightingOffsets = (List<Integer>) jsonHighlightingItem.get("offsets");
				listOfOffsets.add(jsonHighlightingOffsets.get(0));
			}
			
			
			results.put(storyId, listOfOffsets);
		}

		
		return results;
	}
}
