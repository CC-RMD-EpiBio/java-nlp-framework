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
 * MachineLearningContext Summary 
 *
 *
 * @author guy
 * Jun 21, 2013
 * 
 * ------------------------------------------------------------
 *
 *
 *
 * -------------------------------------------------------------
 */
// -------------------------------------------------------------
package gov.nih.cc.rmd.nlp.framework.utils.framework.uima;

import java.util.List;


import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import gov.va.chir.model.Token;
import gov.va.vinci.model.Gold;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;

// -----------------------------------
/**
 * Class Types
 *
 */
// ------------------------------------
public class MachineLearningContext {


 
  private int lastSeen = 0;
  private int lastGoldSeen = 0;

  // -----------------------------------------
  /**
   * Constructor 
   * 
   */
  // -----------------------------------------
  public MachineLearningContext() {
 

    // End Constructor() -----------------------------
  }

  // -----------------------------------------
  /**
   * Constructor 
   * 
   * @param outputFileName
   * @throws Exception 
   */
  // -----------------------------------------
  public MachineLearningContext(String pOutputFileName) throws Exception {
   
    
   
  }  // End Constructor() -----------------------------
  

  

  // ------------------------------------------
  /**
   * getLhs
   *   retrieves the X number of tokens on the left hand side
   *   of the concept, and returns the snippet that covers
   *   these tokens
   *
   * @param pJCas
   * @param pConcept
   * @param pTokens
   * @param pWindow
   * @return String
   */
  // ------------------------------------------
  public String getLhs(JCas pJCas, Annotation pConcept, List<Annotation>pTokens, int pWindow) {
   
    Annotation beginToken = getBeginTokenLeftHandSide( pJCas, pTokens, pConcept, pWindow);
    String lfs = null;
      
    int beginTokenOffset = beginToken.getBegin();
    int endOffset = pConcept.getBegin();
    
    lfs = pJCas.getDocumentText().substring(beginTokenOffset, endOffset);
    
    return lfs;
        
  }  // End Method getLhs() -----------------------
  



  // ------------------------------------------
  /**
   * getBeginTokenLeftHandSide
   *
   *
   * @param pJCas
   * @param pTokens
   * @param pConcept
   * @param pWindow
   * @return
   */
  // ------------------------------------------
  public Annotation getBeginTokenLeftHandSide(JCas pJCas, List<Annotation> pTokens, Annotation pConcept,
      int pWindow) {
    
    Annotation beginToken = null;
    int beginTokenIndex = UIMAUtil.getAnnotationFromList(pTokens, pConcept, this.lastSeen);
    this.lastSeen = Math.abs( beginTokenIndex );
    
    int lfsBeginToken = beginTokenIndex - pWindow;
    
    if ( lfsBeginToken < 0 ) lfsBeginToken = 0;
     beginToken = pTokens.get(lfsBeginToken);
    
     return beginToken;
  }  // End Method getBeginTokenLeftHandSide() -----------------------
  

  // ------------------------------------------
  /**
   * getLhs
   *   retrieves the X number of tokens on the left hand side
   *   of the concept, and returns the snippet that covers
   *   these tokens
   *
   * @param pJCas
   * @param pConcept
   * @param pTokens
   * @param pWindow
   * @return String
   */
  // ------------------------------------------
  public String getRhs(JCas pJCas, Annotation pConcept, List<Annotation>pTokens, int pWindow) {
   
    String lfs = null;
    Token endToken = getEndTokenRightHandSide( pJCas, pTokens, pConcept, pWindow);
   
    int rfsBeginTokenOffset = pConcept.getEnd() + 1;
    int endTokenOffset = endToken.getEnd();
    
    lfs = pJCas.getDocumentText().substring(rfsBeginTokenOffset, endTokenOffset);
    
    return lfs;
    
    
  }  // End Method getLhs() -----------------------
  


  // ------------------------------------------
  /**
   * getEndTokenRightHandSide
   *
   *
   * @param pJCas
   * @param pTokens
   * @param pConcept
   * @param pWindow
   * @return
   */
  // ------------------------------------------
  public Token getEndTokenRightHandSide(JCas pJCas, List<Annotation> pTokens, Annotation pConcept, int pWindow) {
   
    Token endToken = null;
    
    this.lastSeen = Math.abs(this.lastSeen);
    int beginTokenIndex = UIMAUtil.getAnnotationFromList(pTokens, pConcept, this.lastSeen);
    this.lastSeen = Math.abs(beginTokenIndex);
    
    // -----------------------
    // iterate through the tokens until you've gotten
    // beyond the offset of the concept.
    for (int i = this.lastSeen; i < pTokens.size(); i++ ) {
      gov.va.chir.model.Token aToken = (gov.va.chir.model.Token) pTokens.get(i);
      if ( aToken.getEnd() >= pConcept.getEnd() ) {
        beginTokenIndex = i;
        break;
      }      
    }
   
    int   rfsEndToken = beginTokenIndex + pWindow;
    
    
    if ( rfsEndToken >  pTokens.size() -1) rfsEndToken =  pTokens.size() -1;
     endToken = (Token) pTokens.get(rfsEndToken);
    return endToken;
    
  } // End Method getEndTokenRightHandSide() -----------------------
  

  // ------------------------------------------
  /**
   * getContextFormat1 returns the context as 
   *   leftHandSide XXSYMPTOMXX rightHandSide|Answer
   *
   *
   * @param pJCas
   * @param pId
   * @param pConcept
   * @param pTokens
   * @param pAnswer
   * @return String
   */
  // ------------------------------------------
  public String getContextFormat2(JCas pJCas, String pId, Annotation pConcept, List<Annotation> pTokens, String pAnswer) {
  
    String returnVal = null;
    
   try {
      String lhs = U.display2(getLhs(pJCas, pConcept, pTokens, 10));
      String rhs = U.display2(getRhs(pJCas, pConcept, pTokens, 10));
    
      if ( rhs == null ) {
    	  rhs = "$NO_RIGHT_HAND_CONTEXT$";
      }
      returnVal =  lhs + " XXSYMPTOMXX " + rhs + "|" + pAnswer;
  
   } catch (Exception e) {
	   String msg = "Issue with getting context " + e.toString()  + "\n"  + U.getStackTrace(e);
	   throw new RuntimeException (msg);
   }
      
    return returnVal;
    
  }  // End Method getContextFormat2() -----------------------
  



// ------------------------------------------
/**
 * getContextFormat1
 *
 *
 * @param pJCas
 * @param pId
 * @param pConcept
 * @param pTokens
 * @param pAnswer
 * @return
 */
// ------------------------------------------
public String getContextFormat1(JCas pJCas, String pId, Annotation pConcept, List<Annotation> pTokens, String pAnswer) {
 
  String returnVal = null;
  String key = U.display2(pConcept.getCoveredText());
  String lhs = "";
  String rhs = "";
  if ( key != null) {
	  try {
     lhs = U.display2(getLhs(pJCas, pConcept, pTokens, 10));
	  } catch ( Exception e) {lhs = " "; };
    
	  try {
		  rhs = U.display2(getRhs(pJCas, pConcept, pTokens, 10));
	  } catch  (Exception e ) { rhs = " "; }
	  
	  returnVal =  lhs + " " + key + " " + rhs + "|" + pAnswer;
    
  }
  return returnVal;
  
  
}  // End Method getContextFormat1() -----------------------

//------------------------------------------
/**
 * getContextFormatNoAnswer retrieves the lfs XXSYMPTOMXX rhs
 * 
 * (no answer)
 *
 *
 * @param pJCas
 * @param pId
 * @param pConcept
 * @param pTokens
 * @param pAnswer
 * @return
 */
// ------------------------------------------
public String getContextFormatNoAnswer(JCas pJCas, String pId, Annotation pConcept, List<Annotation> pTokens, String pAnswer) {
 
  String returnVal = null;
  String key = U.display2(pConcept.getCoveredText());
  if ( key != null) {
    String lhs = U.display2(getLhs(pJCas, pConcept, pTokens, 10));
    String rhs = U.display2(getRhs(pJCas, pConcept, pTokens, 10));
    returnVal =  lhs + " " + key + " " + rhs + "|" + pAnswer;
    
  }
  return returnVal;
  
  
}  // End Method getContextFormat1() -----------------------


//------------------------------------------
/**
 * getContextFormatNoAnswer retrieves the lfs XXSYMPTOMXX rhs
 * 
 * (no answer)
 *
 *
 * @param pJCas
 * @param pId
 * @param pConcept
 * @param pTokens
 * @param pAnswer
 * @return
 */
// ------------------------------------------
public String getContextFormatNoAnswer2(JCas pJCas, String pId, Annotation pConcept, List<Annotation> pTokens, String pAnswer) {
 
  String returnVal = null;
  String key = U.display2(pConcept.getCoveredText());
  if ( key != null) {
    String lhs = U.display2(getLhs(pJCas, pConcept, pTokens, 10));
    String rhs = U.display2(getRhs(pJCas, pConcept, pTokens, 10));
    returnVal =  lhs  + " XXSYMPTOMXX " + rhs + "|" + pAnswer;
    
  }
  return returnVal;
  
  
}  // End Method getContextFormat1() -----------------------


// =======================================================
/**
 * getContextFormat 
 * 
 * @param pJCas
 * @param pAnnotation
 * @param pTokens
 * @return
 */
// =======================================================
public String getContextFormat(JCas pJCas, Annotation pAnnotation, List<Annotation> pTokens) {
 
  String returnVal = null;
  String key = U.display2(pAnnotation.getCoveredText());
  if ( key != null) {
    String lhs = U.display2(getLhs(pJCas, pAnnotation, pTokens, 10));
    String rhs = U.display2(getRhs(pJCas, pAnnotation, pTokens, 10));
    String term = U.display2(pAnnotation.getCoveredText());
    returnVal =  lhs  + " " + term + " " + rhs;
    
  }
  return returnVal;
} // End Method getContextFormat() ======================


// ------------------------------------------
/**
 * resetLastSeen
 *
 *
 */
// ------------------------------------------
public void resetLastSeen() {
 
  this.lastSeen = 0;
  
} // End Method resetLastSeen() -----------------------

//------------------------------------------
/**
 * isCoveredByGold
 *
 *
 * @param pGolds
 * @param pConcept
 *
 * @return
 */
// ------------------------------------------
public boolean isCoveredByGold(List<Annotation> pGolds, Annotation pConcept ) {

  boolean returnVal = false;
  
  if (pGolds != null && pGolds.size() > 0 ) {
    Annotation aGold = null;
    for ( int i = this.lastGoldSeen; i < pGolds.size(); i++ ) {
      aGold = pGolds.get(i);
      int goldBegin = aGold.getBegin();
      int goldEnd   = aGold.getEnd();
      // System.err.println("key|" + sectionBegin + "|" + sectionEnd + "gold/copper|" + annotationBegin + "|" + annotationEnd );
      if (( goldBegin <= pConcept.getBegin() ) &&
          ( goldEnd   >= pConcept.getEnd() )) {
        ((Gold) aGold).setMarked( true);  
        returnVal = true;
        this.lastGoldSeen = i;
        //  System.err.println(" ----------------- found section " + returnVal);
        break;
      } // end if this annotation is sorounded by this section 
      else if ( aGold.getEnd() > pConcept.getEnd()) {
        // System.err.println("Went beyond");
        break;
      }
    } // end loop through the sections of a document
  } // end if there are any golds
  
  return returnVal;

}  // End Method isCoveredByGold() -----------------------



// ------------------------------------------
/**
 * resetLastGoldSeen
 *
 *
 */
// ------------------------------------------
public void resetLastGoldSeen() {
  this.lastGoldSeen = 0;
  
} // End Method resetLastGoldSeen() -----------------------

// =======================================================
/**
 * clear clears the cas based global variables
 * 
 */
// =======================================================
public void clear() {
 this.lastGoldSeen = 0;
 this.lastSeen = 0;
} // End Method clear() ======================








  
  
} // End MachineLearningContext Class -------------------------------

