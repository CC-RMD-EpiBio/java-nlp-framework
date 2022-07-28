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
 * LRAGRRow is a container for lragr + mrconso + mrdef like rows
 *
 * @author     Guy Divita
 * @created    Jun 14, 2018
 * 
*/
// =================================================
package gov.nih.cc.rmd.nlp.framework.utils.terminologies.lookup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;



import gov.nih.cc.rmd.nlp.framework.utils.U;

public class LRAGRRowDeprecated {

    
  private String query = null;
  private String name = null;
  /**
   * @return the name
   */
  public final String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public final void setName(String name) {
    this.name = name;
  }
  private String cuis = null;
  private String POS = null;
  private String inflection = null;
  private String uninflectedForm = null;
  private String citationForm = null;
  private List<String> semanticTypes = null;
  private String terminology = null;
  private String sourceId = null;
  private String variationHistory= null;
  private int flowDistance = 0;
  private String preferredTerm = null;
  private String termTypeInSource = null;
  private int beginOffsetInQuery = 0;
  private int endOffsetInQuery = 0;
 // private List<Definition> definitions = null;
  private String rowOffsetKey = null;
  private boolean processMe = true;
  private double uncertainty = 0.0;
  
  /**
   * @return the processMe
   */
  public boolean isProcessMe() {
    return processMe;
  }

  /**
   * @param processMe the processMe to set
   */
  public void setProcessMe(boolean processMe) {
    this.processMe = processMe;
  }

  /**
   * @return the uncertainty
   */
  public double getUncertainty() {
    return uncertainty;
  }

  /**
   * @param uncertainty the uncertainty to set
   */
  public void setUncertainty(double uncertainty) {
    this.uncertainty = uncertainty;
  }

  /**
   * @return String of the form
   */
  public final String toLRAGRString() {
    
    StringBuffer buff = new StringBuffer();
  
    buff.append(cuis);                                 buff.append("|");
    buff.append(name);                                 buff.append("|");
    buff.append(POS);                                  buff.append("|");
    buff.append(inflection);                           buff.append("|");
    buff.append(uninflectedForm);                      buff.append("|");
    buff.append(citationForm);                         buff.append("|");
    buff.append(list2String(semanticTypes,  ':') );  buff.append("|");
    buff.append(terminology);                          buff.append("|");
    buff.append(sourceId);                             buff.append("|");
    buff.append(variationHistory);                     buff.append("|");
    buff.append(flowDistance);                         buff.append("|");
    buff.append(preferredTerm);                        buff.append("|");
    buff.append(termTypeInSource);                     buff.append("|"); 
    buff.append(beginOffsetInQuery);                   buff.append("|");
    buff.append(endOffsetInQuery);                     buff.append("|");
    buff.append(uncertainty);                          buff.append("|");
    buff.append(processMe );                           buff.append("|");
  //  buff.append( toDefString( definitions ) );
    
    return buff.toString();
  }
 
  // =================================================
  /**
   * list2String [TBD] summary
   * 
   * @param semanticTypes2
   * @param c
   * @return
  */
  // =================================================
private String list2String(List<String> semanticTypes, char c) {
 
  StringBuffer buff = null;
  String returnVal = null;
  if ( semanticTypes != null && semanticTypes.size() > 0) {
    buff = new StringBuffer();
    for ( int i = 0;  i < semanticTypes.size(); i++) {
      buff.append( semanticTypes.get(i) + ":");
    }
    returnVal = buff.toString().substring(0, buff.length() -1 );
  }  
    
  return returnVal;
}  // end Method list2String() -----------------------

  // =================================================
  /**
   * getSemanticTypes
   * 
   * @param pRows
   * @return
   */
  // =================================================
  public static String getSemanticTypes(List<LRAGRRowDeprecated> pRows) {

    String semanticTypes = "unknown";

    if (pRows != null && !pRows.isEmpty()) {
      HashSet<String> sTypes = new HashSet<String>();
      StringBuffer buff = new StringBuffer();
      for (LRAGRRowDeprecated row : pRows) {
        List<String> semanticTypez = row.getSemanticTypes();
        if (semanticTypez != null && !semanticTypez.isEmpty()) for (String aType : semanticTypez)
          sTypes.add(aType);
      }
      if (sTypes != null && sTypes.size() > 0) {
        Iterator<String> itr = sTypes.iterator();
        while (itr.hasNext())
          buff.append(itr.next() + ":");

        semanticTypes = buff.toString().substring(0, buff.length() - 2);
      }
    }
    return semanticTypes;
  } // end Method getSemanticTypes() -----------------
  

//=================================================
/**
* getCuis retrieves a unique set of cuis give a set
* of lragr rows with cuis in it.
* 
* @param resultRows
* @return String     cui:cui: ...
*/
//=================================================
public static final String getCuis(List<LRAGRRowDeprecated> pLragrRows) {
 
 String returnVal = null;
 HashSet<String> cuiz = getCuisSet( pLragrRows);
 
 Iterator<String> itr = null;
 if ( cuiz != null && !cuiz.isEmpty() ) {
   StringBuffer buff = new StringBuffer();  
   itr = cuiz.iterator();
   while (itr.hasNext() ) 
     buff.append(itr.next() + ":" );
   returnVal = buff.toString().substring(0, buff.toString().length() -1);
  
 }
 return returnVal;
} // end Method getCuis() -------------------------------



// =================================================
/**
* getCuisList 
* 
* @param pRows
* @return List<String>
*/
//=================================================
public static HashSet<String> getCuisSet(List<LRAGRRowDeprecated> pLragrRows) {

  HashSet<String> cuiz = new HashSet<String>();
  
  if ( pLragrRows != null && !pLragrRows.isEmpty())
  for ( LRAGRRowDeprecated row: pLragrRows ) {
    String cuis = row.getCuis();
    if ( cuis != null && !cuis.equals("unknown")) {
       String cuiss[] = U.split(cuis, ":");
       for ( String aCui: cuiss)
         cuiz.add( aCui);
    }
  }
  
  return cuiz;
}

  // =================================================
/**
 * getCuisList 
 * 
 * @param pRows
 * @return List<String>
*/
// =================================================
public static List<String> getCuisList(List<LRAGRRowDeprecated> pLragrRows) {

  HashSet<String> cuiz = getCuisSet( pLragrRows);
  List<String> returnVal = null;
 
  Iterator<String> itr;
  if ( cuiz != null && !cuiz.isEmpty() ) {
    returnVal = new ArrayList<String>( cuiz.size());
    itr = cuiz.iterator();
    while (itr.hasNext())
      returnVal.add(itr.next());
  }
  
  return returnVal;
} // end Method getCuisList() 

  // =================================================
  /**
   * getConceptNames retrieves a unique set of conceptNames give a set
   * of lragr rows with conceptNames in it.
   * 
   * @param resultRows
   * @return String cui:cui: ...
   */
  // =================================================
  public static final String getConceptNames(List<LRAGRRowDeprecated> pLragrRows) {

    String returnVal = null;
    
    if ( pLragrRows != null && !pLragrRows.isEmpty()) {
    HashSet<String> conceptNamez = new HashSet<String>();

    for (LRAGRRowDeprecated row : pLragrRows) {
      String conceptName = row.getName();
      if (conceptName != null && !conceptName.equals("unknown")) {
        String conceptNamess[] = U.split(conceptName, ":");
        for (String aConceptName : conceptNamess)
          conceptNamez.add(aConceptName);
      }
    }
    Iterator<String> itr;
    if (conceptNamez != null && !conceptNamez.isEmpty()) {
      StringBuffer buff = new StringBuffer();
      itr = conceptNamez.iterator();
      while (itr.hasNext())
        buff.append(itr.next() + ":");
      returnVal = buff.toString().substring(0, buff.toString().length() - 1);

    }
    }
    return returnVal;
  } // end Method getConceptNames() -------------------------------


//=================================================
 /**
  * getSAB retrieves a unique set of terminologies give a set
  * of lragr rows with terminologies in it.
  * 
  * @param resultRows
  * @return String cui:cui: ...
  */
 // =================================================
 public static final String getSABs(List<LRAGRRowDeprecated> pLragrRows) {

   String returnVal = null;
   HashSet<String> bufz = new HashSet<String>();

   for (LRAGRRowDeprecated row : pLragrRows) {
     String buf = row.getTerminology();
     if (buf != null && !buf.equals("unknown")) {
       String bufss[] = U.split(buf, ":");
       for (String abuf : bufss)
         bufz.add(abuf);
     }
   }
   Iterator<String> itr;
   if (bufz != null && !bufz.isEmpty()) {
     StringBuffer buff = new StringBuffer();
     itr = bufz.iterator();
     while (itr.hasNext())
       buff.append(itr.next() + ":");
     returnVal = buff.toString().substring(0, buff.toString().length() - 1);

   }
   return returnVal;
 } // end Method getSAB() -------------------------------

 
//=================================================
/**
 * getsourceID retrieves a unique set of sourceIDs give a set
 * of lragr rows with sourceID's in it.
 * 
 * @param resultRows
 * @return String cui:cui: ...
 */
// =================================================
public static final String getsourceIDs(List<LRAGRRowDeprecated> pLragrRows) {

  String returnVal = null;
  HashSet<String> bufz = new HashSet<String>();

  for (LRAGRRowDeprecated row : pLragrRows) {
    String buf = row.getSourceId();
    if (buf != null && !buf.equals("unknown")) {
      String bufss[] = U.split(buf, ":");
      for (String abuf : bufss)
        bufz.add(abuf);
    }
  }
  Iterator<String> itr;
  if (bufz != null && !bufz.isEmpty()) {
    StringBuffer buff = new StringBuffer();
    itr = bufz.iterator();
    while (itr.hasNext())
      buff.append(itr.next() + ":");
    returnVal = buff.toString().substring(0, buff.toString().length() - 1);

  }
  return returnVal;
} // end Method getsourceID() -------------------------------



//=================================================
/**
* getvariantHistorys retrieves a unique set of variantHistoryss give a set
* of lragr rows with variantHistorys's in it.
* 
* @param resultRows
* @return String 
*/
//=================================================
public static final String getvariantHistorys(List<LRAGRRowDeprecated> pLragrRows) {

String returnVal = null;
HashSet<String> bufz = new HashSet<String>();

for (LRAGRRowDeprecated row : pLragrRows) {
  String buf = row.getVariationHistory();
  if (buf != null && !buf.equals("unknown")) {
    String bufss[] = U.split(buf, ":");
    for (String abuf : bufss)
      bufz.add(abuf);
  }
}
Iterator<String> itr;
if (bufz != null && !bufz.isEmpty()) {
  StringBuffer buff = new StringBuffer();
  itr = bufz.iterator();
  while (itr.hasNext())
    buff.append(itr.next() + ":");
  returnVal = buff.toString().substring(0, buff.toString().length() - 1);

}
return returnVal;
} // end Method getvariantHistorys() -------------------------------


//=================================================
/**
* getvariantDistances retrieves a unique set of variantDistancess give a set
* of lragr rows with variantDistances's in it.
* 
* @param resultRows
* @return String 
*/
//=================================================
public static final String getvariantDistancess(List<LRAGRRowDeprecated> pLragrRows) {

String returnVal = null;
HashSet<String> bufz = new HashSet<String>();

for (LRAGRRowDeprecated row : pLragrRows) {
String buf = String.valueOf(row.getFlowDistance() );
if (buf != null && !buf.equals("unknown")) {
  String bufss[] = U.split(buf, ":");
  for (String abuf : bufss)
    bufz.add(abuf);
}
}
Iterator<String> itr;
if (bufz != null && !bufz.isEmpty()) {
StringBuffer buff = new StringBuffer();
itr = bufz.iterator();
while (itr.hasNext())
  buff.append(itr.next() + ":");
returnVal = buff.toString().substring(0, buff.toString().length() - 1);

}
return returnVal;
} // end Method getvariantDistances() -------------------------------


//=================================================
/**
* getPreferredTerms retrieves a unique set of STT give a set
* of lragr rows with STT's in it.
* 
* @param resultRows
* @return String 
*/
//=================================================
public static final String getPerferredTerms(List<LRAGRRowDeprecated> pLragrRows) {

String returnVal = null;
HashSet<String> bufz = new HashSet<String>();

for (LRAGRRowDeprecated row : pLragrRows) {
String buf = row.getPreferredTerm();
if (buf != null && !buf.equals("unknown")) {
  String bufss[] = U.split(buf, ":");
  for (String abuf : bufss)
    bufz.add(abuf);
}
}
Iterator<String> itr;
if (bufz != null && !bufz.isEmpty()) {
StringBuffer buff = new StringBuffer();
itr = bufz.iterator();
while (itr.hasNext())
  buff.append(itr.next() + ":");
returnVal = buff.toString().substring(0, buff.toString().length() - 1);

}
return returnVal;
} // end Method getPreferredTerms() -------------------------------


//=================================================
/**
* getTermTypeInSources retrieves a unique set of TTS give a set
* of lragr rows with TTS's in it.
* 
* @param resultRows
* @return String 
*/
//=================================================
public static final String getTermTypeInSources(List<LRAGRRowDeprecated> pLragrRows) {

String returnVal = null;
HashSet<String> bufz = new HashSet<String>();

for (LRAGRRowDeprecated row : pLragrRows) {
String buf = row.getPreferredTerm();
if (buf != null && !buf.equals("unknown")) {
String bufss[] = U.split(buf, ":");
for (String abuf : bufss)
  bufz.add(abuf);
}
}
Iterator<String> itr;
if (bufz != null && !bufz.isEmpty()) {
StringBuffer buff = new StringBuffer();
itr = bufz.iterator();
while (itr.hasNext())
buff.append(itr.next() + ":");
returnVal = buff.toString().substring(0, buff.toString().length() - 1);

}
return returnVal;
} // end Method getTermTypeInSources() -------------------------------



  /**
   * @return String colon separated defintions
   *
  public final String toDefString(  List<Definition> pDefinitionz ) {
   
    String returnVal = null;
    
    
    if ( pDefinitionz != null && !pDefinitionz.isEmpty() ) {
      StringBuffer buff = new StringBuffer();
   
      for ( Definition def : pDefinitionz) 
        buff.append(  def.getValue()  + ":" );
    
      returnVal = buff.toString();
    }
    
    return returnVal;
  }
  */
  
  /**
   * @return the rowOffsetKey
   */
  public final String getRowOffsetKey() {
    return rowOffsetKey;
  }
  /**
   * @param rowOffsetKey the rowOffsetKey to set
   */
  public final void setRowOffsetKey(String rowOffsetKey) {
    this.rowOffsetKey = rowOffsetKey;
  }
  /**
   * @return the query
   */
  public final String getQuery() {
    return query;
  }
  /**
   * @param query the query to set
   */
  public final void setQuery(String query) {
    this.query = query;
  }
  /**
   * @return the cuis
   */
  public final String getCuis() {
    return cuis;
  }
  /**
   * @param cuis the cuis to set
   */
  public final void setCuis(String cuis) {
    this.cuis = cuis;
  }
  /**
   * @return the pOS
   */
  public final String getPOS() {
    return POS;
  }
  /**
   * @param pOS the pOS to set
   */
  public final void setPOS(String pOS) {
    POS = pOS;
  }
  /**
   * @return the inflection
   */
  public final String getInflection() {
    return inflection;
  }
  /**
   * @param inflection the inflection to set
   */
  public final void setInflection(String inflection) {
    this.inflection = inflection;
  }
  /**
   * @return the uninflectedForm
   */
  public final String getUninflectedForm() {
    return uninflectedForm;
  }
  /**
   * @param uninflectedForm the uninflectedForm to set
   */
  public final void setUninflectedForm(String uninflectedForm) {
    this.uninflectedForm = uninflectedForm;
  }
  /**
   * @return the citationForm
   */
  public final String getCitationForm() {
    return citationForm;
  }
  /**
   * @param citationForm the citationForm to set
   */
  public final void setCitationForm(String citationForm) {
    this.citationForm = citationForm;
  }
  /**
   * @return the semanticTypes
   */
  public final List<String> getSemanticTypes() {
    return semanticTypes;
  }
  /**
   * @param semanticTypes the semanticTypes to set
   */
  public final void setSemanticTypes(List<String> semanticTypes) {
    this.semanticTypes = semanticTypes;
  }
  /**
   * @return the terminology
   */
  public final String getTerminology() {
    return terminology;
  }
  /**
   * @param terminology the terminology to set
   */
  public final void setTerminology(String terminology) {
    this.terminology = terminology;
  }
  /**
   * @return the sourceId
   */
  public final String getSourceId() {
    return sourceId;
  }
  /**
   * @param sourceId the sourceId to set
   */
  public final void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }
  /**
   * @return the variationHistory
   */
  public final String getVariationHistory() {
    return variationHistory;
  }
  /**
   * @param variationHistory the variationHistory to set
   */
  public final void setVariationHistory(String variationHistory) {
    this.variationHistory = variationHistory;
  }
  /**
   * @return the flowDistance
   */
  public final int getFlowDistance() {
    return flowDistance;
  }
  /**
   * @param flowDistance the flowDistance to set
   */
  public final void setFlowDistance(int flowDistance) {
    this.flowDistance = flowDistance;
  }
  /**
   * @return the preferredTerm
   */
  public final String getPreferredTerm() {
    return preferredTerm;
  }
  /**
   * @param preferredTerm the preferredTerm to set
   */
  public final void setPreferredTerm(String preferredTerm) {
    this.preferredTerm = preferredTerm;
  }
  /**
   * @return the termTypeInSource
   */
  public final String getTermTypeInSource() {
    return termTypeInSource;
  }
  /**
   * @param termTypeInSource the termTypeInSource to set
   */
  public final void setTermTypeInSource(String termTypeInSource) {
    this.termTypeInSource = termTypeInSource;
  }
  /**
   * @return the beginOffsetInQuery
   */
  public final int getBeginOffsetInQuery() {
    return beginOffsetInQuery;
  }
  /**
   * @param beginOffsetInQuery the beginOffsetInQuery to set
   */
  public final void setBeginOffsetInQuery(int beginOffsetInQuery) {
    this.beginOffsetInQuery = beginOffsetInQuery;
  }
  /**
   * @return the endOffsetInQuery
   */
  public final int getEndOffsetInQuery() {
    return endOffsetInQuery;
  }
  /**
   * @param endOffsetInQuery the endOffsetInQuery to set
   */
  public final void setEndOffsetInQuery(int endOffsetInQuery) {
    this.endOffsetInQuery = endOffsetInQuery;
  }
  /**
   * @return the definitions
   *
  public final List<Definition> getDefinitions() {
    return definitions;
  } 
  */
  /**
   * @param definitions the definitions to set
   *
  public final void setDefinitions(List<Definition> definitions) {
    this.definitions = definitions;
  }
  */
 
  
}
