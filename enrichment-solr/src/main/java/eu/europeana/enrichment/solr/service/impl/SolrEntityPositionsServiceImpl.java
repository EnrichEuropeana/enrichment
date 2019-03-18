package eu.europeana.enrichment.solr.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import eu.europeana.enrichment.model.ItemEntity;
import eu.europeana.enrichment.model.StoryEntity;
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
	
	@Resource(name = "eTranslationService")
	TranslationService eTranslationService;

	
	private final int LevenschteinDistanceThreshold = 2;
	private final int minRangeOfCharsToObserve = 2000;
	private final double scaleFactorRangeOfCharsToObserve = 1.5;
	private final Logger log = LogManager.getLogger(getClass());
	private List<String> entitiesOriginalText = new ArrayList<String> ();
	
	public SolrEntityPositionsServiceImpl(String translatedEntities) {
		
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
	public int findTermPositionsInStory(String storyId, String term, int startAfterOffset, int rangeToObserve) throws SolrNamedEntityServiceException {
	
		SolrQuery query = new SolrQuery();
		/*
		 * convert to lower case and from UTF-8 to ASCII since in Solr we use the filter for lower case solr.LowerCaseFilterFactory" 
		 */
		
		String termLowerCase=term.toLowerCase();
		//String termLowerCase=term;
		
		termLowerCase = termLowerCase.replace("ă", "a");
		termLowerCase = termLowerCase.replace("â", "a");
		termLowerCase = termLowerCase.replace("î", "i");
		termLowerCase = termLowerCase.replace("ş", "s");
		termLowerCase = termLowerCase.replace("ţ", "t");

		String termLowerCaseAndASCII="";
		try {
			termLowerCaseAndASCII = new String(termLowerCase.getBytes("ISO-8859-2"), "ASCII");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		/*
		Stemmer stemmerTerm = new Stemmer();
		stemmerTerm.add(termLowerCaseAndASCII.toCharArray(), termLowerCaseAndASCII.length());
		stemmerTerm.stem();
		String termLowerCaseStemmed = stemmerTerm.toString();
		*/
		
		/*
		 * this part creates query parameters for the Solr Highlighter using complexphrase query highlighter. An example of the query 
		 * to search for the name: "Dumitru Nistor" in the StoryEntity:
		 * http://localhost:8983/solr/enrichment/select?hl.fl=story_text&hl=on&indent=on&defType=complexphrase&q=story_text:"dumitru~" AND story_text:"nistor~" AND story_id:"bookDumitruTest2"&wt=json
		 */
		query.setRequestHandler("/select");		
		query.set("hl.fl",StoryEntitySolrFields.TEXT);
		query.set("hl","on");		
		query.set("indent","on");
		query.set("defType","complexphrase");
		
		String [] searchTermWords = termLowerCaseAndASCII.split("\\s+");
		String adaptedTerm = "";
		if(searchTermWords.length>1)
		{
			
			for (String termWord : searchTermWords)
			{
				adaptedTerm += StoryEntitySolrFields.TEXT+":"+ "\"" + termWord + "~" + String.valueOf(LevenschteinDistanceThreshold) + "\"";
				adaptedTerm += " AND ";
			}
			adaptedTerm += StoryEntitySolrFields.STORY_ID+":"+storyId;
		}
		else
		{
			adaptedTerm = StoryEntitySolrFields.TEXT+":"+ "\"" + termLowerCaseAndASCII + "~" + String.valueOf(LevenschteinDistanceThreshold) + "\""+" AND "+StoryEntitySolrFields.STORY_ID+":"+storyId;
			
		}
		query.set("q", adaptedTerm);
		query.set("wt","json");	
		
		QueryResponse response=null;
		
		try {
			response = solrServer.query(query);
		} catch (IOException | SolrServerException e) {
			throw new SolrNamedEntityServiceException("Unexpected exception occured when executing Solr query.", e);
		}
		
		
		List<String> terms = new ArrayList<String>();
		List<Double> positions = new ArrayList<Double>();
		List<List<Double>> offsets = new ArrayList<List<Double>>();
		
		log.info("Solr response: " + response.toString());
		
		/*
		 * parsing the obtained json response to extract the offsets, terms and positions
		 */
		try {
			javaJSONParser.getPositionsFromJSON(response, terms, positions, offsets);
		} catch (ParseException e) {
			throw new SolrNamedEntityServiceException("Exception occured when parsing JSON response from Solr. Searched for the term: " + termLowerCaseAndASCII,e);
		}
	
		if(!terms.isEmpty())
		{
			List<String> termsAdapted = new ArrayList<String>();
			List<Double> positionsAdapted = new ArrayList<Double>();
			List<List<Double>> offsetsAdapted = new ArrayList<List<Double>>();
			
			adaptTermsPositionsOffsets(termLowerCaseAndASCII,terms,positions,offsets,termsAdapted,positionsAdapted,offsetsAdapted);
	
			if(termsAdapted.isEmpty()) return -1;
			//finding the exact offset of the term from the list of all offsets
			double exactOffset = findNextOffset(offsetsAdapted, startAfterOffset,searchTermWords.length, rangeToObserve);
			return (int) exactOffset;
		}
		else
		{
			return -1;
		}
	
		
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
	 */
	
	private void adaptTermsPositionsOffsets (String searchTerm, List<String> terms, List<Double> positions, List<List<Double>> offsets, List<String> termsAdapted, List<Double> positionsAdapted, List<List<Double>> offsetsAdapted)
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
				boolean found = true;
				for (int j=0;j<searchTermWords.length;j++)
				{
					/*
					 * here we check that the searching word and the one found start with the same characters in order to avoid 
					 * some conditions where Levenschteins distance alone is not good, e.g. for the word Pola, words like: la, ol, oa, etc. 
					 * will all be matched which is not desirable
					 */
					int lengthToCheck = (int) Math.ceil(searchTermWords[j].length()/2.0);
					//int lengthToCheck = searchTermWords[j].length()/2;
					if(terms.get(i+j).length()>=lengthToCheck)
					{
						int compareStart = terms.get(i+j).substring(0, lengthToCheck).compareTo(searchTermWords[j].substring(0, lengthToCheck));
						if((levenschteinDistance.calculateLevenshteinDistance(terms.get(i+j), searchTermWords[j]) > LevenschteinDistanceThreshold) || compareStart!=0)
						{
							found=false;
							break;
						}
					}
					else
					{
						found=false;
						break;
					}
				}
					
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


	@Override
	public void findEntitiyOffsetsInOriginalText(String originalLanguage, String targetLanguage, String storyId, TreeMap<String, List<List<String>>> identifiedNER) throws SolrNamedEntityServiceException
	{		
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
		
		//String serviceResult = eTranslationService.translateText(entitiesText.toString(),originalLanguage,targetLanguage);
			
		/*
		 * finding the translated entities in the original text
		 */
		
		//finding offset in the original text using Solr Highlighter	
		int offsetsOriginalText = -1;
		int rangeToObserve = 0;
		
		for(int i=0;i<entitiesOriginalText.size();i++)
		{	
			int charRangeTranslatedText = (i==0) ? Integer.valueOf(sortedListAllEntities.get(i).get(1)) : Math.abs((Integer.valueOf(sortedListAllEntities.get(i).get(1))-Integer.valueOf(sortedListAllEntities.get(i-1).get(1))));
			rangeToObserve = (int) Math.ceil((charRangeTranslatedText * scaleFactorRangeOfCharsToObserve < minRangeOfCharsToObserve) ? minRangeOfCharsToObserve : charRangeTranslatedText * scaleFactorRangeOfCharsToObserve);
						  
			int wordOffsetOriginalText = findTermPositionsInStory(storyId, entitiesOriginalText.get(i), offsetsOriginalText, rangeToObserve);
			if(wordOffsetOriginalText!=-1)
			{
				offsetsOriginalText = wordOffsetOriginalText;
			}
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
