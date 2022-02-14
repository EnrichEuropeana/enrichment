package eu.europeana.enrichment.web.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.impl.ItemEntityImpl;
import eu.europeana.enrichment.model.impl.StoryEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.web.service.EnrichmentStoryAndItemStorageService;
import objects.CompletionStatus;
import objects.Item;
import objects.Language;
import objects.Story;
import objects.Transcription;

@Service
public class EnrichmentStoryAndItemStorageServiceImpl implements EnrichmentStoryAndItemStorageService{
	
	@Autowired
	PersistentStoryEntityService persistentStoryEntityService;
	
	@Autowired
	PersistentItemEntityService persistentItemEntityService;

    private static final String transcribathonBaseURLStoriesMinimal = "https://europeana.fresenia.man.poznan.pl/tp-api/storiesMinimal/";
    private static final String transcribathonBaseURLItems = "https://europeana.fresenia.man.poznan.pl/tp-api/items/";
	
	public StoryEntity fetchMinimalStoryFromTranscribathon(String storyId) {
		String storyMinimalResponse = HelperFunctions.createHttpRequest(null, transcribathonBaseURLStoriesMinimal + storyId);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			List<Story> storyMinimal = objectMapper.readValue(storyMinimalResponse, new TypeReference<List<Story>>(){});
			if(storyMinimal!=null && storyMinimal.size()>0) {
				return convertTranscribathonStoryToLocalStory(storyMinimal.get(0));
			}
			else return null;
		} catch (JsonProcessingException e) {
			System.out.println("An exception: " + e.toString() + "is thrown during fetching or deserializing the story (minimal profile) from Transcribathon.");
			e.printStackTrace();
			return null;
		}
		
	}
	
	public StoryEntity convertTranscribathonStoryToLocalStory(Story storyMinimal) {	
		StoryEntity newStory = new StoryEntityImpl();
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
	
	private List<String> getLanguages (List<Language> transcribathonLanguages) {
		List<String> result = new ArrayList<String>();
		for(Language lang : transcribathonLanguages) {
			result.add(lang.Code);
		}
		return result;
	}
	
	private ItemEntity createItemFromTranscribathonOne (Item item) {
		if(item.Transcriptions==null) return null;
		ItemEntity newItem = new ItemEntityImpl();
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
	
	public ItemEntity fetchAndSaveItemFromTranscribathon(String storyId, String itemId) {
		ItemEntity existingItem = persistentItemEntityService.findItemEntity(itemId);
		if(existingItem == null )
		{				
			String response = HelperFunctions.createHttpRequest(null, transcribathonBaseURLItems+itemId);
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				List<Item> listItemTranscribathon = objectMapper.readValue(response, new TypeReference<List<Item>>(){});
				ItemEntity newItem = null;
				if(listItemTranscribathon!=null && listItemTranscribathon.size()>0) {
					newItem = createItemFromTranscribathonOne(listItemTranscribathon.get(0));
					if(newItem!=null) {
						persistentItemEntityService.saveItemEntity(newItem);
						//fetch the story if it does not exist
						StoryEntity existingStory = persistentStoryEntityService.findStoryEntity(storyId);
						if(existingStory==null) {
							StoryEntity newStory = fetchMinimalStoryFromTranscribathon(storyId);
							if(newStory!=null) persistentStoryEntityService.saveStoryEntity(newStory);
						}
					}
				}
				return newItem;
			} catch (JsonProcessingException e) {
				System.out.println("An exception: " + e.toString() + "is thrown during fetching or deserializing an item from Transcribathon.");
				e.printStackTrace();
				return null;
			}
			
		}
		else  {
			return existingItem;
		}

	}

	public StoryEntity fetchAndSaveStoryFromTranscribathon(String storyId)
	{
		StoryEntity transcribathonStory = fetchMinimalStoryFromTranscribathon(storyId);
		if(transcribathonStory!=null) persistentStoryEntityService.saveStoryEntity(transcribathonStory);
		return transcribathonStory;
	}

}
