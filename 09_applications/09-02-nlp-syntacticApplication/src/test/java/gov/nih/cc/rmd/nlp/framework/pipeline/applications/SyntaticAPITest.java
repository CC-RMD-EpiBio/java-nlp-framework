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
 * LineAPITest.java  
 *
 * @author     Guy Divita
 * @created    Mar 4, 2019
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.apache.uima.jcas.JCas;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.FrameworkTestUtilities;

/**
 * @author divitag2
 *
 */
class SyntaticAPITest {
  
  
  // -------------------------------------------------
  // Setup Global test fields
  static ApplicationAPI applicationAPI = null;

  // =================================================
  /**
   * setUpBeforeClass [TBD] summary
   * 
   * @throws java.lang.Exception
  */
  // =================================================
  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    
    String  outputTypes = "Line:Token:SectionZone:ContentHeading:DependentContent:SlotValue:Section:LexicalElement:Sentence:POS:Phrase:Shape:UnitOfMeasure" ;
    
    String args1[] = {"--outputTypes=" + outputTypes , "--printToLog=false" };
    String[] args = SyntaticApplication.setArgs(args1);
    applicationAPI = new SyntaticAPI(args);
  }

  // =================================================
  /**
   * tearDownAfterClass [TBD] summary
   * 
   * @throws java.lang.Exception
  */
  // =================================================
  @AfterAll
  static void tearDownAfterClass() throws Exception {
    
    // ----------------
    // clean up
    // ----------------
    applicationAPI.destroy();

  }

  // =================================================
  /**
   * setUp [TBD] summary
   * 
   * @throws java.lang.Exception
  */
  // =================================================
  @BeforeEach
  void setUp() throws Exception {
    
  
    
  }

  // =================================================
  /**
   * tearDown 
   * 
   * @throws java.lang.Exception
  */
  // =================================================
  @AfterEach
  void tearDown() throws Exception {
  }

 
  
  @Test
  public void testAPI() {
    // -------------------
    // Create an API instance
    // -------------------

    try {

    

      // ---------------------------------------
      // Test if you can pass something in, and get something out
      // ---------------------------------------

      // ------------------
      // Magic happens here ----+
      // ------------------ \|/

      String inputText = "\n" + "Current Medications:\n"
          + "Warfarin (COUMADIN ) 5mg Oral Tab (Started 11/9/2016)\n"
          + "Take orally as directed by anticoagulation clinic to refill by mail, call 10 days ahead. (888) 218-6245\n\n";

     
      
         JCas testResults = applicationAPI.processToCas(inputText  );
        

      // --------------- /|\
      // Magic happens here --+
      // ---------------

      // ---------------
      // find the correct annotation
      // ---------------
         
         
      String pAnnotationType = "Line";   // <------ without namespace 
      String pSpanString = "Current Medications:";
      
      boolean found = FrameworkTestUtilities.findAnnotation( testResults, pAnnotationType, pSpanString );

      
      String msg = "Medication :" + found ;
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "testAPI", msg );
      Assert.assertTrue(msg, found);
      
      
    

    } catch (Exception e) {
      e.printStackTrace();
      // where do test hook exceptions go?
      Assert.fail("Missed Medication ");
    }

  } // end testAPI() --------------------
 
  

  @Test
  public void testProcessToVtt() {
    // -------------------
    // Create an API instance
    // -------------------

    try {

    

      // ---------------------------------------
      // Test if you can pass something in, and get something out
      // ---------------------------------------

      // ------------------
      // Magic happens here ----+
      // ------------------ \|/

       String  inputText = "TITLE\n" + 
       "This is a line with a sentence in it.\n" +
       "This is a priori a line\n\n" + 
       "Slot: Value\n\n" + 
       "Section Heading:\n" + 
       "This is a paragraphs in a section\n" + 
       "that spans two lines. It goes\n" + 
       "on to the next line.\n\n";

     
      
      String testResults = applicationAPI.processToPipedVTT(inputText  );
        

      // --------------- /|\
      // Magic happens here --+
      // ---------------
      boolean returnVal = false;
      if ( testResults != null  && testResults.trim().length() > 0) {
        returnVal = true;
        GLog.println( testResults );
        
      }
      Assert.assertTrue("VTT output:", returnVal);
    
    

    } catch (Exception e) {
      e.printStackTrace();
      // where do test hook exceptions go?
      Assert.fail("Missed Medication ");
    }

  } // end testAPI() --------------------
 
  

} // end Class LineAPITest() ---------------
