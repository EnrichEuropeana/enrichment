package eu.europeana.enrichment.web.exception;

import org.springframework.http.HttpStatus;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.web.common.config.I18nConstants;


public class InternalServerException extends HttpException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public InternalServerException(String message, Throwable th){
		super(message, null, null, HttpStatus.INTERNAL_SERVER_ERROR, th);
	}

	public InternalServerException(Throwable th){
		super(th.getMessage(), I18nConstants.SERVER_ERROR_UNEXPECTED, null, HttpStatus.INTERNAL_SERVER_ERROR, th);
	}

}
