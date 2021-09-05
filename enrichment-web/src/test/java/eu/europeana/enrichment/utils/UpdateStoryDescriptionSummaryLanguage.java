package eu.europeana.enrichment.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.mongo.service.PersistentStoryEntityService;

@SpringBootTest
public class UpdateStoryDescriptionSummaryLanguage {
	
	@Autowired
	PersistentStoryEntityService persistentStoryEntityService;
	
	Logger logger = LogManager.getLogger(getClass());
	
	@Test
	public void updateStoryDescriptionLanguage() throws IOException {		
		Path path = Paths.get("C:\\tmp\\story_description_language_non_english.csv");
		List<String> storyIdLanguage = readSparkLanguageAnnotationStoryFile(path);
		for (String storyIdLanguageItem : storyIdLanguage) {
			String[] splitStoryIdLanguage = storyIdLanguageItem.substring(0, storyIdLanguageItem.length()-1).split(",");
			String storyId = splitStoryIdLanguage[0];
			String language = splitStoryIdLanguage[1];
			StoryEntity story = persistentStoryEntityService.findStoryEntity(storyId);
			story.setLanguageDescription(language);
			persistentStoryEntityService.saveStoryEntity(story);
		}
	}
	
	private List<String> readSparkLanguageAnnotationStoryFile (Path path) throws IOException {
		String fileContent = HelperFunctionsTest.readFileToString(path);
		/**
		 * finding the string pattern: "<story id>,<annotated language>," in the string
		 * since there are line breaks within a single field values
		 */
        //regular expression for an integer number
        String regexInteger = "[+-]?[0-9]+";
        String regexComma = ",";
        //regular expression for 2 small letters
        String regex2Letters = "[a-z]{2}";
        String regexAll = regexInteger + regexComma + regex2Letters + regexComma;

        List<String> allMatches = HelperFunctionsTest.findRegexpMatches(fileContent, regexAll);
        return allMatches;
	}

}
