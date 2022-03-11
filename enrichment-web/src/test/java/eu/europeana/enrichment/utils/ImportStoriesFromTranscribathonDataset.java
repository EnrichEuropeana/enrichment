package eu.europeana.enrichment.utils;

import java.io.IOException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.enrichment.common.commons.HelperFunctions;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.impl.StoryEntityImpl;
import eu.europeana.enrichment.mongo.service.PersistentItemEntityService;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;
import eu.europeana.enrichment.mongo.service.PersistentTranslationEntityService;
import eu.europeana.enrichment.solr.commons.JavaJSONParser;
import objects.Dataset;
import objects.Item;
import objects.Story;
import objects.Transcription;

/**
* 
* @author StevaneticS
*
* Importing stories and items to the mongo db from a json file
*/
//@ContextConfiguration(classes = { EnrichmentApp.class})
@SpringBootTest
@Disabled("Excluded from automated runs.")
public class ImportStoriesFromTranscribathonDataset {
		
	Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	PersistentStoryEntityService persistentStoryEntityService;
	@Autowired
	PersistentItemEntityService persistentItemEntityService;
	@Autowired
	PersistentTranslationEntityService persistentTranslationEntityService;
	@Autowired
	JavaJSONParser javaJSONParser;
	
	@Test
	public void getDatasetEuropeana1989FromTranscribathon() throws IOException {
		String datasetId = "";
		String datasetName = "Europeana 1989";
		String URLDataset = "https://europeana.fresenia.man.poznan.pl/tp-api/datasets/";
		String URLStoriesMinimal = "https://europeana.fresenia.man.poznan.pl/tp-api/storiesMinimal?DatasetId=";
		String URLStoriesComplete = "https://europeana.fresenia.man.poznan.pl/tp-api/stories/";
		String URLItemTranscription = "https://europeana.fresenia.man.poznan.pl/tp-api/transcriptions?ItemId=";
		String responseDatasets = HelperFunctions.createHttpRequest(null, URLDataset);
		ObjectMapper objectMapper = new ObjectMapper();
		List<Dataset> listDatasets = objectMapper.readValue(responseDatasets, new TypeReference<List<Dataset>>(){});
        if(listDatasets==null || listDatasets.isEmpty()) return;
		for(Dataset dataset : listDatasets) {
        	if(dataset.Name.equals(datasetName)) {
            	datasetId=dataset.DatasetId.toString();
            	break;
            }
        }
		
		String responseStoriesMinimal = HelperFunctions.createHttpRequest(null, URLStoriesMinimal+datasetId);

		List<Story> listStoriesMinimal = objectMapper.readValue(responseStoriesMinimal, new TypeReference<List<Story>>(){});
		if(listStoriesMinimal==null || listStoriesMinimal.isEmpty()) return;
		
		for (Story minimalStory : listStoriesMinimal) {

			String responseStoriesComplete = HelperFunctions.createHttpRequest(null, URLStoriesComplete+minimalStory.StoryId.toString());

			List<Story> listStoriesComplete = objectMapper.readValue(responseStoriesComplete, new TypeReference<List<Story>>(){});

			List<Item> storyItems = listStoriesComplete.get(0).Items;
			if(storyItems==null || storyItems.isEmpty()) continue;
			
			String storyTranscription = "";
			for(Item item : storyItems) {
				
				String responseItemTranscription = HelperFunctions.createHttpRequest(null, URLItemTranscription+item.ItemId.toString());
				List<Transcription> listItemTranscription = objectMapper.readValue(responseItemTranscription, new TypeReference<List<Transcription>>(){});
				if(listItemTranscription!=null && listItemTranscription.size()>0) {
					storyTranscription+=listItemTranscription.get(0).Text;
				}
			}
			
			StoryEntity [] newStories = new StoryEntity [1];
			newStories[0] = new StoryEntityImpl();
			
			if(!storyTranscription.equalsIgnoreCase("")) newStories[0].setTranscriptionText(storyTranscription);
			newStories[0].setDescription(minimalStory.dcDescription);
			newStories[0].setSource(minimalStory.dcSource);
			newStories[0].setStoryId(minimalStory.StoryId.toString());
			newStories[0].setTitle(minimalStory.dcTitle);
			
			//uploadStories(newStories);

			//extracting items in one json file
//			String fileName = "C:/conceptual_search_documents/allStoryTranslations-BERT-LDA.csv";
//			BufferedWriter bwTranslations = new BufferedWriter(new FileWriter(new File(fileName)));
//			bwTranslations.write("storyId,text"+"\n");				
//			
//			for(int i=0;i<listStoryTranscribathon.size();i++)
//			{
//				if(storyTranslations.get(i).getLanguage()!=null && storyTranslations.get(i).getLanguage().equalsIgnoreCase("en") && storyTranslations.get(i).getTranscriptionText()!=null && !storyTranslations.get(i).getTranscriptionText().isEmpty())
//				{				
//					String storyTranscriptionOneLine = storyTranslations.get(i).getTranscriptionText().replaceAll("[\r\n\t]+", " ");
//
//					String correctedTranslationsForQuotationWithin = storyTranscriptionOneLine.replaceAll("\"", "");
//					
//					String removedAllNonAsciiCharacters = correctedTranslationsForQuotationWithin.replaceAll("[^\\p{ASCII}]", "");
//
//					bwTranslations.write(storyTranslations.get(i).getStoryId() + ",\"" + removedAllNonAsciiCharacters + "\"\n");
//					
//					logger.debug("Currently analysed story with storyId: " + storyTranslations.get(i).getStoryId() +". \n");
//				
//				}
//			}		
//			bwTranslations.close();

			}
		}

}
