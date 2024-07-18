package eu.europeana.enrichment.solr.commons;

import java.security.SecureRandom;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;

public class SolrUtils {
	
	public static final int FACET_LIMIT = 50;
	private static final String SORT_RANDOM = "random";
    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd = new SecureRandom();
    private static final int SEED_LENGTH = 10;

    public static void buildSortQuery(SolrQuery solrQuery, String[] inputFields) {
		String[] inputArray;
		String fieldName;
		SolrQuery.ORDER order;
	
		for (String field : inputFields) {
		    inputArray = StringUtils.splitByWholeSeparator(field, " ");
		    fieldName = inputArray[0];
		    fieldName = processSortField(fieldName);
	
		    order = (inputArray.length == 2) ? SolrQuery.ORDER.valueOf(inputArray[1]) : SolrQuery.ORDER.asc;
		    solrQuery.addSort(fieldName, order);
		}
    }

    private static String processSortField(String fieldName) {
	// handle random search
	if (SORT_RANDOM.equals(fieldName)) {
	    return SORT_RANDOM + "_" + generateRandomSeed();
	}
	return fieldName;

    }

    private static String generateRandomSeed() {
    	StringBuilder sb = new StringBuilder(SEED_LENGTH);
    	for (int i = 0; i < SEED_LENGTH; i++) {
    	    sb.append(AB.charAt(rnd.nextInt(AB.length())));
    	}
    	return sb.toString();	
    }

}
