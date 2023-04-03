package eu.europeana.enrichment.translation.evaluation;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.common.commons.EnrichmentConstants;
import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.model.impl.RecordTranslationEval;
import eu.europeana.enrichment.mongo.dao.RecordTranslationDao;
import eu.europeana.enrichment.translation.service.impl.DeeplTranslationServiceImpl;
import eu.europeana.enrichment.translation.service.impl.TranslationGoogleServiceImpl;

@Service
public class EnrichmentTranslationEval 
{
	private static final String dcDescriptionLangAwareField = "dcDescriptionLangAware";
	private static final String statusInvalid = "INVALID";
	private static final String statusInvalidMultilingual = "INVALID_MULTILINGUAL";
	
	Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	@Qualifier(EnrichmentConstants.BEAN_ENRICHMENT_CONFIGURATION)
	EnrichmentConfiguration configuration;
	
	@Autowired
    RecordTranslationDao recordTranslationDao;
	
	@Autowired(required=false)
	TranslationGoogleServiceImpl googleTranslationService;
	
	@Autowired
	DeeplTranslationServiceImpl deeplTranslationService;

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
    
    public void saveRecordFromJson(String record) {
    	RecordTranslationEval recordTranslationEval = new RecordTranslationEval();
    	JSONObject recordJson = new JSONObject(record); 
    	boolean saveRecord = false;
    	if(recordJson.has("id")) {
    		saveRecord = true;
    		String recordId = recordJson.getString("id");
	    	recordTranslationEval.setRecordId(recordId);
	    	recordTranslationEval.setIdentifier(recordId);
    	}
    	if(recordJson.has(dcDescriptionLangAwareField)) {
    		Map<String, List<String>> dcDescLangAware = new HashMap<>();
    		List<String> language = new ArrayList<>();
    		//get the dcDescriptionLangAware field
    		JSONObject dcDescLangAwareJson = recordJson.getJSONObject(dcDescriptionLangAwareField);
    		Iterator<String> keys = dcDescLangAwareJson.keys(); 		
    		//setting the description and translation fields
     		while(keys.hasNext()) { 
    			String key = keys.next();
    			if(key.equalsIgnoreCase("en")) {
    				JSONArray enDescription = dcDescLangAwareJson.getJSONArray(key);
    				List<String> translation = new ArrayList<>();
    				for(int i=0; i<enDescription.length(); i++) {
    					translation.add(enDescription.getString(i));
    				}
    				if(! translation.isEmpty()) {
    					recordTranslationEval.setTranslation(translation);
    					dcDescLangAware.put("en", new ArrayList<>(translation));
    				}
    			}
    			else {
    				JSONArray nonEnDescription = dcDescLangAwareJson.getJSONArray(key);
    				List<String> description = new ArrayList<>();
    				for(int i=0; i<nonEnDescription.length(); i++) {
    					language.add(key);
    					description.add(nonEnDescription.getString(i));
    				}
    				if(! description.isEmpty()) {
    					recordTranslationEval.setDescription(description);
    					recordTranslationEval.setLanguage(language);
    					dcDescLangAware.put(key, new ArrayList<>(description));
    				}
    			}
    		}

     		if(! dcDescLangAware.isEmpty()) {
     			recordTranslationEval.setDcDescriptionLangAware(dcDescLangAware);
     		}
     		//validation
    		if(dcDescLangAware.size()!=2 || !dcDescLangAware.containsKey("en")) {
    			recordTranslationEval.setTranslationStatus(statusInvalidMultilingual);
    		}
     		if(! (recordTranslationEval.getTranslation()!=null && recordTranslationEval.getDescription()!=null
     				&& recordTranslationEval.getTranslation().size()==recordTranslationEval.getDescription().size())) {
     			recordTranslationEval.setTranslationStatus(statusInvalid);
     		}     		
    	}
    	
    	if(saveRecord) {
    		recordTranslationDao.saveTranslationEntity(recordTranslationEval);
    	}
    }
    
    public void addGoogleTranslation(RecordTranslationEval record) throws URISyntaxException, ClientProtocolException, IOException {
    	if(record.getDescription()!=null && record.getGoogleTranslation()==null) {
 			List<String> googleTransTextResp = new ArrayList<>();
 			List<String> googleTransDetectedLangResp = new ArrayList<>();
 			googleTranslationService.translateList(record.getDescription(), record.getLanguage().get(0), "en", googleTransTextResp, googleTransDetectedLangResp);
 			record.setGoogleTranslation(googleTransTextResp);
 			record.setGoogleDetectedLang(googleTransDetectedLangResp.get(0));
 			recordTranslationDao.saveTranslationEntity(record);
    	}
	}
    
    public void addEtTranslation(RecordTranslationEval record) throws URISyntaxException, ClientProtocolException, IOException {
    	if(record.getDescription()!=null && record.getEtTranslation()==null) {
			URIBuilder builder = new URIBuilder(configuration.getTranslationETranslationBaseUrlLocal());
			builder.setParameter("wskey", "apidemo");
			builder.setParameter("sourceLang", record.getLanguage().get(0));
			builder.setParameter("targetLang", "en");
			CloseableHttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(builder.build());
			request.addHeader("Accept", "text/plain");
			List<String> etTranslation = new ArrayList<>();
			for(String desc : record.getDescription()) {
				StringEntity body = new StringEntity(desc, StandardCharsets.UTF_8.name());
				request.setEntity(body);
				CloseableHttpResponse result = httpClient.execute(request);
				if(result.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
					String responeString = EntityUtils.toString(result.getEntity(), StandardCharsets.UTF_8.name());
					etTranslation.add(responeString);
				}
		    }
			if(! etTranslation.isEmpty()) {
				record.setEtTranslation(etTranslation);
				recordTranslationDao.saveTranslationEntity(record);
			}				
    	}		    
    }
    
    public void addDeeplTranslation (RecordTranslationEval record) throws ClientProtocolException, IOException {
    	//"ga" (Irish) is still not supported language by Deepl, only "pl" (Polish)
    	if(record.getDescription()!=null && record.getDeeplTranslation()==null && record.getLanguage().contains("pl")) {
			List<String> deeplTranslations = new ArrayList<>();
			for(String desc : record.getDescription()) {
				String resp =  deeplTranslationService.translateText(desc, "en");
				if(resp!=null) {
					deeplTranslations.add(resp);
				}
		    }
			if(! deeplTranslations.isEmpty()) {
				record.setDeeplTranslation(deeplTranslations);
				recordTranslationDao.saveTranslationEntity(record);
			}				
    	}
    }
}
