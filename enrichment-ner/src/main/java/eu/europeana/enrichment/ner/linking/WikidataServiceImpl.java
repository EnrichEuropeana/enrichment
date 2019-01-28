package eu.europeana.enrichment.ner.linking;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class WikidataServiceImpl implements WikidataService {

	private static final String baseUrl = "https://query.wikidata.org/sparql";
	private String geonamesIdQueryString = "SELECT ?city ?cityLabel WHERE { ?city wdt:P1566 \"%s\" . "
			+ "SERVICE wikibase:label { bd:serviceParam wikibase:language \"e\"}};";
	private String labelQueryString = "SELECT ?city ?cityLabel WHERE { ?city rdfs:label \"%s\" @%s . "
			+ "SERVICE wikibase:label { bd:serviceParam wikibase:language \"e\"}}";
	
	//TODO: add type to distinguish between Place/Location and Agent/Person
	
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
	
	private List<String> processResponse(String reponse){
		List<String> retValue = new ArrayList<>();
		if(reponse == null || reponse.equals(""))
			return retValue;
		
		
		return retValue;
	}
	
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
			//TODO: check status code
			return responeString;
			
		} catch (URISyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

}
