package eu.europeana.enrichment.ner.internal;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class EuropeanaEntityServiceImpl implements EuropeanaEntityService {

	private String baseUrl = "https://entity-api-test.eanadev.org/entity/";
	private String schema = "base";
	private String key = "apidemo";
	
	//get labels
	
	@Override
	public String getEntitySuggestions(String text, String classificationType) {
		try {
			URIBuilder builder = new URIBuilder(baseUrl+ "suggest");
			builder.addParameter("wskey", key)
				.addParameter("text", text)
				.addParameter("language", "en")
				.addParameter("type", classificationType);
			
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

	@Override
	public String retriveEntity(String id) {
		try {
			
			URIBuilder builder = new URIBuilder(id);
			builder.addParameter("wskey", key);;
			
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
