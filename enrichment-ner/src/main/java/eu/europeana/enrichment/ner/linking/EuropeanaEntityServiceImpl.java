package eu.europeana.enrichment.ner.linking;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import eu.europeana.entity.client.web.WebEntityProtocolApi;
import eu.europeana.entity.definitions.model.Entity;

public class EuropeanaEntityServiceImpl implements EuropeanaEntityService {

	/*
	 * Europeana API key
	 */
	private String key;
	@Resource(name= "europeanaApiClient")
	private WebEntityProtocolApi europeanaApiClient;
	
	public EuropeanaEntityServiceImpl(String key) {
		this.key = key;
	}
	
	@Override
	public List<String> getEntitySuggestions(String text, String classificationType, String language) {
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
			return null;
		}
		//List<Entity> suggestions = europeanaApiClient.getSuggestions(key, textURIEncoded, "en", "100");
		List<Entity> suggestions = europeanaApiClient.getSearch(key, textURIEncoded, language, classificationType, sortUrlEncoded, "0", "100");
		if(suggestions != null && suggestions.size() > 0) {
			for(Entity entity : suggestions) {
				entityIDs.add(entity.getEntityId());
			}
		}
		else
			return null;
		return entityIDs;
	}

	@Override
	public String retriveEntity(String idUrl, String language) {
		Entity entity = null;//europeanaApiClient.retrieveEntityWithUrl(idUrl);
		if(entity == null)
			return "";
		Map<String, String> prefLabelMap = entity.getPrefLabel();
		if(prefLabelMap.containsKey(""))
			return prefLabelMap.get("");
		else if(prefLabelMap.containsKey("en"))
			return prefLabelMap.get("en");
		else if(prefLabelMap.containsKey(language))
			return prefLabelMap.get(language);
		
		return entity.getInternalType();
	}

}
