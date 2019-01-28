package eu.europeana.enrichment.ner.linking;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import eu.europeana.entity.client.web.WebEntityProtocolApi;
import eu.europeana.entity.client.web.WebEntityProtocolApiImpl;
import eu.europeana.entity.definitions.model.Entity;

public class EuropeanaEntityServiceImpl implements EuropeanaEntityService {

	private String key = "apidemo";
	
	private WebEntityProtocolApi europeanaApiClient;
	
	public EuropeanaEntityServiceImpl() {
		europeanaApiClient = new WebEntityProtocolApiImpl();
	}
	
	//http://entity-api-test.eanadev.org/entity/search?wskey=apidemo&query=label%3AGermany&lang=all&type=Place&sort=derived_score%2Bdesc&page=0&pageSize=10
	@Override
	public String getEntitySuggestions(String text, String classificationType, String language) {
		List<String> entityIDs = new ArrayList<>();
		String textURIEncoded = "";
		String sortUrlEncoded = "derived_score+desc";
		try {
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append("label:");
			strBuilder.append(text);
			textURIEncoded = URLEncoder.encode(strBuilder.toString(), "UTF-8");
			sortUrlEncoded = URLEncoder.encode(sortUrlEncoded, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//List<Entity> suggestions = europeanaApiClient.getSuggestions(key, textURIEncoded, "en", "100");
		List<Entity> suggestions = europeanaApiClient.getSearch(key, textURIEncoded, language, classificationType, sortUrlEncoded, "0", "100");
		if(suggestions != null && suggestions.size() > 0) {
			for(Entity entity : suggestions) {
				entityIDs.add(entity.getEntityId());
			}
		}
		return String.join(",", entityIDs);
	}

	@Override
	public String retriveEntity(String idUrl) {
		Entity entity = europeanaApiClient.retrieveEntityWithUrl(idUrl);
		if(entity == null)
			return "";
		Map<String, String> prefLabelMap = entity.getPrefLabel();
		if(prefLabelMap.containsKey(""))
			return prefLabelMap.get("");
		else if(prefLabelMap.containsKey("en"))
			return prefLabelMap.get("en");
		
		return entity.getInternalType();
	}

}
