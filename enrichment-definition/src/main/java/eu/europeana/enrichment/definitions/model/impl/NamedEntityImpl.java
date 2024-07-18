package eu.europeana.enrichment.definitions.model.impl;

import java.util.List;

import org.bson.types.ObjectId;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Field;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Index;
import dev.morphia.annotations.IndexOptions;
import dev.morphia.annotations.Indexes;
import dev.morphia.annotations.Transient;

@Entity(value="NamedEntityImpl")
@Indexes(@Index(fields = { @Field("label"), @Field("type"), @Field("dbpediaId") }, options = @IndexOptions(unique = true)))
public class NamedEntityImpl {

	protected String type;
	protected String label;
	protected List<String> europeanaIds;
	protected List<String> wikidataSearchIds;
	protected List<String> prefLabelMatchWikiSearchIds;
	protected List<String> altLabelMatchWikiSearchIds;
	protected String dbpediaId;
	protected List<String> dbpediaWikidataIds;
	String prefWikiIdBothStanfordAndDbpedia;
	String prefWikiIdOnlyStanford;
	String prefWikiIdOnlyDbpedia;
	String prefWikiIdBothStanfordAndDbpedia_status;
	String prefWikiIdOnlyStanford_status;
	String prefWikiIdOnlyDbpedia_status;

	@Transient
	protected PositionEntityImpl positionEntity;

	//id will be used for storing MongoDB _id
	@Id
    private ObjectId _id;
	
	public ObjectId get_id() {
		return _id;
	}
	
	public NamedEntityImpl() {
	}
	
	public NamedEntityImpl(String key) {
		this.label = key;
	}

	@Override
	public String toString() {
		return label;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String classificationtype) {
		this.type = classificationtype;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String key) {
		this.label = key;
	}
	
	public List<String> getEuropeanaIds() {
		return europeanaIds;
	}
	
	public void setEuropeanaIds(List<String> ids) {
		this.europeanaIds = ids;
	}
	
	public void addEuopeanaId(String id) {
		this.europeanaIds.add(id);
	}
	
	public List<String> getDbpediaWikidataIds(){
		return dbpediaWikidataIds;
	}	
	
	public void setDbpediaWikidataIds(List<String> ids) {
		dbpediaWikidataIds = ids;
	}
	
	public void setPositionEntity(PositionEntityImpl position) {
		positionEntity = position;
	}
	
	public PositionEntityImpl getPositionEntity() {
		return positionEntity;
	}
	
	public String getDBpediaId() {
		return dbpediaId;
	}
	
	public void setDBpediaId(String id) {
		dbpediaId = id;
	}
	
	public List<String> getWikidataSearchIds() {
		return wikidataSearchIds;
	}

	public void setWikidataSearchIds(List<String> wikidataSearchIds) {
		this.wikidataSearchIds = wikidataSearchIds;
	}
	
	public List<String> getPrefLabelMatchWikiSearchIds() {
		return prefLabelMatchWikiSearchIds;
	}

	public void setPrefLabelMatchWikiSearchIds(List<String> prefLabelMatchWikiSearchIds) {
		this.prefLabelMatchWikiSearchIds = prefLabelMatchWikiSearchIds;
	}

	public List<String> getAltLabelMatchWikiSearchIds() {
		return altLabelMatchWikiSearchIds;
	}

	public void setAltLabelMatchWikiSearchIds(List<String> altLabelMatchWikiSearchIds) {
		this.altLabelMatchWikiSearchIds = altLabelMatchWikiSearchIds;
	}
	
	public String getPrefWikiIdBothStanfordAndDbpedia() {
		return prefWikiIdBothStanfordAndDbpedia;
	}

	public void setPrefWikiIdBothStanfordAndDbpedia(String prefWikiIdAll) {
		this.prefWikiIdBothStanfordAndDbpedia = prefWikiIdAll;
	}

	public String getPrefWikiIdOnlyStanford() {
		return prefWikiIdOnlyStanford;
	}

	public void setPrefWikiIdOnlyStanford(String prefWikiIdStanford) {
		this.prefWikiIdOnlyStanford = prefWikiIdStanford;
	}

	public String getPrefWikiIdOnlyDbpedia() {
		return prefWikiIdOnlyDbpedia;
	}

	public void setPrefWikiIdOnlyDbpedia(String prefWikiIdDbpedia) {
		this.prefWikiIdOnlyDbpedia = prefWikiIdDbpedia;
	}

	public String getPrefWikiIdBothStanfordAndDbpedia_status() {
		return prefWikiIdBothStanfordAndDbpedia_status;
	}

	public void setPrefWikiIdBothStanfordAndDbpedia_status(String prefWikiIdAll_status) {
		this.prefWikiIdBothStanfordAndDbpedia_status = prefWikiIdAll_status;
	}

	public String getPrefWikiIdOnlyStanford_status() {
		return prefWikiIdOnlyStanford_status;
	}

	public void setPrefWikiIdOnlyStanford_status(String prefWikiIdStanford_status) {
		this.prefWikiIdOnlyStanford_status = prefWikiIdStanford_status;
	}

	public String getPrefWikiIdOnlyDbpedia_status() {
		return prefWikiIdOnlyDbpedia_status;
	}

	public void setPrefWikiIdOnlyDbpedia_status(String prefWikiIdDbpedia_status) {
		this.prefWikiIdOnlyDbpedia_status = prefWikiIdDbpedia_status;
	}

}
