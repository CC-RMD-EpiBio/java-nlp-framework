// =======================================================
/**
 * SerializableOject is a mechanism to keep the serialized version ids the
 * same between building a model and loading it back in.
 * 
 * (Many thanks to seeing how Doug Redd did it for RED.)
 *
 * @author  Divita
 * @created Dec 17, 2014
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.annotator;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class SerializableObject implements Serializable{
  private static final long serialVersionUID = -8642637231196646922L;
  public void save(String filePath){
    ObjectOutputStream out=null;
    try {
      out = new ObjectOutputStream(new FileOutputStream(filePath));
      
      out.writeObject(this);

      System.err.println("Save: "+filePath);
      
      
    } catch (Exception e) {
      e.printStackTrace();
    } finally{
      if(out!=null){
        try {
          out.close();
        } catch (Exception e) {
         
          e.printStackTrace();
        }
      }
    }
        
  }
  public static SerializableObject load(String filePath){
    ObjectInputStream in=null;
    SerializableObject obj=null;
    
    try {
      
      System.err.println("Load: "+filePath);
      in= new ObjectInputStream(new FileInputStream(filePath));
      obj=(SerializableObject) in.readObject();
      
      
    } catch (Exception e) {
   
      e.printStackTrace();
    } finally {
      if(in!=null){
        try {
          in.close();
        } catch (IOException e) {
          
          e.printStackTrace();
        }
      }
    }
    
    return obj;
  }
}
