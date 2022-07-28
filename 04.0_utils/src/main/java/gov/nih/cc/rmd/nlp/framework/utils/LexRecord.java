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
 * LexRecord is a container that holds information about a term
 *
 * @author  Guy Divita 
 * @created Feb 21, 2011
 *
 * *  
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.utils;

import java.util.ArrayList;
import java.util.List;



public class LexRecord {

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
  public LexRecord(String pKey, String pTerm, String pCitation, String pPos, String pEui) {
    this.key = pKey;
    this.term = pTerm.toCharArray();
    this.citation = pCitation;
    this.poss.add(pPos);
    this.euis.add(pEui);
  } // end Constructor() ---------------------

  // -----------------------------------------
  /**
   * Constructor: Creates a lexical record that is an an orphan word. An orphan
   * word is one that only happens in the context of a larger term.
   *
   * @param pKey
   * @param pTerm
   * @param pCitation
   * @param pPOS
   * @param pEui
   * @param pNumberOfTokens
   */
  // -----------------------------------------

  public LexRecord(String pkey, String pTerm, String pCitation, String pPOS, String pEui,
      int pNumberOfTokens) {
    this.key = pkey;
    this.term = pTerm.toCharArray();
    this.citation = pCitation;
    this.poss.add(pPOS);
    this.euis.add(pEui);
    this.orphanTerm = false;
    this.numberOfTokens = pNumberOfTokens;

  } // end Constructor() ---------------------

  // -----------------------------------------
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
  public LexRecord(String pKey, String pTerm, String pCitation, String pPOS, String pEui,
      boolean pOrphan, int pNumberOfTokens) {
    this.key = pKey;
    this.term = pTerm.toCharArray();
    this.setCitation(pCitation);
    this.poss.add(pPOS);
    this.euis.add(pEui);
    this.orphanTerm = pOrphan;
    this.numberOfTokens = pNumberOfTokens;
  } // end Constructor() ---------------------

  // -----------------------------------------
  /**
   * Constructor: LexRecord.java Summary [Detail Here]
   *
   * @param pKey
   * @param term2
   * @param citation2
   * @param pennTreebankPOS
   * @param eui2
   * @param numberOfTokens2
   * @param pSemanticType
   */
  // -----------------------------------------

  public LexRecord(String pKey, String pTerm, String pCitation, String pPOS, String pEui,
      int pNumberOfTokens, String pSemanticType) {
    this.key = pKey;
    this.term = pTerm.toCharArray();
    this.setCitation(pCitation);
    this.poss.add(pPOS);
    this.euis.add(pEui);
    this.numberOfTokens = pNumberOfTokens;
    this.setSemanticType(pSemanticType);

  } // end Contstructor() --------------------

  // -----------------------------------------
  /**
   * Constructor
   * 
   * @param object
   * @param term2
   * @param citation2
   * @param pennTreebankPOS
   * @param eui2
   * @param numberOfTokens2
   * @param semanticType2
   * @param extraStuff
   */
  // -----------------------------------------
  public LexRecord(String pKey, String pTerm, String pCitation, String pPOS, String pEui,
      int pNumberOfTokens, String pSemanticType, String pExtraStuff) {
    this.key = pKey;
    this.term = pTerm.toCharArray();
    this.setCitation(pCitation);
    this.poss.add(pPOS);
    this.euis.add(pEui);
    this.numberOfTokens = pNumberOfTokens;
    this.setSemanticType(pSemanticType);
    this.extraStuff = pExtraStuff;

  }

  // -----------------------------------------
  /**
   * parseLRAGR returns a lexRecord given a row from the LRAGR table
   * 
   * This method leaves the key and number of tokens as null. The the key field
   * requires the tokenizer, which is de-coupled from this class, and should be
   * called separately. The number of tokens depends on the term being correctly
   * tokenized, which should be done outside this class.
   * 
   * @return LexRecord
   */
  // -----------------------------------------
  public static LexRecord parseLRAGR(String pRow) {

    LexRecord lexRec = null;
    String columns[] = U.splitBetter(pRow);

    try {
      String eui = columns[EUI];
      String term = columns[TERM];
      String category = columns[CATEGORY];
      String inflection = columns[INFLECTION];
      String citation = columns[CITATION_FORM];
      String semanticType = "";

      try {
        semanticType = columns[SEMANTIC_TYPE];
      } catch (Exception e) {
      }
      ;

      StringBuffer otherBuff = new StringBuffer();
      String extraStuff = null;
      for (int i = 7; i < columns.length; i++)
        otherBuff.append(columns[i] + "|");
      if (otherBuff != null && otherBuff.length() > 0)
        extraStuff = otherBuff.toString();

      String pennTreebankPOS = DTaggerUtilities.toPenn(term, category, inflection);

      int numberOfTokens = -1;
      lexRec = new LexRecord(null, term, citation, pennTreebankPOS, eui, numberOfTokens,
          semanticType, extraStuff);
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("not able to parse line \n|" + pRow + "|\n");
      System.err.println(e.toString());
    }
    return lexRec;
  } // end Method parseLRAGR() --------------

  // -----------------------------------------
  /**
   * toString
   *
   * @return String
   */
  // -----------------------------------------
  public String toString() {
    StringBuffer buff = new StringBuffer();
    buff.append("LexRecord");
    buff.append("|");
    buff.append(this.term);
    buff.append("|");

    buff.append("[");
    for (int i = 0; i < this.euis.size(); i++) {
      buff.append(this.euis.get(i));
      buff.append("|");
      buff.append(this.poss.get(i));
      buff.append("|");
    }
    buff.append("]");
    buff.append(this.orphanTerm);
    buff.append("|");
    buff.append(this.citation);
    buff.append("|");
    buff.append(this.numberOfTokens);
    buff.append("|");

    String subTermsString = this.getSubTermsString();

    if (subTermsString != null)
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
   * getPoss retrieves all the possible parts of speech for this key.
   * 
   * @return List<String>
   */
  // -----------------------------------------
  public List<String> getPoss() {
    return this.poss;
  }

  // -------------------------------------------------------
  /**
   * getPos retrieves the first part of speech from the list of pos's. This
   * method is useful when using the lexRecord to hold sinlge or temporary
   * entries
   *
   * @return String
   */
  // -------------------------------------------------------
  public String getPos() {
    String pos = null;
    if (this.poss != null)
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
   * getEui retrieves the first eui from the list of eui's. This method is
   * useful when using the lexRecord to hold single or temporary entries.
   *
   * @return String
   */
  // -------------------------------------------------------
  public String getEui() {
    String eui = null;
    if (this.euis != null)
      eui = this.euis.get(0);
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
   * getOrphanTerm returns true if this term is only used as part of another
   * term, such as mellitus.
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

    if (this.subTermsString == null) {
      if (this.subTerms != null) {
        StringBuffer subTerms = new StringBuffer();
        StringBuffer normedSubTerms = new StringBuffer();
        for (int k = 0; k < this.subTerms.length; k++) {
          LexRecord lexRecord = this.subTerms[k];
          subTerms.append(lexRecord.getTerm());
          normedSubTerms.append(lexRecord.getCitation());
          if (k < this.subTerms.length - 1) {
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

    if (this.normedSubTermsString == null) {
      if (this.subTerms != null) {
        StringBuffer subTerms = new StringBuffer();
        StringBuffer normedSubTerms = new StringBuffer();
        for (int k = 0; k < this.subTerms.length; k++) {
          LexRecord lexRecord = this.subTerms[k];
          subTerms.append(lexRecord.getTerm());
          normedSubTerms.append(lexRecord.getCitation());
          if (k < this.subTerms.length - 1) {
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
   * setSubTerms sets the preComputed subterms of this term. These are pointers
   * to other lexRecords.
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
    this.euis.add(pEui);
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
  public void setNumberOfTokens(int pNumberOfTokens) {
    this.numberOfTokens = pNumberOfTokens;
  } // end Method setNumberOfTokens() --------

  // -----------------------------------------
  /**
   * setOrphanTerm sets whether or not this term is only a component of a larger
   * term
   * 
   * @param pVal
   */
  // -----------------------------------------
  public void setOrphanTerm(boolean pVal) {
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
  public void setExtraStuff(String pExtraStuff) {
    this.extraStuff = pExtraStuff;
  } // end Method setExtraStuff() ------------

  // =======================================================
  /**
   * toPipedString returns a pipe delimited string in the following format:
   * term|eui|semanticType|extraStuff
   * 
   * @return String
   */
  // =======================================================
  public String toPipedString() {

    StringBuffer out = new StringBuffer();
    out.append(this.getTerm());
    out.append("|");
    out.append(this.getEui());
    out.append("|");
    out.append(this.getSemanticType());
    out.append("|");
    String extraStuff = this.getExtraStuff();
    if (extraStuff != null && extraStuff.length() > 0 && !extraStuff.equals("null"))
      out.append(extraStuff);

    return out.toString();
    // End Method toPipedString() ======================
  }

  // ------------------------
  // Class Variables
  // ------------------------
  private String key = null;

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

  private char[] term = null;

  private String citation = null;

  private ArrayList<String> euis = new ArrayList<String>(1);

  private ArrayList<String> poss = new ArrayList<String>(1);

  private LexRecord subTerms[] = null;

  private String subTermsString = null;

  private String normedSubTermsString = null;

  private String semanticType = null;

  private String extraStuff = null;

  private boolean orphanTerm = false;

  private int numberOfTokens = 0;

  private int longestTermWithThisEnding = 0;

  // LRAGR columns
  public final static int EUI = 0;

  public final static int TERM = 1;

  public final static int CATEGORY = 2;

  public final static int INFLECTION = 3;

  public final static int UNINFLECTION = 4;

  public final static int CITATION_FORM = 5;

  public final static int SEMANTIC_TYPE = 6;

} // end Class LexRecord() -------------------
