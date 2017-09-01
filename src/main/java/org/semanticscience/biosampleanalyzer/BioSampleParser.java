package org.semanticscience.biosampleanalyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.semanticscience.biosampleanalyzer.model.BioAttribute;
import org.semanticscience.biosampleanalyzer.model.BioLink;
import org.semanticscience.biosampleanalyzer.model.BioSample;

public class BioSampleParser {
	private final static boolean debugPrint = false;
	private final String indentStr = new String("--------------------").replaceAll("-", "\t");
	
	private String inputFileName;
	
	public BioSampleParser(String inputFileName) {
		this.inputFileName = inputFileName;
	}
	
	public void extractBioSamples(BioSampleParserCallback bioSampleParserCallback) throws XMLStreamException, FactoryConfigurationError, FileNotFoundException {
		BioSample bioSample = null;
		BioAttribute bioAttribute = null;
		BioLink bioLink = null;

		XMLEventReader xmlEventReader = XMLInputFactory.newInstance().createXMLEventReader(new FileInputStream(new File(inputFileName)));
		StringBuilder content = null;
		int indent = 0;
		while (xmlEventReader.hasNext()) {
			XMLEvent xmlEvent = xmlEventReader.nextEvent();
			if (xmlEvent.isStartElement()) {
				StartElement startElement = xmlEvent.asStartElement();
				String elementName = startElement.getName().getLocalPart();
				content = new StringBuilder();
				if(debugPrint) {
					System.out.print(indentStr.substring(0, indent++) + "<" + elementName);
					@SuppressWarnings("unchecked")
					Iterator<Attribute> iter = startElement.getAttributes();
					while (iter.hasNext()) {
						Attribute attribute = (Attribute) iter.next();
						System.out.print(" " + attribute.getName() + "=\"" + attribute.getValue() + "\"");
					}
					System.out.println(">");
				}
				
				if (elementName.equalsIgnoreCase("BioSample")) {
					bioSample = new BioSample();
					bioSample.submissionDate = getAttributeValueOrNull(startElement, "submission_date");
					bioSample.access = getAttributeValueOrNull(startElement, "access");
					bioSample.lastUpdate = getAttributeValueOrNull(startElement, "last_update");
					bioSample.publicationDate = getAttributeValueOrNull(startElement, "publication_date");
					bioSample.id = getAttributeValueOrNull(startElement, "id");
					bioSample.accession = getAttributeValueOrNull(startElement, "accession");
				} 
				else if (elementName.equalsIgnoreCase("Organism")) {
						bioSample.organismTaxonomyId = getAttributeValueOrNull(startElement, "taxonomy_id");
						bioSample.organismTaxonomyName = getAttributeValueOrNull(startElement, "taxonomy_name");
				}
				else if (elementName.equalsIgnoreCase("Package")) {
					bioSample.packageDisplayName = getAttributeValueOrNull(startElement, "display_name");
				}
				else if (elementName.equalsIgnoreCase("Attribute")) {
					if(getAttributeValueOrNull(startElement, "harmonized_name")!=null && getAttributeValueOrNull(startElement, "attribute_name")!=null && getAttributeValueOrNull(startElement, "display_name")!=null) {
						bioAttribute = new BioAttribute();
						bioAttribute.harmonizedName = getAttributeValueOrNull(startElement, "harmonized_name");
						bioAttribute.attributeName = getAttributeValueOrNull(startElement, "attribute_name");
						bioAttribute.displayName = getAttributeValueOrNull(startElement, "display_name");
					}
				}
				else if (elementName.equalsIgnoreCase("Status")) {
					bioSample.status = getAttributeValueOrNull(startElement, "status");
					bioSample.statusDate = getAttributeValueOrNull(startElement, "when");
				}
				else if (elementName.equalsIgnoreCase("Link")) {
					if(getAttributeValueOrNull(startElement, "type")!=null && getAttributeValueOrNull(startElement, "label")!=null && getAttributeValueOrNull(startElement, "target")!=null) {
						bioLink = new BioLink();
						bioLink.type = getAttributeValueOrNull(startElement, "type");
						bioLink.label = getAttributeValueOrNull(startElement, "label");
						bioLink.target = getAttributeValueOrNull(startElement, "target");
					}
				}
			} else if (xmlEvent.isEndElement()) {
				EndElement endElement = xmlEvent.asEndElement();
				String elementName = endElement.getName().getLocalPart();
				
				if(elementName.equalsIgnoreCase("OrganismName")) {
					if(content!=null)
						bioSample.organismName = content.toString();
				}
				if(elementName.equalsIgnoreCase("Name")) {
					if(content!=null)
						bioSample.ownerName = content.toString();
				}
				if(elementName.equalsIgnoreCase("Model")) {
					if(content!=null)
						bioSample.modelName = content.toString();
				}
				if(elementName.equalsIgnoreCase("Package")) {
					if(content!=null)
						bioSample.packageName = content.toString();
				}
				if(elementName.equalsIgnoreCase("attribute")) {
					if(content!=null && bioAttribute!=null) {
						bioAttribute.value = content.toString();
						if(bioAttribute.value.length()>0)
							bioSample.attributes.put(bioAttribute.harmonizedName, bioAttribute);
					}
					bioAttribute = null;
				}
				if(elementName.equalsIgnoreCase("link")) {
					if(content!=null && bioLink!=null) {
						bioLink.value = content.toString();
						if(bioLink.value.length()>0)
							bioSample.links.add(bioLink);
					}
					bioLink = null;
				}
				
				if(debugPrint) {
					if (content != null) {
						System.out.println(indentStr.substring(0, indent) + content.toString());
						content = null;
					}
					System.out.println(indentStr.substring(0, --indent) + "</" + elementName + ">");
				}
				
				if (elementName.equalsIgnoreCase("BioSample")) {
					if(debugPrint) 
						System.out.println(bioSample);
						
					bioSampleParserCallback.newBioSampleFound(bioSample);
					bioSample = null;
				}
			} else if (xmlEvent.isCharacters()) {
				if (content != null) {
					content.append(xmlEvent.asCharacters().getData());
				}
			}
		}
	}
	
	private String getAttributeValueOrNull(StartElement element, String attributeName) {
		if(element.getAttributeByName(new QName(attributeName))!=null)
			return element.getAttributeByName(new QName(attributeName)).getValue();
		return null;
	}

}
