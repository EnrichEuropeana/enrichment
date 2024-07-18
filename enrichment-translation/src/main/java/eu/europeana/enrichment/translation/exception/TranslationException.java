package eu.europeana.enrichment.translation.exception;

public class TranslationException extends RuntimeException{

	/**
	 * Same UID like eu.europeana.entity.solr.exception 
	 */
	private static final long serialVersionUID = -167560566275881316L;
	
	
	public TranslationException(String message, Throwable th) {
		super(message, th);
	}

	public TranslationException(String message) {
		super(message);
	}
	
}
