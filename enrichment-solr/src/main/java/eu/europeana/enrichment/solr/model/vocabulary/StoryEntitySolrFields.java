package eu.europeana.enrichment.solr.model.vocabulary;

public interface StoryEntitySolrFields {
		
	public static final String STORY_ID = "id";//the abbreviation of ne_id is: named entity id
	public static final String TRANSCRIPTION = "transcription";
	public static final String SUMMARY = "summary";
	public static final String DESCRIPTION = "description";
	public static final String SOURCE = "source";
	public static final String TITLE = "title";
	public static final String LANGUAGE_DESCRIPTION = "language_description";
	public static final String LANGUAGE_SUMMARY = "language_summary";
	public static final String SUMMARY_EN = "summary_en";
	public static final String DESCRIPTION_EN = "description_en";
	public static final String TRANSCRIPTION_LANGUAGES = "transcriptionLanguages";
	public static final String KEYWORDS = "keywords";
	public static final String COMPLETION_STATUS = "completionStatus";
	public static final String ITEM_COUNT = "itemCount";
	
}
