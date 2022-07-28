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








  
  
  // End MachineLearningContext Class -------------------------------
}
