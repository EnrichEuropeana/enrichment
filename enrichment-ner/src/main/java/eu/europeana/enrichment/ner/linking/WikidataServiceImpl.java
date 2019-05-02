package eu.europeana.enrichment.ner.linking;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

//import net.arnx.jsonic.JSONException;

public class WikidataServiceImpl implements WikidataService {
	
	Logger logger = LogManager.getLogger(getClass());

	private static final String baseUrlSparql = "https://query.wikidata.org/bigdata/namespace/wdq/sparql"; // "https://query.wikidata.org/sparql";
	
	/*
	 * Defining Wikidata sparql query construct for Geonames ID and Label search
	 */
	private String geonamesIdQueryString = "SELECT ?item WHERE { ?item wdt:P1566 \"%s\" . "
			+ "SERVICE wikibase:label { bd:serviceParam wikibase:language \"e\"}}";
	private String labelQueryString = "SELECT ?item WHERE { ?item rdfs:label \"%s\"@%s . "
			+ "SERVICE wikibase:label { bd:serviceParam wikibase:language \"e\"}}";

	/*
	 * Wikidata place search
	 * Geographical object: Q618123
	 * Geographic location: Q2221906
	 * Location: Q17334923
	 */
	private String placeLabelQueryString = "SELECT ?item ?description ?type WHERE {\r\n"
			+ "   ?item rdfs:label \"%s\"@%s; \r\n p:P31/ps:P31/wdt:P279* ?type; \r\n"
			+ "      schema:description ?description.\r\n"
			+ "  FILTER(?type in (wd:Q82794,wd:Q2075301,wd:Q7444568,wd:Q12371824,wd:Q18635222,wd:Q25345958,wd:Q56596860,wd:Q207326,wd:Q7444568)) \r\n"
			+ "  FILTER((LANG(?description)) = \"en\") }";

	/*
	 * Defines multilingual Wikidata query including alternative label and 
	 */
	private String placeLabelAltLabelQueryString = "SELECT distinct ?item ?type ?rank WHERE {\r\n"
			+ "  hint:Query hint:optimizer \"None\".\r\n" 
			+ "  values ?labels {\"%s\"@%s \"%s\"@%s}\r\n"
			+ "  ?item (rdfs:label|skos:altLabel) ?labels;\r\n"
			+ "  p:P31/ps:P31/wdt:P279* ?type. #;\r\n"
			+ "  FILTER(?type in (wd:Q82794,wd:Q2075301,wd:Q7444568,wd:Q12371824,wd:Q18635222,wd:Q25345958,wd:Q56596860,wd:Q207326,wd:Q7444568))}";
	/*
	 * Wikidata agent search query
	 */
	private String agentlabelQueryString = "SELECT ?item ?description WHERE {\r\n" 
			+ "  ?item wdt:P31 wd:Q5;\r\n rdfs:label \"%s\"@%s;\r\n" 
			+ "      schema:description ?description.\r\n" 
			+ "  FILTER((LANG(?description)) = \"en\") }";
	
	/*
	 * Wikidata keys for the response JSON
	 */
	private final String wikidataResultKey = "results";
	private final String wikidataBindingsKey = "bindings";
	private final String wikidataItemKey = "item";
	private final String wikidataDescriptionKey = "description";
	private final String wikidataValueKey = "value";
	
	public Logger getLogger() {
		return logger;
	}

	@Override
	public List<String> getWikidataId(String geonameId) {
		String query = String.format(geonamesIdQueryString, geonameId);
		return processResponse(createRequest(baseUrlSparql, query));
	}

	@Override
	public List<String> getWikidataIdWithLabel(String label, String language) {
		String query = String.format(labelQueryString, label, language);
		return processResponse(createRequest(baseUrlSparql, query));
	}

	@Override
	public List<String> getWikidataPlaceIdWithLabel(String label, String language) {
		String query = String.format(placeLabelQueryString, label, language);
		return processResponse(createRequest(baseUrlSparql, query));
	}
	
	@Override
	public List<String> getWikidataPlaceIdWithLabelAltLabel(String label, String language) {
		String query = String.format(placeLabelAltLabelQueryString, label, language, label, "en");
		return processResponse(createRequest(baseUrlSparql, query));
	}
	
	@Override
	public List<String> getWikidataAgentIdWithLabel(String label, String language){
		String query = String.format(agentlabelQueryString, label, language);
		return processResponse(createRequest(baseUrlSparql, query));
	}

	/*
	 * This method process the response of the Wikidata sparql query and returns a
	 * list of Wikidata entity urls or null
	 * 
	 * @param response is the response body of the Wikidata sparql query
	 * 
	 * @return a list of Wikidata entity entity or empty list
	 */
	private List<String> processResponse(String reponse) {
		// TODO: implement function and add type to distinguish between Place/Location
		// and Agent/Person
		List<String> retValue = new ArrayList<>();
		if (reponse == null || reponse.equals(""))
		{
			logger.info("\n" + this.getClass().getSimpleName() + "The response to the Wikidata request is: null" + "\n");
			return retValue;
		}
				

		JSONObject responseJson = new JSONObject(reponse);
		if(!responseJson.has(wikidataResultKey))
		{
			logger.info("\n" + this.getClass().getSimpleName() + "The response to the Wikidata request is: " + retValue.toString() + "\n");
			return retValue;
		}
		JSONObject resultObj = responseJson.getJSONObject(wikidataResultKey);
		if(!resultObj.has(wikidataBindingsKey))
		{
			logger.info("\n" + this.getClass().getSimpleName() + "The response to the Wikidata request is: " + retValue.toString() + "\n");
			return retValue;
		}
		JSONArray bindingsArray = resultObj.getJSONArray(wikidataBindingsKey);
		for(int index = 0; bindingsArray.length() > index; index++) {
			JSONObject bindingsObj = bindingsArray.getJSONObject(index);
			if(!bindingsObj.has(wikidataItemKey))
				continue;
			JSONObject cityObj = bindingsObj.getJSONObject(wikidataItemKey);
			String cityValue = cityObj.getString(wikidataValueKey);
			if(!retValue.contains(cityValue))
				retValue.add(cityValue);
		}
		
		logger.info("\n" + this.getClass().getSimpleName() + "The response to the Wikidata request is: " + retValue.toString() + "\n");
		return retValue;
	}

	/*
	 * This method creates the Wikidata request, extracts the response body from the
	 * rest and returns the response body
	 * 
	 * @param baseUrl is the base URL to which the query is to be added
	 * 
	 * @param query is the Wikidata sparql Geonames ID or label search query
	 * 
	 * @return response body or null
	 */
	private String createRequest(String baseUrl, String query) {
		try {
			URIBuilder builder = new URIBuilder(baseUrl);
			//in case of calling a REST service for Wikidata JSON, the query parameter should be null/empty
			if(query!=null && !query.isEmpty())
			{
				builder.addParameter("query", query);
			}

			logger.info(this.getClass().getSimpleName() + ": " + query);
			logger.info(this.getClass().getSimpleName() + builder.toString());

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(builder.build());
			request.addHeader("content-type", "application/json");
			request.addHeader("accept", "application/json");
			HttpResponse result = httpClient.execute(request);
			String responeString = EntityUtils.toString(result.getEntity(), "UTF-8");
			
			// TODO: check status code
			return responeString;

		} catch (URISyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public String getWikidataJSONFromWikidataID(String WikidataID) {
		
		String WikidataJSONResponse = createRequest(WikidataID, null);
		return WikidataJSONResponse;
		
	}

	@Override
	public List<String> getJSONFieldFromWikidataJSON(String WikidataJSON, String field) {
		
		List<String> result = new ArrayList<String>();
		JSONObject responseJson = new JSONObject(WikidataJSON);
		JSONObject responseJsonEntities = responseJson.getJSONObject("entities");
		Iterator<String> entitiesIterator = responseJsonEntities.keys();
		while(entitiesIterator.hasNext())
		{
			String entityKey = entitiesIterator.next();
			JSONObject entity = responseJsonEntities.getJSONObject(entityKey);
						
			analyseJSONFieldFromWikidataJSON(entity,field,result);
		}
		
		return result;
		
	}

	/**
	 * This function is a recursive implementation that enables taking a specific JSON field
	 * from the obtained Wikidata json response, containing both plain JSONObject but also 
	 * JSONArray json elements.
	 * 
	 * @param jsonElement
	 * @param field
	 * @param result
	 */
	
	private void analyseJSONFieldFromWikidataJSON (Object jsonElement, String field, List<String> result)
	{
		String[] fieldParts = field.split("\\.");
		
		if(fieldParts.length==1)
		{
			JSONObject obj = (JSONObject) jsonElement;
			result.add(obj.getString(fieldParts[0]));
			return;
		}
		
		if(jsonElement instanceof JSONObject)
		{
			JSONObject obj = (JSONObject) jsonElement;
			if(obj.has(fieldParts[0]))
			{
				String [] newField = field.split("\\.",2);
				analyseJSONFieldFromWikidataJSON(obj.get(fieldParts[0]),newField[1], result);
			}
			
		}
		else if(jsonElement instanceof JSONArray)
		{
			JSONArray array = (JSONArray) jsonElement;
			for(int i=0;i<array.length();i++)
			{
				analyseJSONFieldFromWikidataJSON(array.get(i),field, result);
			}
		}
			
	}
}
