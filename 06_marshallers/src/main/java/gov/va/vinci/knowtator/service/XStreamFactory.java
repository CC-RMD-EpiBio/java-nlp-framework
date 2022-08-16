/*  
 */ 
package gov.va.vinci.knowtator.service;



import gov.va.vinci.knowtator.Annotation;
import gov.va.vinci.knowtator.ClassMention;
import gov.va.vinci.knowtator.Document;
import gov.va.vinci.knowtator.HasSlotMention;
import gov.va.vinci.knowtator.Mention;
import gov.va.vinci.knowtator.MentionClass;
import gov.va.vinci.knowtator.MentionSlot;
import gov.va.vinci.knowtator.Span;
import gov.va.vinci.knowtator.StringSlotMention;
import gov.va.vinci.knowtator.StringSlotMentionValue;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class XStreamFactory {

	private static XStream xsXml;
	
	public static XStream getXmlInstance()
	{
		if (xsXml == null) {
			xsXml = new XStream(new DomDriver());
			setAliases(xsXml);
		}
		return xsXml;
	}

	
	private static void setAliases(XStream xs) {
	
	  xs.alias("annotations", gov.va.vinci.knowtator.Document.class);
	  xs.useAttributeFor(gov.va.vinci.knowtator.Document.class, "textSource");
	  xs.addImplicitCollection(gov.va.vinci.knowtator.Document.class, "annotations");
    xs.addImplicitCollection(gov.va.vinci.knowtator.Document.class, "classMentions");
    xs.addImplicitCollection(gov.va.vinci.knowtator.Document.class, "stringSlotMentions");
	  
	  xs.alias("annotation", Annotation.class);
	  xs.alias("mention", Mention.class);
		xs.useAttributeFor(Mention.class, "id");
	  xs.useAttributeFor(Span.class, "end");
		xs.useAttributeFor(Span.class, "start");

	
		
		xs.alias("classMention", ClassMention.class);
		xs.useAttributeFor(ClassMention.class, "id");
    xs.addImplicitCollection(ClassMention.class, "hasSlotMentions");

	  
		
    xs.alias("mentionClass", MentionClass.class);
    xs.useAttributeFor(MentionClass.class, "id");
   
    
    xs.alias("hasSlotMention", HasSlotMention.class);
    xs.useAttributeFor(HasSlotMention.class, "id");

	  
    xs.alias("stringSlotMention", StringSlotMention.class);
    xs.useAttributeFor(StringSlotMention.class, "id");

    
    xs.alias("mentionSlot", MentionSlot.class);
    xs.useAttributeFor(MentionSlot.class, "id");

    xs.alias("stringSlotMentionValue", StringSlotMentionValue.class);
    xs.useAttributeFor(StringSlotMentionValue.class, "value");
		
	}
}
