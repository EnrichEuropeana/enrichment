package eu.europeana.enrichment.ner.exception;

public class NERAnnotateException extends RuntimeException {

	/**
	 * Same UID like eu.europeana.entity.solr.exception 
	 */
	private static final long serialVersionUID = -167560566275881316L;
	
	
	public NERAnnotateException(String message, Throwable th) {
		super(message, th);
	}

	public NERAnnotateException(String message) {
		super(message);
	} 
	
}
