package eu.europeana.enrichment.web.controller;

import java.util.Date;

import org.springframework.util.StringUtils;

import eu.europeana.enrichment.web.common.config.I18nConstants;
import eu.europeana.enrichment.web.exception.ApplicationAuthenticationException;

public abstract class BaseRest {

	public BaseRest() {
		super();
	}
	
	/**
	 * This method is used for validation of the provided api key
	 * @param wsKey
	 * @throws EntityAuthenticationException
	 */
	protected void validateApiKey(String wsKey) throws ApplicationAuthenticationException {
		// throws exception if the wskey is not found
		if (wsKey == null)
			throw new ApplicationAuthenticationException(null, I18nConstants.MISSING_APIKEY);
		if (StringUtils.isEmpty(wsKey))
			throw new ApplicationAuthenticationException(null, I18nConstants.EMPTY_APIKEY);
		if (!wsKey.equals("apidemo"))
			throw new ApplicationAuthenticationException(null, I18nConstants.INVALID_APIKEY, new String[]{wsKey});
	}
	
	
	/**
     * This method generates etag for response header.
     * 
     * @param timestamp The date of the last modification
     * @param format    The MIME format
     * 
     * @return etag value
     */
    public String generateETag(Date timestamp, String format) {
	// add timestamp, format and version to an etag
	Integer hashCode = (timestamp + format).hashCode();
	return hashCode.toString();
    }
	
}
