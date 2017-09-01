package org.semanticscience.biosampleanalyzer;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.metadatacenter.biosample.analyzer.AttributeImpl;
import org.metadatacenter.biosample.analyzer.BioPortalAgent;
import org.metadatacenter.biosample.analyzer.CsvWriter;
import org.metadatacenter.biosample.analyzer.GenericValidator;
import org.metadatacenter.biosample.analyzer.Link;
import org.metadatacenter.biosample.analyzer.Record;
import org.metadatacenter.biosample.analyzer.RecordBuilder;
import org.metadatacenter.biosample.analyzer.RecordValidationReport;
import org.metadatacenter.biosample.analyzer.TermValidator;
import org.semanticscience.biosampleanalyzer.model.BioAttribute;
import org.semanticscience.biosampleanalyzer.model.BioLink;
import org.semanticscience.biosampleanalyzer.model.BioSample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {
	private static final Logger logger = LoggerFactory.getLogger(Application.class);
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);

	}

	@Override
	public void run(String... args) throws Exception {
		String inputFileName = args[0];
		String outputFileName = args[1];
		String bioPortalApiKey = args[2];
		
		logger.error("This is just a drill");
		
		List<Record> records = new ArrayList<>();
		
		new BioSampleParser(inputFileName).extractBioSamples(new BioSampleParserCallback() {
			int count = 0;
			@Override
			public void newBioSampleFound(BioSample bioSample) {
//				System.out.println(bioSample);
				try {
					records.add(toRecord(bioSample));
					count++;
					if(count%1000==0)
						System.out.println("SUCCESS: " + count);
				} catch (Exception e) {
//					System.out.println("ERROR  : " + e.getMessage());
				}
			}
			
			private Record toRecord(BioSample bioSample) {
				RecordBuilder recordBuilder = new RecordBuilder();
				recordBuilder.setAccess(bioSample.access);
				recordBuilder.setAccession(bioSample.accession);
				recordBuilder.setId(bioSample.id);
				recordBuilder.setLastUpdate(bioSample.lastUpdate);
				recordBuilder.setModelName(bioSample.modelName);
				recordBuilder.setOrganismName(bioSample.organismName);
				recordBuilder.setOrganismTaxonomyId(bioSample.organismTaxonomyId);
				recordBuilder.setOrganismTaxonomyName(bioSample.organismTaxonomyName);
				recordBuilder.setOwnerName(bioSample.ownerName);
				recordBuilder.setPackageDisplayName(bioSample.packageDisplayName);
				recordBuilder.setPackageName(bioSample.packageName);
				recordBuilder.setPublicationDate(bioSample.publicationDate);
				recordBuilder.setStatus(bioSample.status);
				recordBuilder.setStatusDate(bioSample.statusDate);
				recordBuilder.setSubmissionDate(bioSample.submissionDate);
				for(String key: bioSample.attributes.keySet()) {
					BioAttribute bioAttribute = bioSample.attributes.get(key);
					recordBuilder.addAttribute(bioAttribute.attributeName, new AttributeImpl(bioAttribute.harmonizedName, bioAttribute.attributeName, bioAttribute.displayName, bioAttribute.value));
				}
				for(BioLink link: bioSample.links)
					recordBuilder.addLink(new Link(link.type, link.target, link.label, link.value));
				
				return recordBuilder.build();
			}
		});
		
		CsvWriter csvWriter = new CsvWriter(new File(outputFileName));
	    GenericValidator validator = new GenericValidator(new TermValidator(new BioPortalAgent(bioPortalApiKey)));
	    DecimalFormat df = new DecimalFormat("#");
	    int nrRecords = records.size();
	    double counter = 0;
	    double percentage = 0;
	    for (Record record : records) {
	      counter++;
	      double percentageNew = Double.valueOf(df.format((counter/nrRecords)*100));
	      if(percentageNew > percentage) {
	        logger.error("\t" + (int) percentage + "%  (record #" + (int) counter + ")");
	      }
	      percentage = percentageNew;
	      RecordValidationReport report = validator.validateBioSampleRecord(record);
	      boolean isValid = validator.isValid(report);
	      csvWriter.writeRecord(record, isValid, report.getAttributeGroupValidationReports());
	    }
	    csvWriter.closeWriter();
		
	}
	
	
}
