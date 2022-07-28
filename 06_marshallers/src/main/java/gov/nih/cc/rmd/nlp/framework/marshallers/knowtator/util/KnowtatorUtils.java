/*******************************************************************************
 * ---------------------------------------------------------------------------
 *                                   NIH Clinical Center 
 *                             Department of Rehabilitation 
 *                        Epidemiology and Biostatistics Branch 
 *                                         2019
 *    
 *  This work is licensed under the Creative Commons Attribution 4.0 International License. 
 *  
 *  This license allows you to use, share and  adapt for any purpose, provided:
 *     Provide attribution to the creators of this work within your work.
 *     Indicate if changes were made to this work.
 *     No claim to merchantability, implied warranty, or liability can be made.
 *     
 *   When attributing this code, please make reference to
 *    [citation/url here] .  
 *    
 *     In the absence of a specific paper or url listed above, reference http://clinicalcenter.nih.gov/rmd/eb/nlp
 *  
 *  To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 ******************************************************************************/
// =================================================
/**
 * ToKnowtator
 *  
 * This class will create Knowtator output (sans the pins and pons files)
 * for UIMA typed annotations, with the caveat that complex features
 * are either flatted out (in the case of stringArrays), or are ignored
 * in the case of references to other instances of classes (tokens of terms, terms of phrases).
 *  
 * This class will create a classname.cfg file to hold
 * the label names and separate .knowtator.xml files
 * for each Cas.
 *
 * The classname.cfg has the following format:
 *
 * [label]
 * (color attribute)  
 *
 * The knowtator format follows the knowtator classes
 *
 * 
 *  <annotation>
 *    <mention id="annotationId_1" />
 *    <span end=" " start=" " />
 *    <spannedText> ... </spannedText>
 *    <creationDate> .... </creationDate> 
 *  </annotation> 
 *
 *  ...
 *  <classMention id="annotationId_1"> <--- a feature by any other name
 *    <mentionClass id="label" </mentionClass>
 *    <hasSlotMentin id="slotId_1"/> 
 *  </classMention>
 *
 *  <stringSlotMention id="slotId_1">
 *    <mentionSlot id="label">
 *       <stringSlotMentionValue value="value" />
 *    </mentionSlot>
 *  </stringSlotMention>
 *
 * @author  Guy Divita 
 * @created Jun 3, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.knowtator.util;

import gov.va.chir.model.VAnnotation;
import gov.va.vinci.knowtator.ClassMention;
import gov.va.vinci.knowtator.Document;
import gov.va.vinci.knowtator.Span;
import gov.va.vinci.knowtator.StringSlotMention;
import gov.va.vinci.knowtator.service.SerializationServiceXmlImpl;
import gov.va.vinci.model.Concept;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.FeatureValuePair;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import config.project.ProjectConf;


public class KnowtatorUtils {

  // -----------------------------------------
  /**
   * Constructor: 
   *
   */
  // -----------------------------------------
  public KnowtatorUtils() {
    readProjectSchemaPreamble();
    
  } // end Constructor() ---------------------
  

  // -----------------------------------------
  /**
   * readProjectScemaPreamble reads in the
   * preamble for the eHost config/project.schema
   * 
   * The preamble is in a file that is in this
   * classpath called eHostProjectSchema.preamble
   * 
   * 
   */
  // -----------------------------------------
  private void readProjectSchemaPreamble() {
    
    if ( eHOSTProjectSchema == null ) {
      String resourceName = // "gov.nih.cc.rmd.nlp.framework.marshallers.knowtator.util.eHostProjectSchema.preamble";
      "gov/nih/cc/rmd/nlp/framework/marshallers/knowtator/util/eHostProjectSchema.preamble";
      try {                  
        
        eHOSTProjectSchema = U.readClassPathResource(resourceName);
      } catch (Exception e) {
        
        e.printStackTrace();
        
      }
    }
  }// end Method readProjectSchemaPreamble

  // -----------------------------------------
  /**
   * convert iterates through the jcas and converts
   * each annotation to the common model
   * 
   * @param pJCas
   * @return gov.va.vinci.knowtator.Document
   * @throws FileNotFoundException 
   */
  // -----------------------------------------
  public gov.va.vinci.knowtator.Document convert( JCas pJCas, HashSet<String> outputTypes, String pAnnotator ) throws FileNotFoundException {
   
   
    
    String                        documentName = VUIMAUtil.getDocumentId(pJCas);
    
    int                             documentId = mDocumentCtr++;
    gov.va.vinci.knowtator.Document  kDocument = new gov.va.vinci.knowtator.Document(documentName);
    ArrayList<gov.va.vinci.knowtator.Annotation> kAnnotations = null;
    ArrayList<gov.va.vinci.knowtator.ClassMention> kClassMentions =null;
    ArrayList<gov.va.vinci.knowtator.StringSlotMention> kStringSlotMentions = null;
    
    List<Annotation> annotations = UIMAUtil.getAnnotationsFromDocument(pJCas, Annotation.type);
    if ( annotations != null ) {
       kAnnotations = new ArrayList<gov.va.vinci.knowtator.Annotation>(annotations.size());
       kClassMentions = new ArrayList<gov.va.vinci.knowtator.ClassMention>();
       kStringSlotMentions = new ArrayList<gov.va.vinci.knowtator.StringSlotMention>();
    } 
    String documentText = pJCas.getDocumentText();
    int[] listOfLineNumbers = null;
    
    boolean containsCRs = false;
    
    String newLineKind = U.getNewlineType( documentText);
    if ( newLineKind.length() == 2 )
      containsCRs = true;
    
    if ( containsCRs )
    	listOfLineNumbers = getLineNumbers( documentText);
    
    if ( annotations != null ) {
      for ( Annotation annotation : annotations ) {
        String fullName = annotation.getType().getName();
        String name = U.getNameWithoutNameSpace(fullName);
        //String[] names = U.split(fullName, ".");
        //String name = names[ names.length -1];
    	 
   
        if ( outputTypes.contains(fullName) ||  outputTypes.contains( name ) ) {
 
          List<FeatureValuePair> featureValuePairs = UIMAUtil.getFeatureValuePairs( annotation);
          try {
            ((gov.va.chir.model.VAnnotation) annotation).setAnnotator(pAnnotator);         
          } catch ( Exception e) {
    		    
            try {
    		     // ((gov.va.vinci.model.Concept) annotation).setAnnotator(pAnnotator);      
            } catch ( Exception e2 ) {
    		      
            }
          }
    		  ClassMention aClassMention = convertVAnnotation  ( annotation, kAnnotations, kClassMentions, kStringSlotMentions, containsCRs, listOfLineNumbers );
    		  if ( featureValuePairs != null )
    		    for ( FeatureValuePair fvp : featureValuePairs )
    		      createAndAddFeature(aClassMention, fvp.getFeatureName(),    fvp.getFeatureValue(),   kStringSlotMentions);
        }
        
      
        
      } // end loop through annotations
    }
    if ( annotations != null  ) {
      kDocument.setAnnotations( kAnnotations );
      kDocument.setClassMentions( kClassMentions);
      kDocument.setStringSlotMentions( kStringSlotMentions);
    }
    return kDocument;   
  } // end Method convert() ------------------

//-----------------------------------------
  /**
   *getLineNumbers returns an array of the newline offsets
   *  
   * @param documentText
   * @return int[] 
   */
  // ----------------------------------------- 
private int[] getLineNumbers(String documentText) {
	
	char[] docChars = documentText.toCharArray();
	int[] lineNumber = new int[docChars.length];
	int nl = 0;
	for (int ic = 0; ic < docChars.length; ic++) {
		if ( docChars[ic] == '\n') 
			lineNumber[nl++]= ic;
	}
	int lineNumbers[] = new int[ nl];
	for (int i = 0; i < nl; i++ ) lineNumbers[i] = lineNumber[i];
	
	return lineNumbers;
} // end Method getLineNumbers() ----------

//-----------------------------------------
  /**
   * seekCRs scans the document to see if the line endings are \r\n
   *  
   * @param pdocumentText
   * @return boolean  
   */
  // -----------------------------------------
  private boolean seekCRs(String documentText) {
	boolean returnVal = false;
	
	char[] docChars = documentText.toCharArray();
	for (int i = 0; i < 1000; i++ ) {
		if ( docChars[i] == '\r') {
			returnVal = true;
			break;
		}
	}
	return returnVal;
} // end Method seekCRs() -----------------





  // -----------------------------------------
  /**
   * convertVAnnotaton creates a knowtator annotation 
   * with the name of the uima annotation label.  The feature gets put
   * on the set of features.
   * 
   * 
   * @param annotation
   * @return ClassMention 
   */
  // -----------------------------------------
  private ClassMention convertVAnnotation(Annotation pAnnotation,
					  ArrayList<gov.va.vinci.knowtator.Annotation>pAnnotations,
					  ArrayList<ClassMention>                     pClassMentions,
					  ArrayList<StringSlotMention>                pStringSlotMentions, 
					  boolean                                     subtractForCR_NL,
					  int[]                                       listOfNewLines
					  ) {
   
    gov.va.vinci.knowtator.Annotation kAnnotation = new gov.va.vinci.knowtator.Annotation();
    
    
    int begin = pAnnotation.getBegin();
    int  eend = pAnnotation.getEnd();
    String annotator = null;
    try {
      annotator = ((VAnnotation)pAnnotation).getAnnotator();
    } catch (Exception e) {
      try {
        //annotator = ((Concept)pAnnotation).getAnnotator();
      } catch (Exception e2) {
        annotator = "unknown";
      }
    }
      if ( subtractForCR_NL ) {
        begin = begin - whatLine( listOfNewLines, begin) ;
        eend = eend - whatLine( listOfNewLines, eend) ;
    }
    gov.va.vinci.knowtator.Span span = new Span( begin, eend );
    kAnnotation.setSpan( span);
    
    try {
      // if ( pAnnotation.getClass().getName().contains("Section")) 
       // System.err.println("here");
      String text = U.normalize(pAnnotation.getCoveredText());
      if (text != null && text.length() > 30) text = text.substring(0,19);
      
      kAnnotation.setContent( text);
      
      
    } catch (Exception e) {
      String id = ((VAnnotation)pAnnotation).getId();
      System.err.println("pAnnotation span is wrong from " + id );
      e.printStackTrace();
      System.err.println("Issue with getting the annotation " + e.toString());
      System.err.println("kind = " + pAnnotation.getClass().getName());
      System.err.println(pAnnotation.getBegin() + "|" + pAnnotation.getEnd());
      System.exit(-1);
    }
      
      String annotationIdString = "annotationId_" + String.valueOf( this.annotationId++ );
    kAnnotation.setAnnotationId(annotationIdString ); 
    kAnnotation.setCreationDate( mCurrentDate);
    kAnnotation.setAnnotator( annotator);
    
    String label = getAnnotationLabel(pAnnotation);
     
    ClassMention aClassMention = new ClassMention( annotationIdString, label);
    
    pAnnotations.add ( kAnnotation);
    pClassMentions.add(aClassMention);
    
    // -----------------------------------------------------------
    // I'm not passing along the parent annotation because 
    // it can be computed, and there is no knowtator use case for it  
    // -----------------------------------------------------------
  
    return aClassMention ;
  } // end Method convertVAnnotation() -------
  
// ---------------------------
  /**
   * whatLine returns the line count that this offset fits within
   * 
   *  @param listOfNewLines
   *  @param offset
   *  @return int  (the line number
   */
  // -------------------------
  private int whatLine(int[] listOfNewLines, int offset) {
	  int lineNumber = 0;
	  while ( (lineNumber< listOfNewLines.length ) && (offset >= listOfNewLines[lineNumber]))
		  lineNumber++;
	return lineNumber;
} // end Method whatLine() --------

// -----------------------------------------
  /**
   * createAndAddFeature will create a feature and add it to the
   * set of class and string slot mentions.
   * 
   * @param pClassMention
   * @param pName
   * @param pValue
   * @param pStringSlotMentions
   */
  // -----------------------------------------
  public void createAndAddFeature( ClassMention pClassMention,
				   String pName, 
				   String pValue,
				   ArrayList<StringSlotMention> pStringSlotMentions
				   ) {

    String slotMentionId = "slotMention_" + String.valueOf( mSlotMentionCtr++);
   
    StringSlotMention aSlotMention  = new StringSlotMention(pClassMention.getId(), pName, pValue);
    aSlotMention.setId(slotMentionId);
    pClassMention.addHasSlotMention(slotMentionId);
    pStringSlotMentions.add( aSlotMention);
    
  } // end Method createAndAddFeature() -----

  // -----------------------------------------
  /**
   * getAnnotationLabel retrieves the label from this annotaiton 
   *
   * @param pAnnotation
   * @return String
   */
  // -----------------------------------------
  private String getAnnotationLabel(Annotation pAnnotation) {
    
    String                label = pAnnotation.getType().getName();
    String[]        labelPieces = label.split("\\.");
    label = labelPieces[labelPieces.length -1];
    
    return ( label );
    
  } // end Method getAnnotationLabel() -----

  
  // -----------------------------------------
  /**
   * write writes out the knowtator document to the file specified.
   * 
   * @param pOutputFileName
   * @param pKnowtatorDocument 
   * @exception Exception
   */
  // -----------------------------------------
  public void write(String pOutputFileName, Document pKnowtatorDocument) throws Exception {
  
    PrintWriter out = null;
  
    try {
      out = new PrintWriter( pOutputFileName);
      
      SerializationServiceXmlImpl marcheller = new SerializationServiceXmlImpl();
      String docString = marcheller.serialize( pKnowtatorDocument);
      
    out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    out.println( docString);
    
    out.close();

  } catch ( Exception e) {
    e.printStackTrace();
    throw e;
  } // end catch
  
} // end Method write() ----------------------

 //-----------------------------------------
  /**
   * writeClassDefinitions creates a file with the class or label 
   * names with color assignments attache to them.
   * 
   * This file will go into the same directory where the 
   * .knowtator.xml files reside.  The file name will be
   * classname.cfg
   * 
   * @param outputFileName
   */
  // -----------------------------------------
  public void writeClassDefinitions(String eHostConfigDir, String[] pOutputTypes, JCas pJCas) throws ResourceInitializationException {
    
  
    try {
      
      
    TypeSystem typeSystem = pJCas.getTypeSystem();
    
    
    HashSet<String> outputTypeHash = createOutputTypeHash( pOutputTypes);
    
    
    String eHostCfgFile = eHostConfigDir + "/projectschema.cfg";
    PrintWriter out = new PrintWriter( eHostCfgFile);
    System.err.println("Writing out to " + eHostCfgFile );
   
   out.println( this.eHOSTProjectSchema);
   int colorCtr = 1;
   int colorR = 128;
   int colorG = 255;
   int colorB = 255;
   for ( Iterator<Type> i = typeSystem.getTypeIterator(); i.hasNext(); ) {
     Type aClass = i.next();
     String label = aClass.getName();
     
     if (( !label.startsWith( "uima")) && (!label.endsWith("[]"))) {
       String labels[] = label.split("\\.");
       label = labels[labels.length -1];
       
       if (( outputTypeHash == null ) || (outputTypeHash.contains( label )  ) ) {
         out.println("[CLASS]" );
         out.println(label);
     
         out.println("( " + colorR + ", " + colorG + ", " + colorB + " )" );
         out.println("v3NLP");
         out.println();
     
       switch ( colorCtr++ ) {
       case 1: colorR-=50; if ( colorR <=  0) colorR = 250; break;
       case 2: colorG+=50; if ( colorG >=250) colorG =   0; break;
       case 3: colorB+=50; if ( colorB >=250) colorB =   0; break;
       default: colorCtr = 1;
             colorG-=50; if ( colorG < 0) colorG =   0;
       }
       }
     }
     
   } // end Loop through types;
   
   out.close();
   
   // ---------------
   // convert this to xml via the ehost utility
   convertToXMLConfig( eHostCfgFile);
   
    } catch (Exception e) {
      e.printStackTrace();
      throw new ResourceInitializationException( e);
    }
  } // end Method writeClassDefinitions() ----

  
  
  // =======================================================
/**
 * convertToXMLConfig converts the old style ehost config file
 * to xml based file that is current used.
 * 
 * @param pEHostCfgFile
 */
// =======================================================
private void convertToXMLConfig(String pEHostCfgFile) {
  

  File eHostCfgFile = new File( pEHostCfgFile) ;
  ProjectConf projectconf = new ProjectConf( eHostCfgFile );
    
  projectconf.loadConfigure();
  projectconf.rename();
  
  
} // End Method convertToXMLConfig() ======================



  /**
   * createOutputTypeHash 
   * @param pOutputTypes
   * @return HashSet
 */
 public HashSet<String> createOutputTypeHash(String[] pOutputTypes) {
  HashSet<String> outputTypeHash  = null;
  if ((pOutputTypes != null) && (pOutputTypes.length > 0)) {
    outputTypeHash = new HashSet<String>();
    for (int i = 0; i < pOutputTypes.length; i++) {
      outputTypeHash.add(pOutputTypes[i]);
    }
  }
  return outputTypeHash;
}



  // -----------------------------------------
  // Global Variables
  // -----------------------------------------
  private int annotationId = 0;
  private int mDocumentCtr = 0;
  private int mSlotMentionCtr = 0;
  private String mCurrentDate = null;
  private static String eHOSTProjectSchema  = null;
   
    
    //";Configure file for storage of annotaion classnames" + U.NL + ";Words surround with character '[' and ']' are annotation classnames." + U.NL + ";Line below each annotation classname is attribute of this annotation classname.";
  
  
} // end Class VinciNLPFramework_UIMA_Knowtator();
