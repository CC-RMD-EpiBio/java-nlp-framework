// ------------------------------------------------------------
/**
 * The Sophia application pulls up a dilog to pick the input files 
 * and set options, with a run button - runs the sophia pipeline
 * and kicks off the display using vtt.
 * 
 * @author Divita
 * Feb 14, 2013
 * 
 * ------------------------------------------------------------
 *
 *
 *
 * -------------------------------------------------------------
 */
// -------------------------------------------------------------
package gov.va.vinci.framework.applications;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

import gov.va.vinci.cm.Document;
import gov.va.vinci.nlp.framework.pipeline.SophiaPipeline;
import gov.va.vinci.nlp.framework.pipeline.applications.FrameworkBaselineApplication;
import gov.va.vinci.nlp.framework.utils.U;

import org.apache.uima.analysis_engine.AnalysisEngine;


// -----------------------------------
/**
 * Class Types
 *
 */
// ------------------------------------
public class Sophia {

  private FrameworkBaselineApplication application;



  // =======================================================
  /**
   * Constructor Sophia 
   *
   * @param pArgs
   */
  // =======================================================
  public Sophia(String[] pArgs) throws Exception {

    initializeApplication( pArgs);
  }
  
  
  public Sophia() {
    // TODO Auto-generated constructor stub
  }


  // =======================================================
  /**
   * Constructor Sophia 
   *
   * @param args
   */
  // =======================================================
  public void initializeApplication(String[] pArgs) throws Exception {

    try {
    // --------------------
    // Read in any command line arguments, and add to them needed ones
    // The precedence is command line args come first, followed by those
    // set by the setArgs method, followed by the defaults set here.
    // --------------------
    String args[]  = setArgs(pArgs );
      
 
    // -------------------
    // Create a BaselineFrameworkApplication instance
    // -------------------
    this.application = new FrameworkBaselineApplication();
    
    // -------------------
    // Create an engine with a pipeline, attach it to the application
    // -------------------
    SophiaPipeline sophiaCONTEXTPipeline = new SophiaPipeline(args);
    AnalysisEngine ae = sophiaCONTEXTPipeline.getAnalysisEngine( );
    this.application.setAnalsyisEngine(ae);
    
    // -------------------
    // Create a reader  
    // -------------------
    this.application.createReader( FrameworkBaselineApplication.STRING_READER, args);
    
   

    // ------------------
    // Create a writers to write out the processed cas's (write out xmi, vtt files, stat file, and concordance file)
    // ------------------
     this.application.addWriter( FrameworkBaselineApplication.COMMON_MODEL_STRING, args);
    // this.application.addWriter( FrameworkBaselineApplication.STRING, args);
     
    } catch (Exception e) {
      e.printStackTrace();
      String msg = "Issue initializing Sophia " + e.toString();
      System.err.println( msg);
      throw new Exception( msg);
      
    }
    
    
  } // end Constructor -------------------------


  
//=======================================================
 /**
  * process takes inputText and returns text that is formatted
  * via the common model.
  * 
  * @param pFormat bioC|xmi|commonModel|vtt ...
  * @param inputText
  * @exeption Exception
  * @return String
  */
 // =======================================================
 public String processTo(String pFormat, String pInputText) throws Exception {
   
   String returnValue = null;
   try {
    
     returnValue = this.application.processTo( pFormat, pInputText );
   } catch (Exception e) {
     e.printStackTrace();
     String msg = "Issue processing the input text " + e.toString();
     throw new Exception (msg);
   }
   return returnValue;
 }  // End Method processTo() ======================

 
 
//=======================================================
/**
 * process takes inputText and returns an instance of
 * a document as specified from the pFormat parameter.
 * 
 * @param pFormat    FrameworkBaselineApplication.BIOC_OBJECT|FrameworkBaselineApplication.COMMON_MODEL_OBJECT|FrameworkBaselineApplication.VTT_OBJECT
 * @param inputText
 * @exeption Exception
 * @return Object as specified from the pFormat above
 */
// =======================================================
public Object processAPI(String pFormat, String pInputText) throws Exception {
  
  Object returnValue = null;
  try {
   
    returnValue = this.application.processAPI( pFormat, pInputText );
  } catch (Exception e) {
    e.printStackTrace();
    String msg = "Issue processing the input text " + e.toString();
    throw new Exception (msg);
  }
  return returnValue;
}  // End Method processAPI() ======================
 
 
 
  // ------------------------------------------
  /**
   * main
   *    See the setArgs method to see what specific command line 
   *    arguments should be passed in here.
   *
   * @param pArgs
   */
  // ------------------------------------------
  public static void main(String[] pArgs) {
    

    try {
    
      String[] args = setArgs( pArgs);
      Sophia anApplication = new Sophia( args);

    
      // ------------------
      // gather and process the cas's
      // -----------------
    
      BufferedReader in = new BufferedReader( new InputStreamReader( System.in ));
      String row = null;
      System.err.print("input : ");
      while ( (row = in.readLine() )!= null && row.trim().length() > 0  ) {
        String results = anApplication.processTo(FrameworkBaselineApplication.BIOC_XML_STRING, row );  // <---------------- all the work is done here
        
        Object doc = anApplication.processAPI(FrameworkBaselineApplication.BIOC_OBJECT, row );
       
      
        System.out.println(results );
        System.err.println("--------------------");
        System.err.print("input : ");
      }
    
      System.err.println("Finished");
    
    } catch (Exception e) {
      e.getStackTrace();
      String msg = "Issue processing the files " + e.toString();
      System.err.println(msg);
    }
    
  } // end Method main() --------------------------------------
    

  // ------------------------------------------
  /**
   * setArgs  These are arguments that can be set on the command line
   *          to pass in input, output directories, what resulting output labels  
   *          will be in the output files.  What parameters are needed for
   *          specific annotators.
   *
   * @return String[]
   */
  // ------------------------------------------
private static String[] setArgs(String pArgs[]) {
    
  String  tmpDir = "/data/inputStrings";
  try {
    tmpDir  = File.createTempFile("xxx", ".tmp").getParent();
  } catch (Exception e) {
    tmpDir = "/data/input/strings";
  }
  String       inputDir = U.getOption(pArgs, "--inputDir=",   tmpDir);
  
  
    String  outputTypes = "Concept";
    
    // -----------------------------------------
    // Paths to the Sophia indexes
    String     sophiaVersion = U.getOption(pArgs, "--sophiaVersion=", "2011lvl0_9");
 
  
    String args[] = { "--inputDir=" + inputDir,
                     
                      "--outputTypes=" +  outputTypes, 
                      "--sophiaVersion=" + sophiaVersion,
                     
                          
                    }; 
    
    return args;
    
    
    // End Method setArgs() -----------------------
  }
  



  // End SophiaApplication Class -------------------------------
}
