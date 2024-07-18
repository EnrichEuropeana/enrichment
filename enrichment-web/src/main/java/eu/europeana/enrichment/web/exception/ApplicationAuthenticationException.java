package eu.europeana.enrichment.web.exception;

import org.springframework.http.HttpStatus;

import eu.europeana.api.commons.web.exception.HttpException;

public class ApplicationAuthenticationException extends HttpException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6790143130321685425L;
	
	public ApplicationAuthenticationException(String message) {
		this(message, null, null, null);
	}
	
	public ApplicationAuthenticationException(String message, String i18nKey) {
		this(message, i18nKey, null, null);
	}
	
	public ApplicationAuthenticationException(String message, String i18nKey, String[] i18nParams){
		this(message, i18nKey, i18nParams, null);
	}
	
	public ApplicationAuthenticationException(String message, String i18nKey, Throwable th){
		this(message, i18nKey, null, th);
	}
	
	public ApplicationAuthenticationException(String message, String i18nKey, String[] i18nParams, Throwable th) {
		super(message, i18nKey, i18nParams, HttpStatus.UNAUTHORIZED, th);
		// TODO Auto-generated constructor stub
	}
	
}
