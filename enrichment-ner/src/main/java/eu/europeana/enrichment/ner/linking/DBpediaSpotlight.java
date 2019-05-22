package eu.europeana.enrichment.ner.linking;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import eu.europeana.enrichment.ner.linking.model.DBpediaResponse;
import eu.europeana.enrichment.ner.linking.model.DBpediaResponseHeader;

public class DBpediaSpotlight {

	private final String DBPEDIA_SPARQL_URL = "http://dbpedia.org/sparql?default-graph-uri=http://dbpedia.org";
	private final String DBPEDIA_SPARQL_QUERY_PATTERN = "DEFINE sql:describe-mode \"SPO\" DESCRIBE <%s>";
	private final String DBPEDIA_SPARQL_FORMAT = "application/rdf+xml";
	
	/*
	 * PREFIX owl:<http://www.w3.org/2002/07/owl#>

		SELECT ?obj WHERE {
		    <http://dbpedia.org/resource/Vienna> owl:sameAs ?obj.
		 filter (strstarts(str(?obj), "http://www.wikidata"))
		}
	 * 
	 * DEFINE sql:describe-mode "SPO"
DESCRIBE <http://dbpedia.org/resource/Vienna>
	 * 
	 */
	
	public DBpediaResponse getDBpediaResponse(String dbpediaUrl) throws JsonParseException, JsonMappingException, IOException {
		String response = createRequest(dbpediaUrl);
		JacksonXmlModule xmlModule = new JacksonXmlModule();
		xmlModule.setDefaultUseWrapper(false);
		XmlMapper xmlMapper = new XmlMapper(xmlModule);
		xmlMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		 
		DBpediaResponseHeader value = xmlMapper.readValue(response, DBpediaResponseHeader.class);
		return value.getResult();
	}
	
	private String createRequest(String dbpediaUrl) {
		try {
			URIBuilder builder = new URIBuilder(DBPEDIA_SPARQL_URL);

			//logger.info(this.getClass().getSimpleName() + ": " + query);
			//logger.info(this.getClass().getSimpleName() + builder.toString());

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(builder.build());

			MultipartEntityBuilder multiBuilder = MultipartEntityBuilder.create();
			multiBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			String query = String.format(DBPEDIA_SPARQL_QUERY_PATTERN, dbpediaUrl);
			multiBuilder.addTextBody("query", query, ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8));
			multiBuilder.addTextBody("format", DBPEDIA_SPARQL_FORMAT, ContentType.TEXT_PLAIN);
			
			HttpEntity entity = multiBuilder.build();
			request.setEntity(entity);
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
