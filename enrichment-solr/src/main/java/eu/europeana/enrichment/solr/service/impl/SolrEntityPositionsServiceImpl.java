package eu.europeana.enrichment.solr.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;
import org.tartarus.snowball.ext.germanStemmer;
import org.tartarus.snowball.ext.romanianStemmer;

import eu.europeana.enrichment.common.commons.AppConfigConstants;
import eu.europeana.enrichment.common.commons.EnrichmentConfiguration;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.model.utils.ModelUtils;
import eu.europeana.enrichment.solr.commons.GoogleTranslator;
import eu.europeana.enrichment.solr.commons.JavaJSONParser;
import eu.europeana.enrichment.solr.commons.LevenschteinDistance;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.solr.model.SolrStoryEntityImpl;
import eu.europeana.enrichment.solr.model.vocabulary.StoryEntitySolrFields;
import eu.europeana.enrichment.solr.service.SolrBaseClientService;
import eu.europeana.enrichment.solr.service.SolrEntityPositionsService;
import eu.europeana.enrichment.translation.service.impl.ETranslationEuropaServiceImpl;

@Service(AppConfigConstants.BEAN_ENRICHMENT_SOLR_ENTITY_POSITIONS_SERVICE)
public class SolrEntityPositionsServiceImpl implements SolrEntityPositionsService{

	Logger logger = LogManager.getLogger(getClass());
	
	//@Resource(name = "solrBaseClientService")
	@Autowired
	SolrBaseClientService solrBaseClientService;
	
	//@Resource(name = "javaJSONParser")
	@Autowired
	JavaJSONParser javaJSONParser;

	//@Resource(name = "levenschteinDistance")
	@Autowired
	LevenschteinDistance levenschteinDistance;
	
	//@Resource(name = "googleTranslator")
	@Autowired
	GoogleTranslator googleTranslator;

	
	//@Resource(name = "eTranslationService")
	@Autowired
	ETranslationEuropaServiceImpl eTranslationService;

	/*
	 * The "scaleFactorRangeOfCharsToObserve" param below is introduced to make sure we are searching in a range that is big enough to with the matching term
	 * for the fuzzy search set the params below to: minRangeOfCharsToObserve = 1500;rangeOfCharsToObserve = 1500;scaleFactorRangeOfCharsToObserve = 1.5;
	 * for normal search (not fuzzy) set the params below to: minRangeOfCharsToObserve = 10000;rangeOfCharsToObserve = 10000;
	*/
	
	private String solrCore = "enrichment";
	private final int LevenschteinDistanceThreshold = 2;
	private int minRangeOfCharsToObserve = 1000;
	private int rangeOfCharsToObserve = 0;
	private double scaleFactorRangeOfCharsToObserve = 1.5;
	private double averageWordLeghtDifference = 1.0;
	
	/*
	 * when the sentences are used to find the named entities in the original text, use the following 2 params:
	 * charsToCheck->the number of characters to search for sentences and the term inside them, starting from the currently reached char-position in the original text
	 * numSentToCheckArround->number of sentences to check around the one where the term in the translated text is found
	 */
	private int charsToCheck=20000;
	private int numSentToCheckArround = 2;
	
	private final Logger log = LogManager.getLogger(getClass());
	private List<String> entitiesOriginalText = new ArrayList<String>();
	private String storyOriginalText;
	private String storyTranslatedText;
	private String storyOriginalLanguage;
	private String storyTranslatedLanguage;
	private String storyIdSolr;
	private boolean fuzzyLogicSolr = false;
	
	private int sentencesByNowTranslated=1;
	private int sentencesByNowOriginal=1;
	private int indexByNowOriginal=0;
	//private Jyandex clientJyandex;

	@Autowired
	public SolrEntityPositionsServiceImpl(EnrichmentConfiguration enrichmentConfiguration) throws IOException {
		
		//clientJyandex = new Jyandex("trnsl.1.1.20190321T145012Z.5582e98b0b19430e.69e76d055bdf6b87efbda7891df751a1df9ba33f");
		
		if(!enrichmentConfiguration.getSolrTranslatedEntities().isEmpty())
		{
			String data; 
			try {
				data = new String(Files.readAllBytes(Paths.get(enrichmentConfiguration.getSolrTranslatedEntities())));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.log(Level.ERROR, "Exception during reading the solr entity positions from a file.", e);
				throw e;
			}
			String[] entities = data.split(",");
			for(int i=0;i<entities.length;i++)
			{
				String[] entityType = entities[i].split("\\s+",2);
				entitiesOriginalText.add(entityType[1]); 
			}			
		}
	}
	
	@Override
	public void store(List<? extends StoryEntity> storyEntities) throws SolrNamedEntityServiceException {
		
		for(StoryEntity ent : storyEntities) {
			store(solrCore, ent, true);
		}
	}

	@Override
	public void store(String solrCollection, StoryEntity storyEntity, boolean doCommit) throws SolrNamedEntityServiceException {

		log.debug("store: " + storyEntity.toString());
		
		SolrStoryEntityImpl solrStoryEntity = null;
		if(storyEntity instanceof SolrStoryEntityImpl) {
			solrStoryEntity=(SolrStoryEntityImpl) storyEntity;
		}
		else {
			solrStoryEntity=new SolrStoryEntityImpl(storyEntity);
		}
		
		solrBaseClientService.storeStoryEntity(solrCollection, solrStoryEntity,doCommit);
		
	}

	@Override
	public int findTermPositionsInStory(String term, int startAfterOffset, int offsetTranslatedText, int rangeToObserve) throws Exception {
	
		/*
		 * filtering the searchTerm
		 */
		String termLowerCaseStemmed = filterSearchTerms(storyOriginalLanguage, term);
				
		/*
		 * creating Solr query for finding offsets unsing Highlighter
		 */
		Map<String, String> querySearchParams = new HashMap<String, String>();
		querySearchParams.put(StoryEntitySolrFields.STORY_ID, storyIdSolr);
		SolrQuery query = createSolrQuery(fuzzyLogicSolr,termLowerCaseStemmed,querySearchParams);
		
		QueryResponse response=null;
		
		List<String> terms = new ArrayList<String>();
		List<Double> positions = new ArrayList<Double>();
		List<List<Double>> offsets = new ArrayList<List<Double>>();

	
		/*
		 * quering Solr server and parsing the obtained json response to extract the offsets, terms and positions
		 */
		try {
			response = solrBaseClientService.query(solrCore, query);
			javaJSONParser.getPositionsFromJSON(response, terms, positions, offsets);
		} catch (ParseException e) {
			throw new SolrNamedEntityServiceException("Exception occured when parsing JSON response from Solr. Searched for the term: " + termLowerCaseStemmed,e);
		}
		
		if(terms.isEmpty()) return -1;
	
		if(fuzzyLogicSolr)
		{
			List<String> termsAdapted = new ArrayList<String>();
			List<Double> positionsAdapted = new ArrayList<Double>();
			List<List<Double>> offsetsAdapted = new ArrayList<List<Double>>();
			
			log.info("Solr query: " + query.toString());
			log.info("Solr query response, terms adapted: " + termsAdapted.toString());
			log.info("Solr query response, offsets adapted: " + offsetsAdapted.toString());
			
			adaptTermsPositionsOffsets(offsetTranslatedText,termLowerCaseStemmed,terms,positions,offsets,termsAdapted,positionsAdapted,offsetsAdapted);
	
			if(termsAdapted.isEmpty()) return -1;
			//finding the exact offset of the term from the list of all offsets
			double exactOffset = findNextOffset(offsetsAdapted, startAfterOffset,termLowerCaseStemmed.split("\\s+").length, rangeToObserve);
			return (int) exactOffset;
		}
		else
		{
			double exactOffset = findNextOffset(offsets, startAfterOffset,termLowerCaseStemmed.split("\\s+").length, rangeToObserve);
			return (int) exactOffset;
		}
	
		
	}
	/**
	 * This function filters the initial search term with additional analysis. It corresponds to the solr filter and analyzers 
	 * used for indexing and searching. This function should exactly mimic the behavior of the solr configuration filters and 
	 * analyzers in the solr configuration file schema.xml. However, it does not have to be that way. 
	 * 
	 * @param language
	 * @param searchTerm
	 * @return
	 */
	
	private String filterSearchTerms(String language, String searchTerm)
	{
		/*
		 * to lower case filter
		 */
		//String termLowerCase=searchTerm.toLowerCase();
		String termLowerCase=searchTerm;
		
		Pattern ptn = Pattern.compile("[\\p{Punct}\\p{Digit}&&[^-]]");
        Matcher mtch = ptn.matcher(termLowerCase);
        String termRemovedSpecialChars = mtch.replaceAll("");
		
		/*
		 * to ASCII code filter
		 */

		String termLowerCaseStemmed = "";
		
//		termLowerCase = termLowerCase.replace("ă", "a");
//		termLowerCase = termLowerCase.replace("â", "a");
//		termLowerCase = termLowerCase.replace("î", "i");
//		termLowerCase = termLowerCase.replace("ş", "s");
//		termLowerCase = termLowerCase.replace("ţ", "t");
//				
//		termLowerCase = Normalizer.normalize(termLowerCase, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
//        					
//		try {
//			/*
//			 * please see here for the char standards for different languages: https://www.terena.org/activities/multiling/ml-docs/iso-8859.html
//			 */
//			termLowerCaseAndASCII = new String(termLowerCase.getBytes("ISO-8859-2"), "ASCII");
//		} catch (UnsupportedEncodingException e1) {				
//			e1.printStackTrace();
//		}
		
		SnowballStemmer snowballStemmer = null;
		//TODO: this is hardcoded language - needs to be improved
		if(language.compareTo("Romanian")==0)
		{
			snowballStemmer = new romanianStemmer();
		}
		else if (language.compareTo("English")==0)
		{
			snowballStemmer = new englishStemmer();
		}
		else if (language.compareTo("German")==0)
		{
			snowballStemmer = new germanStemmer();
		}
		
		String [] termLowerCaseWords = termRemovedSpecialChars.split("\\s+");	
		
		for (int i=0;i<termLowerCaseWords.length;i++)
		{		
			snowballStemmer.setCurrent(termLowerCaseWords[i]);
		    snowballStemmer.stem();
		    if(i==termLowerCaseWords.length-1)
		    {
				/*
				 * to ASCII replacement filter (e.g.ä->a, etc.) together with a stemmer
				 */
		    	termLowerCaseStemmed += Normalizer.normalize(snowballStemmer.getCurrent(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
		    }
		    else
		    {
		    	termLowerCaseStemmed += Normalizer.normalize(snowballStemmer.getCurrent(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "") + " ";	
		    }

		}
		
		/*
		//another stemmer
		Stemmer stemmerTerm = new Stemmer();
		stemmerTerm.add(termLowerCaseAndASCII.toCharArray(), termLowerCaseAndASCII.length());
		stemmerTerm.stem();
		String termLowerCaseStemmed = stemmerTerm.toString();
		*/
		
		return termLowerCaseStemmed;
	
	}
	
	
	/**
	 * This function creates solr query for the Solr Highlighter. It can use also complexphrase request handler. 
	 * An example of the query to search for the name: "Dumitru Nistor" in the StoryEntity using "complexphrase" request handler is:
	 * http://localhost:8983/solr/enrichment/select?hl.fl=story_text&hl=on&indent=on&defType=complexphrase&q=story_text:"dumitru~2" AND story_text:"nistor~2" AND story_id:"bookDumitruTest2"&wt=json
	 * It uses fuzzy logic (sign ~) to match both words in the term with a constraint that they have maximal Editing distance 2 (Levenschteins distance).
	 * We can also use search without complexphrase request handler and an example of that query is the following:
	 * http://localhost:8983/solr/enrichment/select?hl.fl=story_text&hl=on&indent=on&q=story_text:Dum* AND story_text:Nistor~ AND story_id:"bookDumitruTest2"&wt=json
	 * 
	 * @param fuzzySearch
	 * @param searchTerm
	 * @param params
	 * @return
	 */

	private SolrQuery createSolrQuery (boolean fuzzySearch, String searchTerm, Map<String, String> params) {
		
		String [] searchTermWords = searchTerm.split("\\s+");
		
		SolrQuery query = new SolrQuery();

		query.setRequestHandler("/select");		
		query.set("hl.fl",StoryEntitySolrFields.TRANSCRIPTION);
		query.set("hl","on");		
		query.set("indent","on");
		
		String adaptedTerm = "";
		if(fuzzySearch)
		{
			//query.set("defType","complexphrase");
			
			if(searchTermWords.length>1)
			{
				
				for (int i=0;i<searchTermWords.length;i++)
				{				
					adaptedTerm += StoryEntitySolrFields.TRANSCRIPTION+":"+ searchTermWords[i] + "~" + String.valueOf(LevenschteinDistanceThreshold);
					//adaptedTerm += StoryEntitySolrFields.TEXT+":"+ "\"" + searchTermWords[i] + "~" + String.valueOf(LevenschteinDistanceThreshold) + "\"";
					if(i<searchTermWords.length-1)
					{
						adaptedTerm += " AND ";
					}
				}			
			}
			else
			{
				adaptedTerm = StoryEntitySolrFields.TRANSCRIPTION+":"+ searchTermWords[0] + "~" + String.valueOf(LevenschteinDistanceThreshold);
				//adaptedTerm = StoryEntitySolrFields.TEXT+":"+ "\"" + searchTermWords[0] + "~" + String.valueOf(LevenschteinDistanceThreshold) + "\"";
				
			}
		}
		else
		{
			adaptedTerm = StoryEntitySolrFields.TRANSCRIPTION+":"+ "\"" + searchTerm + "\"";
		}
		
		for (String param : params.keySet()) {
			adaptedTerm += " AND "+ param +":"+params.get(param);
		}
		
		query.set("q", adaptedTerm);
		query.set("wt","json");	

		return query;
		
	}

	/**
	 * This method performs a binary search in the array of offsets to find the first one greater than the given number,
	 * taking into account that it is in the given searching range, i.e. lastOffset+rangeToObserve, otherwise the function 
	 * returns -1 if the offset is not found
	 * 
	 * @param offsets
	 * @param lastOffset
	 * @param numberWordsInTerm
	 * @param rangeToObserve
	 * @return
	 */
	
	private double findNextOffset (List<List<Double>> offsets, int lastOffset, int numberWordsInTerm, int rangeToObserve) 
    { 
		/*
		 * taking just the first number in the list because the "arr" list returns offsets 
		 * for each word in the term: e.g. for the term "dumitru nistor" it will return [[0,7],[8,14]]
		 */

		int skip = numberWordsInTerm;
		int size = offsets.size();
		// Limit to carefully avoid IndexOutOfBoundsException
		int limit = size / skip + Math.min(size % skip, 1);

		List<Double> adaptedOffsets = Stream.iterate(offsets, l -> l.subList(skip, l.size()))
		    .limit(limit)
		    .map(l -> l.get(0).get(0))
		    .collect(Collectors.toList());

		//sorting in ascending order (1,5,7,13,...)
		Collections.sort(adaptedOffsets);
			
        int start = 0, end = adaptedOffsets.size() - 1;
        
        if(adaptedOffsets.get(end) <= lastOffset) 
        {
        	return -1;
        }
  
        int ans = 0; 
        while (start <= end) { 
            int mid = (start + end) / 2; 
  
            // Move to right side if target is greater. 
            if (adaptedOffsets.get(mid) <= lastOffset) { 
                start = mid + 1; 
            } 
  
            // Move left side. 
            else { 
                ans = mid; 
                end = mid - 1; 
            } 
        } 
        
        if(adaptedOffsets.get(ans) > lastOffset + rangeToObserve) 
        {
        	return -1;
        }
        
        return adaptedOffsets.get(ans); 
    } 


	/**
	 * This function adapts the terms, positions (in terms of words), and offsets (in terms of characters) obtained
	 * from the Solr HIghlighter query using "complexphrase" query parser. This parser is used in order to find the 
	 * NamedEntities that contain several words, e.g. "Dumitru Nistor". In the original text the name can be a bit 
	 * different like "Dumitrua Nistora" where we have to use fuzzy search in order to find both parts of the name.
	 * So, since there can be very different words found for the query, this function removes the ones that definitely
	 * do not belong there. For example, if we search for the complex phrase "Dumitru Nistor":
	 * 1) two separate searches are done in the query: "dumitru~" AND "nistor~", which also finds the 2 words that are not exactly one after another
	 * and we just keep those combinations that are one after another by checking the "positions" list (the position of the word in terms of how many words are in front of it) 
	 * to be 2 consecutive numbers. The reason why we used 2 separate phrases for the search in that the new adapted Solr Highlighter cannot deal with one complex phrase search
	 * like "dumitru~ nistor~".
	 * 2) another check is done so that the words that are found correspond to the words in the term (so that we do not have two the same words one after another like "dumitru" "dumitru")
	 * but like ("dumitru" "nistor")
	 * 3) additional logic for the word matching is added: the search and found words must start with the same chars (the number of checked chars is the half of the searched string).
	 * This is done to avoid matching like this: for "Pola" the match can be "ol, oa, oli", etc. 
	 *  
	 * contain 
	 * 
	 * @param searchTerm
	 * @param terms
	 * @param positions
	 * @param offsets
	 * @param termsAdapted
	 * @param positionsAdapted
	 * @param offsetsAdapted
	 * @throws Exception 
	 */
	
	private void adaptTermsPositionsOffsets (int offsetTranslatedText, String searchTerm, List<String> terms, List<Double> positions, List<List<Double>> offsets, List<String> termsAdapted, List<Double> positionsAdapted, List<List<Double>> offsetsAdapted) throws Exception
	{
		String [] searchTermWords = searchTerm.split("\\s+");
		
		for (int i=0;i<terms.size()-searchTermWords.length+1;i++)
		{
			boolean consecutivePositions = true;
			for (int l=0;l<searchTermWords.length-1;l++)
			{
				if((positions.get(i+l+1)-positions.get(i+l))!=1.0)
				{
					consecutivePositions=false;
					break;
				}
			}
			if(consecutivePositions)
			{				
				boolean found = foundSearchPhrase(terms, searchTermWords, i, offsets.get(i).get(0).intValue(), offsetTranslatedText);
					
				if(found)
				{
					for (int j=0;j<searchTermWords.length;j++)
					{
						termsAdapted.add(terms.get(i+j));
						positionsAdapted.add(positions.get(i+j));
						offsetsAdapted.add(offsets.get(i+j));					
					}
					i += searchTermWords.length-1;
				}
			}
			
		}
	}
	
	/**
	 * This function is the key function that determines when the term is found in Solr when fuzzy search is used.
	 * Namely we try first to find all terms that are very similar to the one we are looking for. This
	 * is done using Solr query with fuzzy search, and solr filters and analyzers that manipulate the words
	 * we are searching for. From the solr response we have to select the exact found term and this is done in this function.
	 *  
	 * @param terms
	 * @param searchTermWords
	 * @param startIndex
	 * @param offsetOriginalText
	 * @param offsetTranslatedText
	 * @return
	 * @throws Exception
	 */
	private boolean foundSearchPhrase (List<String> terms, String [] searchTermWords, int startIndex,int offsetOriginalText, int offsetTranslatedText) throws Exception
	{
		List<String> termsNew = new ArrayList<String>();
		List<String> searchTermWordsNew = new ArrayList<String>();
		
		for (int i=0;i<searchTermWords.length;i++)
		{
			termsNew.add(terms.get(startIndex+i));	
			searchTermWordsNew.add(searchTermWords[i]);
		}
		
		for (int i=0;i<termsNew.size();i++)
		{
			int found=-1;
			for(int j=0;j<searchTermWordsNew.size();j++)
			{
				
				/*
				 * here we check both Levenschteins distance and that the words start with the same chars if they are
				 * short words, to avoid matching whatever short word satisfies Levenschteins distance constraint 
				 */
				int lengthToCheckStrict = lengthToCheck(searchTermWordsNew.get(j),1);
				int lengthToCheckNoStrict = lengthToCheck(searchTermWordsNew.get(j),0);
				int compareStartStrict = -1;
				int compareStartNoStrict = -1;
				int levenschteinDistanceValue = levenschteinDistance.calculateLevenshteinDistance(termsNew.get(i), searchTermWordsNew.get(j));
				if(termsNew.get(i).length()>=lengthToCheckStrict)
				{
					compareStartStrict = (lengthToCheckStrict==0) ? 0 : termsNew.get(i).substring(0, lengthToCheckStrict).compareTo(searchTermWordsNew.get(j).substring(0, lengthToCheckStrict));
				}
				if(termsNew.get(i).length()>=lengthToCheckNoStrict)
				{
					compareStartNoStrict = (lengthToCheckNoStrict==0) ? 0 : termsNew.get(i).substring(0, lengthToCheckNoStrict).compareTo(searchTermWordsNew.get(j).substring(0, lengthToCheckNoStrict));
				}
				
				if(((levenschteinDistanceValue <= LevenschteinDistanceThreshold) && compareStartStrict==0) || 
						((levenschteinDistanceValue <= 1) && compareStartNoStrict==0))
				{
					found=j;
					break;
				}
								
			}
			
			if(found!=-1)
			{
				termsNew.remove(i);
				searchTermWordsNew.remove(found);
				i-=1;
			}
			else
			{
				break;
			}
			
		}
		
		if (termsNew.size()==0) 
		{
			/*
			 * here an additional check is done, basically the sentence from the original text that contains the found term
			 * is taken and translated to the target language ("en" here) and then the translated sentence is compared with
			 * the one that is taken from the translated text and contains the term (i.e. NamedEntity) in the translated text
			 */
//			double checkSentenceLevenschteinDistance = checkSentenceSimilarityLevenschtein(offsetOriginalText, offsetTranslatedText);
//			if(checkSentenceLevenschteinDistance>=0.8) return true;
//			else return false;
			
			return true;
		}		
		else return false;
		
	}

	/**
	 * This function defines the logic for finding a match for the searched term.
	 * Basically it says that for the short words, in order to find a match the words should start with same letters.
	 * The parameter  @param strict in this case is used to differentiate the case where Levenschteins distance is 2 (strict==1)
	 * and the one where Levenschteins distance is 1, where some less strict constraint on the starting letters is applied.
	 * 
	 * @param word
	 * @param strict
	 * @return
	 */
	private int lengthToCheck(String word, int strict) {
		
		if(strict==1)
		{
			if(word.length() < 4) {
				return word.length();
			}
			else if(word.length() < 6)
			{
				return 3;
			}
			else
			{
				return 0;
			}
		}
		else
		{
			if(word.length() < 4) {
				return word.length();
			}
			else if(word.length() < 5)
			{
				return 3;
			}	
			else
			{
				return 0;
			}
		}
	}
	
	/**
	 * This function can be used for searching the term in the original text by translating the part of the sentence 
	 * that contains the found term in original language to the translated language ("en") and comparing the translated part
	 * with the one that is in the translated text (already translated before NER process) and contains the found term.
	 * This logic can be used to check if the found term in the original text corresponds to the part of the text where the
	 * term in the translated text is found.   
	 * 
	 * @param termIndexOriginal
	 * @param termIndexTranslated
	 * @return
	 * @throws Exception
	 */
	private double checkSentenceSimilarityLevenschtein (int termIndexOriginal, int termIndexTranslated) throws Exception
	{
		int beginIndexOriginal = storyOriginalText.lastIndexOf(".", termIndexOriginal)+1;
		int endIndexOriginal = storyOriginalText.indexOf(".", termIndexOriginal)+1;		
		if(beginIndexOriginal==-1 || endIndexOriginal==-1) return 0.0;
				
		int beginIndexTranslated = storyTranslatedText.lastIndexOf(".", termIndexTranslated)+1;
		int endIndexTranslated = storyTranslatedText.indexOf(".", termIndexTranslated)+1;
		if(beginIndexTranslated==-1 || endIndexTranslated==-1) return 0.0;

		String sentenceOriginalText = storyOriginalText.substring(beginIndexOriginal,endIndexOriginal);
		String sentenceTranslatedText = storyTranslatedText.substring(beginIndexTranslated,endIndexTranslated);
		
		String newSentenceTranslated = googleTranslator.callUrlAndParseResult(storyOriginalLanguage, storyTranslatedLanguage, sentenceOriginalText);
		
		//String newSentenceTranslated = clientJyandex.translateText(sentenceOriginalText, Language.ROMANIAN, Language.ENGLISH).toString();
		
		int ldValue= levenschteinDistance.calculateLevenshteinDistance(newSentenceTranslated, sentenceTranslatedText);
		  
		return 1-1.0*ldValue/(newSentenceTranslated.length()+sentenceTranslatedText.length());

	}
    /**
     * This function updates the range of characters to look for in the original text during searching the given term or phrase.
     * Outside of that range no matches are searched, to avoid matching some other terms that are similar but lie in totally 
     * different parts of the text.
     * 
     * @param rengeOfCharsTranslatedText
     * @return
     */
	private int rangeOfCharsToObserve (int rengeOfCharsTranslatedText, int wordOffsetOriginalText)
	{
		
		int newRangeOfCharsToObserve = (int) Math.ceil(rengeOfCharsTranslatedText * averageWordLeghtDifference * scaleFactorRangeOfCharsToObserve < minRangeOfCharsToObserve ? minRangeOfCharsToObserve : rengeOfCharsTranslatedText * averageWordLeghtDifference * scaleFactorRangeOfCharsToObserve);
			
		//the previous searched term is found
		if(wordOffsetOriginalText!=-1)
		{	
			rangeOfCharsToObserve=newRangeOfCharsToObserve;
		}
		else
		{			
			rangeOfCharsToObserve += newRangeOfCharsToObserve;	
		}	
		
		return rangeOfCharsToObserve;
		
	}
	
	/**
	 * This function updates the current search offset (number of chars from the beginning of the text), 
	 * staring from which we are looking for the first next found match.
	 * 
	 * @param currentOffset
	 * @param searchTerm
	 * @param wordOffsetOriginalText
	 * @param rengeOfCharsTranslatedText
	 * @return
	 */
	private int updateCurrentSearchIndex (int currentOffset, String searchTerm, int wordOffsetOriginalText, int rengeOfCharsTranslatedText)
	{
		if(wordOffsetOriginalText!=-1)
		{			
			return wordOffsetOriginalText+searchTerm.length();			
		}
		else
		{	
			// here 0.8 scaling factor is used to make sure we do not jump over the word when we move forward
			int rangeOfCharsNew = (int) Math.ceil(rengeOfCharsTranslatedText * averageWordLeghtDifference * 0.8);
			rangeOfCharsToObserve -= rangeOfCharsNew;
			return currentOffset + rangeOfCharsNew;  
			
		}
	}
	
	/**
	 * This function is to be used to optimize the fuzzy search based on the number of sentences
	 * that are passed in order to find the range of characters in which the term need to be searched in the original text. 
	 * It uses Java BreakIterator to break the text into sentences.
	 * 
	 * @param wordOffsetOriginalText
	 * @param indexTranslatedTextPrevious
	 * @param indexTranslatedTextCurrent
	 * @param originalText
	 * @param translatedText
	 * @param indexOriginalTextStartEnd
	 */
	private void updateSearchIndexFromSentences(int wordOffsetOriginalText, int indexTranslatedTextPrevious, int indexTranslatedTextCurrent, String originalText, String translatedText, int [] indexOriginalTextStartEnd) {

		Locale currentLocale1=null;
		if(storyTranslatedLanguage.compareTo("English")==0)
		{
			currentLocale1 = new Locale ("en","US");
		}
		Locale currentLocale2=null;
		if(storyOriginalLanguage.compareTo("Romanian")==0)
		{
			currentLocale2 = new Locale ("ro","RO");
		}
		
		
		/*
		 * this commed part is used to test if the translated and original text have the same number of sentences
		 * for the search logic that deals with the sentences 
		 */
/*	    	    
        BufferedWriter output_tr = null;
        BufferedWriter output_or = null;
        try {
        	
            File file_tr = new File("sentences-translated.txt");
            output_tr = new BufferedWriter(new FileWriter(file_tr));

            File file_or = new File("sentences-original.txt");
            output_or = new BufferedWriter(new FileWriter(file_or));

            
    		BreakIterator sentenceIteratorTranslatedAll = BreakIterator.getSentenceInstance(currentLocale1);
    		String translSentencesStringAll = translatedText.substring(0, translatedText.length());
    		sentenceIteratorTranslatedAll.setText(translSentencesStringAll);
    		
    		//count the number of sentences from the last index
    		int numberSentencesTranslatedAll=0;
    			
    		int start = sentenceIteratorTranslatedAll.first();
    	    for (int end = sentenceIteratorTranslatedAll.next();end != BreakIterator.DONE;start = end, end = sentenceIteratorTranslatedAll.next()) {
    	    	numberSentencesTranslatedAll++;
    	    	output_tr.write(translSentencesStringAll.substring(start, end)+"\n\n");
    	    }

    		
    		BreakIterator sentenceIteratorOrigindAll = BreakIterator.getSentenceInstance(currentLocale2);
    		String originSentencesStringAll = originalText.substring(0, originalText.length());
    		sentenceIteratorOrigindAll.setText(originSentencesStringAll);
    		
    		//count the number of sentences from the last index
    		int numberSentencesOriginAll=0;
    			
    		start = sentenceIteratorOrigindAll.first();
    	    for (int end = sentenceIteratorOrigindAll.next();end != BreakIterator.DONE;start = end, end = sentenceIteratorOrigindAll.next()) {
    	    	numberSentencesOriginAll++;
    	    	output_or.write(originSentencesStringAll.substring(start, end)+"\n\n");
    	    }

    	    
            int hh=0;
            int gg=hh;
            
            
            
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
          if ( output_or != null ) {
        	  try {
				output_or.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          }
          if ( output_tr != null ) {
        	  try {
				output_tr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
          }
        }
	    
*/
		
		BreakIterator sentenceIteratorTranslated = BreakIterator.getSentenceInstance(currentLocale1);
		String translSentencesString = translatedText.substring(indexTranslatedTextPrevious, indexTranslatedTextCurrent+1);
		sentenceIteratorTranslated.setText(translSentencesString);
		
		//count the number of sentences from the last index
		int numberSentencesTranslated=0;
			
		int start = sentenceIteratorTranslated.first();
	    for (int end = sentenceIteratorTranslated.next();end != BreakIterator.DONE;start = end, end = sentenceIteratorTranslated.next()) {
	    	numberSentencesTranslated++;
	    }

	   
	    sentencesByNowTranslated+=numberSentencesTranslated-1;
		
	    
		BreakIterator sentenceIteratorOriginal = BreakIterator.getSentenceInstance(currentLocale2);
		
		/*
		 * update sentencesByNowOriginal and indexByNowOriginal values
		 */
		if(wordOffsetOriginalText!=-1)//if the term has been found in the original text
		{
			int numberSentencesToAdd=0;
			sentenceIteratorOriginal.setText(originalText.substring(indexByNowOriginal, wordOffsetOriginalText+1));
			
			start = sentenceIteratorOriginal.first();
		    for (int end = sentenceIteratorOriginal.next();end != BreakIterator.DONE;start = end, end = sentenceIteratorOriginal.next()) {
		    	numberSentencesToAdd++;
		    }			
			
			sentencesByNowOriginal+=numberSentencesToAdd-1;
			indexByNowOriginal=wordOffsetOriginalText;
		}
		
		int endIndexOrigin = originalText.length()<=indexByNowOriginal+charsToCheck ? originalText.length() : indexByNowOriginal+charsToCheck;
		String originalSentencesString = originalText.substring(indexByNowOriginal, endIndexOrigin);
		sentenceIteratorOriginal.setText(originalSentencesString);
		
		//count the number of sentences from the last index
		int firstSentenceOriginalToStart=sentencesByNowTranslated-numSentToCheckArround<=0 ? 1 : sentencesByNowTranslated-numSentToCheckArround;
		int lastSentenceOriginalToStart=sentencesByNowTranslated+numSentToCheckArround;
		
		
		
		int numSentCurrentOriginal=sentencesByNowOriginal;
		
		indexOriginalTextStartEnd[0] = indexByNowOriginal;
		indexOriginalTextStartEnd[1] = indexByNowOriginal;
		
		start = sentenceIteratorOriginal.first();
		for (int end = sentenceIteratorOriginal.next();end != BreakIterator.DONE;start = end, end = sentenceIteratorOriginal.next())
		{
			numSentCurrentOriginal+=1;
			int addedFirst=0;
			int addedLast=0;
			
	    	if(numSentCurrentOriginal < firstSentenceOriginalToStart) {
	    		indexOriginalTextStartEnd[0] += originalSentencesString.substring(start,end).length();
	    		addedFirst=1;
	    	}
	    	
	    	if(numSentCurrentOriginal <= lastSentenceOriginalToStart) {
	    		indexOriginalTextStartEnd[1] += originalSentencesString.substring(start,end).length();
	    		addedLast=1;
	    	}

	    	if(addedFirst==0 && addedLast==0)
	    	{
	    		break;
	    	}
		}    

	}
	@Override
	public void findEntitiyOffsetsInOriginalText(boolean fuzzyLogic, StoryEntity dbStoryEntity, String targetLanguage, String translatedText, TreeMap<String, List<List<String>>> identifiedNER) throws Exception
	{		
		/*
		 * if the original language is the same as the target language, add positions in the original text
		 * to be the same as in the translated text
		 */
		if(ModelUtils.compareSingleTranslationLanguage(dbStoryEntity, targetLanguage))
		{			
			for (String classificationType : identifiedNER.keySet()) {
				for (List<String> entityList : identifiedNER.get(classificationType)) {
					
					entityList.add(entityList.get(1));
					
				}
			}
			return;
		}		
		/*
		 * store the entity in Solr (indexing of the story)
		 */
		store(solrCore, dbStoryEntity, true);
		
		storyOriginalText=dbStoryEntity.getTranscriptionText();
		storyTranslatedText=translatedText;
		storyTranslatedLanguage=targetLanguage;
		storyIdSolr=dbStoryEntity.getStoryId();
		fuzzyLogicSolr=fuzzyLogic;		
		averageWordLeghtDifference = storyOriginalText.length()*1.0 / (storyTranslatedText.length()*1.0);
		
		/*
		 * get all entities in one list in order to sort them
		 */
		List<List<String>> sortedListAllEntities = new ArrayList<List<String>>();		
		for (String classificationType : identifiedNER.keySet()) {
			for (List<String> entityList : identifiedNER.get(classificationType)) {
				/*
				 * adding a new element to the list based on the original element, e.g. {"Bistrita", "234"}->{"location Bistrita","234"}
				 * to be easier translated because it contains a bit more context than just a name of the entity
				 */
				List<String> newList = new ArrayList<String>();
				newList.add(classificationType+" "+entityList.get(0));
				newList.add(entityList.get(1));
				sortedListAllEntities.add(newList);
			}
		}
		
		/*
		 * sort the list based on the second element in the inner list, which is the position of the entity in the translated text 
		 */
		Collections.sort(sortedListAllEntities, new Comparator<List<String>>() {
	        @Override
	        public int compare(List<String> o1, List<String> o2) {
	            try {
	                return Integer.valueOf(o1.get(1)).compareTo(Integer.valueOf(o2.get(1)));
	            } catch (NullPointerException e) {
	                return 0;
	            }
	        }
	    });
		
		StringBuilder entitiesText = new StringBuilder(); 
		for(List<String> entityList: sortedListAllEntities) {
			entitiesText.append(entityList.get(0)+",");
		}
		entitiesText.deleteCharAt(entitiesText.length()-1);//delete the last comma
		
		//String serviceResult = eTranslationService.translateText(entitiesText.toString(),targetLanguage, originalLanguage);
			
		/*
		 * finding the translated entities in the original text
		 */
		
		//finding offset in the original text using Solr Highlighter	
		int offsetsOriginalText = -1;		
		int rangeToObserve = 0;
		
		int [] indexOriginalTextStartEnd = new int [2];
		int wordOffsetOriginalText=-1;
		
		for(int i=0;i<entitiesOriginalText.size();i++)
		{	

			/*
			 * use the 4 lines below when searching with the predefined logic for the range of characters to search the term in
			 */
			int charRangeTranslatedText = (i==0) ? Integer.valueOf(sortedListAllEntities.get(i).get(1)) : Math.abs((Integer.valueOf(sortedListAllEntities.get(i).get(1))-Integer.valueOf(sortedListAllEntities.get(i-1).get(1))));
			rangeToObserve = rangeOfCharsToObserve(charRangeTranslatedText, wordOffsetOriginalText);
			wordOffsetOriginalText = findTermPositionsInStory(entitiesOriginalText.get(i), offsetsOriginalText, Integer.valueOf(sortedListAllEntities.get(i).get(1)), rangeToObserve);
			offsetsOriginalText = updateCurrentSearchIndex(offsetsOriginalText, entitiesOriginalText.get(i), wordOffsetOriginalText, charRangeTranslatedText);

			/*
			 * use the 3 lines below when searching with the sentences logic for the range of characters to search the term in
			 */
//			int indexTranslatedTextPrevious = (i==0) ? 0 : Integer.valueOf(sortedListAllEntities.get(i-1).get(1));
//			updateSearchIndexFromSentences(wordOffsetOriginalText, indexTranslatedTextPrevious, Integer.valueOf(sortedListAllEntities.get(i).get(1)), storyOriginalText, storyTranslatedText, indexOriginalTextStartEnd);
//			wordOffsetOriginalText = findTermPositionsInStory(entitiesOriginalText.get(i), indexOriginalTextStartEnd[0], Integer.valueOf(sortedListAllEntities.get(i).get(1)), indexOriginalTextStartEnd[1]-indexOriginalTextStartEnd[0]);
			
			
			
			//update the main NER map with the position of the entity in the original text
			String [] entityType = sortedListAllEntities.get(i).get(0).split("\\s+",2);
			List<List<String>> entities = identifiedNER.get(entityType[0]);
			List<String> findThisEntity = new ArrayList<String>();
			findThisEntity.add(entityType[1]);
			findThisEntity.add(sortedListAllEntities.get(i).get(1));
			List<String> foundEntity = entities.get(entities.indexOf(findThisEntity));
			foundEntity.add(String.valueOf(wordOffsetOriginalText));
			
		}
		
		
	}
}
