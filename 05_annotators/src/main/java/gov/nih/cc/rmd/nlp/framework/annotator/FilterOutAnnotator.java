// =================================================
/**
 * FilterOutAnnotator filters out those classes of annotations
 * that are specified.
 * 
 * 
 *        
 * @author  Guy Divita 
 * @created March 01, 2015
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;


import java.util.HashSet;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class FilterOutAnnotator extends JCasAnnotator_ImplBase {
 
  
  
  // -----------------------------------------
  /**
   * process 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

	this.performanceMeter.startCounter();
    try {
        
    		
    
    List<Annotation> annotations = UIMAUtil.getAnnotations(pJCas);
   
    
   if ( annotations != null ) {
     for ( Annotation annotation : annotations ) {
      if ( filterOut ( pJCas, annotation)) 
         annotation.removeFromIndexes();
     }
   }
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
      
    }
    this.performanceMeter.stopCounter();
    
  } // end Method process() ----------------

  // -----------------------------------------
  /**
   * filterOut returns true if this class (devoid of namespace) is on the
   * list of labels 
   * 
   * @param annotation
   * @return boolean (true if this type of annotation is on the filterOut list)
   */
  // -----------------------------------------
  private boolean filterOut(JCas pJCas, Annotation annotation) {
    boolean val = false;
    
    Class<?> annotationClass = annotation.getClass();
    String name = annotationClass.getSimpleName();
    name = U.getNameWithoutNameSpace(name);
   
    val = this.labelHash.contains( name);
    
   
    return val;
  } // end Method filterOutByAttributes


//----------------------------------
 /**
  * destroy
 * 
  **/
 // ----------------------------------
 public void destroy() {
   this.performanceMeter.writeProfile( this.getClass().getSimpleName());
 }


  //----------------------------------
  /**
   * initialize expects the command line args parameter to be passed in.
   * Within that, a --filterOut= argument filled with a pipe delimited 
   * set of short form labels (devoid of name spaces) to filter out.
   * 
   * For instance, "Token:LexicalElement" 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
   
    String[] args = (String[]) aContext.getConfigParameterValue("args");
    
    initialize ( args);
   
  }
    
  //----------------------------------
    /**
     * initialize expects the command line args parameter to be passed in.
     * Within that, a --filterOut= argument filled with a pipe delimited 
     * set of short form labels (devoid of name spaces) to filter out.
     * 
     * For instance, "Token:LexicalElement" 
     * 
     * @param pArgs
     * 
     **/
    // ----------------------------------
    public void initialize(String pArgs[] ) throws ResourceInitializationException {
     

        this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
      
    	
      String filterOutLabelz = U.getOption(pArgs,  "--filterOut=", "");
     
      
    
    if ( filterOutLabelz != null ) { 
      String[] filteredOutLabelz = U.split( filterOutLabelz, ":" );
      initializeAux( filteredOutLabelz);
    }
  }
  //----------------------------------
    /**
     * initialize loads in the resources. 
     * 
     * @param pLabelsToFilterOut
     * 
     **/
    // ----------------------------------
    public void initializeAux( String[] pLabelsToFilterOut) throws ResourceInitializationException {
      
    this.labelHash= new HashSet<String>();
    
    
    if (pLabelsToFilterOut != null && pLabelsToFilterOut.length > 0 ) 
    	for ( String label: pLabelsToFilterOut )
    		this.labelHash.add( label);
    
  } // end Method initialize() -------
  


   
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------

  private HashSet<String> labelHash = null;
  ProfilePerformanceMeter              performanceMeter = null;
  
  
} // end Class MetaMapClient() ---------------
