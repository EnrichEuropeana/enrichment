package eu.europeana.enrichment.mongo.dao;

// Collection field names
public class EntityFields {

    private EntityFields() {
        // private constructor to prevent instantiation
    }

    public static final String ITEM_ID = "itemId";
    public static final String STORY_ID = "storyId";
    public static final String ID = "id";
    public static final String OBJECT_ID = "_id";
    public static final String KEY = "key";
    public static final String TOOL = "tool";
    public static final String LANGUAGE = "language";
    public static final String TYPE = "type";
    public static final String WIKIDATA_ID = "wikidataId";
    public static final String PROPERTY = "property";
    public static final String LABEL = "label";
    public static final String POSITION_ENTITIES = "positionEntities";
    public static final String FIELD_USED_FOR_NER = "fieldUsedForNER";
    public static final String TRANSLATION_KEY = "translationKey";
    public static final String NER_TOOLS = "nerTools";
}