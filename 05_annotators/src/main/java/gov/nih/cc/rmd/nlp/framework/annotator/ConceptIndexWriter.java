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
// ------------------------------------------------------------
/**
 * ConceptIndexWriter writes out rows in the following format to the 
 * passed in printwriter.
 *
 *                              format 
 *       
 *      // +--------+--------+----+----------+---------+-------+-----+----+
 *      // |fileName|concepts|cuis|categories|assertion|snippet|begin|end |
 *      // +--------+--------+----+----------+---------+-------+-----+----+
 *      
 *      // All the fields are variable length pipe delimited fields (for now), to conserve space.
 *      // concepts,cuis,categories are colon delimited lists
 *      // assertion will be Asserted|Negated| (might be other values later on
 *      // Snippets will be cleaned up to remove non-printable whitespace such as \n's \r's, \m's, \t's
 *      // There should be one row per snippet that mapped to a concept.
 *      
 *      
 *
 * @author Divita
 * Apr 23, 2013
 * 
 * ------------------------------------------------------------
 *
 *
 *
 * -------------------------------------------------------------
 */
// -------------------------------------------------------------
package gov.nih.cc.rmd.nlp.framework.annotator;

import java.io.PrintWriter;
import java.util.List;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import gov.va.vinci.model.Concept;
import gov.va.vinci.model.Concept_Type;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.framework.uima.VUIMAUtil;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

// -----------------------------------
/**
 * Class Types
 *
 */
// ------------------------------------
public class ConceptIndexWriter {

  // -----------------------------------------
  /**
   * Constructor 
   * 
   * @param pOutputTypes
   */
  // -----------------------------------------
  public ConceptIndexWriter() {
    
    
  }  // End Constructor() -----------------------------
  

  // ------------------------------------------
  /**
   * process
   *
   *
   * @param pJCas
   * @param pOut
   * @param pNoNegation  ( set to true if you don't want negated rows)
   * @return number of Rows put into the file
   */
  // ------------------------------------------
  public int process(JCas pJCas, PrintWriter pOut, boolean pNoNegation) {
  
    int numberOfRows = 0;
    
    // ----------------------------
    // if first time, write out a header
    if ( this.firstTime )
        printHeader( pOut, pNoNegation);
    
    // ----------------------------
    // Retrieve the filename
    String fileName = VUIMAUtil.getDocumentId(pJCas);
    
    // -----------------------------
    // Retrieve the simple concepts
    
    List<Annotation> concepts = UIMAUtil.getAnnotations( pJCas, Concept_Type.typeIndexID);
    
    if ( concepts == null ) {
      System.err.println("don't got concepts");
    } else {
      System.err.println(" got concepts");
      for ( Annotation concept: concepts ) {
        Concept aConcept = (Concept) concept;
        String conceptNames = clean( aConcept.getConceptNames());
        String cuis         = clean( aConcept.getCuis());
        String categories   = clean( aConcept.getCategories());
        String assertion    = aConcept.getAssertionStatus() ;
        String negation     = "";
        
        if ( assertion == null || assertion.contains("null"))
          assertion = "Asserted";
        
        // ------------------------------
        // Filter out negated forms
        if ( !pNoNegation  )
          negation = assertion + "|";
        
          pOut.println( fileName                                         + "|" +
                        conceptNames                                     + "|" + 
                        cuis                                             + "|" +
                        categories                                       + "|" +
                        negation                                         +
                        U.display(aConcept.getCoveredText() )            + "|" +
                        aConcept.getBegin()                              + "|" +
                        aConcept.getEnd()                                        );
        
        numberOfRows++;
        
        
      } // end loop through concepts
      pOut.flush();
      
    } // end if there concepts for this record
    
    
    
    return numberOfRows;
    
  }  // End Method process() -----------------------
  
  
  // ------------------------------------------
  /**
   * printHeader
   *
   *
   * @param pOut
   * @param pNoNegation
   */
  // ------------------------------------------
  private void printHeader(PrintWriter pOut, boolean pNoNegation) {
    
    
   String msg1 = "#----------+------------+-----+-----------+";
   String msg2 = "#DocumentId|conceptNames|cuis |categories |";
 
   String msg3 = null;
   String msg4 = null;
   if ( pNoNegation ) {  
     msg3 = "-------+-----+----+";  
     msg4 = "Snippet|Begin|end |";
   
   } else {
     msg3 = "---------+-------+-----+----+";  
     msg4 = "Assertion|Snippet|Begin|end |";
   }
   
   String msg5 = msg1 + msg3  ;
   String msg6 = msg2 + msg4  ;
   
   pOut.println( msg5);
   pOut.println( msg6);
   pOut.println( msg5);
   
   this.firstTime = false;
  } // End Method printHeader() -----------------------
  


  // ------------------------------------------
  /**
   * clean replaces pipes with colons, without erroring out
   *
   *
   * @param conceptNames
   * @return
   */
  // ------------------------------------------
  private String clean(String pPipedString ) {
  
    String returnValue = pPipedString;
    String buff = pPipedString;
    
    if ( buff != null )
      if ( buff.contains("|")) {
        while ( ( buff.indexOf('|')) > -1)
          buff = buff.replace('|', ':');
      returnValue = buff;
      }
    
    return returnValue;
    
    
    // End Method clean() -----------------------
  }



  private boolean firstTime = true;
  

  

  // End ConceptIndexWriter Class -------------------------------
}
