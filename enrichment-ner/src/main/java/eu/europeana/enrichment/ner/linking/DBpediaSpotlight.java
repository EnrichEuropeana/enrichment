package eu.europeana.enrichment.ner.linking;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.ner.linking.model.DBpediaResponse;
import eu.europeana.enrichment.ner.linking.model.DBpediaResponseHeader;

@Service
public class DBpediaSpotlight implements InitializingBean{

	Logger logger = LogManager.getLogger(getClass());
	
	private final JAXBContext jaxbContext;

	/** Create a JAXB unmarshaller for each thread */
	private ThreadLocal<Unmarshaller> unmarshaller;

	private final String DBPEDIA_SPARQL_URL = "http://dbpedia.org/sparql?default-graph-uri=http://dbpedia.org";
	private final String DBPEDIA_SPARQL_QUERY_PATTERN = "DEFINE sql:describe-mode \"SPO\" DESCRIBE <%s>";
	private final String DBPEDIA_SPARQL_FORMAT = "application/rdf+xml";
	
	  @Autowired
	  public DBpediaSpotlight(JAXBContext jaxbContext) {
	    this.jaxbContext = jaxbContext;
	  }
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
	
	public DBpediaResponse getDBpediaResponse(String dbpediaUrl) throws JAXBException {
		String response = createRequest(dbpediaUrl);
		if(response==null) return null;
//		JacksonXmlModule xmlModule = new JacksonXmlModule();
//		xmlModule.setDefaultUseWrapper(false);
//		XmlMapper xmlMapper = new XmlMapper(xmlModule);
//		xmlMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
//		DBpediaResponseHeader value = xmlMapper.readValue(response, DBpediaResponseHeader.class);
//		if(value==null) return null;
//		return value.getResult();
	    InputStream stream = new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8));
	    return ((DBpediaResponseHeader) unmarshaller.get().unmarshal(stream)).getResult();
		
	}
	
	private String createRequest(String dbpediaUrl) {
		try {
			String query = String.format(DBPEDIA_SPARQL_QUERY_PATTERN, dbpediaUrl);
			String wholeUrl = DBPEDIA_SPARQL_URL;
			wholeUrl += "&query=" + URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
			wholeUrl += "&format=" + URLEncoder.encode(DBPEDIA_SPARQL_FORMAT, StandardCharsets.UTF_8.toString());	
			
			URIBuilder builder = new URIBuilder(wholeUrl);

			//logger.info(this.getClass().getSimpleName() + ": " + query);
			//logger.info(this.getClass().getSimpleName() + builder.toString());

			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(builder.build());

//			MultipartEntityBuilder multiBuilder = MultipartEntityBuilder.create();
//			multiBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//			String query = String.format(DBPEDIA_SPARQL_QUERY_PATTERN, dbpediaUrl);
//			multiBuilder.addTextBody("query", query, ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8));
//			multiBuilder.addTextBody("format", DBPEDIA_SPARQL_FORMAT, ContentType.TEXT_PLAIN);
//			HttpEntity entity = multiBuilder.build();
//			request.setEntity(entity);

			HttpResponse result = httpClient.execute(request);
			String responeString = EntityUtils.toString(result.getEntity(), "UTF-8");
			
			// TODO: check status code
			return responeString;

		} catch (URISyntaxException | IOException e) {
			logger.log(Level.ERROR, "Exception during sending the dbpedia NER request.", e);
			return null;
		}

	}
	
	private void setupJaxb() {
	    unmarshaller =
	        ThreadLocal.withInitial(
	            () -> {
	              try {
	                return jaxbContext.createUnmarshaller();
	              } catch (JAXBException e) {
	                throw new RuntimeException("Error creating JAXB unmarshaller ", e);
	              }
	            });
	}

	@Override
	public void afterPropertiesSet() {
		setupJaxb();
	}
	
}
