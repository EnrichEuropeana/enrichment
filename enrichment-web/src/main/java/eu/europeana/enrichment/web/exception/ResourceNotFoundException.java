package eu.europeana.enrichment.web.exception;

import org.springframework.http.HttpStatus;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.web.common.config.I18nConstants;


public class ResourceNotFoundException extends HttpException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3664526076494279093L;
	public static final String MESSAGE_BLANK_PARAMETER_VALUE = "Resource not found! Id: ";
	
	
	public ResourceNotFoundException(String parameterName, String parameterValue){
		this(I18nConstants.RESOURCE_NOT_FOUND, parameterName, parameterValue);
	}
	
	public ResourceNotFoundException(String i18nKey, String parameterName, String parameterValue){
		this(i18nKey, parameterName, parameterValue, null);
	}
	public ResourceNotFoundException(String i18nKey, String parameterName, String parameterValue, Throwable th){
		this(i18nKey, parameterName, parameterValue, HttpStatus.NOT_FOUND, th);
	}
	
	public ResourceNotFoundException(String i18nKey, String parameterName, String parameterValue, HttpStatus status, Throwable th){
		this(MESSAGE_BLANK_PARAMETER_VALUE + parameterValue, i18nKey, new String[]{parameterName, parameterValue}, status, th);
	}
	
	public ResourceNotFoundException(String message, String i18nKey, String[] i18nParams, HttpStatus status, Throwable th){
		super(message, i18nKey, i18nParams, status, th);
	}
}
