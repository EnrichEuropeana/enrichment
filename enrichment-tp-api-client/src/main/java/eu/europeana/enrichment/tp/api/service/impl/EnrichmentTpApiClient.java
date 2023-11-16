package eu.europeana.enrichment.tp.api.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.definitions.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.definitions.model.impl.StoryEntityImpl;
import eu.europeana.enrichment.tp.api.client.model.CompletionStatus;
import eu.europeana.enrichment.tp.api.client.model.Item;
import eu.europeana.enrichment.tp.api.client.model.Language;
import eu.europeana.enrichment.tp.api.client.model.Story;
import eu.europeana.enrichment.tp.api.client.model.Transcription;
import eu.europeana.enrichment.tp.api.utils.XsltXmlParser;

@Service
public class EnrichmentTpApiClient {
	
	Logger logger = LogManager.getLogger(getClass());

	@Autowired
	EnrichmentConfiguration configuration;
		
	public StoryEntityImpl getStoryFromTranscribathonMinimalStory(String storyId) throws ClientProtocolException, IOException {
		Story storyMinimal = getTranscribathonMinimalStory(storyId);
		if(storyMinimal!=null) {
			return convertTranscribathonStoryToLocalStory(storyMinimal);
		}
		else {
			return null;
		}
	}

	public ItemEntityImpl getItemFromTranscribathon(String itemId) throws ClientProtocolException, IOException, TransformerException {
		Item tpItem = getTranscribathonItem(itemId);
		if(tpItem!=null) {
			return convertTranscribathonItemToLocalItem(tpItem);
		}
		else {
			return null;
		}
	}
	
	public List<String> getItemIdsForStoryFromTranscribathon(String storyId) throws ClientProtocolException, IOException {
		List<String> itemIds = new ArrayList<>();
		Story tpStory = getTranscribathonFullStory(storyId);
		if(tpStory!=null) {
			if(tpStory.Items!=null) {
				for(Item tpItem : tpStory.Items) {
					if(tpItem.ItemId!=null) {
						itemIds.add(Integer.toString(tpItem.ItemId));
					}
				}
			}
		}
		return itemIds;
	}

	private Story getTranscribathonMinimalStory(String storyId) throws ClientProtocolException, IOException {
		String storyMinimalResponse = HelperFunctions.createHttpRequest(null, configuration.getTranscribathonBaseUrlStoriesMinimal() + storyId, null);
		if(storyMinimalResponse==null) {
			return null;
		}

		ObjectMapper objectMapper = new ObjectMapper();
		List<Story> storiesMinimal = objectMapper.readValue(storyMinimalResponse, new TypeReference<List<Story>>(){});
		if(storiesMinimal!=null && storiesMinimal.size()>0) {
			return storiesMinimal.get(0);
		}
		else {
			return null;
		}
	}
	
	private Item getTranscribathonItem(String itemId) throws ClientProtocolException, IOException {
		String response = HelperFunctions.createHttpRequest(null, configuration.getTranscribathonBaseUrlItems() + itemId, null);
		if(response==null) {
			return null;
		}
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(response, new TypeReference<Item>(){});
	}
	
	/**
	 * Returns a json string.
	 * @param itemId
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws TransformerException
	 */
	private String getHtrdataItemTranscription(String itemId) throws ClientProtocolException, IOException, TransformerException {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", configuration.getHtrdataItemsAuthorization());
		String htrdataResp = HelperFunctions.createHttpRequest(null, configuration.getHtrdataItemsBaseUrl() + itemId + configuration.getHtrdataItemsSuffix(), headers);
		if(htrdataResp==null) {
			return null;
		}
		return htrdataResp;
	}

	private Story getTranscribathonFullStory(String storyId) throws ClientProtocolException, IOException {
		String storyFullResponse = HelperFunctions.createHttpRequest(null, configuration.getTranscribathonBaseUrlStories() + storyId, null);
		if(storyFullResponse==null) {
			return null;
		}
		ObjectMapper objectMapper = new ObjectMapper();
		List<Story> storiesFull = objectMapper.readValue(storyFullResponse, new TypeReference<List<Story>>(){});
		if(storiesFull!=null && storiesFull.size()>0) {
			return storiesFull.get(0);
		}
		else {
			return null;
		}
	}

	private StoryEntityImpl convertTranscribathonStoryToLocalStory(Story storyMinimal) {	
		StoryEntityImpl newStory = new StoryEntityImpl();
		newStory.setStoryId(storyMinimal.StoryId.toString());
		newStory.setDescription(storyMinimal.dcDescription);
		newStory.setSummary(storyMinimal.Summary);
		newStory.setTitle(storyMinimal.dcTitle);
		newStory.setSource(storyMinimal.dcSource);
		if(storyMinimal.dcDescription!=null) newStory.setLanguageDescription(storyMinimal.dcLanguage);
		if(storyMinimal.Summary!=null) newStory.setLanguageSummary(storyMinimal.dcLanguage);
		List<CompletionStatus> completionStatus = storyMinimal.CompletionStatus;
		if(completionStatus!=null && completionStatus.size()>0) {
			Map<String, Integer> completionStatus2 = new HashMap<String, Integer>();
			int itemCount = 0;
			for (CompletionStatus csElem : completionStatus) {
				completionStatus2.put(csElem.Name, csElem.Amount);
				itemCount += csElem.Amount.intValue();
			}
			newStory.setCompletionStatus(completionStatus2);
			newStory.setItemCount(itemCount);
		}
		return newStory;
	}

	private ItemEntityImpl convertTranscribathonItemToLocalItem (Item item) throws ClientProtocolException, IOException, TransformerException {
		ItemEntityImpl newItem = new ItemEntityImpl();
		if(item.Transcriptions!=null) {
			for (Transcription trElem : item.Transcriptions) {
				if(trElem.TextNoTags!=null && !trElem.TextNoTags.isBlank())
				{
					newItem.setTranscriptionText(trElem.TextNoTags);
					if(trElem.Languages!=null) {
						newItem.setTranscriptionLanguages(getLanguages(trElem.Languages));
					}
					break;
				}
			}
		}
		newItem.setItemId(String.valueOf(item.ItemId));
		newItem.setStoryId(String.valueOf(item.StoryId));
		newItem.setTitle(item.Title);

		//update the htrdata data, i.e. fetch it from the remote url
		String htrdataResp = getHtrdataItemTranscription(String.valueOf(item.ItemId));
		if(htrdataResp!=null) {
			JSONObject htrdataRespJson = new JSONObject(htrdataResp);
			//check the response is right -> "success": true 
			if(htrdataRespJson.getBoolean("success")) {
				String pageXml = htrdataRespJson.getJSONObject("data").getString("TranscriptionData");
				String htrdataTranscr = XsltXmlParser.transfromXmlToString(configuration.getHtrdataItemsXslFile(), pageXml);
				List<String> htrdataTranscrLangs = new ArrayList<String>();
				JSONArray langsJson = htrdataRespJson.getJSONObject("data").getJSONArray("Language");
				for(int i=0; i<langsJson.length(); i++) {
					htrdataTranscrLangs.add(langsJson.getJSONObject(i).getString("Code"));
				}
				newItem.setHtrdataTranscription(htrdataTranscr);
				newItem.setHtrdataTranscriptionLangs(htrdataTranscrLangs);
			}
		}

		return newItem;
	}

	private List<String> getLanguages (List<Language> transcribathonLanguages) {
		List<String> result = new ArrayList<String>();
		for(Language lang : transcribathonLanguages) {
			result.add(lang.Code);
		}
		return result;
	}

}
