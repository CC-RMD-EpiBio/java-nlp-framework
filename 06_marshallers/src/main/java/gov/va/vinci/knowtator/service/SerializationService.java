// =================================================
/**
 * SerializationService.java Summary
 *  Class Detail
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


/**
 * Serialization service for serializing/de-serializing model objects.
 * 
 * @author ryancornia
 *
 */
public interface SerializationService {

  public abstract String serialize(Object a); 

  public abstract <T> T deserialize(String stringToDeserialize, Class<T> typeToDeserializeTo); 

  //public List<Class> getSupportedTypes(); 
}
