package org.semanticscience.biosampleanalyzer.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class BioAttribute {
	public String harmonizedName;
	public String attributeName;
	public String displayName;
	public String value;
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
