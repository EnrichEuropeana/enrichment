package eu.europeana.enrichment.mongo.utils;

import com.mongodb.WriteConcern;
import com.mongodb.client.model.ReturnDocument;

import dev.morphia.DeleteOptions;
import dev.morphia.ModifyOptions;

public class MorphiaUtils {

    // Morphia deletes the first matching document by default. This is required for
    // deleting all matches.
    public static final DeleteOptions MULTI_DELETE_OPTS = new DeleteOptions().multi(true);

    /**
     * ModifyOptions used for ID generation. Ensures consistency when Mongo is deployed in a
     * replica-set
     */
    public static final ModifyOptions MAJORITY_WRITE_MODIFY_OPTS = 
		new ModifyOptions().returnDocument(ReturnDocument.AFTER).writeConcern(WriteConcern.MAJORITY);    

    private MorphiaUtils() {
	// private constructor to prevent instantiation
    }

}
