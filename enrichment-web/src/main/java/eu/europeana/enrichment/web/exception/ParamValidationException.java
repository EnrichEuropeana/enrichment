package eu.europeana.enrichment.web.exception;

import org.springframework.http.HttpStatus;

import eu.europeana.api.commons.web.exception.HttpException;
import eu.europeana.enrichment.web.common.config.I18nConstants;


public class ParamValidationException extends HttpException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3664526076494279093L;
	public static final String MESSAGE_BLANK_PARAMETER_VALUE = "Invalid request. Parameter value must not be null or empty!";
	
//	String parameterName;
//	String parameterValue;
	
	
	public ParamValidationException(String parameterName, String parameterValue){
		this(I18nConstants.INVALID_PARAM_VALUE, parameterName, parameterValue, null);
	}
	
	public ParamValidationException(String i18nKey, String parameterName, String parameterValue){
		this(i18nKey, parameterName, parameterValue, null);
	}
	public ParamValidationException(String i18nKey, String parameterName, String parameterValue, Throwable th){
		this(i18nKey, parameterName, parameterValue, HttpStatus.BAD_REQUEST, th);
	}
	
	public ParamValidationException(String i18nKey, String parameterName, String parameterValue, HttpStatus status, Throwable th){
		this(null, i18nKey, new String[]{parameterName, parameterValue}, status, th);
	}
	
	public ParamValidationException(String message, String i18nKey, String[] i18nParams, HttpStatus status, Throwable th){
		super(message, i18nKey, i18nParams, status, th);
	}
}
