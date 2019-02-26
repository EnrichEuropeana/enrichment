package eu.europeana.enrichment.solr.exception;

public class AnnotationServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -167560566275881316L;

	public AnnotationServiceException(String message, Throwable th) {
		super(message, th);
	}

	public AnnotationServiceException(String message) {
		super(message);
	}
	
	
}
