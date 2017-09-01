package org.semanticscience.biosampleanalyzer;

import org.semanticscience.biosampleanalyzer.model.BioSample;

public interface BioSampleParserCallback {
	
	public void newBioSampleFound(BioSample bioSample);

}
