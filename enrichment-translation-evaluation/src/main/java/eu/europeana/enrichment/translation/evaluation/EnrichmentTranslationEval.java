package eu.europeana.enrichment.translation.evaluation;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.common.commons.HelperFunctions;

@Service
public class EnrichmentTranslationEval 
{
	Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	@Qualifier(EnrichmentConstants.BEAN_ENRICHMENT_CONFIGURATION)
	EnrichmentConfiguration configuration;

    public void saveDRICollectionItemsToJsonFiles() throws Exception {
		try {
			URIBuilder builder = new URIBuilder(configuration.getSearchApiBaseUrl());
			builder.addParameter("query", "europeana_collectionName:\"15405__Royal_Irish_Academy\"");
			builder.addParameter("wskey", "apidemo");
			builder.addParameter("profile", "minimal");
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			int start=1;
			//this is max number of returned items
			int rows=100;
			builder.addParameter("start", String.valueOf(start));
			builder.addParameter("rows", String.valueOf(rows));
			while(true) {
				HttpGet request = new HttpGet(builder.build());
				request.addHeader("content-type", "application/json");
				HttpResponse result = httpClient.execute(request);
				String responeString = EntityUtils.toString(result.getEntity(), "UTF-8");				
				JSONObject response = new JSONObject(responeString);
				
				if(response.has("items")) {
					JSONArray items = response.getJSONArray("items");
					if(items.length()<=0) {
						break;
					}
					for (int i = 0; i < items.length(); i++) { 
						JSONObject itemEl = items.getJSONObject(i);
						String itemId = itemEl.getString("id");
						String fileName = itemId.substring(itemId.lastIndexOf("/") + 1);
						String fullFileName = configuration.getEnrichDRICollectionDirectory() + "/" + fileName + ".json";
						HelperFunctions.saveToFile(fullFileName, itemEl.toString());
					}
					start=start + rows;
					builder.setParameter("start", String.valueOf(start));
				}
				else {
					break;
				}				
			}
		} catch (URISyntaxException | IOException e) {
			logger.log(Level.ERROR, "Data cannot be fetched from: " + configuration.getSearchApiBaseUrl(), e);
			throw e;
		}
    }
    
    public void saveUWRCollectionItemsToJsonFiles() throws IOException, URISyntaxException {
		String UWrCollectionRecordsFile = "/home/ait/enrich/translation-eval/UWr_collection_records.csv";
		try (
            Reader reader = Files.newBufferedReader(Paths.get(UWrCollectionRecordsFile));
            CSVParser csvParser = CSVParser.parse(reader, CSVFormat.DEFAULT.builder().setDelimiter(',').setHeader("Record identifier", "Content URL", "Metadata URL").setSkipHeaderRecord(true).build());
        ) {
			URIBuilder builder = new URIBuilder(configuration.getSearchApiBaseUrl());
			builder.setParameter("wskey", "apidemo");
			builder.setParameter("profile", "minimal");
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            for (CSVRecord csvRecord : csvParser) {
                // Accessing Values by Column Index
                String contentUrl = csvRecord.get(1);
                
    			String query = "provider_aggregation_edm_isShownAt:\"" + contentUrl  + "\"";
    			builder.setParameter("query", query);
				HttpGet request = new HttpGet(builder.build());
				request.addHeader("content-type", "application/json");
				HttpResponse result = httpClient.execute(request);
				String responeString = EntityUtils.toString(result.getEntity(), "UTF-8");				
				JSONObject response = new JSONObject(responeString);
	
				if(response.has("items")) {
					JSONArray items = response.getJSONArray("items");
					JSONObject itemEl = items.getJSONObject(0);
					String itemId = itemEl.getString("id");
					String fileName = itemId.substring(itemId.lastIndexOf("/") + 1);
					String fullFileName = configuration.getEnrichUWRCollectionDirectory() + "/" + fileName + ".json";
					HelperFunctions.saveToFile(fullFileName, itemEl.toString());
				}
            }
		}	
    }
}
