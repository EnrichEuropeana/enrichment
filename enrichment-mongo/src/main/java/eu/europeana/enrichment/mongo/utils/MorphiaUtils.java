package eu.europeana.enrichment.mongo.utils;

import dev.morphia.DeleteOptions;

public class MorphiaUtils {

    // Morphia deletes the first matching document by default. This is required for
    // deleting all matches.
    public static final DeleteOptions MULTI_DELETE_OPTS = new DeleteOptions().multi(true);

    private MorphiaUtils() {
	// private constructor to prevent instantiation
    }

}
