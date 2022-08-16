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
