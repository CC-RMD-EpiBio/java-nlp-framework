package gov.nih.cc.rmd.nlp.framework.utils; 
 
import java.io.File; 
import java.io.PrintWriter; 
 
 
public class StripCopyright { 
 
	public static void main(String[] pArgs) { 
		 try { 
		    
		   String[] args = setArgs( pArgs); 
		   String inputDir = U.getOption(args, "--inputDir=", "./"); 
		    
		    
		   // Iterate through dirs looking for .java files 
		   File aDir = new File ( inputDir); 
		   if ( aDir != null && aDir.canRead()) { 
		     File[] someFiles = aDir.listFiles(); 
		     for ( File aFileOrDir : someFiles ) { 
		       processFileOrDir( aFileOrDir); 
		     } 
		    
		   } 
		      
		    
		    
		    
		    
		    
		    
		 } catch (Exception e) { 
		   e.printStackTrace(); 
		 } 
 
	} 
	 
	 // ================================================= 
  /** 
   * processFileOrDir  
   *  
   * @param aFileOrDir 
  */ 
  // ================================================= 
 private static void processFileOrDir(File pFileOrDir) { 
     
   if ( pFileOrDir.isDirectory()) { 
     File[] someFiles = pFileOrDir.listFiles(); 
     for ( File aFileOrDir : someFiles ) { 
       processFileOrDir( aFileOrDir); 
     } 
   } else { 
     processFile ( pFileOrDir); 
   } 
     
  } // end Method processFileOrDir() --------- 
 
  // ================================================= 
  /** 
   * processFile  
   *  
   * @param pFileOrDir 
  */ 
  // ================================================= 
 private static void processFile(File pFileOrDir) { 
     
   String fileName = pFileOrDir.getAbsolutePath(); 
   if ( fileName.endsWith(".java")) 
     processFileAux( pFileOrDir); 
     
  } // end Method processFile() --------------- 
 
  // ================================================= 
  /** 
   * processFileAux  
   *  
   * @param pFileOrDir 
  */ 
  // ================================================= 
  private static void processFileAux(File pFile) { 
    
    System.err.println( "----> process --> " + pFile.getAbsolutePath()); 
     
    String newBuff = null; 
    // Open the file up  
    boolean found = false;
    try { 
      String buff[] = U.readFileIntoStringArray(pFile.getAbsolutePath()) ;      
       
      for ( int i = 0; i < buff.length; i++ )   { 
        String buff1 = buff[i]; 
        if ( buff1.contains(" This work is licensed under the Creative Commons" )) { 
           System.err.println("found old copyright in file " + pFile.getAbsolutePath()); 
           newBuff = replaceCopyright( buff, i); 
          found = true;
          break;
        } 
         
      } // end loop thru file 
      
      
      if ( found &&  newBuff != null ) { 
          // replace the content of the file 
          U.deleteFile(  pFile.getAbsolutePath()); 
          // remove the old file 
          PrintWriter out = new PrintWriter( pFile.getAbsolutePath()); 
          out.print(newBuff); 
          out.close(); 
          System.err.println("Check me " + pFile.getAbsolutePath()); 
        } 
       
         
         
    } catch (Exception e) { 
      e.printStackTrace(); 
    } 
     
  } // end processFileAux() ------------------- 
 
  // ================================================= 
  /** 
   * replaceCopyright 
   *  
   * @param buff 
   * @return String 
  */ 
  // ================================================= 
 private static String replaceCopyright(String[] oldBuff, int ptr) { 
    
   String[] buff = new String[ oldBuff.length - 20]; 
   String returnVal = null; 
    
   // copy the stuff before line ptr - 7 into buff 
    
   int k = 0; 
   for ( int i = 0; i < ptr - 7; i++ ) { 
       buff[k++] = oldBuff[i]; 
   } 
   for ( int i = ptr + 14; i < oldBuff.length; i++ ) 
     buff[k++] = oldBuff[i]; 
    
   StringBuffer newBuff = new StringBuffer(); 
    
   for ( int i = 0;  i < buff.length; i++) 
     newBuff.append( buff[i] + '\n'); 
    
    returnVal = newBuff.toString(); 
   
   return returnVal; 
     
  } // end Method replaceCopyright() ---------- 
 
  // ------------------------------------------ 
  /** 
   * setArgs 
   *  
   *  
   * @return 
   */ 
  // ------------------------------------------ 
  public static String[] setArgs(String pArgs[]) { 
 
    
    String    inputDir  = U.getOption(pArgs, "--inputDir=", "/data/input/"); 
    
   
    String args[] = { 
         
        "--inputDir=" + inputDir 
        
    }; 
 
        
      
 
    return args; 
 
  }  // End Method setArgs() ----------------------- 
   
  // ---------------------- 
  // Global Variables 
  
} // end Class StripCopyright() -------------------- 

