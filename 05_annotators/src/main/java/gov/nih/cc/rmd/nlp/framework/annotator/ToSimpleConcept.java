// =================================================
/**
 * ToSimpleConcept filters to entities that are inherited from
 * simple Concepts to be viewable with eHOST - 
 * 
 *    Question - doing this for Symptoms, which includes gold,copper, fp,fn,tp,tn, 
 *    potential symptom - where I'd like to keep the label. Maybe an easier way would be to have all
 *    of these inherit Concept 
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


import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.Section;
import gov.va.chir.model.SlotValue;
import gov.va.vinci.model.Concept;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;


public class ToSimpleConcept extends JCasAnnotator_ImplBase {
 
  
  
  // -----------------------------------------
  /**
   * process 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
    List<Annotation> annotations = UIMAUtil.getAnnotations(pJCas);
    
    Type          conceptType = pJCas.getCasType(Concept.typeIndexID);
    Type   documentHeaderType = pJCas.getCasType(DocumentHeader.typeIndexID);
    Type          sectionType = pJCas.getCasType(Section.typeIndexID);
    Type   contentHeadingType = pJCas.getCasType(ContentHeading.typeIndexID);
    Type        slotValueType = pJCas.getCasType(SlotValue.typeIndexID);
    Type dependentContentType = pJCas.getCasType(DependentContent.typeIndexID);
    
   if ( annotations != null ) {
     for ( Annotation annotation : annotations ) {
       
       if (!UIMAUtil.isInstanceOf( annotation,  conceptType )           && 
           !UIMAUtil.isInstanceOf( annotation,   documentHeaderType )   && 
           !UIMAUtil.isInstanceOf( annotation,   sectionType)           &&
           !UIMAUtil.isInstanceOf( annotation,   contentHeadingType)    &&
           !UIMAUtil.isInstanceOf( annotation,   slotValueType)         &&
           !UIMAUtil.isInstanceOf( annotation,   dependentContentType)  ) 
       {
       
        annotation.removeFromIndexes();
  
       }
     }
   }

    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
      throw new AnalysisEngineProcessException();
    }
  } // end Method process() ----------------

  //----------------------------------
  /**
   * initialize loads in the resources needed for phrase chunking. 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
   
   
  }
  
  


   
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------


  
} // end Class MetaMapClient() ---------------
