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
//=================================================
/**
 * EvaluateWithAttributeWriter is a wrapper around
 * the EvaluateWriter, which, in turn is a wrapper
 * around the evaluateAnnotator.  Historically,
 * there is an evaluate and evaluate with Attributes.
 * The  latter functionality has either been folded
 * into the evaluate or it's no longer needed. This
 * class is a vestigial organ, here for convenience.
 *
 * @author  Guy Divita 
 * @created June 26, 2012
 *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.evaluate;


import org.apache.uima.resource.ResourceInitializationException;



public class EvaluateWithAttributeWriter extends EvaluateWriter  {

//-----------------------------------------
 /** 
  * constructor
  * 
  * @param args
 * @throws ResourceInitializationException 
  */
 // ---------------------------------------
  public EvaluateWithAttributeWriter( String[] pArgs) throws ResourceInitializationException {
    
    super.initialize(pArgs);
    super.initializeAux( pArgs );
    
    
  } // end Constructor() -------------------


} // end Class EvaluateWithAttributeWriter
