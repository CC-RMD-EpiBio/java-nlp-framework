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
 * SerializationServiceXmlImpl
 *
 *
 * @author  Guy Divita 
 * @created Jun 13, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.va.vinci.knowtator.service;

import gov.va.vinci.knowtator.service.SerializationService;

import com.thoughtworks.xstream.XStream;

public class SerializationServiceXmlImpl implements SerializationService {
  XStream xsXml = XStreamFactory.getXmlInstance();
  

  /* (non-Javadoc)
   * @see gov.va.vinci.cm.SerializationService#serialize(java.lang.Object)
   */
  public String serialize(Object a) {
    return xsXml.toXML(a);    
  }
  
  /* (non-Javadoc)
   * @see gov.va.vinci.cm.SerializationService#deserialize(java.lang.String, java.lang.Class)
   */
  @SuppressWarnings("unchecked")
  public <T> T deserialize(String stringToDeserialize, Class<T> type) {
    return (T)xsXml.fromXML(stringToDeserialize);
  }
}
