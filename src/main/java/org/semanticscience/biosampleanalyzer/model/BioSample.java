package org.semanticscience.biosampleanalyzer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class BioSample {
	public String id;
	public String access;
	public String publicationDate;
	public String lastUpdate;
	public String submissionDate;
	public String accession;
	public String organismTaxonomyId;
	public String organismTaxonomyName;
	public String organismName;
	public String modelName;
	public String packageDisplayName;
	public String packageName;
	public String status;
	public String statusDate;
	public String ownerName;
	public Map<String, BioAttribute> attributes = new HashMap<>();
	public List<BioLink> links = new ArrayList<>();
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
