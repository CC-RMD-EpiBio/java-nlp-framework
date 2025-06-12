// =================================================
/**
 * PersonTokensAnnotator creates a word token from the
 * tokens that make up a Person annotation. 
 * The underlying tokens are removed.
 * 
 * This is to make a de-identified name look like a single token for machine learning work.
 * 
 * Example [ First Name = 2018 ]     <--  all of that would be within one WordToken
 *
 *
 * @author  Guy Divita 
 * @created November 7 2019
 *
 * 

 */
// ================================================
package gov.nih.cc.rmd.nlp.framework.annotator.shapes;

import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import gov.nih.cc.rmd.framework.model.Person;
import gov.nih.cc.rmd.nlp.framework.utils.GLog;
import gov.nih.cc.rmd.nlp.framework.utils.ProfilePerformanceMeter;
import gov.nih.cc.rmd.nlp.framework.utils.uima.UIMAUtil;
import gov.va.chir.model.Token;
import gov.va.chir.model.WordToken;
import gov.va.chir.model.VAnnotation;



public class PersonTokensAnnotator extends JCasAnnotator_ImplBase {
 
  
  // -----------------------------------------
  /**
   * process  creates a word token from the
   * tokens that make up a Person annotation. 
   * The underlying tokens are removed.
   * 
   * This is to make a de-identified name look like a single token for machine learning work.
   * 
   * Example [ First Name = 2018 ]     <--  all of that would be within one WordToken
   * 
   */
  // -----------------------------------------
  public void process(JCas pJCas) throws AnalysisEngineProcessException {
   
    try {
    this.performanceMeter.startCounter();
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process", " Start " + this.getClass().getSimpleName());
    

    List<Annotation> persons = UIMAUtil.getAnnotations(pJCas, Person.typeIndexID, true);
    
    if ( persons != null && !persons.isEmpty() ) {
      for ( Annotation person: persons )
        convertPersonTokens( pJCas, person);
    }
    
   
    
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Issue with " + this.getClass().getName() + " " + e.toString());
   //   throw new AnalysisEngineProcessException();
    }
    GLog.println(GLog.DEBUG_LEVEL, this.getClass(), "process", " End " + this.getClass().getSimpleName());
    this.performanceMeter.stopCounter();
  
  } // end Method process() ----------------
   


// =================================================
  /**
   * convertPersonTokens retrieves all the tokens withi the Person span,
   * and creates a single wordToken of them, removing the individual tokens.
   * 
   * @param pJCas
   * @param person
  */
  // =================================================
 private final void convertPersonTokens(JCas pJCas, Annotation pPerson) {
   
  String nameType = "Name";
  String txt = pPerson.getCoveredText();
  if ( txt.toLowerCase().contains("first"))
    nameType = "fname";
  else if ( txt.toLowerCase().contains("last"))
    nameType = "lname";
  else if ( txt.toLowerCase().contains("nickname"))
    nameType = "nname";
  
  List<Annotation> personTokens = UIMAUtil.getAnnotationsBySpan(pJCas, Token.typeIndexID, pPerson.getBegin(),  pPerson.getEnd(), true);
  Annotation firstToken = null;
  if ( personTokens != null && !personTokens.isEmpty() ) {
    firstToken = personTokens.get(0);
    int k = 0;
    while ( firstToken.getClass().getName().contains("White") )
      firstToken = personTokens.get(k++);
    try {
    createWordToken( pJCas, pPerson.getBegin(), pPerson.getEnd(), (Token) firstToken, nameType );  
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    for ( int i = 1; i < personTokens.size(); i++ ) {
      if ( personTokens.get(i) != null )
       personTokens.get(i).removeFromIndexes();
    }
  }
    
  } // end Method convertPersonTokens() --------------



  // =================================================
/**
 * createWordToken creates a word token
 * 
 * @param pJCas
 * @param pBegin
 * @param pEnd
*/
// =================================================
private final void createWordToken(JCas pJCas, int pBegin, int pEnd, Annotation pToken, String pNameType) {
 
  
  
	
  WordToken statement =  new WordToken(pJCas );
  statement.setBegin(pToken.getBegin());
  statement.setEnd( pEnd);
  statement.setId(((Token) pToken).getId() );
  
  statement.setAllCapitalization(  ((Token) pToken).getAllCapitalization() );
  statement.setAttributedToPatient( ((Token) pToken).getAttributedToPatient() );
  statement.setAssertionPredicate(((Token) pToken).getAssertionPredicate());
  statement.setConditional(((Token) pToken).getConditional() );
  statement.setContainsPunctuation(((Token) pToken).getContainsPunctuation());
  statement.setContainsSymbol(((Token) pToken).getContainsSymbol() );
  statement.setContextLeft( ((Token) pToken).getContextLeft() );
  statement.setContextRight(((Token) pToken).getContextRight() );
  statement.setDisplayString(((Token) pToken).getDisplayString() );
  statement.setEventDate(((Token) pToken).getEventDate() );
  statement.setGeneric( ((Token) pToken).getGeneric() );
  statement.setHistorical(((Token) pToken).getHistorical() );
  statement.setInCopper(((Token) pToken).getInCopper());
  statement.setInGold(((Token) pToken).getInGold() );
  statement.setInitialCapitalization(((Token) pToken).getInitialCapitalization() );
  statement.setInProse( ((Token) pToken).getInProse ()  );
  statement.setListDelimiter(((Token) pToken).getListDelimiter() );
  statement.setMarked( ((Token) pToken).getMarked() );
  statement.setNegation_Status(((Token) pToken).getNegation_Status() );
  statement.setOtherFeatures(((Token) pToken).getOtherFeatures() );
  statement.setParent(((Token) pToken).getParent() );
  statement.setPartOfSpeech( ((Token) pToken).getPartOfSpeech());
  statement.setPhraseTag(((Token) pToken).getPhraseTag() );
  statement.setPos(((Token) pToken).getPos() );
  statement.setProcessMe(((Token) pToken).getProcessMe() );
  
  statement.setProvenance(((Token) pToken).getProvenance() );
  statement.setSection(((Token) pToken).getSection() );
  statement.setSectionName(((Token) pToken).getSectionName() );
  statement.setSentenceBreak(((Token) pToken).getSentenceBreak() );
  statement.setStatementDate(((Token) pToken).getStatementDate() );
  statement.setSubject(((Token) pToken).getSubject() );
  statement.setSubType( ((Token) pToken).getSubType() );
  statement.setTokenNumber(((Token) pToken).getTokenNumber() );
  statement.setTokenType(((Token) pToken).getTokenType() );
  statement.setWhiteSpaceFollows(((Token) pToken).getWhiteSpaceFollows() );
  
  statement.setReplaceWithClass( true);
  statement.setDisplayString( pNameType );
  statement.addToIndexes(pJCas);
  pToken.removeFromIndexes();
  
  
  
} // end Method createWordToken() -------------





//----------------------------------
/**
 * destroy
* 
 **/
// ----------------------------------
public void destroy() {
  this.performanceMeter.writeProfile( this.getClass().getSimpleName());
}

  
  //----------------------------------
  /**
   * initialize loads in the resources. 
   * 
   * @param aContext
   * 
   **/
  // ----------------------------------
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
       
     
      String[] args = null;
      try {
        args                 = (String[]) aContext.getConfigParameterValue("args");  

        initialize(args);
        
      } catch (Exception e ) {
        String msg = "Issue in initializing class " + this.getClass().getName() + " " + e.toString() ;
        GLog.println(GLog.ERROR_LEVEL, msg);     // <------ use your own logging here
        throw new ResourceInitializationException();
      }
      
   
      
  } // end Method initialize() -------
  
  //----------------------------------
  /**
   * initialize initializes the class.  Parameters are passed in via a String
   *                array  with each row containing a --key=value format.
   *                
   *                It is important to adhere to the posix style "--" prefix and
   *                include a "=someValue" to fill in the value to the key. 
   * @param pArgs
   * @throws  ResourceInitializationException            
   * 
   **/
  // ----------------------------------
  public void initialize(String[] pArgs) throws ResourceInitializationException {
       
    this.performanceMeter = new ProfilePerformanceMeter( pArgs, this.getClass().getSimpleName() );
    
    
      
  } // end Method initialize() -------
 
  // ---------------------------------------
  // Global Variables
  // ---------------------------------------
  protected int annotationCtr = 0;
  ProfilePerformanceMeter performanceMeter = null;
  
  
  
} // end Class LineAnnotator() ---------------

