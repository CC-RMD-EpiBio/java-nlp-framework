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
 * FromKnowtatorSimple assumes that the the source file has already
 * been read in via a collection reader. This class will read in the
 * knowtator annotations and create uima annotations.
 * 
 * This has been broken down into a collection reader which reads in
 * the source text and an annotator which reads in the knowtator annotation
 * because when these two operations were combined, the annotations would
 * not show up.
 * 
 * 
 * @author Guy Divita
 * @created Mar 1, 2011
 * 
 *          *
 *          *
 *          *
 *          *
 *          *
 *          *
 *          *
 *          *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.knowtator;

import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.VAnnotation;
import gov.va.vinci.knowtator.ClassMention;
import gov.va.vinci.knowtator.ComplexSlotMention;
import gov.va.vinci.knowtator.HasSlotMention;
import gov.va.vinci.knowtator.MentionSlot;
import gov.va.vinci.knowtator.StringSlotMention;
import gov.va.vinci.knowtator.StringSlotMentionValue;
import gov.va.vinci.knowtator.service.KnowtatorReader;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

public class FromKnowtator extends gov.nih.cc.rmd.nlp.framework.marshallers.reader.Reader {




  // =======================================================
  /**
   * Constructor FromKnowtator 
   *
   */
  // =======================================================
  public FromKnowtator()  {
    
  }
   
  
	 // =======================================================
  /**
   * Constructor FromKnowtator 
   *
   * @param pLabelMap  (can be null )
   * @param pFixOffsets
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public FromKnowtator(String[] pLabelMap, boolean pFixOffsets) throws ResourceInitializationException {
   
    initialize( pLabelMap, pFixOffsets);
    
  } // end Constructor() --------------------

  // =======================================================
  /**
   * Constructor FromKnowtator 
   *
   * @param pInputDir
   * @param pRecurse
   * @param pFixOffsets
   * @param pKnowtatorLabels
  
   * @throws ResourceInitializationException 
   */
  // =======================================================
  public FromKnowtator( String pInputDir, boolean pRecurse, boolean pFixOffsets, String pKnowtatorLabels ) throws Exception {
      
    initialize(  pInputDir,  pRecurse,  pFixOffsets, pKnowtatorLabels );
    
  } // end Constructor() --------------------
  
  
  
  
  
	  // -----------------------------------------
	  /**
	   * process reads in knowtator annotations and creates
	   * uima annotations for them.
	   * 
	   * This method assumes that the knowtator annotations file
	   * is named the same way the source file was named. The
	   * location of the path to the annotations file is gotten
	   * from a context variable: annotationsDir
	   * 
	   * 
	   * 
	   * @param aJCas
	   */
	  // -----------------------------------------
	  public void process(JCas pJCas) throws AnalysisEngineProcessException {

		 process1( pJCas);
		 
		 checkForDuplicateAnnotations(pJCas);
		 
		 
	 } // end Method Process()
	 
	 
  private void checkForDuplicateAnnotations(JCas pJCas) {
		
	  // ------------------------------
	  // Make a list of each id (if not null
	  // drop the second one
	  
	  List<Annotation> annotations = UIMAUtil.getAnnotations(pJCas);
	
	  
	  if ( annotations != null ) {
		  HashSet<String> ids = new HashSet<String>();
		  ArrayList<Annotation> candidates = new ArrayList<Annotation>(annotations.size());
		  for ( Annotation annotation : annotations ) {
			  try {
				  
				 
				  String v = ((VAnnotation) annotation).getId();
				  if ( v != null ) {
				
					  candidates.add( annotation);
				  
				  }
			  } catch (Exception e ){}
		  } // end loop through annotations

		  for ( Annotation candidate: candidates ) {
			  
			  String key = ((VAnnotation)candidate).getId();
			  if ( key != null ) {
				 if (  ids.contains(key) ) {
					 candidate.removeFromIndexes();
					 // System.err.println("Removing " + key  + " from index");
				 } else {
					 ids.add(key);
				 }
			  } // if the id isn't a null key
				  
			  
		  } // end loop through canidates
		  
	  } // end if there are annotations
	} // end Method checkForDuplicateAnnotations()


// -----------------------------------------
  /**
   * process reads in knowtator annotations and creates
   * uima annotations for them.
   * 
   * This method assumes that the knowtator annotations file
   * is named the same way the source file was named. The
   * location of the path to the annotations file is gotten
   * from a context variable: annotationsDir
   * 
   * 
   * 
   * @param aJCas
   */
  // -----------------------------------------
  public void process1(JCas pJCas) throws AnalysisEngineProcessException {

    // -----------------
    // Figure out the annotations file name
    // -----------------
	//  System.err.println(" hereee");
    String sourceFullFileName = "charlie";
    try {
      sourceFullFileName = VUIMAUtil.getDocumentId(pJCas);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException("someting wrong in getInputFileName " + e.toString());
    }

    if (sourceFullFileName == null) throw new RuntimeException("No file name attached to the cas!");

    File sourceFullFile = new File(sourceFullFileName);

    String sourceFileName = sourceFullFile.getName();
    String sourceBaseName = U.getFileNamePrefix(sourceFileName);

    String annotationFileName = this.inputAnnotationDir + "/" + sourceBaseName + this.extension;

    File annotationFile = new File(annotationFileName);

    try {
      
      fromKnowtator( pJCas, sourceFullFile, annotationFile);
      
    } catch (Exception e2) {
      String msg = e2.toString() + "\n" + U.getStackTrace(e2);
      throw new RuntimeException(msg);
    }

  } // end Method process() ------------------

  // =======================================================
/**
 * fromKnowtator returns annotations in the jcas from the knowtator file(s)
 * 
 * @param pJCas
 * @param sourceFullFile
 * @param annotationFile
 * @throws Exception 
 */
// =======================================================
public void fromKnowtator(JCas pJCas, File sourceFullFile, File annotationFile) throws Exception {
  gov.va.vinci.knowtator.Document kDoc = KnowtatorReader.read(sourceFullFile, annotationFile);

  int newLineOffsetList[] = U.getNewlineOffsets(kDoc.getTextSource());
  int oldNewLineOffsetList[] = fixNewLines(newLineOffsetList);

  // -----------------------------------------------------
  // Iterate through the knowtator annotations
  // -----------------------------------------------------
  List<gov.va.vinci.knowtator.Annotation> kAnnotations = kDoc.getAnnotations();
  List<gov.va.vinci.knowtator.ClassMention> kClassMentions = kDoc.getClassMentions();
  List<gov.va.vinci.knowtator.StringSlotMention> kStringSlotMentions = kDoc.getStringSlotMentions();
  List<gov.va.vinci.knowtator.ComplexSlotMention> kComplexSlotMentions = kDoc.getComplexSlotMentions();

  // --------------------------------------------
  // Create a knowtator annotation hash, key'd on the mention id
  // --------------------------------------------
  Hashtable<String, gov.va.vinci.knowtator.Annotation> kAnnotationHash = createKnowtatorAnnotationHashFromList(kAnnotations);
  Hashtable<String, gov.va.vinci.knowtator.StringSlotMention> kSlotMentionHash = createKnowtatorSlotMentionHashFromList(kStringSlotMentions);
  Hashtable<String, gov.va.vinci.knowtator.ComplexSlotMention> kComplexSlotMentionHash = createKnowtatorComplexSlotMentionHashFromList(kComplexSlotMentions);

  // --------------------------------------------
  // Index classMentions (UIMA Annotation labels), and tie them back to the
  // spans
  // --------------------------------------------
  gov.va.vinci.knowtator.Annotation kAnnotation = null;
  for (ClassMention cm : kClassMentions) {
    String annotationId = cm.getId();
    String label = cm.getMentionClass().getId();
    kAnnotation = kAnnotationHash.get(annotationId);
    if ( kAnnotation == null ) {
      System.err.println("issue with can't find the kannotation for annotation id " + annotationId);
    } else {
    // ----------------------------------------------
    // If you know before hand the mapping between
    // the knowtator label and the type descriptor
    // use it
    // ----------------------------------------------
    String knownLabel = this.knowtatorToUIMAMapping.get(label);

    Class<?> uimaLabelClass = null;
    if (knownLabel != null) uimaLabelClass = UIMAUtil.mapLabelToUIMAClass(knownLabel);
    else
    // ----------------------------------------------
    // Map this label to a uima class
    // ----------------------------------------------
    uimaLabelClass = UIMAUtil.mapLabelToUIMAClass(label);

    // ----------------------------------------------
    // create a UIMA annotation from this label
    // ----------------------------------------------
    try {

      Constructor<?> c = uimaLabelClass.getConstructor(new Class[] { JCas.class });
      Object uimaAnnotation = c.newInstance(pJCas);
      

      // --------------------------------------------------
      // Add span and slot attributes to the uimaAnnotation
      // --------------------------------------------------
      try {
        ((VAnnotation) uimaAnnotation).setId(annotationId);
        //VUIMAUtil.setProvenance(pJCas, (VAnnotation) uimaAnnotation, this.getClass().getName());
       // System.err.println("Set provenance = " +  UIMAUtil.stringArrayToString(((VAnnotation) uimaAnnotation).getProvenance()));
        // System.err.println("Set id = " +  ((VAnnotation) uimaAnnotation).getId());
        
        
      } catch (Exception e) { 
        // e.printStackTrace(); 
        //System.err.println("issue here " + e.toString());
      };
      if ( kAnnotation.getSpan() == null ) {
        System.err.println("No span for id :" + annotationId + "|" + kAnnotation.getSpannedText());
        continue;
      }
      int beginOffset = kAnnotation.getSpan().getStart();
      try {
       beginOffset = fixOffset(kAnnotation.getSpan().getStart(), oldNewLineOffsetList);
      } catch (Exception e2 ) {
        e2.printStackTrace();
       
        System.err.println("Issue with fixing the offset " + "|" + oldNewLineOffsetList.length + "|" + kAnnotation.getSpan());
        throw new RuntimeException();
      }
      int endOffset = beginOffset + kAnnotation.getSpannedText().length();
     // System.err.println("Begin span = " + beginOffset + " endSpan = " + endOffset + "|" + kAnnotation.getSpannedText() + "|");
      ((org.apache.uima.jcas.tcas.Annotation) uimaAnnotation).setBegin(beginOffset);
          
      ((org.apache.uima.jcas.tcas.Annotation) uimaAnnotation).setEnd(endOffset);

      
      

      ((org.apache.uima.jcas.tcas.Annotation) uimaAnnotation).addToIndexes(pJCas);
      // System.err.println("created annotation for " + ((org.apache.uima.jcas.tcas.Annotation) uimaAnnotation).getType().getName());

      // ----------------------------------------------------
      // find the features(slots/slotMentions) associated with this
      // annotation(classMention)
      // -----------------------------------------------------
      ArrayList<HasSlotMention> slotMentions = cm.getHasSlotMentions();

      for (HasSlotMention slotMention : slotMentions) {
        StringSlotMention mention = kSlotMentionHash.get(slotMention.getId());
        if (mention == null) {

          // --------------------------------------------------
          // see if there are any complex slot mentions - these are
          // relationships to other concepts - Convert these to attributes 
          //  i.e. negation or assertion ConceptConvert any relationships to attributes
          // --------------------------------------------------
          ComplexSlotMention mentionX = kComplexSlotMentionHash.get( slotMention.getId());
          MentionSlot mentionSlot = mentionX.getMentionSlot();
          String      featureName = mentionSlot.getId();
          
          // ----------------------------------- not needed 
          //  val = mentionX.getComplexSlotValue();
          // if (val != null) {
          //  String featureValue = val.getValue();
          // } ----------------------------------
          
          // ----------------------------
          // Right now, we've only got one relationship mapping - I cannot
          // think of a generic way of mapping it other than hard coding it
          // featureName = "Negation_Yes" <==> uima attribute = NegationStatus with a value of "Negated"
          if ( featureName.equals("Negation_Yes")) {
            try {
            ((VAnnotation) uimaAnnotation).setNegation_Status("Negated");
            } catch (Exception e) {
              try {
                ((gov.va.vinci.model.Concept) uimaAnnotation).setAssertionStatus("Negated");
              } catch (Exception e2) {
                this.log.warn("Missed a negation relationship");
              }
            }
          }
          
          
          
          
          // System.err.println("Missing a mention for " + slotMention.getId());
        } else {
          // String mentionId = mention.getId();
          MentionSlot mentionSlot = mention.getMentionSlot();
          String featureName = mentionSlot.getId();
          StringSlotMentionValue val = mention.getStringSlotMentionValue();
          if (val != null) {
            String featureValue = val.getValue();

            // --------------------------------------------------------------
            // Is there a generic way to find out if this feature is a
            // class?
            // --------------------------------------------------------------
            Method uimaSetFeatureMethod = UIMAUtil.mapFeatureToUIMAFeature(uimaLabelClass, featureName);
            if (uimaSetFeatureMethod != null) try {
              if (featureValue != null) uimaSetFeatureMethod.invoke(featureValue, 0);
            } catch (Exception e) {}

            
            
          } 
        }
        
        
        
        
        //((org.apache.uima.jcas.tcas.Annotation) uimaAnnotation).addToIndexes(pJCas);

      } // end loop through the attributes/slots/features of this
        // annotation/classMention
      
    } catch (Exception e) {
      String msg = e.toString() + "\n" + U.getStackTrace(e);
      throw new RuntimeException(msg);
    }
    }
  }
     
}  // End Method fromKnowtator() ======================



  private int[] fixNewLines(int[] newLineOffsetList) {

    int oldNewLines[] = new int[newLineOffsetList.length];

    for (int i = 0; i < newLineOffsetList.length; i++)
      oldNewLines[i] = newLineOffsetList[i] - i;
    return oldNewLines;
  }

  // -----------------------------------------
  /**
   * fixOffset will add X to the offset to fix an issue
   * where eHOST strips \r's off the text, when computing the offsets.
   * 
   * @param pOffset
   * @param newLineOffsetList
   * @return
   */
  // -----------------------------------------
  private int fixOffset(int pOffset, int[] newLineOffsetList) {
    int newOffset = pOffset;

    if (this.fixOffsets == true) {
      int lineNumber = getLineNumber(pOffset, newLineOffsetList);
      newOffset += (lineNumber);
    }

    return newOffset;
  } // end Method fixOffset() -------------------------


//=======================================================
/**
* fixIssueWithOrphanCarrageReturn looks for the first orphan carriage return 
* (\r)   without at newline (\n)
* 
* It adds a newline before the \r
* 
* 
* @param documentText
* @return
*/
//=======================================================
private String fixIssueWithOrphanCarriageReturn(String pBuff) {
 
 StringBuffer buff = new StringBuffer();
 
 int a = pBuff.indexOf("\r\n");
 int b = pBuff.indexOf("\r");
 int c = pBuff.indexOf("\n");
 
 if ( a == b && b + 1 == c )
   buff = buff.append(pBuff);
 else if ( a > b ) {
   String x = pBuff.substring(0, b +1);
   String p = pBuff.substring(b+1, pBuff.length());
   buff.append(x + '\n' + p);
 } else {
   buff = buff.append(pBuff);
 }
 
 return buff.toString();
 
 
}  // End Method fixIssueWithOrphanCarrageReturn() ======================


  
  // -----------------------------------------
  /**
   * getLineNumber returns the line number given an offset,
   * and a list of newline offsets. First line is 1.
   * 
   * @param pOffset
   * @param newLineOffsetList
   * @return int
   */
  // -----------------------------------------
  private int getLineNumber(int pOffset, int[] newLineOffsetList) {
    int lineNumber = 0;
    for (int i = 0; i < newLineOffsetList.length; i++)
      if (newLineOffsetList[i] > pOffset) {
        lineNumber = i;
        break;
      }
    // int lineNumber2 = Math.abs(Arrays.binarySearch(newLineOffsetList, pOffset));

    return lineNumber;
  } // end Method getLineNumber() ----------------

  // ----------------------------------------------
  /**
   * createKnowtatorAnnotationHashFromList creates a hash of annotations
   * key'd on mention ids
   * 
   * @param kAnnotations
   * @return Hashtable
   */
  // ----------------------------------------------
  private Hashtable<String, gov.va.vinci.knowtator.Annotation> createKnowtatorAnnotationHashFromList(
      List<gov.va.vinci.knowtator.Annotation> kAnnotations) {

    Hashtable<String, gov.va.vinci.knowtator.Annotation> hash = new Hashtable<String, gov.va.vinci.knowtator.Annotation>(
        kAnnotations.size() * 2);

    for (gov.va.vinci.knowtator.Annotation annotation : kAnnotations)
      hash.put(annotation.getMention().getMentionId(), annotation);

    return hash;
  } // end Method createKnowtatorAnnotationHashFromList() ---

  // ----------------------------------------------
  /**
   * createKnowtatorSlotMentionsHashFromList creates a hash of annotations
   * key'd on mention ids
   * 
   * @param kAnnotations
   * @return Hashtable
   */
  // ----------------------------------------------
  private Hashtable<String, gov.va.vinci.knowtator.StringSlotMention> createKnowtatorSlotMentionHashFromList(
      List<gov.va.vinci.knowtator.StringSlotMention> kStringSlotMentions) {

    Hashtable<String, gov.va.vinci.knowtator.StringSlotMention> hash = new Hashtable<String, gov.va.vinci.knowtator.StringSlotMention>(
        kStringSlotMentions.size() * 2);

    for (gov.va.vinci.knowtator.StringSlotMention stringSlotMention : kStringSlotMentions)
      hash.put(stringSlotMention.getId(), stringSlotMention);

    return hash;
  } // end Method createKnowtatorAnnotationHashFromList() ---

  // ------------------------------------------
  /**
   * createKnowtatorComplexSlotMentionHashFromList
   * 
   * 
   * @param kComplexSlotMentions
   * @return
   */
  // ------------------------------------------
  private Hashtable<String, ComplexSlotMention> createKnowtatorComplexSlotMentionHashFromList( List<ComplexSlotMention> kComplexSlotMentions) {
   
    Hashtable<String, gov.va.vinci.knowtator.ComplexSlotMention> hash = new Hashtable<String, gov.va.vinci.knowtator.ComplexSlotMention>(
        kComplexSlotMentions.size() * 2);

    for (gov.va.vinci.knowtator.ComplexSlotMention complexSlotMention : kComplexSlotMentions)
      hash.put(complexSlotMention.getId(), complexSlotMention);

    return hash;
  } // End Method createKnowtatorComplexSlotMentionHashFromList() -----------------------

  // ----------------------------------
  /**
   * initialize initializes the FromKnowtator annotator. This annotator is looking for
   * the following descriptor parameters
   * inputDir (the dir where the input text files are
   * annotationInputDirectory (the dir where the knowtator annotation files are)
   * knowtatorFileExtension (usually .knowtator.xml) <- no longer an option.
   * knowtatorLabelMap pairs of knowtatorLabel|uimaLabel if known beforehand. These are optional. 
   *    These pairs are delimited by a :
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {

    if (aContext != null) 

   
    try {

      this.inputAnnotationDir = (String) aContext.getConfigParameterValue("annotationInputDirectory");
      this.extension = ".knowtator.xml";
      String knowtatorLabelMaps = (String) aContext.getConfigParameterValue("knowtatorLabelMap");
      String[] knowtatorLabelMap = U.split(knowtatorLabelMaps,":");
      
      
      boolean _fixOffsets = ((Boolean) aContext.getConfigParameterValue("fixOffsets")).booleanValue();

      initialize( knowtatorLabelMap, _fixOffsets);
      

    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue with initializing the FromKnowtator Reader " + e.toString();
      System.err.println(msg);
      this.log.error( msg);
      throw new ResourceInitializationException(e);
    }

  } // end Method initialize() -------
  



//-----------------------------------------
/** 
* initialize reads the directory containing the input files.  By default, this
* recurses through sub directories. By default, this initialize sets fixOFfsets to false.
* 
* @param pInputDir
* @throws ResourceInitializationException

*/
//-----------------------------------------
public void initialize(String pInputDir ) throws ResourceInitializationException  {

initialize( pInputDir, true, false, (String) null);

} // End Method initialize() ---------------------------


//=======================================================
/**
 * initialize 
 * 
 * @param pArgs    assumes there is the arg --inputDir= 
 * @throws ResourceInitializationException
 *
 */
// ======================================================
public void initialize(String[] pArgs) throws ResourceInitializationException {
 
 
  try {
 
    String inputDir = U.getOption(pArgs, "--inputDir=", "./");
    boolean recurseIntoSubDirs = Boolean.parseBoolean(U.getOption(pArgs,  "--recurseIntoSubDirs=", "true"));
    boolean fixOffsets = Boolean.parseBoolean(U.getOption(pArgs,  "--fixOffsets=", "false"));
    String knowtatorLabels = U.getOption(pArgs, "--knowtatorLabels=", "");
    
    initialize( inputDir, recurseIntoSubDirs, fixOffsets, knowtatorLabels);
   
   } catch ( Exception e ) {
     e.printStackTrace();
     System.err.println("Issue within method initialize " + e.getMessage() );
     throw e;
   }
 } // End Method initialize ============


//-----------------------------------------
/** 
 * initialize opens the directory containing .knowtator.xml files
 * 
 *  The inputDir should point to the directory ABOVE the ./saved and ./corpus directories.
 *
 * @param pInputDir
 * @param recurseIntoSubDirs
 * @param pFixOffsets
 * @param pKnowtatorLabelsString
 * 
 * @exception ResourceInitializationException
 
 * 
 */
// -----------------------------------------
public void initialize(String pInputDir, boolean recurseIntoSubDirs, boolean pFixOffsets, String pKnowtatorLabelsString ) throws ResourceInitializationException  {
  
  
  String[] knowtatorLabelMap = parseKnowtatorLabelsString( pKnowtatorLabelsString);
  
  this.knowtatorToUIMAMapping = new HashMap<String, String>();
  if (knowtatorLabelMap != null && knowtatorLabelMap.length > 0  ) {
    for (String aMap : knowtatorLabelMap) {
      if ( aMap != null ) {
        String[] buff = U.split(aMap);
        this.knowtatorToUIMAMapping.put(buff[0], buff[1]);
      }
    }
  }
  
  
  
  File inputDir = null;
  this.listOfFilesToProcess = new ArrayList<File>();
  this.fixOffsets = pFixOffsets;
  
  if ( pInputDir != null && pInputDir.length() > 0 ) {
    
    String savedDir = pInputDir + "/saved";
    
    inputDir = new File ( savedDir );
    
    if ( inputDir.isDirectory() && inputDir.canRead() ) {
      
      File[] filesAndSubDirs = inputDir.listFiles();
        
      if ( recurseIntoSubDirs ) {
         getFiles(this.listOfFilesToProcess, filesAndSubDirs );
      } else {
        this.listOfFilesToProcess = filterFiles( filesAndSubDirs) ;
      }
    } // end if the inputDir can be read                
  } // end if the inputDir is not null
  
  
  this.numberOfFiles = this.listOfFilesToProcess.size();
  this.setCatalog(this.numberOfFiles);
  System.err.println("The number ofiles to process = " + this.numberOfFiles);
  

       
} // end Method initialize() --------------
  


// =======================================================
/**
 * parseKnowtatorLabelsString takes xxx:xxx|yyy:yyy|zzz:zzz into an array [xxx:xxx, yyy:yyy, zzz:zzz ]
 * 
 * @param knowtatorLabelsString
 * @return
 */
// =======================================================
private String[] parseKnowtatorLabelsString(String knowtatorLabelsString) {

  String returnValue[] = null;
  if ( knowtatorLabelsString != null && knowtatorLabelsString.length() > 0 )
    returnValue = U.split(knowtatorLabelsString);
  
  return returnValue;
  // End Method parseKnowtatorLabelsString() ======================
}


//----------------------------------
 /**
  * initialize initializes the FromKnowtator annotator.
  *
  * knowtatorLabelMap pairs of knowtatorLabel|uimaLabel if known beforehand. These are optional.
  * 
  *  This is an annotator initialize() method
  * 
  **/
 // ----------------------------------
 public void initialize(String[] knowtatorLabelMap, boolean pFixOffsets) throws ResourceInitializationException {

  
     this.fixOffsets = pFixOffsets;

     this.knowtatorToUIMAMapping = new HashMap<String, String>();
     if (knowtatorLabelMap != null) {
       for (String aMap : knowtatorLabelMap) {
         String[] buff = U.split(aMap);
         this.knowtatorToUIMAMapping.put(buff[0], buff[1]);
       }
     }

 } // end Method initialize() -------
  

//=======================================================
/**
* filterFiles returns a list of text files
* 
* @param pFiles
* @return List<File>
*/
// =======================================================
private List<File> filterFiles(File[] pFiles) {

 ArrayList<File> filteredFiles = new ArrayList<File>(pFiles.length);
 for ( File file: pFiles )
   if ( filterInKnowtatorFiles( file))
     filteredFiles.add( file);
   
 return filteredFiles;
}  // End Method filterFiles() ======================


// =======================================================
/**
 * filterInKnowtatorFiles returns true if the file is a knowtator file
 * 
 * @param pFile
 * @return boolean
 */
// =======================================================
private boolean filterInKnowtatorFiles(File pFile) {
 
  boolean returnValue = false;
  if ( pFile.getName().endsWith(".knowtator.xml") ) 
      returnValue = true;
    
  return returnValue;
  
}  // End Method filterInKnowtatorFiles() ======================


// =======================================================
/**
* getFiles retrieves the list of files from the directory
* that match the filtering criteria
* 
* @param pListOfFiles
* @param pFilesAndSubDirs
* 
*/
// =======================================================
private void  getFiles(List<File> pListOfFiles, File[] pFilesAndSubDirs) {
 
 for ( File file: pFilesAndSubDirs )
   if ( file.isDirectory()) {
     getFiles( pListOfFiles, file.listFiles());
   } else if ( filterInKnowtatorFiles ( file )){
    // System.err.println("adding file " + file.getAbsolutePath());
     pListOfFiles.add( file);
   }
 
}   // End Method getFiles() ======================




  // -------------------------------------------
  // Class Variables
  // -------------------------------------------

  private Logger                                     log = null;
  private String                  inputAnnotationDir     = null;
  private String                  extension              = "txt.knowtator.xml";
  private HashMap<String, String> knowtatorToUIMAMapping = null;
  private boolean                 fixOffsets             = true;
  public static final String PARAM_INPUTFILE = "inputFile";
  private List<File>        listOfFilesToProcess = null;
  private int                        fileCounter = 0;
  private int                      numberOfFiles = 0;
  private boolean knowtatorDebug = true;
  
//-----------------------------------------
/** 
* getNext retrieves the next knowtator file, finds the text file it came from
* reads both, converts the annotations in the knowtator file to uima annotations.
* 
* @param pCAS
* @throws IOException
* @throws CollectionException
*

*/
//-----------------------------------------
@Override
public void getNext(CAS pCAS) throws IOException, CollectionException {

File corpusFile = null;
JCas     jcas = null;  
try {
  jcas = pCAS.getJCas();
  File  savedFile = this.listOfFilesToProcess.get(this.fileCounter);

  if ( this.knowtatorDebug ) System.err.println("Processing " + savedFile.getName());

  
  try {
    corpusFile = getTextFile( savedFile);
  } catch (Exception e) {
    e.printStackTrace();
    throw new IOException(e.toString());
  }
  
  
  String documentId    = corpusFile.getName();
  String documentURI   = documentId;
  String documentText  = U.readFile(corpusFile);
  String documentTitle = "unknown";
  String documentType  = "unknown";
  String documentName  = documentId; 
  int documentSpan     = documentText.length();
  String patientID     = "unknown";
  String referenceDate = "unknown";
  String metaData      = "unknown";
 
  documentText = fixIssueWithOrphanCarriageReturn(documentText);
  VUIMAUtil.setDocumentText(jcas, documentType, documentTitle, referenceDate, documentText, false);
  
  createDocumentHeaderAnnotation( jcas, documentId, documentTitle, documentType, documentName, documentSpan, patientID, referenceDate, metaData );

  
  // --------------------------------------
  // Add to the cas annotations from the knowtator file

  try {
     fromKnowtator( jcas, corpusFile, savedFile);
     
     
  } catch (Exception e2) {
    e2.getStackTrace();
    String msg = "Issue converting the knowtator file " + savedFile + " " + e2.toString();
    System.err.println(msg);
    throw new IOException(msg);
  }

} catch (Exception e ) {
  e.printStackTrace();
  String msg = "Issue creating a cas  " + " " + e.toString();
  System.err.println(msg);
  throw new CollectionException();

}
 
 
// --------------------------
// increment the fileCounter
this.fileCounter++;

} // end Method getNext() -----------------------







  /* (non-Javadoc)
   * @see gov.nih.cc.rmd.nlp.framework.marshallers.reader.Reader#hasNext()
   */
  @Override
  public boolean hasNext()  {
    boolean returnValue = false;
    

    if (this.fileCounter < this.numberOfFiles ) 
      returnValue = true;
    
     return returnValue;
    // End Method hasNext() ======================
  }


//=======================================================
/**
* getTextFile retrieves the text file associated with
* the knowtator file.  This is done by replacing the
* "saved" part of the path with "corpus"
* 
* @param aFile
* @return
* @throws Exception 
*/
//=======================================================
private File getTextFile(File aFile) throws Exception {

 File returnVal = null;
 
 String textPath = aFile.getAbsolutePath().replace("/saved/", "/corpus/");
 textPath = textPath.replace("\\saved\\", "\\corpus\\");
 textPath = textPath.replace(".knowtator.xml", "");
 
 
 returnVal = new File( textPath);
 
 if ( !returnVal.canRead())
   throw new Exception ("Cannot read the file that should be at " + textPath);
 
 return returnVal;
}  // End Method getTextFile() ======================


//------------------------------------------
/**
* createDocumentHeaderAnnotation
*
*
* @param pJCas
* @param pDocumentId
* @param pDocumentTitle
* @param pDocumentType
* @param pDocumentName
* @param pDocumentSpan
* @param pPatientId
* @param pReferenceDate,
* @param pDocumentMetaData
*/
//------------------------------------------
private void createDocumentHeaderAnnotation(JCas   pJCas, 
                                          String pDocumentId, 
                                          String pDocumentTitle, 
                                          String pDocumentType,
                                          String pDocumentName,
                                          int    pDocumentSpan,
                                          String pPatientId,
                                          String pReferenceDate,
                                          String pDocumentMetaData) {

DocumentHeader documentHeader = new DocumentHeader(pJCas);

documentHeader.setDocumentId( pDocumentId);
documentHeader.setDocumentName( pDocumentName);
documentHeader.setBegin( 0 );
documentHeader.setEnd( 1);
documentHeader.setDocumentType( pDocumentType);
documentHeader.setDocumentTitle( pDocumentTitle);
documentHeader.setOtherMetaData( pDocumentMetaData);
documentHeader.setPatientID( pPatientId);
documentHeader.setReferenceDate(pReferenceDate);    
documentHeader.addToIndexes(pJCas);


}  // End Method createDocumentHeaderAnnotation() -----------------------


//-----------------------------------------
/** 
* getProgress is method required for the 
* interface that is populated with the 
* fraction of files processed by the number of files to process.
*
* @return Progress[]
*/
//-----------------------------------------
@Override
public Progress[] getProgress() {
Progress[] p = new Progress[] { new ProgressImpl(this.fileCounter, this.numberOfFiles, Progress.ENTITIES)};
return p;

} // end Method getProgress() --------------


} // end Class FromKnowtatorSimple() -------------
