package eu.europeana.enrichment.solr.commons;

import java.util.Arrays;

public class LevenschteinDistance {

	private int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }
	
	private int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }
	
	/*
	 * Normalized similaity between 2 strings based on Levenshtein's distance.
	 * Value (1-levenshteinDistance/(x.length()+y.length())) range: 0-1, meaning: 0 - totally different, 1 - the same 
	 */
	public int calculateLevenshteinDistance (String x, String y) {
				
	    int[][] dp = new int[x.length() + 1][y.length() + 1];
	    
	    for (int i = 0; i <= x.length(); i++) {
	        for (int j = 0; j <= y.length(); j++) {
	            if (i == 0) {
	                dp[i][j] = j;
	            }
	            else if (j == 0) {
	                dp[i][j] = i;
	            }
	            else {
	            	dp[i][j] = min(dp[i - 1][j - 1] + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)), 
	                  dp[i - 1][j] + 1, 
	                  dp[i][j - 1] + 1);
	            }
	        }
	    }
	 
	    return dp[x.length()][y.length()];
	    
	    /*
	     * add an additional constraint for the matching words and it is that the word in the original text cannot be shorter than the one we are looking for (e.g. Tigan -> Tiganilor)
	     */
	    //int minStringLength = min(x.length(),y.length());
	    //int stringsContainEachOther = x.substring(0, minStringLength).compareToIgnoreCase(y.substring(0, minStringLength));
	    //int stringsHalfStartSame = x.substring(0, 2).compareToIgnoreCase(y.substring(0, 2));
	  	//return ((1-levenshteinDistance/(x.length()+y.length())>=0.7 || stringsContainEachOther==0) && x.length()>=y.length() && stringsHalfStartSame==0);
	    
	    //return (1-levenshteinDistance/(x.length()+y.length())) >= threshold ? true : false;
	    
	    		
	}
}
