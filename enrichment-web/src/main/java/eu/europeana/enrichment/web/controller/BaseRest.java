package eu.europeana.enrichment.web.controller;

import org.springframework.util.StringUtils;

import eu.europeana.enrichment.common.config.I18nConstants;
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
	
}
