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
/*******************************************************************************
 *                                   NIH Clinical Center 
 *                             Department of Rehabilitation 
 *                       Epidemiology and Biostatistics Branch 
 *                                            2019 - 2022
 *   ---------------------------------------------------------------------------
 *   Copyright Notice:
 *   This software was developed and funded by the National Institutes of Health
 *   Clinical Center (NIHCC), part of the National Institutes of Health (NIH),
 *   and agency of the United States Department of Health and Human Services,
 *   which is making the software available to the public for any commercial
 *   or non-commercial purpose under the following open-source BSD license.
 *  
 *   Government Usage Rights Notice:
 *   The U.S. Government retains unlimited, royalty-free usage rights to this 
 *   software, but not ownership, as provided by Federal law. Redistribution 
 *   and use in source and binary forms, with or without modification, 
 *   are permitted provided that the following conditions are met:
 *      1. Redistributions of source code must retain the above copyright
 *         and government usage rights notice, this list of conditions and the 
 *         following disclaimer.
 *  
 *      2. Redistributions in binary form must reproduce the above copyright
 *         notice, this list of conditions and the following disclaimer in the
 *         documentation and/or other materials provided with the distribution.
 *        
 *      3. Neither the names of the National Institutes of Health Clinical
 *         Center, the National Institutes of Health, the U.S. Department of
 *         Health and Human Services, nor the names of any of the software
 *         developers may be used to endorse or promote products derived from
 *         this software without specific prior written permission.
 *   
 *      4. The U.S. Government retains an unlimited, royalty-free right to
 *         use, distribute or modify the software.
 *   
 *      5. Please acknowledge NIH CC as the source of this software by including
 *         the phrase: "Courtesy of the U.S. National Institutes of Health Clinical Center"
 *          or 
 *                     "Source: U.S. National Institutes of Health Clinical Center."
 *  
 *     THIS SOFTWARE IS PROVIDED BY THE U.S. GOVERNMENT AND CONTRIBUTORS "AS
 *     IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *     TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 *     PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE U.S. GOVERNMENT
 *     OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *     EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *     PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *     PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *     LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *     NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *     SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 *     When attributing this code, please make reference to:
 *        Divita G, Carter ME, Tran LT, Redd D, Zeng QT, Duvall S, Samore MH, Gundlapalli AV. 
 *        v3NLP Framework: tools to build applications for extracting concepts from clinical text. 
 *        eGEMs. 2016;4(3). 
 *      
 *     In the absence of a specific paper or url listed above, reference https://github.com/CC-RMD-EpiBio/java-nlp-framework
 *   
 *     To view a copy of this license, visit https://github.com/CC-RMD-EpiBio/java-nlp-framework/blob/main/LICENSE.MD
 *******************************************************************************/
  // ================================================= 
 private static String replaceCopyright(String[] oldBuff, int ptr) { 
    
   String[] buff = new String[ oldBuff.length - 21]; 
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
