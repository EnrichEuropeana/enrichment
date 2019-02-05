package eu.europeana.enrichment.ner.linking;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
import org.json.JSONArray;
import org.json.JSONObject;

public class WikidataServiceImpl implements WikidataService {

	private static final String baseUrl = "https://query.wikidata.org/sparql";
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
	 * Location: Q17334923
	 */
	private String placeLabelQueryString = "SELECT ?item ?description ?type WHERE {\r\n"
			+ "   ?item rdfs:label \"%s\"@%s;\r\n p:P31/ps:P31/wdt:P279* wd:Q17334923;\r\n"
			+ "      #wdt:P31 ?type;\r\n" + "      schema:description ?description.\r\n"
			+ "  FILTER((LANG(?description)) = \"en\") }";

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
	
	@Override
	public List<String> getWikidataId(String geonameId) {
		String query = String.format(geonamesIdQueryString, geonameId);
		return processResponse(createRequest(query));
	}

	@Override
	public List<String> getWikidataIdWithLabel(String label, String language) {
		String query = String.format(labelQueryString, label, language);
		return processResponse(createRequest(query));
	}

	@Override
	public List<String> getWikidataPlaceIdWithLabel(String label, String language) {
		String query = String.format(placeLabelQueryString, label, language);
		return processResponse(createRequest(query));
	}
	
	@Override
	public List<String> getWikidataAgentIdWithLabel(String label, String language){
		String query = String.format(agentlabelQueryString, label, language);
		return processResponse(createRequest(query));
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
			return retValue;

		JSONObject responseJson = new JSONObject(reponse);
		if(!responseJson.has(wikidataResultKey))
			return retValue;
		JSONObject resultObj = responseJson.getJSONObject(wikidataResultKey);
		if(!resultObj.has(wikidataBindingsKey))
			return retValue;
		
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
		return retValue;
	}

	/*
	 * This method creates the Wikidata request, extracts the response body from the
	 * rest and returns the response body
	 * 
	 * @param query is the Wikidata sparql Geonames ID or label search query
	 * 
	 * @return response body or null
	 */
	private String createRequest(String query) {
		try {
			URIBuilder builder = new URIBuilder(baseUrl);
			builder.addParameter("query", query);

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

}
