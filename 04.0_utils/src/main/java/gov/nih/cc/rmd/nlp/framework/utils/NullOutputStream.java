// =======================================================
/**
 * NullOutputStream.java [Summary here]
 *
 * @author  guy
 * @created Oct 11, 2013
 *
   
 */
// =======================================================
package gov.nih.cc.rmd.nlp.framework.utils;

import java.io.IOException;
import java.io.OutputStream;


  public class NullOutputStream extends OutputStream {
    public void write(int i) throws IOException {
        //do nothing
    }
}
