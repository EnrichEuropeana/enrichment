package eu.europeana.enrichment.ner.linking;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
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
	
	public DBpediaResponse getDBpediaResponse(String dbpediaUrl) throws Exception {
		
		DBpediaResponse dbpediaResp = null;
		boolean redirect = true; 
		List<String> alreadyUsedDbpediaIds = new ArrayList<>();
		alreadyUsedDbpediaIds.add(dbpediaUrl);
		//keep fetching the redirect dbpedia links until there is not any one more or we find the wikidata id in the sameAs part 
		while (redirect) {
			String responseStr = createRequest(dbpediaUrl);
			if(responseStr==null) return null;
			
		    InputStream stream = new ByteArrayInputStream(responseStr.getBytes(StandardCharsets.UTF_8));
		    dbpediaResp = ((DBpediaResponseHeader) unmarshaller.get().unmarshal(stream)).getResult();

//		    try {
//		    	dbpediaResp = ((DBpediaResponseHeader) unmarshaller.get().unmarshal(stream)).getResult();
//			} catch (JAXBException e) {
//				logger.error("Cannot unmarschall the dbpedia response. Probably no valid data within it.", e);
//				return null;
//			}

		    if(dbpediaResp.getWikipageRedirect()==null || dbpediaResp.getWikidataUrls().size()>0) {
		    	redirect=false;
		    }
		    else {
		    	if(!alreadyUsedDbpediaIds.contains(dbpediaResp.getWikipageRedirect().getResourceUrl())) {
		    		alreadyUsedDbpediaIds.add(dbpediaResp.getWikipageRedirect().getResourceUrl());
		    		dbpediaUrl=dbpediaResp.getWikipageRedirect().getResourceUrl();
		    	}	
		    	else {
		    		redirect=false;
		    	}
		    }
		}
		return dbpediaResp;

//		JacksonXmlModule xmlModule = new JacksonXmlModule();
//		xmlModule.setDefaultUseWrapper(false);
//		XmlMapper xmlMapper = new XmlMapper(xmlModule);
//		xmlMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
//		DBpediaResponseHeader value = xmlMapper.readValue(response, DBpediaResponseHeader.class);
//		if(value==null) return null;
//		return value.getResult();
		
	}
	
	private String createRequest(String dbpediaUrl) throws Exception {
			String query = String.format(DBPEDIA_SPARQL_QUERY_PATTERN, dbpediaUrl);
			String wholeUrl = DBPEDIA_SPARQL_URL;
			wholeUrl += "&query=" + URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
			wholeUrl += "&format=" + URLEncoder.encode(DBPEDIA_SPARQL_FORMAT, StandardCharsets.UTF_8.toString());	
			
			URIBuilder builder = new URIBuilder(wholeUrl);

			//logger.debug(this.getClass().getSimpleName() + ": " + query);
			//logger.debug(this.getClass().getSimpleName() + builder.toString());

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
