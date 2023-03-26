package eu.europeana.enrichment.ner.exception;

public class InvalidWikidataIdJsonException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6285535680004227604L;

	public InvalidWikidataIdJsonException(String message, Throwable th) {
		super(message, th);
	}

	public InvalidWikidataIdJsonException(String message) {
		super(message);
	} 
	
}
