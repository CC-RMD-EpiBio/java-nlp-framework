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
 * LexRecord is a container that holds information about a term
 *
 * @author  Guy Divita 
 * @created Feb 21, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.utils.terminologies.utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import gov.nih.cc.rmd.nlp.framework.utils.U;


@SuppressWarnings("serial")
public class LexRecord implements Serializable {

 
  
  // -----------------------------------------
	  /**
	   * Constructor 
	   *
	   */
	  // -----------------------------------------
	  public LexRecord() {
	    
	  }

// -----------------------------------------
  /**
   * Constructor
   *
   * @param pKey
   * @param pTerm
   * @param pCitation
   * @param pEui
   * @param pPos
   */
  // -----------------------------------------
  public LexRecord(String pKey, String pTerm, String pCitation,  String pPos, String pEui) {
    this.key = pKey;
    this.term = pTerm.toCharArray();
    this.citation = pCitation;
    this.poss.add( pPos);
    this.euis.add( pEui);
  } // end Constructor() ---------------------
  
  // -----------------------------------------
  /**
   * Constructor: Creates a lexical record that is an
   * an orphan word.  An orphan word is one that only happens
   * in the context of a larger term. 
   *
   * @param pKey 
   * @param pTerm
   * @param pCitation
   * @param pPOS
   * @param pEui
   * @param pNumberOfTokens
   */
  // -----------------------------------------
                      
  public LexRecord(String pkey, String pTerm, String pCitation, String pPOS, String pEui, int pNumberOfTokens) {
    this.key = pkey;
    this.term = pTerm.toCharArray();
    this.citation = pCitation;
    this.poss.add( pPOS);
    this.euis.add( pEui);
    this.orphanTerm = false;
    this.numberOfTokens = pNumberOfTokens;
    
  } // end Constructor() ---------------------
  
//-----------------------------------------
  /**
   * Constructor: Creates a dummy record
   *
   * @param pWord
   */
  // -----------------------------------------
  public LexRecord(String pWord) {
    this.key = pWord;
    this.term = pWord.toCharArray();
    this.citation = pWord;
    this.poss.add("unknown");
    this.euis.add("unknown");
    
  } // end Constructor() ---------------------

  // -----------------------------------------
  /**
   * Constructor 
   *
   * @param pKey
   * @param pTerm
   * @param pCitation
   * @param pPOS
   * @param pEui
   * @param pOrphan
   * @param pNumberOfTokens
   */
  // -----------------------------------------
  public LexRecord( String pKey, String pTerm, String pCitation, String pPOS, String pEui, boolean pOrphan, int pNumberOfTokens) {
    this.key = pKey;
    this.term = pTerm.toCharArray();
    this.setCitation(pCitation);
    this.poss.add( pPOS);
    this.euis.add( pEui);
    this.orphanTerm = pOrphan;
    this.numberOfTokens = pNumberOfTokens;
  } // end Constructor() ---------------------

  // -----------------------------------------
/**
 * Constructor
 * 
 * @param pKey
 * @param pTerm
 * @param pCitation
 * @param pPOS (penn treebank style)
 * @param pEui
 * @param pNumberOfTokens
 * @param pSemanticType
 */
// -----------------------------------------
                 
public LexRecord( String pKey, String pTerm, String pCitation, String pPOS, String pEui, int pNumberOfTokens, String pSemanticType) {
	this.key = pKey;
	this.term = pTerm.toCharArray();
	this.setCitation(pCitation);
	this.poss.add( pPOS);
	this.euis.add( pEui);
	this.numberOfTokens = pNumberOfTokens;
	this.setSemanticType(pSemanticType);

} // end Contstructor() --------------------

// -----------------------------------------
  /**
   * Constructor 
   * 
   * @param pKey
  * @param pTerm
  * @param pCitation
  * @param pPOS  (penn treebank style)
  * @param pEui
  * @param pNumberOfTokens
  * @param pSemanticType
  * @param pExtraStuff
   */
  // -----------------------------------------
  public LexRecord(String pKey, String pTerm, String pCitation, String pPOS, String pEui, int pNumberOfTokens, String pSemanticType, String pExtraStuff) {
    this.key = pKey;
    this.term = pTerm.toCharArray();
    this.setCitation(pCitation);
    this.poss.add( pPOS);
    this.euis.add( pEui);
    this.numberOfTokens = pNumberOfTokens;
    this.setSemanticType(pSemanticType);
    if ( pExtraStuff != null )
      setExtraStuff( pExtraStuff, pEui );
   
    
   
  }
  
//-----------------------------------------
 /**
  * Constructor 
  * 
  * @param pKey
  * @param pTerm
  * @param pCitation
  * @param pPOS  (penn treebank style)
  * @param pEui
  * @param pNumberOfTokens
  * @param pSemanticType
  * @param pExtraStuff
  * @param pTree
  */
 // -----------------------------------------
 public LexRecord(String pKey, String pTerm, String pCitation, String pPOS, String pEui, int pNumberOfTokens, String pSemanticType, String pExtraStuff, String pTree) {
   this.key = pKey;
   this.term = pTerm.toCharArray();
   this.setCitation(pCitation);
   this.poss.add( pPOS);
   this.euis.add( pEui);
   this.numberOfTokens = pNumberOfTokens;
   this.setSemanticType(pSemanticType);
   setExtraStuff(  pExtraStuff, pEui );
   this.tree = pTree;  
   
  
 }
 
  
  
  

// -----------------------------------------
  /** 
   * parseLRAGR returns a lexRecord given a row from
   * the LRAGR table 
   * 
   * This method leaves the key and number of tokens as null.
   * The the key field requires the tokenizer, which is de-coupled 
   * from this class, and should be called separately.  The number
   * of tokens depends on the term being correctly tokenized, which
   * should be done outside this class.
   *  
   * @return LexRecord
   */
  // ----------------------------------------- 
  public static LexRecord parseLRAGR(String pRow) {
  	
    LexRecord lexRec = null;
    String columns[] = U.splitBetter(  pRow ); 
 
    try {
    String eui = columns[EUI];
    String term = columns[TERM];
    String category = columns[CATEGORY];
    String inflection = columns[INFLECTION];
    String citation = columns[CITATION_FORM];
    String semanticType = "";
    
    try { semanticType = columns[SEMANTIC_TYPE]; } catch (Exception e ) {};
    
   
    
    StringBuffer otherBuff = new StringBuffer();
    String extraStuff = null;
    for ( int i = 7; i < columns.length; i++ )
      otherBuff.append( columns[i] + "|");
    if ( otherBuff != null  && otherBuff.length() > 1)
      extraStuff = otherBuff.toString();
  
    String pennTreebankPOS = DTaggerUtilities.toPenn( term, category, inflection);
    
    int numberOfTokens = -1;
    lexRec = new LexRecord( null, term, citation, pennTreebankPOS, eui, numberOfTokens, semanticType, extraStuff); 
    } catch (Exception e ) {
      e.printStackTrace();
      System.err.println("not able to parse line \n|" + pRow  + "|\n");
      System.err.println(e.toString());
    }
  	return lexRec;
  } // end Method parseLRAGR() --------------
  
  
  // -----------------------------------------
  /**
   * lexItemsToStringArray converts the List<LexRecord> 
   * into an array of pipe delimited field lragr rows
   *
   * @param pLexItems
   * @return String[]
   */
  // -----------------------------------------
  public static String[] lexItemsToStringArray(List<LexRecord> pLexItems) {
   
    String[] returnValue = null;
    
    if (pLexItems != null && !pLexItems.isEmpty()) {

      returnValue = new String[pLexItems.size()];
      for (int i = 0; i < pLexItems.size(); i++)
        returnValue[i] = pLexItems.get(i).toPipedString();

    }

    return returnValue;
  } // end Method lexItemsToStringArray() ------------


  // -----------------------------------------
  /** 
   * toString
   *
   * @return String
   */
  // -----------------------------------------
  public String toString() {
    StringBuffer buff = new StringBuffer();
    buff.append("LexRecord");  buff.append("|");
    buff.append(this.term  );  buff.append("|");
  
    buff.append("[");
    for ( int i = 0; i < this.euis.size(); i++ ) {
      buff.append(this.euis.get(i));  buff.append("|");
      buff.append(this.poss.get(i));  buff.append("|");
    }
    buff.append("]");
    buff.append(this.orphanTerm);     buff.append("|");
    buff.append(this.citation);       buff.append("|");
    buff.append(this.numberOfTokens); buff.append("|");
    
    String subTermsString = this.getSubTermsString();
    
    if ( subTermsString != null )
      buff.append("[" + subTermsString + "]");
   
   
    return buff.toString();
  } // end Method toString() --------------------

  // -----------------------------------------
  /** 
   * getkey retrieves key
   *  
   * @return the key
   */
  // -----------------------------------------
  public String getKey() {
    return this.key;
  }

  // -----------------------------------------
  /** 
   * getterm retrieves term
   *  
   * @return String
   */
  // -----------------------------------------
  public String getTerm() {
    return new String(this.term);
  }
  // -----------------------------------------
  /** 
   * getPoss retrieves all the possible parts of
   * speech for this key.
   *  
   * @return List<String>
   */
  // -----------------------------------------
  public List<String> getPoss() {
    return this.poss;
  }
  // -------------------------------------------------------
  /**
   * getPos retrieves the first part of speech from the list of
   * pos's.  This method is useful when using the lexRecord
   * to hold sinlge or temporary entries
   *
   * @return String
   */
  // -------------------------------------------------------
  public String getPos() {
    String pos = null;
    if ( this.poss != null )
        this.poss.get(0);
    return pos;
  } // end Method getEui() ---------------------------------

  // -----------------------------------------
  /** 
   * getEuis retrieves all the possible euis for this key
   *  
   * @return List<String>
   */
  // -----------------------------------------
  public List<String> getEuis() {
    return this.euis;
  }
  // -------------------------------------------------------
  /**
   * getEui retrieves set of euis
   *
   * @return String
   */
  // -------------------------------------------------------
  public String getEui() {
    String eui = null;
    if ( this.euis != null )
      eui = U.list2String(this.euis, ':' );
      
    return eui;
  }

  // -----------------------------------------
  /** 
   * getlongestTermWithThisEnding retrieves longestTermWithThisEnding
   *  
   * @return the longestTermWithThisEnding
   */
  // -----------------------------------------
  public int getLongestTermWithThisEnding() {
    return this.longestTermWithThisEnding;
  } // end getLogestTermWithThisEnding() -----
  
  // -----------------------------------------
  /**
   * getOrphanTerm returns true if this term
   * is only used as part of another term, such
   * as mellitus.  
   * 
   * @return boolean
   */
  // -----------------------------------------
  public boolean getOrphanTerm() {
    return this.orphanTerm;
  } // end Method getOrphanTerm() --------

  // -----------------------------------------
  /**
   * getNumberOfTokens returns the number of tokens in this key
   * 
   * @return
   */
  // -----------------------------------------
  public int getNumberOfTokens() {
    return this.numberOfTokens;
  } // end Method getNumberOfTokens() --------
  
  // -----------------------------------------
  /** 
   * getcitation retrieves citation
   *  
   * @return the citation
   */
  // -----------------------------------------
  public String getCitation() {
    return citation;
  }

  // -------------------------------------------------------
  /**
   * getSubTerms
   *
   * @return LexRecord[]
   */
  // -------------------------------------------------------
  public LexRecord[] getSubTerms() {
    return subTerms;
  } // end Method getSubTerms() ----------------------------

  // -------------------------------------------------------
  /**
   * getSubTermsString
   *
   * @return subTermsString
   */
  // -------------------------------------------------------
  public String getSubTermsString() {
   
    if ( this.subTermsString == null ) {
      if ( this.subTerms != null ) {
        StringBuffer       subTerms = new StringBuffer();
        StringBuffer normedSubTerms = new StringBuffer();
        for ( int k = 0; k < this.subTerms.length; k++ ) {
          LexRecord lexRecord = this.subTerms[k];
          subTerms.append( lexRecord.getTerm());
          normedSubTerms.append( lexRecord.getCitation());
          if ( k < this.subTerms.length -1) {
            subTerms.append("|");
            normedSubTerms.append("|");
          }
        } // end For loop
        this.subTermsString = subTerms.toString();
        this.normedSubTermsString = normedSubTerms.toString();
      }
    }
    return this.subTermsString;
  } // end Method getSubTermsString() -------------

  // ----------------------------------------------
  /** 
   * getNormedSubTermsString retrieves normedSubTermsString
   *  
   * @return the normedSubTermsString
   */
  // ----------------------------------------------
  public String getNormedSubTermsString() {
    
    if ( this.normedSubTermsString == null ) {
      if ( this.subTerms != null ) {
        StringBuffer       subTerms = new StringBuffer();
        StringBuffer normedSubTerms = new StringBuffer();
        for ( int k = 0; k < this.subTerms.length; k++ ) {
          LexRecord lexRecord = this.subTerms[k];
          subTerms.append( lexRecord.getTerm());
          normedSubTerms.append( lexRecord.getCitation());
          if ( k < this.subTerms.length -1) {
            subTerms.append("|");
            normedSubTerms.append("|");
          }
        } // end For loop
        this.subTermsString = subTerms.toString();
        this.normedSubTermsString = normedSubTerms.toString();
      }
    }
    return this.normedSubTermsString;
  } // end Method getNormedSubTermsString() --

  // -----------------------------------------
  /** 
   * getsemanticType retrieves semanticType
   *  
   * @return the semanticType
   */
  // -----------------------------------------
  public String getSemanticType() {
    return semanticType;
  }

//-----------------------------------------
 /** 
  * getTree returns the tree
  *  
  * @return the tree
  */
 // -----------------------------------------
 public String getTree() {
   return tree;
 }

//-----------------------------------------
/** 
 * getSABs returns the source abbreviations, colon delimited
 *  
 * @return the list of sabs
 */
// -----------------------------------------
 public String getSABs() {
   return this.sabs;
   
 }

 
//=================================================
/**
* getTTY retrieves the tty  - example rxNorm terms with tty=DF are dose forms.
* 
* @return String
*/
//=================================================
public String getTTY() {

   return tty;
}
 
  /**
 * @return the history
 */
public final String getHistory() {
  return history;
}

/**
 * @param history the history to set
 */
public final void setHistory(String history) {
  this.history = history;
}

/**
 * @return the distance
 */
public final int getDistance() {
  return distance;
}

/**
 * @param distance the distance to set
 */
public final void setDistance(int distance) {
  this.distance = distance;
}



  // -----------------------------------------
  /** 
   * setkey sets the value of key
   *  
   * @param pKey the key to set
   */
  // -----------------------------------------
  public void setKey(String pKey) {
    this.key = pKey;
  }

  // -----------------------------------------
  /** 
   * setTerm sets the value of term
   *  
   * @param pTerm the term to set
   */
  // -----------------------------------------
  public void setTerm(String pTerm) {
    this.term = pTerm.toCharArray();
  }
  // -------------------------------------------------------
  /**
   * setSubTerms sets the preComputed subterms of this term. 
   * These are pointers to other lexRecords.
   *
   * @param subTermz
   */
  // -------------------------------------------------------
  public void setSubTerms(LexRecord[] subTermz) {
    this.subTerms = subTermz;
    
  } // end Method setSubTerms() ----------------------------

  // -----------------------------------------
  /** 
   * setpos sets the value of pos
   *  
   * @param pPos the pos to set
   */
  // -----------------------------------------
  public void addPos(String pPos) {
    this.poss.add(pPos);
  }
  // -----------------------------------------
  /** 
   * addEui adds the eui to the list of eui's
   *  
   * @param pEui the eui to set
   */
  // -----------------------------------------
  public void addEui(String pEui) {
    this.euis.add( pEui );
  }
  // -----------------------------------------
  /** 
   * setlongestTermWithThisEnding sets the value of longestTermWithThisEnding
   *  
   * @param pLongestTermWithThisEnding the longestTermWithThisEnding to set
   */
  // -----------------------------------------
  public void setLongestTermWithThisEnding(int pLongestTermWithThisEnding) {
    this.longestTermWithThisEnding = pLongestTermWithThisEnding;
  }
  // -----------------------------------------
  /**
   * setNumberOfTokens sets the number of tokens in this key
   * 
   * @param pNumberOfTokens
   */
  // -----------------------------------------
  public void setNumberOfTokens( int pNumberOfTokens) {
     this.numberOfTokens= pNumberOfTokens;
  } // end Method setNumberOfTokens() --------
  
  
//-----------------------------------------
  /**
   * setOrphanTerm sets whether or not this term is
   * only a component of a larger term
   * 
   * @param pVal
   */
  // -----------------------------------------
  public void setOrphanTerm( boolean pVal ) {
     this.orphanTerm = pVal;
  } // end Method setOprhanTerm() ------------

  // -----------------------------------------
  /** 
   * setcitation sets the value of citation
   *  
   * @param citation the citation to set
   */
  // -----------------------------------------
  public void setCitation(String citation) {
    this.citation = citation;
  }

  // ----------------------------------
  /**
   * set Description
   *
   * @param subTermsString the subTermsString to set
   */
  // ----------------------------------
  public void setSubTermsString(String subTermsString) {
      
    this.subTermsString = subTermsString;
  }

  
  
  // -----------------------------------------
  /** 
   * setsemanticType sets the value of semanticType
   *  
   * @param semanticType the semanticType to set
   */
  // -----------------------------------------
  public void setSemanticType(String semanticType) {
    this.semanticType = semanticType;
  }


  // -----------------------------------------
  /** 
   * addsemanticType adds the value of semanticType
   *  
   * @param pSemanticType the semanticType to set
   */
  // -----------------------------------------
  public void addSemanticType(String pSemanticType) {
   
    if ( pSemanticType != null && pSemanticType.trim().length() > 0 )
      if ( this.semanticType != null && this.semanticType.trim().length() > 0 )
       this.semanticType = this.semanticType + ":" + pSemanticType;
      else
        this.semanticType = pSemanticType;
  }

  
  
  // -----------------------------------------
  /** 
   * setSABs sets the sab's 
   *  
   * @param pSabs
   */
  // -----------------------------------------
  public void setSABs(String pSabs) {
    this.sabs = pSabs;
  }

  
  // -----------------------------------------
  /** 
   * addSABs add to the set of colon separated sab's 
   *  
   * @param pSabs
   */
  // -----------------------------------------
  public void addSABs(String pSabs) {
  
    if ( pSabs != null && pSabs.trim().length() > 0  )
      if ( this.sabs != null && this.sabs.trim().length() > 0  )
        this.sabs = this.sabs + ":" + pSabs;
      else
        this.sabs = pSabs;
  }

  
  
  // -----------------------------------------
  /** 
   * setTTY sets the TTY (term type in the source) 
   *  
   * @param pTTY
   */
  // -----------------------------------------
  public void setTTY(String pTTY) {
    this.tty = pTTY;
  }


  // -----------------------------------------
  /** 
   * addTTY adds to the set of the TTY (term type in the source) 
   *  
   * @param pTTY
   */
  // -----------------------------------------
  public void addTTY(String pTTY) {

    if ( pTTY != null && pTTY.trim().length() > 0 )
      if ( this.tty != null && this.tty.trim().length() > 0 )
        this.tty = this.tty + ":" + pTTY;
      else
        this.tty = pTTY;
  }
  
  
  
  
  // -----------------------------------------
  /** 
   * setTree sets the value of the tree or subcategorization this term is in
   *  
   * @param pTree the tree to set
   */
  // -----------------------------------------
  public void setTree(String pTree) {
    this.tree = pTree;
  }

  

  // -----------------------------------------
  /** 
   * addTree adds to the set of values of the tree or subcategorization this term is in
   *  
   * @param pTree the tree to set
   */
  // -----------------------------------------
  public void addTree(String pTree) {
 
    if ( pTree != null && pTree.trim().length() > 0 )
      if ( this.tree != null && this.tree.trim().length() > 0 )
        this.tree = this.tree + ":" + pTree;
      else 
        this.tree = pTree;
  }



  // -----------------------------------------
  /**
   * getExtraStuff
   * 
   * @return
   */
  // -----------------------------------------
  public String getExtraStuff() {
    return this.extraStuff;
  } // end Method getExtraStuff() ------------



  // -----------------------------------------
  /**
   * setExtraStuff
   * 
   * @param pExtraStuff
   */
  // -----------------------------------------
  public void setExtraStuff(String pExtraStuff, String pCui) {
    this.extraStuff = pExtraStuff;
    
    if ( pExtraStuff != null && pExtraStuff.trim().length() > 0 ) {
      addExtraStuffAux( pExtraStuff, pCui);
    }
  
  } // end Method setExtraStuff() ------------

//-----------------------------------------
 /**
  * setExtraStuff
  * 
  * @param pExtraStuff
  */
 // -----------------------------------------
 public void addExtraStuff(String pExtraStuff, String pCui) {
   
   if ( pExtraStuff != null && pExtraStuff.trim().length() > 0   ) {
     if ( this.extraStuff != null && this.extraStuff.trim().length() > 0 )
       this.extraStuff =  this.extraStuff + "~" + pExtraStuff;
     else 
       this.extraStuff = pExtraStuff; 
   
     addExtraStuffAux( pExtraStuff, pCui);
   }

 
 } // end Method addExtraStuff() ------------


  
  
  
  // =================================================
/**
 * addExtraStuffAux 
 * 
 * @param pExtraStuff
*/
// =================================================
private void addExtraStuffAux(String pExtraStuff, String pCui) {
  String[] cols = U.split(pExtraStuff);
  if ( cols != null ) {
    
    switch( cols.length ) {
      
    case 8:  // data coming from lvg output + mrcon  
      //                                          |-> extra stuff --->
      //     # cui|key|pos|infl|base|citation|tuis|sab|sourceID|history|distance|tty|stt|reverseKey|numberEndsWith|tree
      if ( cols[0] != null )  addSABs(      cols[0]);
      // if ( cols[1] != null )  setSourceID(  cols[1]);
      if ( cols[2] != null )  setHistory(   cols[2]);
      if ( cols[3] != null )  { if ( cols[3] != null && U.isNumber(cols[3])) setDistance(  Integer.parseInt(cols[3])); }
      if ( cols[4] != null )  
        addTTY(       cols[4]);
      // if ( cols[5] != null )  setReverseKey(cols[5]);
      //if ( cols[6] != null )  setNumberEndsWith( cols[6]);
      if ( cols[7] != null )  setTree(      cols[7]);
      break;
    case 5:  
    case 4:  
    case 3: // from local .lragr files 
      if ( cols[0] != null )  addSABs(      cols[0]);
      if ( cols[1] != null )  addTTY(       cols[1]);
      if ( cols[2] != null )  addTree(      cols[2]); 
      break;
      
    case 12:  // This coming from mrconsosty rows - keep mrconsosty 
      if ( cols[0] != null )  addSABs(      cols[0]);
      if ( cols[4] != null )  addTTY(       cols[4]);
      if ( cols[7] != null )  setTree(      cols[7]);
      
      if ( this.mrconsostyFields == null )
        this.mrconsostyFields = new ArrayList<String>();
      this.mrconsostyFields.add( pCui + "|" + pExtraStuff);
      
      break;
    }
  }

} // end Method addExtraStuff() ------------------------

  // =======================================================
  /**
   * toPipedString returns a pipe delimited string in the 
   * following format: 
   * 
   *     term|eui|semanticType|extraStuff  ... |tree  if it is filled out
   *  where  
   *  
   *     |-> extra stuff --->
   *     |sab|sourceID|history|distance|tty|stt|reverseKey|numberEndsWith|tree
   *
   * 
   * @return String 
   */
  // =======================================================
  public String toPipedString() {
    
    StringBuffer out = new StringBuffer();
    out.append( this.getTerm());              out.append("|");
    out.append( this.getEui() );              out.append("|");
    out.append( this.getSemanticType());      out.append("|");
    String extraStuff =  this.getExtraStuff();
    if ( extraStuff != null  && extraStuff.length() > 0 && !extraStuff.equals("null")) {
      out.append( extraStuff);
      if ( this.tree != null )
        out.append("|" + this.tree );
    }
    return out.toString();
    // End Method toPipedString() ======================
  }



  // =================================================
  /**
   * toPipedStringRows returns a row for each cui 
   *   
   * @return String[]
  */
  // =================================================
    public final String[] toPipedStringRows() {
    
      String returnVal[] = null;
      int i = 0;
     
      
      if ( this.mrconsostyFields != null && !this.mrconsostyFields.isEmpty()) {
        returnVal = new String[ this.mrconsostyFields.size()];
        
          for ( String row: this.mrconsostyFields ) 
            returnVal[i++] =  this.getEui() + "|" +  this.getCitation() + "|" + this.getSemanticType() + "|" + row ;
        
      }
      
      
      return returnVal;
  } // end Method toPipedStringRows() ---------------



  // ------------------------
  // Class Variables 
  // ------------------------
  private String                  key = null;
  // ----------------------------------------------
  /** 
   * setNormedSubTermsString sets the value of normedSubTermsString
   *  
   * @param normedSubTermsString the normedSubTermsString to set
   *
   */
  // ----------------------------------------------
  public void setNormedSubTermsString(String normedSubTermsString) {
    this.normedSubTermsString = normedSubTermsString;
  }



  private char[]                 term = null;
  private String             citation = null;
  private ArrayList<String>      euis = new ArrayList<String>(1);
  private ArrayList<String>      poss = new ArrayList<String>(1);
  private LexRecord         subTerms[] = null;
  private String       subTermsString = null;
  private String normedSubTermsString = null;
  private String         semanticType = null;
  private String           extraStuff = null;
  private String                 tree = null;
  private String                 sabs = null;
  private String                  tty = null;
  private String              history = null;
  private int                distance = 0;
  private List<String>     mrconsostyFields = null;
//   private String     termTypeInSource = null  ==  tty 
  
  private boolean       orphanTerm = false;
  private int       numberOfTokens = 0;
  private int longestTermWithThisEnding = 0;
  
  
  //LRAGR columns 
  public final static int  EUI = 0;
  public final static int  TERM = 1;
  public final static int  CATEGORY = 2;
  public final static int  INFLECTION = 3;
  public final static int  UNINFLECTION = 4;
  public final static int  CITATION_FORM = 5;
  public final static int  SEMANTIC_TYPE = 6;
  public final static int  SABS = 7;
  
  
  
  
  public final static int  TREE = 15;
 
 
  
  
  //     # cui|key|pos|infl|base|citation|tuis|sab|sourceID|history|distance|tty|stt|reverseKey|numberEndsWith|tree
      

 public static final int LRAGR_FIELD_ID                        =  0;
 public static final int LRAGR_FIELD_KEY                       =  1;
 public static final int LRAGR_FIELD_POS                       =  2;
 public static final int LRAGR_FIELD_INFL                      =  3;
 public static final int LRAGR_FIELD_BASEFORM                  =  4;
 public static final int LRAGR_FIELD_CITATION_FORM             =  5;
 public static final int LRAGR_FIELD_CATEGORIES                =  6;
 public static final int LRAGR_FIELD_SAB                       =  7;
 public static final int LRAGR_FIELD_SOURCE_ID                 =  8;
 public static final int LRAGR_FIELD_FLOW_HISTORY              =  9;
 public static final int LRAGR_FIELD_DISTANCE                  = 10;
 public static final int LRAGR_FIELD_TTY                       = 11;
 public static final int LRAGR_FIELD_STT                       = 12;
 public static final int LRAGR_FIELD_REVERSE_KEY               = 13;
 public static final int LRAGR_FIELD_NUMBER_THAT_ENDS_WITH_KEY = 14;
 public static final int LRAGR_FIELD_TREE                      = 15;

 public static final int LRAGR_FIELD_CUI                       = LRAGR_FIELD_ID;

 
 
  
  
} // end Class LexRecord() -------------------
