/*******************************************************************************
 * ---------------------------------------------------------------------------
 * NIH Clinical Center
 * Department of Rehabilitation
 * Epidemiology and Biostatistics Branch
 * 2019
 * 
 * This work is licensed under the Creative Commons Attribution 4.0 International License.
 * 
 * This license allows you to use, share and adapt for any purpose, provided:
 * Provide attribution to the creators of this work within your work.
 * Indicate if changes were made to this work.
 * No claim to merchantability, implied warranty, or liability can be made.
 * 
 * When attributing this code, please make reference to
 * [citation/url here] .
 * 
 * In the absence of a specific paper or url listed above, reference http://clinicalcenter.nih.gov/rmd/eb/nlp
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 ******************************************************************************/
// =================================================
/**
 * PunctuationTerms finds known terms that are soley punctuation
 * but should be terms.  This is a fix to the term lookup alogorithm 
 * which ignores punctuation sequences of characters.
 * 
 * This process relies upon a lragr file with the punctuation being looked for
 * 
 *
 * @author Guy Divita
 * @created Sept 16 2020
 *
 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.shapes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.U;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.LexicalElement;
import gov.va.chir.model.WordToken;

public class PunctuationTermsAnnotator extends JCasAnnotator_ImplBase {

  // -----------------------------------------
  /**
   * process retrieves lines of the document
   * looks for patterns with the line
   * 
   * Sometimes a line will include columns in tables
   * Keep the window to look in within a column.
   * Columns are delimited by tabs.
   * 
   * The tension here is that the more I make separate
   * patterns, the slower this annotator becomes.
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {

    try {
      this.performanceMeter.startCounter();
      GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process", " Start " + this.getClass().getSimpleName());
      

      List<Annotation> words = UIMAUtil.getAnnotations(pJCas, WordToken.typeIndexID);

      if (words != null && !words.isEmpty()) {
        for ( Annotation word : words )   {
          
          
          String buff = word.getCoveredText();
          
          if ( !U.containsLetters(buff) && U.containsNumber(buff))
            possiblecreateNumber(pJCas, word );
          
          if ( ((WordToken)word).getPunctuationOnly() ) {
            
           List<String> lragrRows = this.punctuationTerms.get( buff );
           
          
           // sometimes there is trailing punctuation after the term
           if ( lragrRows == null && buff.length() > 1) {
             buff = buff.substring(0, buff.length() -1 );
             lragrRows = this.punctuationTerms.get( buff);
             
           }
             
            if ( lragrRows != null && !lragrRows.isEmpty()  )
              createLexicalElement( pJCas, (WordToken) word, buff, lragrRows );
          }
        }
        
      }

    } catch (

    Exception e) {
      e.printStackTrace();
      String msg = "Issue with one of the shapes " + e.toString();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "process", msg);
    }
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process", " End " + this.getClass().getSimpleName());
    this.performanceMeter.stopCounter();    

  } // end Method process() ----------------

  // =================================================
  /**
   * possiblecreateNumber if a number doesn't already exist, will create a number
   * 
   * @param pJCas
   * @param pWord
  */
  // =================================================
   private void possiblecreateNumber(JCas pJCas, Annotation pWord) {

     List<Annotation> numbers = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, gov.nih.cc.rmd.framework.model.PotentialNumber.typeIndexID, pWord.getBegin(), pWord.getEnd() );
     
     if ( numbers != null && !numbers.isEmpty()) {
       Annotation aNumber = numbers.get(0);
     
       if (!( aNumber.getBegin() == pWord.getBegin() && aNumber.getEnd() == pWord.getEnd() )) {
         createNumber( pJCas, pWord );
         for ( Annotation number: numbers ) {
          
           number.removeFromIndexes();
         }
       }
     } else {
       List<Annotation> dates = UIMAUtil.fuzzyFindAnnotationsBySpan(pJCas, gov.nih.cc.rmd.framework.model.Date.typeIndexID, pWord.getBegin(), pWord.getEnd() );
       if ( dates == null || dates.isEmpty() )
         createNumber( pJCas, pWord );
     }
   
     
  } // end Method possiblecreateNumber() -----

  // =================================================
  /**
   * createNumber 
   * 
   * @param pJCas
   * @param pWord
  */
  // =================================================
  private final void createNumber(JCas pJCas, Annotation pWord) {
 
    gov.nih.cc.rmd.framework.model.PotentialNumber statement = new gov.nih.cc.rmd.framework.model.PotentialNumber( pJCas);
    statement.setBegin( pWord.getBegin() );
    statement.setEnd( pWord.getEnd() );
    statement.setId("PunctuationTermsAnnotator_createNumber_" + this.annotationCounter);
    statement.addToIndexes();
    
  } // end Method createNumber() -------------

  // -----------------------------------------
  /**
   * createAnnotation will create an annotation of the class pClassType.
   *
   * @param pJCas
   * @param pWorkdToken
   * @param pLragrRows
   */
  // -----------------------------------------
  private void createLexicalElement(JCas pJCas, WordToken pWordToken, String pMatch, List<String> pLragrRows) {

    
    LexicalElement statement = new LexicalElement( pJCas);
    statement.setBegin( pWordToken.getBegin());
    statement.setEnd( pWordToken.getBegin() + pMatch.length() );
    statement.setId( this.getClass().getSimpleName()+ "_" + this.annotationCounter++);
    List<String> euis = new ArrayList<String>();
    StringBuffer categories = new StringBuffer();
    
    String pos = null;
    for ( String row : pLragrRows ) {
      String[] cols = U.split( row );
      String eui = cols[0];
             pos = cols[2];
      String infl = cols[3];
      String uninfl = cols[4];
      String cit = cols[5];
      String category = cols[6];
      
      euis.add( eui );
      if ( categories.length() > 0 )
        categories.append(":");
      categories.append( category );
      }
   
    statement.setEuis( UIMAUtil.list2StringArray(pJCas, euis) );
   
    statement.setAssertionPredicate( pWordToken.getAssertionPredicate() );
    statement.setConditional( pWordToken.getConditional());
    statement.setDisplayString( pWordToken.getCoveredText() );
    statement.setGeneric(pWordToken.getGeneric() );
    statement.setHistorical(pWordToken.getHistorical() );
    statement.setInProse( pWordToken.getInProse());
    statement.setMarked( false);
    statement.setNegation_Status( pWordToken.getNegation_Status() );
    statement.setPos(pos);
    statement.setSection(pWordToken.getSection() );
    statement.setSectionName(pWordToken.getSectionName() );
    statement.setSemanticTypes( categories.toString() );
    statement.setSubject( pWordToken.getSubject() );
    statement.addToIndexes();
    
    
  } // end Method createLexicalElement() ---

  // ----------------------------------
  /**
   * destroy.
   */
  // ----------------------------------
  @Override
  public void destroy() {
    this.performanceMeter.writeProfile(this.getClass().getSimpleName());
  }

  // ----------------------------------
  /**
   * initialize loads in the resources.
   * 
   * @param pContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext pContext) throws ResourceInitializationException {

    String args[] = null;

    try {

      args = (String[]) pContext.getConfigParameterValue("args");

    } catch (Exception e) {

    }
    initialize(args);

  } // end Method initialize() -------

  // ----------------------------------
  /**
   * initialize loads in the resources.
   * 
   * @param pArgs
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {

    this.performanceMeter = new ProfilePerformanceMeter(pArgs, this.getClass().getSimpleName());
    
    String punctuationTermsFile = U.getOption(pArgs,"--punctuationTermFile=", "resources/vinciNLPFramework/term/punctuationTerms.lragr" );
    
    try {
      loadPunctuationTermsHash( punctuationTermsFile );
    } catch (Exception e) {
       throw new ResourceInitializationException();
    }
    

  } // end Method initialize() -------

  
  //================================================= 
  /** 
   * loadPunctuationTermsHash reads in the punctuation terms into
   * into a hash
   * 
   * This relies on a table in resources/vinciNLPFramework/terms/punctuationTerms.lragr
   * @param pInputFile 
   * 
   * @throws Exception 
   */
  //=================================================
  private final  void loadPunctuationTermsHash( String pInputFile) throws Exception {

   this.punctuationTerms = new HashMap<String,List<String>>();

    
    try {
      String[] rows = U.readClassPathResourceIntoStringArray(pInputFile);
     
    
    
      if (rows != null)  
        for ( String row : rows) {
          
          
          try {
          if (row != null && row.trim().length() > 0 && !row.startsWith("#")) {
            String cols[] = U.split(row,"|");
            String eui = cols[0].trim();
            String term = cols[1].trim();
            String pos = cols[2].trim();
            String infl = cols[3].trim();
            String citationForm = cols[4];
            String uninflectedForm = cols[5];
            String category = cols[6];
            
            List<String> lragr = null;
            if ( (lragr = this.punctuationTerms.get( term )) == null ) {
              List<String> newList = new ArrayList<String>();
              newList.add( row);
              this.punctuationTerms.put( term, newList );
            } else {
              lragr.add( row );
           }
          }
            
          } catch (Exception e3) {
            e3.printStackTrace();
            GLog.println(GLog.ERROR_LEVEL, this.getClass(), "loadPunctuationTermsHash", "Issue reading in line "  + e3.toString() + "\n" + row + "\n");
            throw e3;
         
          }
        }
    }
    catch (Exception e2 ) {
      e2.printStackTrace();
      GLog.println(GLog.ERROR_LEVEL, this.getClass(), "loadPunctuationTermsHash", "Issue reading in the file  " + pInputFile + " " + e2.toString() );
      throw e2;
   }

    
 
} // end Method loadPunctuationTermsHash() -----------


  // ---------------------------------------
  // Global Variables
  // ---------------------------------------

  protected int                   annotationCounter = 0;   // new Term Counter.
  private ProfilePerformanceMeter performanceMeter  = null;
  private HashMap<String,List<String>>  punctuationTerms = null;
  
} // end Class TermShapeAnnotator() ---------------
