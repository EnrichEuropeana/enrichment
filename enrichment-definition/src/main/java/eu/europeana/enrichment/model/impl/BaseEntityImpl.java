package eu.europeana.enrichment.model.impl;

import java.util.Date;

public class BaseEntityImpl {
	
	protected Date created;
	protected Date modified;

	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public Date getModified() {
		return modified;
	}
	public void setModified(Date modified) {
		this.modified = modified;
	}
	
}
