package eu.europeana.enrichment.tp.api.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.model.impl.StoryEntityImpl;
import eu.europeana.enrichment.tp.api.client.model.CompletionStatus;
import eu.europeana.enrichment.tp.api.client.model.Item;
import eu.europeana.enrichment.tp.api.client.model.Language;
import eu.europeana.enrichment.tp.api.client.model.Story;
import eu.europeana.enrichment.tp.api.client.model.Transcription;

@Service
public class EnrichmentTpApiClient {
	
	Logger logger = LogManager.getLogger(getClass());

	@Autowired
	EnrichmentConfiguration configuration;
		
	public StoryEntityImpl getStoryFromTranscribathonMinimalStory(String storyId) {
		Story storyMinimal = getTranscribathonMinimalStory(storyId);
		if(storyMinimal!=null) {
			return convertTranscribathonStoryToLocalStory(storyMinimal);
		}
		else {
			return null;
		}
	}

	public ItemEntityImpl getItemFromTranscribathon(String itemId) {
		Item tpItem = getTranscribathonItem(itemId);
		if(tpItem!=null) {
			return convertTranscribathonItemToLocalItem(tpItem);
		}
		else {
			return null;
		}
	}
	
	public List<String> getItemIdsForStoryFromTranscribathon(String storyId) {
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

	private Story getTranscribathonMinimalStory(String storyId) {
		String storyMinimalResponse = HelperFunctions.createHttpRequest(null, configuration.getTranscribathonBaseUrlStoriesMinimal() + storyId);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			List<Story> storiesMinimal = objectMapper.readValue(storyMinimalResponse, new TypeReference<List<Story>>(){});
			if(storiesMinimal!=null && storiesMinimal.size()>0) {
				return storiesMinimal.get(0);
			}
			else {
				return null;
			}
		} catch (JsonProcessingException e) {
			logger.log(Level.ERROR, "Exception during deserializing a minimal story from Transcribathon with storyId=" + storyId, e);
			return null;
		}
	}
	
	private Item getTranscribathonItem(String itemId) {
		String response = HelperFunctions.createHttpRequest(null, configuration.getTranscribathonBaseUrlItems() + itemId);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readValue(response, new TypeReference<Item>(){});
		}
		catch (JsonProcessingException e) {
			logger.log(Level.ERROR, "Exception during deserializing an item from Transcribathon with itemId=" + itemId, e);
			return null;
		}
	}
	

	private Story getTranscribathonFullStory(String storyId) {
		String storyFullResponse = HelperFunctions.createHttpRequest(null, configuration.getTranscribathonBaseUrlStories() + storyId);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			List<Story> storiesFull = objectMapper.readValue(storyFullResponse, new TypeReference<List<Story>>(){});
			if(storiesFull!=null && storiesFull.size()>0) {
				return storiesFull.get(0);
			}
			else {
				return null;
			}
		} catch (JsonProcessingException e) {
			logger.log(Level.ERROR, "Exception during deserializing a full story from Transcribathon with storyId= " + storyId, e);
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

	private ItemEntityImpl convertTranscribathonItemToLocalItem (Item item) {
		if(item.Transcriptions==null) return null;
		ItemEntityImpl newItem = new ItemEntityImpl();
		boolean foundTranscription = false;
		for (Transcription trElem : item.Transcriptions) {
			if(trElem.TextNoTags!=null && !trElem.TextNoTags.isBlank())
			{
				newItem.setTranscriptionText(trElem.TextNoTags);
				newItem.setTranscriptionLanguages(getLanguages(trElem.Languages));
				foundTranscription = true;
				break;
			}
		}
		if(!foundTranscription) return null;

		newItem.setItemId(String.valueOf(item.ItemId));
		newItem.setStoryId(String.valueOf(item.StoryId));
		newItem.setTitle(item.Title);

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
