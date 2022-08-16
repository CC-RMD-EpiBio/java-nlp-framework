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
 * FilterAnnotator filters out entities from downstream processing
 * by setting the processMe flags on annotations to be filtered or
 * not.
 * 
 * Annotations that have a children that are filtered will adhere
 * to the filtering as well.  For example, If Section=Administrative
 * is filtered out, Phrases that have a parent of an Administrative
 * phrase should be filtered out as well. How is this done - for
 * those Annotations that have a parent feature, the parent:processMe
 * feature is consulted to determine if the annotation at hand
 * should be processed.
 * 
 *   The Kinds of Annotations
 *   For each annotation type, it's frequency in the corpus.
 * 
 * 
 *        
 * @author  Guy Divita 
 * @created March 20, 2015
 *
 * [Utah/Salt Lake City Preamble Here]
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;


import java.util.HashMap;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.VAnnotation;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class FilterAnnotator extends JCasAnnotator_ImplBase {
 
  
  
  // -----------------------------------------
  /**
   * process 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
    List<Annotation> annotations = null;
    Type[] types = initializeTypes( pJCas);
    if ( types != null  &&  types.length > 0) {
      annotations = UIMAUtil.getAnnotations(pJCas, types);
    }
   
    
   if ( annotations != null ) {
     for ( Annotation annotation : annotations ) {
       
       // --------------------------
       // Treat slot Values specially - filter out slots or values that 
       // are not asserted - This is done in the questionAnswer annotator.
           
       if ( filterOutByAttributes ( pJCas, annotation)) 
         ((VAnnotation)annotation).setProcessMe(false);
       else 
         ((VAnnotation)annotation).setProcessMe( false);
     }
   }
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
      throw new AnalysisEngineProcessException();
    }
    
  } // end Method process() ----------------

  // -----------------------------------------
  /**
   * filterOutByAttributes 
   * 
   * @param annotation
   * @return
   */
  // -----------------------------------------
  private boolean filterOutByAttributes(JCas pJCas, Annotation annotation) {
    boolean val = false;
    
    Class<?> annotationClass = annotation.getClass();
    String name = annotationClass.getName();
   
    String filter = this.labelHash.get( name);
    
    
    if ( filter.contains(":")) {
      String [] pieces = U.split(filter, ":");
      String attribute = pieces[1];
      String [] slotValue = U.split(attribute, "=");
      if ( slotValue != null ) {
        String attributeName  = slotValue[0];
        String attributeValue = slotValue[1];
        // get the value of the attribute --from annotation
        Feature feature = null;
        if ( (feature = UIMAUtil.getAttributeByName( annotation, attributeName))  != null ) {
          if ( attributeValue != null ) {
            String featureValue = annotation.getFeatureValueAsString(feature);
            if ( featureValue.contains(attributeValue) ) {
              val = true;
            }           
          } else {
            val = true;
          }  // end if we are looking for a value from a feature
        } // end if this is the right kind of annotation
      } else {
        val = true;
      } 
    } else {
      val= true;
    }
      
    return val;
  } // end Method filterOutByAttributes

  //-----------------------------------------
  /**
   * initializeTypes transforms the label strings
   * to org.uima.cas.types 
   * 
   * @param pJCas
   */
  // -----------------------------------------
  private org.apache.uima.cas.Type[] initializeTypes(JCas pJCas) {
    Type[] clabelTypes = null;
    
    
    
    if ( this.filterOut != null ) {
      clabelTypes = new Type[ this.filterOut.length];
      int i = 0;
      for ( String fullLabel: this.filterOut ) {
        String labels[] = U.split(fullLabel, ":");
        String label = labels[0];
        Type aType = UIMAUtil.getLabelType( pJCas, label);
        clabelTypes[i++] = aType;
       
        this.labelHash.put( label, fullLabel);
    
      } // end Loop through labels
    }  // end if there are any labels to index   
    
     
    return clabelTypes;
  } // end Method initializeTypes() ----------
  

  //----------------------------------
  /**
   * initialize loads in the resources needed for phrase chunking. 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
   
    String _filterOut = (String) aContext.getConfigParameterValue("filterOut");
    String[] filteredOutLabelz = U.split( _filterOut );
    
    initialize( filteredOutLabelz);
  }
  //----------------------------------
    /**
     * initialize loads in the resources needed for phrase chunking. 
     * 
     * @param aContext
     * 
     **/
    // ----------------------------------
    public void initialize( String[] pFilterOut) throws ResourceInitializationException {
      
  
   
    this.filterOut = pFilterOut;
    
  
    this.labelHash= new HashMap<String,String>();
    
  } // end Method initialize() -------
  


   
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------

  private String[] filterOut = null;
  private HashMap<String, String> labelHash = null;
  
} // end Class MetaMapClient() ---------------
