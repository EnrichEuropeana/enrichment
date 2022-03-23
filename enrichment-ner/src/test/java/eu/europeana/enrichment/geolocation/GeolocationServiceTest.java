package eu.europeana.enrichment.geolocation;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

import fr.dudie.nominatim.client.JsonNominatimClient;
import fr.dudie.nominatim.model.Address;

@ContextConfiguration(locations = "classpath:test-ner-config.xml")
public class GeolocationServiceTest {

	Logger logger = LogManager.getLogger(getClass());
	
	private String nameKey = "name";
	private String streetKey = "street";
	private String administrativeEntityKey = "administrative";
	private String coordinateLocationLatitudeKey = "coordinateLatitude";
	private String coordinateLocationLongitudeKey = "coordinateLongitude";
	private String osmEndpoint = "https://nominatim.openstreetmap.org/";
	
	
	List<TreeMap<String, String>> initStreetLocation() {
		List<TreeMap<String, String>> allEntries = new ArrayList<>();
		
		// Wikidata BNF
		TreeMap<String, String> entry = new TreeMap<String, String>();
		entry.put(nameKey, "Bibliothèque nationale de France");
		entry.put(administrativeEntityKey, "2nd arrondissement of Paris");
		entry.put(coordinateLocationLatitudeKey, "48.833611");
		entry.put(coordinateLocationLongitudeKey, "2.375833");
		entry.put(streetKey + "_1", "rue de Richelieu 58");
		entry.put(streetKey + "_2", "rue des Petits-Champs 8");
		entry.put(streetKey + "_3", "rue Colbert");
		entry.put(streetKey + "_4", "rue Vivienne 5");
		entry.put(streetKey + "_5", "quai François-Mauriac");
		allEntries.add(entry);
		
		// Wikidata BL
		TreeMap<String, String> entryBL = new TreeMap<String, String>();
		entryBL.put(nameKey, "British Library");
		entryBL.put(administrativeEntityKey, "London Borough of Camden");
		entryBL.put(coordinateLocationLatitudeKey, "51.529444");
		entryBL.put(coordinateLocationLongitudeKey, "-0.126944");
		entryBL.put(streetKey + "_1", "96 Euston Road");
		allEntries.add(entryBL);
		
		// Wikidata NISV
		TreeMap<String, String> entryNISV = new TreeMap<String, String>();
		entryNISV.put(nameKey, "Netherlands Institute for Sound and Vision");
		entryNISV.put(administrativeEntityKey, "Hilversum");
		entryNISV.put(coordinateLocationLatitudeKey, "52.235278");
		entryNISV.put(coordinateLocationLongitudeKey, "5.173056");
		entryNISV.put(streetKey + "_1", "Media Parkboulevard 1");
		allEntries.add(entryNISV);
		
		// Wikidata SAS
		TreeMap<String, String> entrySAS = new TreeMap<String, String>();
		entrySAS.put(nameKey, "Swiss Social Archives");
		entrySAS.put(administrativeEntityKey, "Zürich");
		entrySAS.put(coordinateLocationLatitudeKey, "47.366828");
		entrySAS.put(coordinateLocationLongitudeKey, "8.547531");
		entrySAS.put(streetKey + "_1", "Stadelhoferstrasse 12");
		allEntries.add(entrySAS);
		
		return allEntries;
	}
	
	@Test
	public void geocodingNominatimServiceTest2() throws IOException {

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        
        final String baseUrl = "https://nominatim.openstreetmap.org/";
        final String email = "";
        JsonNominatimClient nominatimClient = new JsonNominatimClient(baseUrl, httpClient, email);
		//final Address address = nominatimClient.getAddress(1.64891269513038, 48.1166561643464, 10);
		List<TreeMap<String, String>> allEntries = initStreetLocation();

		for(TreeMap<String, String> entryMap : allEntries) {
			logger.debug("Entry: " + entryMap.get(nameKey));
			
			Double orgLatitude = Double.parseDouble(entryMap.get(coordinateLocationLatitudeKey));
			Double orgLongitude = Double.parseDouble(entryMap.get(coordinateLocationLongitudeKey));
			final Address address = nominatimClient.getAddress(orgLongitude, orgLatitude);
			logger.debug("Adress: " + address.getDisplayName());
		}

        assertTrue(true);
		
	}
	
	@Test
	public void geocodingNominatimServiceTest() {
		//https://github.com/AtlisInc/Nominatim-API
		// second: https://github.com/jeremiehuchet/nominatim-java-api
		/*
		List<TreeMap<String, String>> allEntries = new ArrayList<>();//initStreetLocation();
		
		for(TreeMap<String, String> entryMap : allEntries) {
			System.out.println("Entry: " + entryMap.get(nameKey));
			for(Map.Entry<String, String> entry : entryMap.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if(key.startsWith(streetKey)) {
					String strAddress = entryMap.get(administrativeEntityKey)+ ", " + value ;
					Address address = new Address();
					address.setCountry(strAddress);
					MapPoint geocodingPoint = NominatimAPI.with(osmEndpoint).getMapPointFromAddress( address, 5);
					StringBuilder strBuilder = new StringBuilder();
					strBuilder.append("Street (");
					strBuilder.append(value);
					if(geocodingPoint == null) {
						strBuilder.append(") no result!");
						System.out.println(strBuilder.toString());
						continue;
					}
					Double longitude = geocodingPoint.getLongitude();
					Double latitude = geocodingPoint.getLatitude();
					strBuilder.append(") result: long (");
					strBuilder.append(longitude);
					strBuilder.append(") lat (");
					strBuilder.append(latitude);
					strBuilder.append(")");
					System.out.println(strBuilder.toString());
				}
				
			}
			
		}
		
		// Test
		
		Address address = new Address();
		//address.setStreet("quai François-Mauriac");
		//address.setDistrict("2nd arrondissement of Paris");
		//address.setCity("2nd arrondissement of Paris");
		//address.setState("Paris");
		address.setCountry("2nd arrondissement of Paris, rue des Petits-Champs 8");
		MapPoint geocodingPoint = NominatimAPI.with(osmEndpoint).getMapPointFromAddress( address, 5);
		
		/*
		Double latitude = 48.833611;
		Double longitude = 2.375833;
		MapPoint reverseGeocodingmapPoint = new MapPoint().buildMapPoint(latitude, longitude);
		Address reverseAddress = NominatimAPI.with(osmEndpoint).getAddressFromMapPoint(reverseGeocodingmapPoint);*/
		assertTrue(true);
	}
	
	/*
	 * TODO: create the proper google_api_key.txt file used in the test
	 */
	//@Test
	public void geocodingGoogleServiceTest() throws IOException, ApiException, InterruptedException {
		// https://github.com/googlemaps/google-maps-services-java
		/*
		 * TODO: create the proper google_api_key.txt file used in the test
		 */
		String credentialPath = "C:\\Users\\katicd\\Documents\\Europeana\\Code\\Ait\\additional_data\\google_api_key.txt";
		// Open the file
		FileInputStream fstream = new FileInputStream(credentialPath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String googleApiKey = "";
		String tempString;
		while ((tempString = br.readLine()) != null)   {
			googleApiKey = tempString;
		}
		fstream.close();
		GeoApiContext context = new GeoApiContext.Builder().apiKey(googleApiKey).build();
		
		List<TreeMap<String, String>> allEntries = new ArrayList<>();//initStreetLocation();
		
		for(TreeMap<String, String> entryMap : allEntries) {
			logger.debug("Entry: " + entryMap.get(nameKey));
			
			Double orgLatitude = Double.parseDouble(entryMap.get(coordinateLocationLatitudeKey));
			Double orgLongitude = Double.parseDouble(entryMap.get(coordinateLocationLongitudeKey));
			LatLng location = new LatLng(orgLatitude, orgLongitude);
			GeocodingApiRequest req = GeocodingApi.reverseGeocode(context, location);
			GeocodingResult[] resultsReverse = req.await();
			if(resultsReverse == null || resultsReverse.length == 0) {
				logger.debug("No reverse Gocoding result");
				continue;
			}
			for(GeocodingResult geocodingEntity : resultsReverse) {
				logger.debug(geocodingEntity.formattedAddress);
			}
			
			
			for(Map.Entry<String, String> entry : entryMap.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				if(key.startsWith(streetKey)) {
					String strAddress = entryMap.get(administrativeEntityKey)+ " " + value;
					GeocodingResult[] results =  null;//GeocodingApi.geocode(context, strAddress).await();
					StringBuilder strBuilder = new StringBuilder();
					strBuilder.append("Street (");
					strBuilder.append(value);
					if(results == null || results.length == 0) {
						strBuilder.append(") no result!");
						logger.debug(strBuilder.toString());
						continue;
					}
					String firstPart =strBuilder.toString();
					for(GeocodingResult geocodingEntity : results) {
						Double longitude = geocodingEntity.geometry.location.lng;
						Double latitude = geocodingEntity.geometry.location.lat;
						strBuilder = new StringBuilder();
						strBuilder.append(firstPart);
						strBuilder.append(") result: long (");
						strBuilder.append(longitude);
						strBuilder.append(") lat (");
						strBuilder.append(latitude);
						strBuilder.append(")");
						logger.debug(strBuilder.toString());
					}
				}
				
			}
			
		}
		fail("");
	}
	
	public void distanceComparison(List<String> coords, Double latitude, Double longitutde) {
		for(String coord : coords) {
			String[] split = coord.split(",");
			Double lat = Double.parseDouble(split[0]);
			Double lon =  Double.parseDouble(split[1]);
			Double distance = Math.abs(lat-latitude) + Math.abs(lon-longitutde);
			double factor = 10000;
			Double distanceRound = Math.round((distance * factor)) / factor;
			logger.debug(coord + " Distance: (" + distance + ") " +distanceRound);
		}
	}
	
	@Test
	public void checkDistance() {
		Double bnfLong = 2.375833;
		Double bnfLat = 48.833611;
		List<String> bnfGoogleList = Arrays.asList("48.8672911,2.3384118","48.86643549999999,2.3386024",
				"48.8682118,2.3381815","48.8669707,2.3394572","48.8672227,2.3389067", "48.83391109999999,2.3776154");
		logger.debug("Google BNF distances");
		distanceComparison(bnfGoogleList, bnfLat, bnfLong);
		List<String> bnfOSMList = Arrays.asList("48.8677158,2.3383186", "48.866471,2.3383888", "48.8679195,2.3394708",
				"48.8670223,2.3389229", "48.834463,2.3768672");
		logger.debug("OSM BNF distances");
		distanceComparison(bnfOSMList, bnfLat, bnfLong);
		
		Double BLLong = -0.126944;
		Double BLLat = 51.529444;
		List<String> BLList = Arrays.asList("51.5299658,-0.1276734","51.5298765,-0.127719844483978");
		logger.debug("BL distances");
		distanceComparison(BLList, BLLat, BLLong);
		
		Double NISVLong = 5.173056;
		Double NISVLat = 52.235278;
		List<String> NISVList = Arrays.asList("52.235459,5.1730565","52.2356788,5.1733122");
		logger.debug("NISVL distances");
		distanceComparison(NISVList, NISVLat, NISVLong);
		
		Double SASLong = 8.547531;
		Double SASLat = 47.366828;
		List<String> SASList = Arrays.asList("47.366825,8.547526200000002","47.3668235,8.5474436");
		logger.debug("SAS distances");
		distanceComparison(SASList, SASLat, SASLong);
	}
}
