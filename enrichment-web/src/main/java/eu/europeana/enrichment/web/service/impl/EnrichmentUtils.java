package eu.europeana.enrichment.web.service.impl;

public class EnrichmentUtils {

    public static String buildResourcePath(String... idParts) {
        StringBuilder builder = new StringBuilder();
        for (String part : idParts) {
            builder.append('/').append(part);
        }
        return builder.toString();
    }
}
