// =================================================
/**
 * OnTheFlyAnnotator marks negated search terms as NegatedSearchTerms
 *
 *
 * @author  Guy Divita 
 * @created July 26, 2016
 *
 **   *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.va.vinci.model.Concept;
import gov.va.vinci.model.SearchTerm;
import gov.va.vinci.model.SearchTermNegated;
import gov.nih.cc.rmd.nlp.framework.utils.PerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class OnTheFlyNegationAnnotator extends JCasAnnotator_ImplBase {
 
  
//=======================================================
/**
* process
* 
* @param pJCas
* @throws AnalysisEngineProcessException
*
*/
// =======================================================
public void process(JCas pJCas) throws AnalysisEngineProcessException {

  this.performanceMeter.startCounter();
  
 
  try {

    // --------------------------------------------------------------
    // create Negation, Assertion, Historical, Subject Annotations
    List<Annotation> searchTerms = UIMAUtil.getAnnotations(pJCas, SearchTerm.typeIndexID );  

    if ( searchTerms != null && !searchTerms.isEmpty())
      for ( Annotation term : searchTerms ) {
       
        String assertionStatus = VUIMAUtil.getAssertionStatus(pJCas, term);
      
        if ( !assertionStatus.equals("Asserted") )
          createNegatedSearchTerm( pJCas, term );
       
      } // end loop thru terms
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with the assertion annotator " + e.toString());
    
    }
    this.performanceMeter.startCounter();
   
  }
   /**
    * createConceptAnnotation creates a Concept
    * See the type.decriptor project gov.va.chir.Model.xml for the 
    * the definition of what a Concept Type is.
    * 
    * @param pJCas
    * @param pSearchTerm
    * 
    * 
    */
   private void createNegatedSearchTerm( JCas pJCas,  Annotation pSearchTerm )  {
    
     SearchTermNegated term = new SearchTermNegated( pJCas);
     term.setBegin(  pSearchTerm.getBegin());
     term.setEnd(    pSearchTerm.getEnd());
     term.setAssertionStatus(((Concept)pSearchTerm).getAssertionStatus());
     term.setCategories("SearchTermNegated");
     term.setId("OnTheFly_" + this.termId++);
     term.addToIndexes(pJCas);   // <------------- very important to do this
     
     pSearchTerm.removeFromIndexes(pJCas);
     
     
   } // end Method createEvidence() --------------------

   

 //----------------------------------
 /**
 * destroy
 * 
 **/
 // ----------------------------------
 public void destroy() {
  this.performanceMeter.writeProfile( this.getClass().getSimpleName());
 }


//-----------------------------------------
 /**
  * initialize loads in the resources.
  *   Put pipeline parameters in the args parameter
  *   to be retrieved here.
  * 
  *      Pass in on your args, an arg with the value: "--outputDir=/some/path/to/put/outputFiles"
  *
  *   This class is tooled up to have performance statisitcs get put into $outputDir/logs/Class.log  
  *
  * @param aContext
  *
  */
 // -----------------------------------------
  public void initialize(UimaContext aContext)  throws ResourceInitializationException {
   
    String args[] = null;
    
    try {
      args                  = (String[]) aContext.getConfigParameterValue("args");
      this.outputDir        = U.getOption(args, "--outputDir=", "./");
      this.performanceMeter = new PerformanceMeter( this.outputDir + "/logs/profile_" + this.getClass().getSimpleName() + ".log"  );
      
  
     
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with getting the passed in arguments " + e.toString());
      throw new ResourceInitializationException();
    
    }
 
  } // end Method initialize() -----------
  
// ---------------------------
   // Global Variables
   // ---------------------------
   PerformanceMeter              performanceMeter = null;
   private String                       outputDir = null;
   private int                             termId = 0;


  
  
} // end Class onTheFlyAnnotator() ---------------
