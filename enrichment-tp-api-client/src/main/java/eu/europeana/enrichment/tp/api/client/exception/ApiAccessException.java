package eu.europeana.enrichment.tp.api.client.exception;

public class ApiAccessException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -3446531864190013187L;

    public ApiAccessException(String message, Throwable th) {
        super(message, th);
    }
}
