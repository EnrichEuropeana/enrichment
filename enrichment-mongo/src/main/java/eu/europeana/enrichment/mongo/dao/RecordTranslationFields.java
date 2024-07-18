package eu.europeana.enrichment.mongo.dao;

// Collection field names
public abstract class RecordTranslationFields {

    private RecordTranslationFields() {
        // private constructor to prevent instantiation
    }

    public static final String OBJECT_ID = "_id";
    public static final String DESCRIPTION = "description";
    public static final String TRANSLATION = "translation";
    public static final String RECORD_ID = "recordId";
    public static final String LANGUAGE = "language";
    
}