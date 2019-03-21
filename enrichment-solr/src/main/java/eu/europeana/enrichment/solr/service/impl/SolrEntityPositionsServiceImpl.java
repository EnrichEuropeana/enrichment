package eu.europeana.enrichment.solr.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocumentList;
import org.json.simple.parser.ParseException;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;
import org.tartarus.snowball.ext.romanianStemmer;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import co.aurasphere.jyandex.Jyandex;
import co.aurasphere.jyandex.dto.Language;
import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
import eu.europeana.enrichment.solr.commons.GoogleTranslator;
import eu.europeana.enrichment.solr.commons.JavaJSONParser;
import eu.europeana.enrichment.solr.commons.LevenschteinDistance;
import eu.europeana.enrichment.solr.commons.Stemmer;
import eu.europeana.enrichment.solr.exception.SolrNamedEntityServiceException;
import eu.europeana.enrichment.solr.model.SolrItemEntityImpl;
import eu.europeana.enrichment.solr.model.SolrStoryEntityImpl;
import eu.europeana.enrichment.solr.model.vocabulary.StoryEntitySolrFields;
import eu.europeana.enrichment.solr.service.SolrEntityPositionsService;
import eu.europeana.enrichment.translation.service.TranslationService;


public class SolrEntityPositionsServiceImpl implements SolrEntityPositionsService{

	@Resource
	SolrClient solrServer;
	
	@Resource(name = "javaJSONParser")
	JavaJSONParser javaJSONParser;

	@Resource(name = "levenschteinDistance")
	LevenschteinDistance levenschteinDistance;
	
	@Resource(name = "googleTranslator")
	GoogleTranslator googleTranslator;

	
	@Resource(name = "eTranslationService")
	TranslationService eTranslationService;

	
	private final int LevenschteinDistanceThreshold = 2;
	private int minRangeOfCharsToObserve = 2000;
	private int rangeOfCharsToObserve = 2000;
	private final double scaleFactorRangeOfCharsToObserve = 1.5;
	private final Logger log = LogManager.getLogger(getClass());
	private List<String> entitiesOriginalText = new ArrayList<String>();
	private String storyOriginalText="";
	private String storyTranslatedText="";
	private String storyOriginalLanguage="";
	private String storyTranslatedLanguage="";
	private String storyIdSolr="";
	private boolean fuzzyLogicSolr = false;
	//private Jyandex clientJyandex;

	
	public SolrEntityPositionsServiceImpl(String translatedEntities) {
		
		//clientJyandex = new Jyandex("trnsl.1.1.20190321T145012Z.5582e98b0b19430e.69e76d055bdf6b87efbda7891df751a1df9ba33f");
		
		if(!translatedEntities.isEmpty())
		{
			String data = ""; 
			try {
				data = new String(Files.readAllBytes(Paths.get(translatedEntities)));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String[] entities = data.split(",");
			for(int i=0;i<entities.length;i++)
			{
				String[] entityType = entities[i].split("\\s+",2);
				entitiesOriginalText.add(entityType[1]); 
			}
			
		}

	}
	


	public void setSolrServer(SolrClient solrServer) {
		this.solrServer = solrServer;
	}

	@Override
	public List<Integer> searchByEntityName(String entityName) throws SolrNamedEntityServiceException {


		return null;
	}
	

	@Override
	public boolean store(StoryEntity storyEntity) throws SolrNamedEntityServiceException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void store(List<? extends StoryEntity> storyEntities) throws SolrNamedEntityServiceException {
		
		for(StoryEntity ent : storyEntities) {
			store(ent, false);
		}
		
		try {
			solrServer.commit();
		} catch (SolrServerException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected Solr server exception occured when storing a list of StoryEntity.", ex);			
		} catch (IOException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected IO exception occured when storing a list of StoryEntity", ex);
		}
		
	}

	@Override
	public void store(StoryEntity storyEntity, boolean doCommit) throws SolrNamedEntityServiceException {
		try {
			
			log.debug("store: " + storyEntity.toString());
			
			SolrStoryEntityImpl solrStoryEntity = null;
			if(storyEntity instanceof SolrStoryEntityImpl) {
				solrStoryEntity=(SolrStoryEntityImpl) storyEntity;
			}
			else {
				solrStoryEntity=new SolrStoryEntityImpl(storyEntity);
			}
			
			UpdateResponse rsp = solrServer.addBean(solrStoryEntity);
			log.info("store response: " + rsp.toString());
			if(doCommit)
				solrServer.commit();
		} catch (SolrServerException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected Solr server exception occured when storing StoryEntity with storyId: " + storyEntity.getStoryId(),
					ex);
		} catch (IOException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected IO exception occured when storing StoryEntity with storyId: " + storyEntity.getStoryId(), ex);
		}
		
	}

	
	@Override
	public void search (String term) throws SolrNamedEntityServiceException {

		log.info("search StoryEntity by term: " + term);

		/**
		 * Construct a SolrQuery
		 */
		SolrQuery query = new SolrQuery(term);
		log.info("query: " + query.toString());

		/**
		 * Query the server
		 */
		try {
			QueryResponse rsp = solrServer.query(query);
			log.info("query response: " + rsp.toString());
			
		} catch (IOException | SolrServerException e) {
			throw new SolrNamedEntityServiceException("Unexpected exception occured when searching StoryEntity in Solr for the term: " + term,
					e);
		}

	}

	@Override
	public void update(StoryEntity stryEntity) throws SolrNamedEntityServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteByQuery(String query) throws SolrNamedEntityServiceException {
		try {
			log.info("Solr deleteByQuery call: " + query);
			UpdateResponse rsp = solrServer.deleteByQuery(query);
			log.info("Solr deleteByQuery response: " + rsp.toString());
			solrServer.commit();
		} catch (IOException | SolrServerException ex) {
			throw new SolrNamedEntityServiceException(
					"Unexpected solr server or IO exception occured when deleting StoryEntity with query: " + query, ex);
		}		
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
		
		try {
			response = solrServer.query(query);
		} catch (IOException | SolrServerException e) {
			throw new SolrNamedEntityServiceException("Unexpected exception occured when executing Solr query.", e);
		}
		
		
		List<String> terms = new ArrayList<String>();
		List<Double> positions = new ArrayList<Double>();
		List<List<Double>> offsets = new ArrayList<List<Double>>();
		
		/*
		 * parsing the obtained json response to extract the offsets, terms and positions
		 */
		try {
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
		String termLowerCase=searchTerm.toLowerCase();
		
		Pattern ptn = Pattern.compile("[\\p{Punct}\\p{Digit}&&[^-]]");
        Matcher mtch = ptn.matcher(termLowerCase);
        String termRemovedSpecialChars = mtch.replaceAll("");
		
		/*
		 * to ASCII code filter
		 */

		String termLowerCaseStemmed = "";
		if(language.compareToIgnoreCase("ro")==0)
		{
//			termLowerCase = termLowerCase.replace("ă", "a");
//			termLowerCase = termLowerCase.replace("â", "a");
//			termLowerCase = termLowerCase.replace("î", "i");
//			termLowerCase = termLowerCase.replace("ş", "s");
//			termLowerCase = termLowerCase.replace("ţ", "t");
					
//			termLowerCase = Normalizer.normalize(termLowerCase, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	        					
//			try {
//				/*
//				 * please see here for the char standards for different languages: https://www.terena.org/activities/multiling/ml-docs/iso-8859.html
//				 */
//				termLowerCaseAndASCII = new String(termLowerCase.getBytes("ISO-8859-2"), "ASCII");
//			} catch (UnsupportedEncodingException e1) {				
//				e1.printStackTrace();
//			}
			
			SnowballStemmer snowballStemmer = new romanianStemmer();
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


		}
		
		return termLowerCaseStemmed;
	
	}
	
	
	/**
	 * This function creates solr query for the Solr Highlighter. It can use also complexphrase request handler. 
	 * An example of the query to search for the name: "Dumitru Nistor" in the StoryEntity using "complexphrase" request handler is:
	 * http://localhost:8983/solr/enrichment/select?hl.fl=story_text&hl=on&indent=on&defType=complexphrase&q=story_text:"dumitru~2" AND story_text:"nistor~2" AND story_id:"bookDumitruTest2"&wt=json
	 * It uses fuzzy logic (sign ~) to match both words in the term with a constraint that they have maximal Editing distance 2 (Levenschteins distance).
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
		query.set("hl.fl",StoryEntitySolrFields.TEXT);
		query.set("hl","on");		
		query.set("indent","on");
		
		String adaptedTerm = "";
		if(fuzzySearch)
		{
			query.set("defType","complexphrase");
			
			if(searchTermWords.length>1)
			{
				
				for (int i=0;i<searchTermWords.length;i++)
				{
					adaptedTerm += StoryEntitySolrFields.TEXT+":"+ "\"" + searchTermWords[i] + "~" + String.valueOf(LevenschteinDistanceThreshold) + "\"";
					if(i<searchTermWords.length-1)
					{
						adaptedTerm += " AND ";
					}
				}			
			}
			else
			{
				adaptedTerm = StoryEntitySolrFields.TEXT+":"+ "\"" + searchTermWords[0] + "~" + String.valueOf(LevenschteinDistanceThreshold) + "\"";
				
			}
		}
		else
		{
			adaptedTerm = StoryEntitySolrFields.TEXT+":"+ "\"" + searchTerm + "\"";
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

	private int lengthToCheck(String word, int strict) {
		
		if(strict==1)
		{
			if(word.length() < 5) {
				return word.length();
			}
			else
			{
				return 4;
			}
		}
		else
		{
			if(word.length() < 4) {
				return word.length();
			}
			else 
			{
				return 3;
			}			
		}
	}
	
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
	private int rangeOfCharsToObserve (int rengeOfCharsTranslatedText)
	{
		return (int) Math.ceil((rengeOfCharsTranslatedText * scaleFactorRangeOfCharsToObserve < rangeOfCharsToObserve) ? rangeOfCharsToObserve : rengeOfCharsTranslatedText * scaleFactorRangeOfCharsToObserve);
	}
	
	/**
	 * This function updates the current search index, staring from which we are looking for the first next found match.
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
			rangeOfCharsToObserve = minRangeOfCharsToObserve;
			return wordOffsetOriginalText+searchTerm.length();			
		}
		else
		{			
			rangeOfCharsToObserve += rengeOfCharsTranslatedText * (scaleFactorRangeOfCharsToObserve-1.0);
			return currentOffset + rengeOfCharsTranslatedText;
			
		}
	}
	
	@Override
	public void findEntitiyOffsetsInOriginalText(boolean fuzzyLogic, String originalLanguage, String targetLanguage, String originalText, String translatedText, String storyId, TreeMap<String, List<List<String>>> identifiedNER) throws Exception
	{		
		storyOriginalText=originalText;
		storyTranslatedText=translatedText;
		storyOriginalLanguage=originalLanguage;
		storyTranslatedLanguage=targetLanguage;
		storyIdSolr=storyId;
		fuzzyLogicSolr=fuzzyLogic;
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
		
		for(int i=0;i<entitiesOriginalText.size();i++)
		{	
			
			int charRangeTranslatedText = (i==0) ? Integer.valueOf(sortedListAllEntities.get(i).get(1)) : Math.abs((Integer.valueOf(sortedListAllEntities.get(i).get(1))-Integer.valueOf(sortedListAllEntities.get(i-1).get(1))));
			
			rangeToObserve = rangeOfCharsToObserve(charRangeTranslatedText);
						  
			int wordOffsetOriginalText = findTermPositionsInStory(entitiesOriginalText.get(i), offsetsOriginalText, Integer.valueOf(sortedListAllEntities.get(i).get(1)), rangeToObserve);
			
			offsetsOriginalText = updateCurrentSearchIndex(offsetsOriginalText, entitiesOriginalText.get(i), wordOffsetOriginalText, charRangeTranslatedText);
			
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
