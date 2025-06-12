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
 * CheckBoxRepair annotator fixes the negation status after assertion assignment
 * has happened - just looking at 
 *    check boxes
 *   
 * @author  Guy Divita 
 * @created August 25, 2014
 *
 * *  
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

import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.ContentHeading;
import gov.va.chir.model.DependentContent;
import gov.va.chir.model.SlotValue;


public class CheckBoxRepairAnnotator extends JCasAnnotator_ImplBase {
  
  @Override
  // -----------------------------------------
  /**
   * process finds check boxes from lines of documents
   * 
   * 
   * 
   * @param aJCas
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
    this.performanceMeter.startCounter();
    
    // Loop thru lines
    List<Annotation> slotValues = UIMAUtil.getAnnotations(pJCas, SlotValue.typeIndexID);
    
    if ( slotValues != null && !slotValues.isEmpty() ) {
    
      for ( Annotation slotValue : slotValues ) {
        
        if ( slotValue != null  ) {
         // ------------------------
          // look for begin brackets
          String kind = ((SlotValue)slotValue).getId();
          
          if ( kind.contains("CheckBox")) {
        	  processCheckBox( (SlotValue) slotValue);
      
        
         } // end process me 
        } // end if the line isn't null 
        
        } //end loop thru lines
      
    } //end if there are  lines
    
    this.performanceMeter.stopCounter();
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
     //  throw new AnalysisEngineProcessException();
    }
    
  } // end Method process
      
      
      
 
  
  // =======================================================
  /**
   * processCheckBox 
   *   
   * 
   * @param pSlotValue
   * 
   */
  // =======================================================
  private void processCheckBox( SlotValue pSlotValue ) {
   
	  ContentHeading heading = pSlotValue.getHeading();
	  DependentContent value = pSlotValue.getDependentContent();
	  
	  if ( value != null ) 
		  if ( value.getNegation_Status().contentEquals( "Negated") ) 
			  if ( value.getCoveredText() == null ||  value.getCoveredText().trim().length() == 0 ) {
				  value.setNegation_Status(  null );
				  heading.setNegation_Status( null);
				  pSlotValue.setNegation_Status(null );
			  }
	  
   
  } // End Method processCheckBox() ======================
  

  
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
   * initialize loads in the resources needed for slotValues. Currently, this involves
   * a list of known slot names that might show up.
   *
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    
    String[] args = null;
    try {
      args                 = (String[]) aContext.getConfigParameterValue("args");  
      
      initialize( args );   
      
    } catch (Exception e ) {
      System.err.println("Issue - no args were passed in to the initialize, and the outputDir needs to be passed in ");
      throw new ResourceInitializationException();
    }
    

    
    
  
  } // end Method initialize() ---------

  //----------------------------------
    /**
     * initialize loads in the resources needed for slotValues. Currently, this involves
     * a list of known slot names that might show up.
     *
     * @param pArgs
     * 
     **/
    // ----------------------------------
    public void initialize(String pArgs[]) throws ResourceInitializationException {
      
      this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
      
    
  
  } // end Method initialize() --------------
  

 
  // -----------------------------------------
  // class Variables
  // -----------------------------------------
    SlotValueAnnotator slotValueAnnotator = null;
    private ProfilePerformanceMeter performanceMeter = null;
  

  
} // end Class SlotValue ---------------------
