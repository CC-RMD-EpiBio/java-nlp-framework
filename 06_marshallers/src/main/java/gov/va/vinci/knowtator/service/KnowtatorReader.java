/*******************************************************************************
 *                                   NIH Clinical Center 
 *                             Department of Rehabilitation 
 *                       Epidemiology and Biostatistics Branch 
 *                                            2019 - 2022
 *   ---------------------------------------------------------------------------
 *   Copyright Notice:
 *   This software was developed and funded by the National Institutes of Health
 *   Clinical Center (NIHCC), part of the National Institutes of Health (NIH),
 *   and agency of the United States Department of Health and Human Services,
 *   which is making the software available to the public for any commercial
 *   or non-commercial purpose under the following open-source BSD license.
 *  
 *   Government Usage Rights Notice:
 *   The U.S. Government retains unlimited, royalty-free usage rights to this 
 *   software, but not ownership, as provided by Federal law. Redistribution 
 *   and use in source and binary forms, with or without modification, 
 *   are permitted provided that the following conditions are met:
 *      1. Redistributions of source code must retain the above copyright
 *         and government usage rights notice, this list of conditions and the 
 *         following disclaimer.
 *  
 *      2. Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *        
 *      3. Neither the names of the National Institutes of Health Clinical
 *         Center, the National Institutes of Health, the U.S. Department of
 *         Health and Human Services, nor the names of any of the software
 *         developers may be used to endorse or promote products derived from
 *         this software without specific prior written permission.
 *   
 *      4. The U.S. Government retains an unlimited, royalty-free right to
 *         use, distribute or modify the software.
 *   
 *      5. Please acknowledge NIH CC as the source of this software by including
 *         the phrase: "Courtesy of the U.S. National Institutes of Health Clinical Center"
 *          or 
 *                     "Source: U.S. National Institutes of Health Clinical Center."
 *  
 *     THIS SOFTWARE IS PROVIDED BY THE U.S. GOVERNMENT AND CONTRIBUTORS "AS
 *     IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *     TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *     PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE U.S. GOVERNMENT
 *     OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *     EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *     PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *     PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *     LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *     NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *     SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 *     When attributing this code, please make reference to:
 *        Divita G, Carter ME, Tran LT, Redd D, Zeng QT, Duvall S, Samore MH, Gundlapalli AV. 
 *        v3NLP Framework: tools to build applications for extracting concepts from clinical text. 
 *        eGEMs. 2016;4(3). 
 *      
 *     In the absence of a specific paper or url listed above, reference https://github.com/CC-RMD-EpiBio/java-nlp-framework
 *   
 *     To view a copy of this license, visit https://github.com/CC-RMD-EpiBio/java-nlp-framework/blob/main/LICENSE.MD
 *******************************************************************************/
// =================================================
/**
 * KnowtatorReader is a class that reads in knowtator
 * and returns the knowtator structuers
 *
 *
 * @author  Guy Divita 
 * @created Aug 1, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.va.vinci.knowtator.service;

import gov.va.vinci.knowtator.Annotation;
import gov.va.vinci.knowtator.ClassMention;
import gov.va.vinci.knowtator.ComplexSlotMention;
import gov.va.vinci.knowtator.ComplexSlotMentionValue;
import gov.va.vinci.knowtator.HasSlotMention;
import gov.va.vinci.knowtator.Mention;
import gov.va.vinci.knowtator.MentionClass;
import gov.va.vinci.knowtator.MentionSlot;
import gov.va.vinci.knowtator.Span;
import gov.va.vinci.knowtator.StringSlotMention;
import gov.va.vinci.knowtator.StringSlotMentionValue;
import gov.nih.cc.rmd.nlp.framework.utils.U;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;




public class KnowtatorReader {

  
  // ----------------------------------------------
  /**
   * read
   * 
   * @param sourceFile
   * @param annotationFile
   * @return
   * @throws Exception 
   */
  // ----------------------------------------------
  public static gov.va.vinci.knowtator.Document read(File sourceFile, File annotationFile) throws Exception {
    
    String sourceFileName = sourceFile.getAbsolutePath();
    String annotationFileName = annotationFile.getAbsolutePath();
    gov.va.vinci.knowtator.Document doc = read ( sourceFileName, annotationFileName);
    
    return doc;
  }

  // ----------------------------------------------
  /**
   * read
   * 
   * @param inputTextFile
   * @param inputAnnotationFile
   * @throws Exception 
   */
  // ----------------------------------------------
  private static gov.va.vinci.knowtator.Document read(String inputTextFile, String inputAnnotationFile) throws Exception {
    
    String docText = U.readFile( inputTextFile);
 // String newLine = U.getNewlineType( docText);
    
   
    
    gov.va.vinci.knowtator.Document                                 doc = new gov.va.vinci.knowtator.Document(docText);
    annotations = new ArrayList<gov.va.vinci.knowtator.Annotation>();
    classMentions = new ArrayList<gov.va.vinci.knowtator.ClassMention>();
    complexSlotMentions = new ArrayList<gov.va.vinci.knowtator.ComplexSlotMention>();
    hasSlotMentions = new ArrayList<HasSlotMention>();
    stringSlotMentions = new ArrayList<StringSlotMention>();

    readAnnotationFile( inputAnnotationFile );
    doc.setAnnotations( annotations);
    doc.setClassMentions( classMentions);
    doc.setStringSlotMentions(stringSlotMentions);
    doc.setComplexSlotMentions( complexSlotMentions);
 
    
    return doc;
    
  } // end Method read() --------------------------

  

  // ----------------------------------------------
  /**
   * readAnnotationFile (Wondering if this can be read in using a dom object read, or rest or spring
   * or something like that. 
   * 
   * @param inputAnnotationFile
   * @param annotations
   * @param classMentions
   * @param complexSlotMentions
   */
  // ----------------------------------------------
  private static void readAnnotationFile(String inputAnnotationFile  ) {
   
    try {
      
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser saxParser = factory.newSAXParser();
  
      // ----------------------------------------------
      /**
       *  Inner Method for default handler
       */
      // ----------------------------------------------
      DefaultHandler handler = new DefaultHandler()  {
  

        boolean             spannedText = false;
        boolean            mentionClass = false;
        boolean            creationDate = false;
        boolean               annotator = false;
 
        String  mentionString = null;
        Span            aSpan = null;
        Mention           aMention = null;
        MentionClass  aMentionClass = null;
        MentionSlot    aMentionSlot = null;
        StringSlotMentionValue stringSlotMentionValue = null;
        ComplexSlotMentionValue aComplexSlotMentionValue = null;
        String   aSpannedText = null;
        String  aCreationDate = null;
        String     aAnnotator = null;
        String          begin = null;
        String            end = null;
        String classMentionId = null;
        
        String stringSlotMentionValueValue = null;
        String mentionClassId = null;
        String complexSlotMentionId = null;
        String aMentionSlotId = null;
        String stringSlotMentionId = null;
        String hasSlotMentionId = null;
        String relationship = null;
        String complexSlotMentionValueValue = null;
        
        
        // ----------------------------------------------
        /**
         *  Inner Method for startElement 
         */
        // ----------------------------------------------
        public void startElement(String uri, 
                                 String localName,
                                 String qName, 
                                 Attributes attributes) throws SAXException {

          
          if (qName.equalsIgnoreCase("mention"))      { 
            mentionString = attributes.getValue("id");
          }
          
          if ( qName.equalsIgnoreCase("SpannedText"))               spannedText = true;
          if ( qName.equalsIgnoreCase("creationDate"))             creationDate = true;
          if ( qName.equalsIgnoreCase("annotator"))                   annotator = true;
          
          if (qName.equalsIgnoreCase("Span"))   { 
            begin = attributes.getValue("start");
            end   = attributes.getValue("end");
          }
          
          if (qName.equalsIgnoreCase("stringSlotMentionValue"))  {      
          stringSlotMentionValueValue = attributes.getValue("value");
        }
          
          if (qName.equalsIgnoreCase("mentionSlot"))  { 
            aMentionSlotId = attributes.getValue("id");
          }

          if (qName.equalsIgnoreCase("ClassMention")) {            
            classMentionId = attributes.getValue("id");
          }
          
          if (qName.equalsIgnoreCase("stringSlotMention")) {            
            stringSlotMentionId = attributes.getValue("id");
          }
          
        if (qName.equalsIgnoreCase("ComplexSlotMentionValue")) {
          complexSlotMentionValueValue = attributes.getValue("value");
        }
        
        if (qName.equalsIgnoreCase("ComplexSlotMention")) {
          complexSlotMentionId = attributes.getValue("id");
        }
        if (qName.equalsIgnoreCase("MentionClass")) { 
          mentionClass = true;
          mentionClassId = attributes.getValue("id");
        }
        
      
        if (qName.equalsIgnoreCase("hasSlotMention")) {     
          hasSlotMentionId = attributes.getValue("id");
        }
          
         
        } // end Inner Method DefaultHandler() -------------
  
      

        // ----------------------------------------------
        /**
         *  Inner Method for endElement 
         */
        // ----------------------------------------------
        public void endElement(String  uri,   String  localName,  String qName) throws SAXException {
                  
          
          if (qName.equalsIgnoreCase("mention") )  { 
            aMention = new Mention(mentionString); 
          
          }
          
          if (qName.equalsIgnoreCase("span"))    { 
            aSpan = new Span( begin, end);
          
          }

          if (qName.equalsIgnoreCase("Annotation")) {
            gov.va.vinci.knowtator.Annotation anAnnotation = new Annotation(aMention, aSpan, aSpannedText, aCreationDate, aAnnotator );
                        
            annotations.add( anAnnotation);
          }
            
          if (qName.equalsIgnoreCase("MentionClass")) {
            aMentionClass = new MentionClass(mentionClassId, relationship);
          }
          
          if ( qName.equalsIgnoreCase("ClassMention")) {
            ClassMention aClassMention = new ClassMention( classMentionId, aMentionClass, hasSlotMentions );
            classMentions.add( aClassMention);
            hasSlotMentions = new ArrayList<HasSlotMention>();
          }
          
          if ( qName.equalsIgnoreCase("stringSlotMentionValue")) {
            stringSlotMentionValue = new StringSlotMentionValue( stringSlotMentionValueValue );
          }
          
          if ( qName.equalsIgnoreCase("mentionSlot")) {
            aMentionSlot = new MentionSlot( aMentionSlotId );
          }
        
          if ( qName.equalsIgnoreCase("stringSlotMention")) {
            StringSlotMention aStringSlotMention = new StringSlotMention( stringSlotMentionId, aMentionSlot, stringSlotMentionValue );
            stringSlotMentions.add( aStringSlotMention);
           
          }
          
          if ( qName.equalsIgnoreCase("hasSlotMention")) {
            hasSlotMentions.add( new HasSlotMention( hasSlotMentionId )); 
          }
          
          
          if ( qName.equalsIgnoreCase("ComplexSlotMentionValue")) {
            aComplexSlotMentionValue = new ComplexSlotMentionValue( complexSlotMentionValueValue);
          }
          
          
          
          if (qName.equalsIgnoreCase("complexSlotMention")) {
           
            ComplexSlotMention aComplexSlotMention = new ComplexSlotMention(complexSlotMentionId, aMentionSlot, aComplexSlotMentionValue);
            complexSlotMentions.add( aComplexSlotMention );
          }
          
        } // end Method endElement() ---------------------
  
        // ----------------------------------------------
        /**
         *  Inner Method for characters 
         */
        // ----------------------------------------------
        public void characters(char ch[], int start, int length) throws SAXException {
  
          String buff = new String( ch, start, length);
            
          if (spannedText)        { 
            aSpannedText = buff;
            spannedText  = false;  
          }
          
          if (creationDate)       {
            aCreationDate = buff;
            creationDate        = false;
          }
   
          if (annotator) {
            aAnnotator = buff;
            annotator = false;
          }
          
          if (mentionClass)       { 
           relationship = buff;
            mentionClass       = false; 
          }

        } // end inner method characters() ----------------
  
      };  // end inner class defaultHandler() ----------
  
       saxParser.parse(inputAnnotationFile, handler);
              
     } catch (Exception e) {
       e.printStackTrace();
     }
     
    
  } // end Method readAnnotatonFile () ------------


  // ----------------------------------------
  // Global variables
  // ----------------------------------------
  private static ArrayList<Annotation>                 annotations = null;
  private static ArrayList<ClassMention>             classMentions = null;
  private static ArrayList<ComplexSlotMention> complexSlotMentions = null;
  private static ArrayList<HasSlotMention>         hasSlotMentions = null;
  private static ArrayList<StringSlotMention>   stringSlotMentions = null;
  
} // end Class Knowtator Reader
