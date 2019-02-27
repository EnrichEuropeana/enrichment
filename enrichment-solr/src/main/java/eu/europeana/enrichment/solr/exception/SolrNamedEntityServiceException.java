package eu.europeana.enrichment.solr.exception;

public class SolrNamedEntityServiceException extends Exception {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = -167560566275881316L;

	public SolrNamedEntityServiceException(String message, Throwable th) {
		super(message, th);
	}

	public SolrNamedEntityServiceException(String message) {
		super(message);
	}
	
	
}
