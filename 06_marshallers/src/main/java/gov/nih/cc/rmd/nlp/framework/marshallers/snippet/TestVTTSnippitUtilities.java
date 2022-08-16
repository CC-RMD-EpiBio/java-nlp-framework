// =================================================
/**
 * TestVTTSnippitUtilities reads in a vtt snippet file and writes it out again
 *
 *
 * @author  Guy Divita 
 * @created Sep 19, 2017
 *
 **  *
 * 
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.marshallers.snippet;

import java.io.File;

import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.Use;

public class TestVTTSnippitUtilities {

  // =============================================
  /**
   * main 
   * 
   * @param pArgs
   *
   */
  // =============================================
  public static void main(String[] pArgs) {
   
    try {

      String[]    args = setArgs(pArgs);
      String  inputDir = U.getOption(args,  "--inputDir=",  "");
      String outputDir = U.getOption(args,  "--outputDir=", "");
      
      // ----------------------------
      // Make the output directory
      U.mkDir(outputDir );
      // ----------------------------
      // read in .vtt files from the inputDir=
      
      File aDir = new File( inputDir);
      if ( aDir.exists() && aDir.canRead()) {
        File[] someFiles = aDir.listFiles();
        if ( someFiles != null ) {
          for ( File aFile: someFiles ) {
            String fileName = aFile.getAbsolutePath();
            if ( fileName.endsWith(".vtt")) {
              System.err.println("Reading in file " + fileName);
              SnippetsContainer vttSnippetsContainer = VTTSnippetUtils.readVTTSnippetFile(aFile);
              
              RedSnippetsContainer redSnippetsContainer = new RedSnippetsContainer( vttSnippetsContainer );
              
           
              
              String outputFileName = outputDir + "/" + aFile.getName() + ".snippets.vtt";
              System.err.println("Writing out file " + outputFileName );
               // vttSnippetsContainer.write( outputFileName );
             
            //  redSnippetsContainer.convertSnippetsToYesNo();
              redSnippetsContainer.write( outputFileName );
              
             
              
              System.err.println("+");
              
            }
          } // loop thru the files of the dir
        }  // there are files in the inputDir
        
      } // end if inputDir exists
      
      System.err.println("Dohn");
      // ----------------------------
      // for each vtt file, parse thru it make a new one from the 
      // containers
      
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue within method main " + e.getMessage());
     
    }
  } // End Method main ============

  // ------------------------------------------
  /**
   * setArgs takes command line args, which override
   *         default args set here
   * 
   * 
   * @return String
   */
  // ------------------------------------------
  public static String[] setArgs(String pArgs[]) {

  
    // -------------------------------------
    // dateStamp
    String dateStamp = U.getDateStampSimple();

    // -------------------------------------
    // Input and Output

    String     drive = U.getOption(pArgs,  "--drive=", "d:");
    String  inputDir = U.getOption(pArgs,  "--inputDir=",  drive + "/data/vhaslcdivitg/data/ORD_Samore_201606098D/diarrhea2017-09-14_13_05_18/snippets");
    String outputDir = U.getOption(pArgs,  "--outputDir=", drive + "/data/vhaslcdivitg/data/ORD_Samore_201606098D/diarrhea" + dateStamp + "/redSnippets");
    
    String    logDir = outputDir + "/logs"; 
    String args[] = {
        "--inputDir=" + inputDir,
        "--outputDir=" + outputDir,
        "--logDir="    + logDir
    };
    
    String description = 
        " \n\n\n TestVTTSnippetUtilities reads in and writes out vtt snippet files\n" +
        " in the v3NLPFramework format.\n\n"   ;
            

    if ( Use.usageAndExitIfHelp(TestVTTSnippitUtilities.class.getCanonicalName(), pArgs, args) ) {
      System.out.print( description);
      System.exit(0);
    }
     
     

    return args;

  }  // End Method setArgs() -----------------------
  

} // End Class TestVTTSnippitUtilities ============
