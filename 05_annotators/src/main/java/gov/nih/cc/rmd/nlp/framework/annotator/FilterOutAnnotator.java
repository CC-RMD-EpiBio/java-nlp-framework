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
