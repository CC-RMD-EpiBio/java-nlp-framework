// =================================================
/**
 * An annotator that populates the document header with
 * the number of word tokens, phrases, sentences, sections, negations,
 * lists and the like
 * 
 * 
 *
 *
 * @author  Guy Divita 
 * @created jan 25, 2017
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

import gov.va.chir.model.DocumentHeader;
import gov.va.chir.model.Section;
import gov.va.chir.model.Sentence;
import gov.va.chir.model.WordToken;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;



public class DocumentCharacteristics extends JCasAnnotator_ImplBase {
 
  public static final String annotatorName = DocumentCharacteristics.class.getSimpleName();
  
  private ProfilePerformanceMeter performanceMeter = null;
  
  // -----------------------------------------
  /**
   * process overrides the JCasAnnotator_ImplBase.  
   * This version labels lines of the document.
   * 
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
    
	  
	  this.performanceMeter.startCounter();
	  
	  int numberOfSentences = 0;
	    int numberOfSections = 0;
	    int numberOfWords = 0;
	    int numberOfChars = 0;
	    
    // -----------------------
    // retrieve the documentId - tiu note id from the documentId
    
	  try {
		  DocumentHeader documentHeader = VUIMAUtil.getDocumentHeader(pJCas);
		  
		  
		    try {
		    	List<Annotation> sentences = UIMAUtil.getAnnotations(pJCas, Sentence.typeIndexID);
		    	if ( sentences != null )  numberOfSentences = sentences.size();
				 
		    } catch (Exception e) {};
			  
		    try {
		    	List<Annotation> sections = UIMAUtil.getAnnotations(pJCas, Section.typeIndexID);
		    	if ( sections != null ) 
		    		numberOfSections = sections.size();
		    	} catch (Exception e) {}
		    	
		    try {
		    	  List<Annotation> words = UIMAUtil.getAnnotations(pJCas, WordToken.typeIndexID);
		    	  if ( words != null ) 
		    		  numberOfWords = words.size();
		    } catch (Exception e) {}
		    
		    try {
		  	  String report = pJCas.getDocumentText();
		  	  if ( report != null ) numberOfChars = report.length();
		  	  
		  	 
		  } catch (Exception e) {}
		    
		documentHeader.setNumberOfSentences( numberOfSentences);
		documentHeader.setNumberOfSections( numberOfSections);
		documentHeader.setNumberOfWords( numberOfWords);
		documentHeader.setNumberOfChars( numberOfChars);
			  
		
		  
	  } catch ( Exception e) {
			  
	  }
     
   this.performanceMeter.stopCounter();
   
    
  } // end Method process() ----------------
   
 
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
  * @param aContext
  *
  */
 // -----------------------------------------
  public void initialize(UimaContext aContext)  throws ResourceInitializationException {
   
    String args[] = null;
    
    try {
      args                  = (String[]) aContext.getConfigParameterValue("args");
      this.performanceMeter = new ProfilePerformanceMeter( args, this.getClass().getSimpleName() );
      
      
     
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with getting the passed in arguments " + e.toString());
      throw new ResourceInitializationException();
    
    }
 
  } // end Method initialize() -----------
  

  
  
} // end Class DocumentHeaderFromLeoCSI() ---------------
