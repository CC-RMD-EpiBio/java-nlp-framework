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
 * IndexWriter writes out rows of Annotation X into a pipe delimited file for the corpus
 * This writer opens a file at the beginning of processing, writes to it when it comes
 * across the annotation(s) to index, then closes the file upon destroy() method.
 * 
 * This writer relies upon three parameters:
 *   labels:     a required array of annotation labels.  Each label needs to be the full namespace label.
 *   DELIMITER:  an optional character to delimit between fields.  By default this is a pipe, because
 *               there is an assumption that pipes shouldn't appear within text. (This is a false assumption)
 *               One can make the delimiter to be a comma.  The caveat is that there is no attempt 
 *               within this code to quote or backslash string values to make sure that commas within
 *               the values don't mess up the fields. 
 *  indexFileName: This is the file to deposit the rows into.  This needs to be an absolute path to insure
 *                 that one makes sure this file is placed in a folder that doesn't violate VINCI patient
 *                 privacy policy.  
 *                 
 *  Each record will include the following fields 
 *  
 *    docId|patientId|begin|end|Label|annotationId|provenance|displayString| .....
 * 
 * The fields that follow are dependent upon what kind of label this from.  Only the values are
 * displayed, not the field name.  You ask why not the field name.  This is an index. If you are
 * creating an index from this output, you'll figure out what are the fields from the annotations 
 * that are relevant to you.
 * 
 * 
 *        
 * @author  Guy Divita 
 * @created March 20, 2015
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;


import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Logger;

import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.VAnnotation;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class IndexWriter extends JCasAnnotator_ImplBase {
 
  
  // ------------------------------------------
  /**
   * process
   *
   *
   * @param pJCas
   * @param outputFile
   * @return
   */
  // ------------------------------------------
  public int process(JCas pJCas, PrintWriter outputFile) {
    // TODO Auto-generated method stub
    return 0;
    
    
    // End Method process() -----------------------
  }


  // -----------------------------------------
  /**
   * process 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    if ( this.labelTypes == null )
      this.labelTypes = initializeTypes( pJCas);
	   
    
    String     docId = null;
    String   docName = null;
    String patientId = null;
    DocumentHeader docHeader = VUIMAUtil.getDocumentHeader(pJCas);
    if ( docHeader != null ) {
      docId     = docHeader.getDocumentId();
      docName   = docHeader.getDocumentName();
      // patientId = docHeader.getPatient(); 
    }
    
    // ------------------------------------
    // Gather the index-able annotations
    // ------------------------------------
    
    List<Annotation>    someAnnotations = null;
    List<Annotation>   indexAnnotations = new ArrayList<Annotation>();
    if ( this.labelTypes != null )
    for ( Type aType : this.labelTypes )
    {    
      someAnnotations = UIMAUtil.getAnnotations(pJCas, aType);
      if (( someAnnotations != null ) && ( someAnnotations.size() > 0) ) {
        indexAnnotations.addAll(someAnnotations );
      }
    } 
    
    // Loop through the annotations to index, create a row, one per annotation
    for ( Annotation annotation : indexAnnotations ) {
        try {
          addToIndex( pJCas, docId, patientId, annotation);
        } catch (FileNotFoundException e) {
          
          e.printStackTrace();
          throw new RuntimeException( "Not able to create an inde row from annotation " + annotation.getType().getName() + " " +  e.toString());
        }
    } // end Loop thru annotations
    
  } // end Method process() ----------------
  
  
  // -----------------------------------------
  /**
   * addToIndex creates a row in the index 
   *   docId|beginSpan|endSpan|label|id|provanence|DisplayString|Feature1|feature2| ... feature N
   *   
   *     Where the features dependent upon the type of label
   * 
   *  The index is going to an open file.  If the file is not
   *  open, it will be opened in this method
   *  
   * @param annotation
   * @throws FileNotFoundException 
   */
  // -----------------------------------------
  private void addToIndex(JCas pJCas, String docId, String patientId, Annotation pAnnotation) throws FileNotFoundException {
    
    String          name = pAnnotation.getType().getName();
    int        beginSpan = pAnnotation.getBegin();
    int          endSpan = pAnnotation.getEnd();
    String            id = ((VAnnotation) pAnnotation).getId(); 
    String    provenance = ((VAnnotation) pAnnotation).getProvenance();
    String displayString = ((VAnnotation) pAnnotation).getDisplayString(); 

    String featureValueList = getFeatureValues(pJCas, pAnnotation );
    
    
    if ( this.out == null)
      this.out = openIndexFile();
    
    StringBuffer buff = new StringBuffer();
   //  beginSpan|endSpan|label|id|provanence|DisplayString|Feature1|feature2| ... feature N

    buff.append( docId);        buff.append(DELIMETER);
    buff.append( patientId);    buff.append(DELIMETER);
    buff.append(beginSpan);     buff.append(DELIMETER);
    buff.append(endSpan);       buff.append(DELIMETER);
    buff.append(name);          buff.append(DELIMETER);    
    buff.append(id);            buff.append(DELIMETER);
    buff.append(provenance);    buff.append(DELIMETER);
    buff.append(displayString); buff.append(DELIMETER);
    buff.append( featureValueList);

    this.out.println(buff.toString());
    
  } // end addToIndex() ---------------------


  // -----------------------------------------
  /**
   * getFeatureValues 
   * 
   * @param pJCas
   * @param pAnnotation
   * @return String
   */
  // -----------------------------------------
  private String getFeatureValues(JCas pJCas, Annotation pAnnotation) {

    // -------------------------------
    // For a given type of annotation, 
    //   retrieve the list of features
    // 
    //   Iterate thru each feature
    
    Type aType = pAnnotation.getType();
    List<Feature> features = aType.getFeatures();
    String returnValue = null;
 
    if ( features != null ){
     StringBuffer buff = new StringBuffer();
      for ( Feature aFeature: features) {
        String featureValue = getFeatureValue( pJCas, aFeature, pAnnotation);  
        if ( featureValue != null ) 
          buff.append(featureValue);  buff.append( DELIMETER);          
      } // end loop thru features
      returnValue = buff.toString();
    }
    return returnValue;
  } // end Method getFeatureValues() ---------


  // -----------------------------------------
  /**
   * getFeatureValue 
   * 
   * @param pJCas
   * @param aFeature
   * @return
   */
  // -----------------------------------------
  private String getFeatureValue(JCas pJCas, Feature aFeature, Annotation pAnnotation) {
    
    Type FeatureType = null;
    ArrayList<String> feature = new ArrayList<String>();
    Type domain = aFeature.getDomain();
    if ( domain != null )
      // ----------------------------------
      // Filter out uima features
      // ----------------------------------
      if ( !domain.getName().contains("uima")) {
        
        // ----------------------
        // Switch on feature type 
        // ----------------------
        FeatureType = aFeature.getRange();
        
        // --------------------------
        // String
        // --------------------------
        if ( FeatureType.getName().equals("uima.cas.String")) {
          
          feature.add( pAnnotation.getFeatureValueAsString(aFeature));
          
        // --------------------------
        // StringArray  (concatenate the list into one string delimited by :)
        // --------------------------
        } else if (FeatureType.getName().equals("uima.cas.StringArray")) {

          org.apache.uima.jcas.cas.StringArray stringArray = (StringArray) pAnnotation.getFeatureValue(aFeature);
          if ( stringArray != null ) {
            String[] vals = stringArray.toStringArray();
            if ( vals != null ) { 
              StringBuffer buff = new StringBuffer();
              for ( int i = 0; i < vals.length; i++ ) {
                buff.append( vals[i]);
                if ( i < vals.length -1)
                  buff.append(":");
              } // loop thru the string array
              feature.add(buff.toString());
            } // end if there are vals
          } // end if there are stringArray is not null
     
        // --------------------------
        // Boolean
        // --------------------------  
        } else if ( FeatureType.getName().equals("uima.cas.Boolean")) {
          feature.add( pAnnotation.getFeatureValueAsString(aFeature));
      } else {
          // System.err.println("The unknown feature type = " + FeatureType.getName());
        }
      }
    return null;
  }


  // -----------------------------------------
  /**
   * openIndexFile opens the output file for printing
   * 
   * @return PrintWriter
   * @throws FileNotFoundException 
   */
  // -----------------------------------------
  private PrintWriter openIndexFile() throws FileNotFoundException {
   
    PrintWriter cOut = null;
    try {
       cOut = new PrintWriter( this.indexFileName);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.err.println("Not able to open for writing the outputFile " + this.indexFileName);
      throw e;
    }
    return cOut;
  }


  // -----------------------------------------
  /**
   * initializeTypes transforms the label strings
   * to org.uima.cas.types 
   * 
   * @param pJCas
   */
  // -----------------------------------------
  private org.apache.uima.cas.Type[] initializeTypes(JCas pJCas) {
    Type[] clabelTypes = null;
    if ( this.labelsToIndex != null ) {
      clabelTypes = new Type[ labelsToIndex.length];
      int i = 0;
      for ( String label: this.labelsToIndex ) {
        Type aType = UIMAUtil.getLabelType( pJCas, label);
        clabelTypes[i++] = aType;
      } // end Loop through labels
    }  // end if there are any labels to index   
    return clabelTypes;
  } // end Method initializeTypes() ----------


  // -----------------------------------------
  /** 
   * destroy closes the index file
   *
   */
  // -----------------------------------------
  @Override
  public void destroy() {

   
    out.close();
    
    
    super.destroy();
  } // end Method Destroy() -----------------



  //----------------------------------
  /**
   * initialize loads in the resources needed for phrase chunking. 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    
  
    if (aContext != null)
      super.initialize(aContext);
    this.logger = aContext.getLogger();
    
    String          delem = (String) aContext.getConfigParameterValue("DELIMETER");
    if ( delem != null )
      this.DELIMETER = delem;
    
    this.labelsToIndex   = (String[])aContext.getConfigParameterValue("labels");
    try {
      this.indexFileName = (String)  aContext.getConfigParameterValue("indexFileName");
    } catch ( Exception e) {
      e.printStackTrace();
      String msg = "No indexFileName was found, please fill out the indexFile";
      out.println(msg);
      throw new ResourceInitializationException();
    }
   
    
  } // end Method initialize() -------
  
  
  // ------------------------------------------
  /**
   * initialize
   *   this method is called from the indexListener
   *
   * @param outputTypes
   */
  // ------------------------------------------
  public void initialize(String[] outputTypes) {
    
    
    
  }  // End Method initialize() -----------------------


  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  private Logger logger = null;
  private String[] labelsToIndex = null;
  private Type[]  labelTypes = null;
  private String indexFileName = "./indexFile.txt";
  private PrintWriter out = null;
  private String DELIMETER = "|";
  
 
  
} // end Class MetaMapClient() ---------------
