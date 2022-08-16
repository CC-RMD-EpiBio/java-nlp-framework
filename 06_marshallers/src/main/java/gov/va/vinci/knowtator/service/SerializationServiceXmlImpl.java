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
