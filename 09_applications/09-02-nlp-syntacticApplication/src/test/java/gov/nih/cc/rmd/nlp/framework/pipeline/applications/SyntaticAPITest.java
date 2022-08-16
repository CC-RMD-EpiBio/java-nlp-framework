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
/**
 * LineAPITest.java  
 *
 * @author     Guy Divita
 * @created    Mar 4, 2019
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.pipeline.applications;

import org.apache.uima.jcas.JCas;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
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
