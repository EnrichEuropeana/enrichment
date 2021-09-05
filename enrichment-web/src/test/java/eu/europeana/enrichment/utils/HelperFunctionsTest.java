package eu.europeana.enrichment.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelperFunctionsTest {

	public static List<String> findRegexpMatches (String input, String regex) throws IOException {
		List<String> allMatches = new ArrayList<String>();
		Matcher m = Pattern.compile(regex).matcher(input);
		while (m.find()) {
			allMatches.add(m.group());
		}
		return allMatches;
	}
	
	public static String readFileToString (Path fileName) throws IOException {
		return Files.readString(fileName);
	}
}
