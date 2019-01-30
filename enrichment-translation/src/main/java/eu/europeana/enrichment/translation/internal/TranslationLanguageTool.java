package eu.europeana.enrichment.translation.internal;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;

public class TranslationLanguageTool {

	JLanguageTool langTool;
	
	/*
	 * This class constructor initialized JLanguageTool with 
	 * the American English corpus 
	 */
	public TranslationLanguageTool() {
		langTool = new JLanguageTool(new AmericanEnglish());
	}
	
	/*
	 * This method divides the translated text into sentences 
	 * for further steps.
	 * 
	 * @param text						is the translated text
	 * @return							a list of sentences
	 */
	public List<String> sentenceSplitter(String text){
		List<String> retValue = new ArrayList<String>();
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		//String source = "This is a test. This is a T.L.A. test. Now with a Dr. in it.";
		iterator.setText(text);
		int start = iterator.first();
		for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
			//system.out.println(source.substring(start,end));
			retValue.add(text.substring(start, end));
		}
		return retValue;
	}
	
	/*
	 * This method calculates the English word ratio based on the non English words
	 * and the size of this sentences.
	 * 
	 * @param sentence					is one sentence of the translated text
	 * @return							English sentence ratio
	 */
	public double getLanguageRatio(String sentence) {
		List<RuleMatch> matches;
		List<String> nonEnglishWords = new ArrayList<>();
		int nonEnglishWordsSize = 0;
		try {
			matches = langTool.check(sentence);
			for (RuleMatch match : matches) {
				nonEnglishWords.add(sentence.substring(match.getFromPos(), match.getToPos()));
				nonEnglishWordsSize += (match.getToPos()- match.getFromPos());
			  //System.out.println("Suggested correction(s): " + match.getSuggestedReplacements());
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		double ratio = 1 - (nonEnglishWordsSize / (double)(sentence.length()));
		return ratio;
	}
	
}
